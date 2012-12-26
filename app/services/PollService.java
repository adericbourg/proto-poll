package services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Answer;
import models.AnswerDetail;
import models.Choice;
import models.Poll;
import models.User;
import play.db.ebean.Model.Finder;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.ExpressionList;

/**
 * Poll management service.
 * 
 * @author adericbourg
 * 
 */
public class PollService {

	private static final Finder<Long, Poll> POLL_FINDER = new Finder<Long, Poll>(
			Long.class, Poll.class);
	private static final Finder<Long, Choice> CHOICE_FINDER = new Finder<Long, Choice>(
			Long.class, Choice.class);

	private PollService() {
		// No instance.
		throw new AssertionError();
	}

	public static Long createPoll(Poll poll) {
		poll.save();
		return poll.id;
	}

	public static void saveChoices(Long pollId, List<Choice> choices) {
		Poll poll = getPoll(pollId);
		poll.choices = choices;
		poll.save();
	}

	public static Poll getPoll(Long id) {
		return POLL_FINDER.byId(id);
	}

	public static Choice getChoice(Long id) {
		return CHOICE_FINDER.byId(id);
	}

	public static List<Poll> polls() {
		return Ebean.find(Poll.class).findList();
	}

	public static List<Choice> getChoicesByPoll(Long pollId) {
		List<Choice> choices = Ebean.find(Choice.class).where()
				.eq("poll.id", pollId).findList();
		Ebean.sort(choices, "sortOrder");
		return choices;
	}

	public static void answerPoll(String username, Long pollId,
			Collection<Long> choiceIds) {
		User user = UserService.getUserOrRegisterByName(username);
		Poll poll = getPollWithChoices(pollId);
		Answer answer = getOrCreateAnswer(user, poll);

		// Clear all previous answers.
		for (AnswerDetail detail : answer.details) {
			detail.delete();
		}

		// Map poll choices.
		Map<Long, Choice> choices = new HashMap<Long, Choice>();
		for (Choice choice : poll.choices) {
			choices.put(choice.id, choice);
		}

		// Save current choices.
		Choice choice;
		AnswerDetail detail;
		List<AnswerDetail> details = new ArrayList<AnswerDetail>();
		for (Long choiceId : choiceIds) {
			choice = choices.get(choiceId);
			detail = new AnswerDetail();
			detail.choice = choice;
			details.add(detail);
		}
		answer.details = details;
		answer.save();
	}

	private static Answer getOrCreateAnswer(User user, Poll poll) {
		ExpressionList<Answer> el = Ebean.find(Answer.class).fetch("details")
				.where().eq("user.id", user.id).eq("poll.id", poll.id);
		if (el.findRowCount() == 0) {
			Answer answer = new Answer();
			answer.user = user;
			answer.poll = poll;
			answer.save();
			return getOrCreateAnswer(user, poll);
		}
		return el.findUnique();
	}

	public static Poll getPollWithAnswers(Long pollId) {
		return Ebean.find(Poll.class).fetch("answers").fetch("answers.user")
				.fetch("answers.details").fetch("answers.details.choice")
				.where().eq("id", pollId).findUnique();
	}

	private static Poll getPollWithChoices(Long pollId) {
		return Ebean.find(Poll.class).fetch("choices").where().eq("id", pollId)
				.findUnique();

	}
}
