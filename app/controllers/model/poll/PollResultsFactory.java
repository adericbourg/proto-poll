package controllers.model.poll;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import models.EventAnswer;
import models.EventAnswerDetail;
import models.EventChoice;
import models.Poll;
import models.QuestionAnswer;
import models.QuestionAnswerDetail;
import models.QuestionChoice;
import models.User;
import services.exception.poll.InconsistentPollException;
import ui.util.UIUtil;
import util.binders.UuidBinder;
import util.security.CurrentUser;

public final class PollResultsFactory {

	public static PollResults build(Poll poll) {
		PollResults results;

		if (poll.isEvent()) {
			results = buildEvent(poll);
		} else if (poll.isQuestion()) {
			results = buildQuestion(poll);
		} else {
			throw new InconsistentPollException();
		}

		buildStats(results);

		return results;
	}

	private static PollResults buildEvent(Poll poll) {
		PollResults results = initWithHeaders(poll);

		// Choices.
		Map<Long, String> choices = new LinkedHashMap<Long, String>();
		for (EventChoice choice : poll.event.dates) {
			choices.put(choice.id, UIUtil.formatDate(choice.date));
		}
		results.choices = choices;

		// Results.
		Map<Long, User> users = new LinkedHashMap<Long, User>();
		Map<Long, Set<Long>> answers = new HashMap<Long, Set<Long>>();
		for (EventAnswer answer : poll.event.answers) {
			users.put(answer.user.id, answer.user);
			answers.put(answer.user.id, new HashSet<Long>());
			for (EventAnswerDetail detail : answer.details) {
				answers.get(answer.user.id).add(detail.choice.id);
			}
		}
		results.users = Collections.unmodifiableMap(users);
		results.answers = Collections.unmodifiableMap(answers);

		return results;
	}

	private static PollResults buildQuestion(Poll poll) {
		PollResults results = initWithHeaders(poll);

		// Choices.
		Map<Long, String> choices = new LinkedHashMap<Long, String>();
		for (QuestionChoice choice : poll.question.choices) {
			choices.put(choice.id, choice.label);
		}
		results.choices = Collections.unmodifiableMap(choices);

		// Results.
		Map<Long, User> users = new LinkedHashMap<Long, User>();
		Map<Long, Set<Long>> answers = new HashMap<Long, Set<Long>>();
		for (QuestionAnswer answer : poll.question.answers) {
			users.put(answer.user.id, answer.user);
			answers.put(answer.user.id, new HashSet<Long>());
			for (QuestionAnswerDetail detail : answer.details) {
				answers.get(answer.user.id).add(detail.choice.id);
			}
		}
		results.users = Collections.unmodifiableMap(users);
		results.answers = Collections.unmodifiableMap(answers);

		return results;
	}

	private static PollResults initWithHeaders(Poll poll) {
		PollResults results = new PollResults();

		results.uuid = UuidBinder.create(poll.uuid);
		results.title = poll.title;
		results.description = poll.description;
		results.userCreator = poll.userCreator;
		results.singleAnswer = poll.singleAnswer;

		return results;
	}

	private static void buildStats(PollResults results) {
		results.totals = getTotal(results);
		results.maxValue = findMaxValue(results);
		if (CurrentUser.isAuthenticated()) {
			results.isAlreadyAnswered = results.users.containsKey(CurrentUser
					.currentUser().get().id);
		}
	}

	private static Map<Long, AtomicLong> getTotal(PollResults results) {
		Map<Long, AtomicLong> totals = new HashMap<Long, AtomicLong>();
		for (Set<Long> selection : results.answers.values()) {
			for (Long choiceId : selection) {
				if (!totals.containsKey(choiceId)) {
					totals.put(choiceId, new AtomicLong(0));
				}
				totals.get(choiceId).addAndGet(1);
			}
		}
		return totals;
	}

	private static long findMaxValue(PollResults results) {
		Long maxValue = -1L;
		for (AtomicLong count : results.totals.values()) {
			maxValue = Math.max(maxValue, count.get());
		}
		return maxValue;
	}
}
