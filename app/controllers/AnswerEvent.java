package controllers;

import static ui.tags.Messages.error;
import static ui.tags.Messages.info;

import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import play.data.DynamicForm;
import play.mvc.Controller;
import play.mvc.Result;
import services.EventService;
import services.exception.AnonymousUserAlreadyAnsweredPoll;
import util.binders.UuidBinder;
import util.security.SessionUtil;

import com.google.common.base.Strings;

public class AnswerEvent extends Controller {

	private static final String USERNAME_KEY = "data[username]";

	public static Result answer(UuidBinder uuid) {

		DynamicForm form = form().bindFromRequest();
		Set<Long> choices = new HashSet<Long>();
		for (Entry<String, String> entry : form.data().entrySet()) {
			if (!isUsername(entry.getKey())) {
				choices.add(Long.valueOf(entry.getValue()));
			}
		}
		if (SessionUtil.isAuthenticated()) {
			EventService.answerEvent(uuid.uuid(), choices);
		} else {
			String username = form.data().get(USERNAME_KEY);
			if (Strings.isNullOrEmpty(username)) {
				error("Choose a user name.");
				return badRequest(AnswerPoll.getEventViewContent(uuid.uuid()));
			}
			try {
				EventService.answerEvent(username, uuid.uuid(), choices);
			} catch (AnonymousUserAlreadyAnsweredPoll e) {
				error("Your user name has already been used by someone else. If you want to be able to modifiy your answers, you have to be registered.");
				return badRequest(AnswerPoll.getEventViewContent(uuid.uuid()));
			}
		}
		info("Thank you for answering!");
		return redirect(routes.AnswerPoll.viewPoll(uuid));
	}

	private static boolean isUsername(String key) {
		return USERNAME_KEY.equals(key);
	}

}
