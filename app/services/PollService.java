package services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import models.Comment;
import models.Event;
import models.EventAnswer;
import models.Poll;
import models.Question;
import models.QuestionAnswer;
import models.User;
import models.reference.PollStatus;

import org.joda.time.DateTime;

import play.db.ebean.Model.Finder;
import play.db.ebean.Transactional;
import services.exception.poll.InconsistentPollException;
import services.exception.poll.NoAnswerFoundException;
import services.exception.poll.NoAuthenfiedUserInSessionException;
import services.exception.poll.PollNotFoundException;
import util.security.SessionUtil;

import com.avaje.ebean.Ebean;

public class PollService {

	private static final Finder<UUID, Poll> POLL_FINDER = new Finder<UUID, Poll>(
			UUID.class, Poll.class);

	public static Poll createPoll(Poll poll) {
		if (poll.uuid != null) {
			throw new IllegalArgumentException("Poll is already saved");
		}
		if (!poll.isEvent() && !poll.isQuestion()) {
			throw new IllegalArgumentException(
					"Poll is must be a question or an event");
		}
		if (poll.isEvent() && poll.isQuestion()) {
			throw new IllegalArgumentException(
					"Poll is must be a question or an event but not both");
		}

		if (poll.isEvent()) {
			poll.event.save();
		}
		if (poll.isQuestion()) {
			poll.question.save();
		}
		if (SessionUtil.currentUser().isDefined()) {
			poll.userCreator = SessionUtil.currentUser().get();
		}
		poll.creationDate = DateTime.now();
		poll.status = PollStatus.DRAFT;
		poll.save();
		return poll;
	}

	@Transactional
	public static List<Poll> polls() {
		return Ebean.find(Poll.class).orderBy("creationDate DESC").findList();
	}

	@Transactional
	public static List<Poll> listUserPolls() {
		return listUserPolls(PollStatus.COMPLETE);
	}

	private static List<Poll> listUserPolls(PollStatus... statuses) {
		if (!SessionUtil.isAuthenticated()) {
			throw new NoAuthenfiedUserInSessionException();
		}
		if (statuses == null) {
			return new ArrayList<Poll>();
		}
		return POLL_FINDER.where()
				.eq("userCreator.id", SessionUtil.currentUser().get().id)
				.in("status", Arrays.asList(statuses))
				.orderBy("creationDate DESC").findList();
	}

	@Transactional
	public static Poll getPoll(UUID uuid) {
		Poll poll = POLL_FINDER
				.fetch("userCreator")
				// Question
				.fetch("question").fetch("question.choices")
				.fetch("question.answers").fetch("question.answers.details")
				// Event
				.fetch("event").fetch("event.dates").fetch("event.answers")
				.fetch("event.answers.details")
				// Filter
				.orderBy("creationDate DESC").where().idEq(uuid).findUnique();
		if (poll != null) {
			Ebean.sort(poll.comments, "submitDate ASC");
			if (poll.isEvent()) {
				Ebean.sort(poll.event.dates, "date ASC");
			}
			if (poll.isQuestion()) {
				Ebean.sort(poll.question.choices, "sortOrder ASC");
			}
		}
		return poll;
	}

	@Transactional
	public static void answerPollAnonymous(String username, UUID uuid,
			Collection<Long> choiceIds) {
		Poll poll = getPoll(uuid);
		if (poll == null) {
			throw new PollNotFoundException();
		}

		if (poll.isEvent()) {
			EventService.answerEventAnonymous(username, uuid, choiceIds);
		} else if (poll.isQuestion()) {
			QuestionService.answerQuestionAnonymous(username, uuid, choiceIds);
		} else {
			throw new InconsistentPollException();
		}
	}

	@Transactional
	public static void answerPollRegistered(UUID uuid,
			Collection<Long> choiceIds) {
		Poll poll = getPoll(uuid);
		if (poll == null) {
			throw new PollNotFoundException();
		}

		if (poll.isEvent()) {
			EventService.answerEventRegistered(uuid, choiceIds);
		} else if (poll.isQuestion()) {
			QuestionService.answerQuestionRegistered(uuid, choiceIds);
		} else {
			throw new InconsistentPollException();
		}
	}

	@Transactional
	public static Question getQuestion(UUID uuid) {
		Poll poll = getPoll(uuid);
		if ((poll == null) || !poll.isQuestion()) {
			return null;
		}
		return poll.question;
	}

	@Transactional
	public static Event getEvent(UUID uuid) {
		Poll poll = getPoll(uuid);
		if ((poll == null) || !poll.isEvent()) {
			return null;
		}
		return poll.event;
	}

	@Transactional
	public static void postCommentRegistered(UUID uuid, String comment) {
		if (SessionUtil.currentUser().isDefined()) {
			postComment(uuid, comment, SessionUtil.currentUser().get());
		} else {
			throw new NoAuthenfiedUserInSessionException();
		}
	}

	@Transactional
	public static void postCommentAnonymous(UUID uuid, String comment,
			String username) {
		User user = UserService.registerAnonymousUser(username);
		postComment(uuid, comment, user);
	}

	private static void postComment(UUID uuid, String commentText, User user) {
		Poll poll = getPoll(uuid);

		Comment comment = new Comment();
		comment.content = commentText;
		comment.user = user;
		comment.submitDate = DateTime.now();
		comment.poll = poll;

		comment.save();
	}

	@Transactional
	public static void removeCurrentUserAnswer(UUID uuid) {
		if (!SessionUtil.isAuthenticated()) {
			throw new NoAuthenfiedUserInSessionException();
		}

		Poll poll = getPoll(uuid);
		User user = SessionUtil.currentUser().get();
		if (poll.isEvent()) {
			removeEventCurrentUserAnswer(poll.event, user);
		}
		if (poll.isQuestion()) {
			removeQuestionCurrentUserAnswer(poll.question, user);
		}
	}

	private static void removeEventCurrentUserAnswer(Event event, User user) {
		Long userId = user.id;
		EventAnswer toBeDeleted = null;
		for (EventAnswer answer : event.answers) {
			if (answer.user.id.equals(userId)) {
				toBeDeleted = answer;
				break;
			}
		}
		if (toBeDeleted == null) {
			throw new NoAnswerFoundException();
		}
		toBeDeleted.delete();
	}

	private static void removeQuestionCurrentUserAnswer(Question question,
			User user) {
		Long userId = user.id;
		QuestionAnswer toBeDeleted = null;
		for (QuestionAnswer answer : question.answers) {
			if (answer.user.id.equals(userId)) {
				toBeDeleted = answer;
				break;
			}
		}
		if (toBeDeleted == null) {
			throw new NoAnswerFoundException();
		}
		toBeDeleted.delete();
	}

	static Poll updateStatus(UUID uuid, PollStatus status) {
		Poll poll = getPoll(uuid);
		poll.status = status;
		poll.save();
		return poll;
	}
}
