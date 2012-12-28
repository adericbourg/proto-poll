package ui.tags;

public final class MessagesHelper {
	private MessagesHelper() {
		throw new AssertionError();
	}

	public static void invalidForm() {
		Messages.error("Please fix errors below");
	}
}
