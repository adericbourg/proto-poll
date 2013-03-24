package controllers.model.poll;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import models.User;
import util.binders.UuidBinder;

import com.google.common.base.Strings;

public class PollResults {

	PollResults() {
		super();
	}

	// Header fields.

	public UuidBinder uuid;
	public String title;
	public String description;
	public boolean singleAnswer;
	public User userCreator;

	// Choices.

	public Map<Long, String> choices;

	// Results.

	public Map<Long, User> users; // User id -> User.
	public Map<Long, Set<Long>> answers; // User id -> Set(choice id).

	// Stats.

	public boolean isAlreadyAnswered;
	Map<Long, AtomicLong> totals;
	Long maxValue;

	public long total(Long choiceId) {
		if (!totals.containsKey(choiceId)) {
			return 0L;
		}
		return totals.get(choiceId).get();
	}

	public boolean isBetterChoice(Long choiceId) {
		if (!totals.containsKey(choiceId)) {
			return false;
		}
		return maxValue.equals(totals.get(choiceId).get());
	}

	public boolean checked(Long userId, Long choiceId) {
		if (answers.containsKey(userId)) {
			return answers.get(userId).contains(choiceId);
		}
		return false;
	}

	// Util.

	public boolean hasDescription() {
		return !Strings.isNullOrEmpty(description);
	}

	public boolean hasRegisteredCreator() {
		return userCreator != null;
	}

	public boolean singleChoice() {
		return choices.size() == 1;
	}
}
