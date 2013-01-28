package services;

import java.util.List;

import models.Poll;
import play.db.ebean.Model.Finder;

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
}
