package models;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import scala.Option;
import util.security.SessionUtil;

/**
 * Used in UI. Agregated results wrapper for a poll. Non-persistent.
 * 
 * @author adericbourg
 * 
 */
public class PollResults {
	// Username -> checked choices.
	private final Map<String, Set<Long>> results = new TreeMap<String, Set<Long>>();
	// Choice -> answer count.
	private final Map<Long, Long> totals = new HashMap<Long, Long>();
	// Users.
	private final Map<String, User> users = new HashMap<String, User>();
	private Long maxValue;
	private Boolean alreadyAnswered;

	public void registerUser(User user) {
		results.put(user.username, new HashSet<Long>());
		users.put(user.username, user);
	}

	public void addAnswer(String username, Long choiceId) {
		results.get(username).add(choiceId);
		// Totals.
		if (!totals.containsKey(choiceId)) {
			totals.put(choiceId, 1L);
		} else {
			totals.put(choiceId, totals.get(choiceId) + 1);
		}
	}

	public Collection<String> usernames() {
		return results.keySet();
	}

	public boolean isCheckedChoice(String username, Long choiceId) {
		return (results.get(username) != null)
				&& results.get(username).contains(choiceId);
	}

	public Long total(Long choiceId) {
		return totals.containsKey(choiceId) ? totals.get(choiceId) : 0;
	}

	public boolean isBetterChoice(Long choiceId) {
		if (maxValue == null) {
			findMaxValue();
		}
		return maxValue.equals(total(choiceId));
	}

	public boolean isAlreadyAnswered() {
		if (alreadyAnswered == null) {
			Option<User> user = SessionUtil.currentUser();
			if (user.isEmpty()) {
				alreadyAnswered = false;
			} else {
				alreadyAnswered = results.containsKey(user.get().username);
			}
		}
		return alreadyAnswered.booleanValue();
	}

	public Option<User> getUser(String username) {
		return Option.apply(users.get(username));
	}

	private void findMaxValue() {
		maxValue = -1L;
		for (Long value : totals.values()) {
			maxValue = Math.max(maxValue, value);
		}
	}
}
