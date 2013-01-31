package ui.tags;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public final class Messages {

	private static final ThreadLocal<Queue<String>> INFO_QUEUE = new ThreadLocal<Queue<String>>();
	private static final ThreadLocal<Queue<String>> WARNING_QUEUE = new ThreadLocal<Queue<String>>();
	private static final ThreadLocal<Queue<String>> ERROR_QUEUE = new ThreadLocal<Queue<String>>();

	private Messages() {
		// No instance.
		throw new AssertionError();
	}

	public static void info(String message) {
		if (INFO_QUEUE.get() == null) {
			INFO_QUEUE.set(new LinkedList<String>());
		}
		INFO_QUEUE.get().add(message);
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
		return INFO_QUEUE.get().poll();
	}

	private static boolean hasInfo() {
		return INFO_QUEUE.get() != null && !INFO_QUEUE.get().isEmpty();
	}

	public static void warning(String message) {
		if (WARNING_QUEUE.get() == null) {
			WARNING_QUEUE.set(new LinkedList<String>());
		}
		WARNING_QUEUE.get().add(message);
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
		return WARNING_QUEUE.get().poll();
	}

	private static boolean hasWarning() {
		return WARNING_QUEUE.get() != null && !WARNING_QUEUE.get().isEmpty();
	}

	public static void error(String message) {
		if (ERROR_QUEUE.get() == null) {
			ERROR_QUEUE.set(new LinkedList<String>());
		}
		ERROR_QUEUE.get().add(message);
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
		return ERROR_QUEUE.get().poll();
	}

	private static boolean hasError() {
		return ERROR_QUEUE.get() != null && !ERROR_QUEUE.get().isEmpty();
	}

}