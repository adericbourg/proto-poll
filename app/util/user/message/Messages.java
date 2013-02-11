package util.user.message;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import util.security.SessionUtil;

public final class Messages {

	private static class Message {
		private MessageKey messageKey;
		private Object[] params;

		private Message(MessageKey messageKey, Object... params) {
			this.messageKey = messageKey;
			this.params = params;
		}

		private String resolve() {
			return Messages.resolve(messageKey.getCode(), params);
		}
	}

	private static final ThreadLocal<Queue<Message>> INFO_QUEUE = new ThreadLocal<Queue<Message>>();
	private static final ThreadLocal<Queue<Message>> WARNING_QUEUE = new ThreadLocal<Queue<Message>>();
	private static final ThreadLocal<Queue<Message>> ERROR_QUEUE = new ThreadLocal<Queue<Message>>();

	private Messages() {
		// No instance.
		throw new AssertionError();
	}

	// --- UTIL ---

	public static String resolve(String code, Object... params) {
		if (SessionUtil.preferredLang().isDefined()) {
			return play.i18n.Messages.get(SessionUtil.preferredLang().get(),
					code, params);
		}
		return play.i18n.Messages.get(code, params);
	}

	// --- INFO ---

	public static void info(MessageKey messageKey, Object... params) {
		if (INFO_QUEUE.get() == null) {
			INFO_QUEUE.set(new LinkedList<Message>());
		}
		INFO_QUEUE.get().add(new Message(messageKey, params));
	}

	public static List<String> listInfo() {
		List<String> infos = new ArrayList<String>();
		if (hasInfo()) {
			do {
				infos.add(popInfo());
			} while (hasInfo());
		}
		return infos;
	}

	private static String popInfo() {
		if (INFO_QUEUE.get() == null) {
			return null;
		}
		return INFO_QUEUE.get().poll().resolve();
	}

	private static boolean hasInfo() {
		return (INFO_QUEUE.get() != null) && !INFO_QUEUE.get().isEmpty();
	}

	// --- WARNING ---

	public static void warning(MessageKey messageKey, Object... params) {
		if (WARNING_QUEUE.get() == null) {
			WARNING_QUEUE.set(new LinkedList<Message>());
		}
		WARNING_QUEUE.get().add(new Message(messageKey, params));
	}

	public static List<String> listWarning() {
		List<String> warnings = new ArrayList<String>();
		if (hasWarning()) {
			do {
				warnings.add(popWarning());
			} while (hasWarning());
		}
		return warnings;
	}

	private static String popWarning() {
		if (WARNING_QUEUE.get() == null) {
			return null;
		}
		return WARNING_QUEUE.get().poll().resolve();
	}

	private static boolean hasWarning() {
		return (WARNING_QUEUE.get() != null) && !WARNING_QUEUE.get().isEmpty();
	}

	// --- ERROR ---

	public static void error(MessageKey messageKey, Object... params) {
		if (ERROR_QUEUE.get() == null) {
			ERROR_QUEUE.set(new LinkedList<Message>());
		}
		ERROR_QUEUE.get().add(new Message(messageKey, params));
	}

	public static List<String> listError() {
		List<String> errors = new ArrayList<String>();
		if (hasError()) {
			do {
				errors.add(popError());
			} while (hasError());
		}
		return errors;
	}

	private static String popError() {
		if (ERROR_QUEUE.get() == null) {
			return null;
		}
		return ERROR_QUEUE.get().poll().resolve();
	}

	private static boolean hasError() {
		return (ERROR_QUEUE.get() != null) && !ERROR_QUEUE.get().isEmpty();
	}

}
