package controllers;
import static play.data.Form.form;
import static ui.tags.Messages.error;
import static ui.tags.Messages.info;

import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import play.data.DynamicForm;
import play.mvc.Controller;
import play.mvc.Result;
import services.QuestionService;
import services.exception.AnonymousUserAlreadyAnsweredPoll;
import util.binders.UuidBinder;
import util.security.SessionUtil;

import com.google.common.base.Strings;

/**
 * Poll answer controller.
 * 
 * @author adericbourg
 * 
 */
public class AnswerQuestion extends Controller {

	private static final String USERNAME_KEY = "data[username]";

	public static Result answer(UuidBinder uuidBinded) {
		UUID uuid = uuidBinded.uuid();
		DynamicForm form = form().bindFromRequest();
		Set<Long> choices = new HashSet<Long>();
		for (Entry<String, String> entry : form.data().entrySet()) {
			if (!isUsername(entry.getKey())) {
				choices.add(Long.valueOf(entry.getValue()));
			}
		}
		if (SessionUtil.isAuthenticated()) {
			QuestionService.answerQuestionAuthenticated(uuid, choices);
		} else {
			String username = form.data().get(USERNAME_KEY);
			if (Strings.isNullOrEmpty(username)) {
				error("Choose a user name.");
				return badRequest(AnswerPoll.getQuestionViewContent(uuid));
			}
			try {
				QuestionService
						.answerQuestionAnonymous(username, uuid, choices);
			} catch (AnonymousUserAlreadyAnsweredPoll e) {
				error("Your user name has already been used by someone else. If you want to be able to modifiy your answers, you have to be registered.");
				return badRequest(AnswerPoll.getQuestionViewContent(uuid));
			}
		}
		info("Thank you for answering!");
		return redirect(routes.AnswerPoll.viewPoll(uuidBinded));
	}

	private static boolean isUsername(String key) {
		return USERNAME_KEY.equals(key);
	}
}
