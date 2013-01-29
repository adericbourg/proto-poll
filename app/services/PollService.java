package services;

import java.util.List;

import models.Event;
import models.Poll;
import models.Question;

import org.joda.time.DateTime;

import play.db.ebean.Model.Finder;
import util.security.SessionUtil;

import com.avaje.ebean.Ebean;

public class PollService {

	private static final Finder<Long, Poll> POLL_FINDER = new Finder<Long, Poll>(
			Long.class, Poll.class);

	public static List<Poll> polls() {
		return Ebean.find(Poll.class).findList();
	}

	public static Poll getPoll(Long id) {
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
}
