package ui.tags;

import play.mvc.Content;
import play.mvc.Result;
import play.mvc.Results;
import ui.user.message.UserMessage;
import util.user.message.Messages;

public final class MessagesHelper {
	private MessagesHelper() {
		throw new AssertionError();
	}

	public static void invalidForm() {
		Messages.error(UserMessage.FIX_ERRORS);
	}

	public static Result invalidForm(Content content) {
		invalidForm();
		return Results.badRequest(content);
	}
}
