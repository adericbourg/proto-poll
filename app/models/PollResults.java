package models;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Used in UI. Agregated results wrapper for a poll. Non-persistent.
 * 
 * @author adericbourg
 * 
 */
public class PollResults {
	private final Map<String, Set<Long>> results = new TreeMap<>();
	private final Map<Long, Long> totals = new HashMap<>();
	private Long maxValue;

	public void registerUser(String username) {
		results.put(username, new HashSet<Long>());
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
		return results.get(username) != null
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

	private void findMaxValue() {
		maxValue = -1L;
		for (Long value : totals.values()) {
			maxValue = Math.max(maxValue, value);
		}
	}
}
