package ui.tags;

import play.mvc.Content;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Results;

public final class MessagesHelper {
	private MessagesHelper() {
		throw new AssertionError();
	}

	public static void invalidForm() {
		Messages.error("Please fix errors below");
	}

	public static Result invalidForm(Content content) {
		invalidForm();
		return Results.badRequest(content);
	}
}
