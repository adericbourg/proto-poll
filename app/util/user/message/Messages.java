package util.user.message;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import play.mvc.Http.Context;
import play.mvc.Http.Flash;
import util.security.SessionUtil;

public final class Messages {

	private static final String INFO_COUNT = "_info_count";
	private static final String WARN_COUNT = "_warn_count";
	private static final String ERROR_COUNT = "_err_count";

	private static final String INFO_PREFIX = "info_";
	private static final String WARN_PREFIX = "warn_";
	private static final String ERROR_PREFIX = "err_";

	private Messages() {
		// No instance.
		throw new AssertionError();
	}

	// --- INFO ---

	public static void info(MessageKey messageKey, Object... params) {
		flash(INFO_PREFIX, INFO_COUNT, messageKey, params);
	}

	public static List<String> listInfo() {
		return new ArrayList<String>(mapInfos().values());
	}

	private static Map<String, String> mapInfos() {
		return mapByPrefix(INFO_PREFIX);
	}

	// --- WARNING ---

	public static void warning(MessageKey messageKey, Object... params) {
		flash(WARN_PREFIX, WARN_COUNT, messageKey, params);
	}

	public static List<String> listWarning() {
		return new ArrayList<String>(mapWarnings().values());
	}

	private static Map<String, String> mapWarnings() {
		return mapByPrefix(WARN_PREFIX);
	}

	// --- ERROR ---

	public static void error(MessageKey messageKey, Object... params) {
		flash(ERROR_PREFIX, ERROR_COUNT, messageKey, params);
	}

	public static List<String> listError() {
		return new ArrayList<String>(mapErrors().values());
	}

	private static Map<String, String> mapErrors() {
		return mapByPrefix(ERROR_PREFIX);
	}

	// --- MESSAGE UTIL

	public static String resolve(MessageKey messageKey, Object... params) {
		return resolve(messageKey.getCode(), params);
	}

	public static String resolve(String code, Object... params) {
		if (SessionUtil.preferredLang().isDefined()) {
			return play.i18n.Messages.get(SessionUtil.preferredLang().get(),
					code, params);
		}
		return play.i18n.Messages.get(code, params);
	}

	private static synchronized void flash(String prefix, String counterKey,
			MessageKey messageKey, Object... params) {
		int count = flash().get(counterKey) == null ? 0 : Integer
				.valueOf(flash().get(counterKey));
		flash().put(prefix + ++count, resolve(messageKey.getCode(), params));
		flash().put(counterKey, String.valueOf(count));
	}

	private static Map<String, String> mapByPrefix(String prefix) {
		Map<String, String> messages = new LinkedHashMap<String, String>();
		for (Entry<String, String> entry : flash().entrySet()) {
			if (entry.getKey().startsWith(prefix)) {
				messages.put(entry.getKey(), entry.getValue());
			}
		}
		return messages;
	}

	private static Flash flash() {
		return Context.current().flash();
	}
}
