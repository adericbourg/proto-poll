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
import services.exception.NoAuthenfiedUserInSessionException;
import util.security.SessionUtil;

import com.avaje.ebean.Ebean;

public class PollService {

	private static final Finder<UUID, Poll> POLL_FINDER = new Finder<UUID, Poll>(
			UUID.class, Poll.class);

	public static List<Poll> polls() {
		return Ebean.find(Poll.class).findList();
	}

	public static Poll getPoll(UUID id) {
		return POLL_FINDER.byId(id);
	}

	public static void initPoll(Event event) {
		Poll poll = new Poll(event);
		event.poll = initPoll(poll);
	}

	public static void initPoll(Question question) {
		Poll poll = new Poll(question);
		question.poll = initPoll(poll);
	}

	private static Poll initPoll(Poll poll) {
		poll.userCreator = SessionUtil.currentUser();
		poll.creationDate = DateTime.now();
		poll.save();
		return poll;
	}

	public static List<Poll> listUserPolls() {
		if (!SessionUtil.isAuthenticated()) {
			throw new NoAuthenfiedUserInSessionException();
		}
		return POLL_FINDER.where()
				.eq("userCreator.id", SessionUtil.currentUser().id)
				.orderBy("creationDate DESC").findList();
	}

	public static void postComment(UUID id, String comment) {
		postComment(id, comment, SessionUtil.currentUser());
	}

	public static void postComment(UUID id, String comment, String username) {
		User user = UserService.registerAnonymousUser(username);
		postComment(id, comment, user);
	}

	private static void postComment(UUID pollId, String commentText, User user) {
		Poll poll = getPoll(pollId);

		Comment comment = new Comment();
		comment.content = commentText;
		comment.user = user;
		comment.submitDate = DateTime.now();
		comment.poll = poll;

		comment.save();
	}
}
