package controllers;

import static play.data.Form.form;
import static util.user.message.Messages.error;
import static util.user.message.Messages.info;

import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import play.data.DynamicForm;
import play.db.ebean.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import services.QuestionService;
import services.exception.user.AnonymousUserAlreadyAnsweredPoll;
import util.binders.UuidBinder;
import util.security.SessionUtil;

import com.google.common.base.Strings;

import controllers.message.ControllerMessage;

/**
 * Poll answer controller.
 * 
 * @author adericbourg
 * 
 */
public class AnswerQuestion extends Controller {

	private static final String USERNAME_KEY = "username";

	@Transactional
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
				error(ControllerMessage.POLL_CHOOSE_USER_NAME);
				return badRequest(AnswerPoll.getQuestionViewContent(uuid));
			}
			try {
				QuestionService
						.answerQuestionAnonymous(username, uuid, choices);
			} catch (AnonymousUserAlreadyAnsweredPoll e) {
				error(ControllerMessage.POLL_USERNAME_ALREADY_TAKEN);
				return badRequest(AnswerPoll.getQuestionViewContent(uuid));
			}
		}
		info(ControllerMessage.POLL_ANSWER_SUCCESS);
		return redirect(routes.AnswerPoll.viewPoll(uuidBinded));
	}

	private static boolean isUsername(String key) {
		return USERNAME_KEY.equals(key);
	}
}
