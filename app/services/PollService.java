package services;

import java.util.List;
import java.util.UUID;

import models.Comment;
import models.Event;
import models.Poll;
import models.Question;
import models.User;

import org.joda.time.DateTime;

import play.db.ebean.Model.Finder;
import play.db.ebean.Transactional;
import services.exception.NoAuthenfiedUserInSessionException;
import util.security.SessionUtil;

import com.avaje.ebean.Ebean;

public class PollService {

	private static final Finder<UUID, Poll> POLL_FINDER = new Finder<UUID, Poll>(
			UUID.class, Poll.class);

	@Transactional
	public static List<Poll> polls() {
		return Ebean.find(Poll.class).orderBy("creationDate DESC").findList();
	}

	@Transactional
	public static List<Poll> listUserPolls() {
		if (!SessionUtil.isAuthenticated()) {
			throw new NoAuthenfiedUserInSessionException();
		}
		return POLL_FINDER.where()
				.eq("userCreator.id", SessionUtil.currentUser().get().id)
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

	static void initPoll(Event event) {
		Poll poll = new Poll(event);
		event.poll = initPoll(poll);
	}

	static void initPoll(Question question) {
		Poll poll = new Poll(question);
		question.poll = initPoll(poll);
	}

	private static Poll initPoll(Poll poll) {
		if (SessionUtil.currentUser().isDefined()) {
			poll.userCreator = SessionUtil.currentUser().get();
		}
		poll.creationDate = DateTime.now();
		poll.save();
		return poll;
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
}
