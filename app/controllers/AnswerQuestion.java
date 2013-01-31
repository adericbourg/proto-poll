package controllers;

import static ui.tags.Messages.error;
import static ui.tags.Messages.info;

import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import models.PollResults;
import models.Question;
import models.QuestionAnswer;
import models.QuestionAnswerDetail;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Content;
import play.mvc.Controller;
import play.mvc.Result;
import services.QuestionService;
import services.exception.AnonymousUserAlreadyAnsweredPoll;
import util.security.SessionUtil;
import views.html.question.questionAnswer;

import com.google.common.base.Strings;

/**
 * Poll answer controller.
 * 
 * @author adericbourg
 * 
 */
public class AnswerQuestion extends Controller {

	private static final String USERNAME_KEY = "data[username]";

	public static Result view(Long id) {
		return ok(getPollViewContent(id));
	}

	public static Result view(Long id, Form<AnswerPoll.PollComment> formComment) {
		return badRequest(getPollViewContent(id, formComment));
	}

	private static Content getPollViewContent(Long id) {
		return getPollViewContent(id, AnswerPoll.FORM_COMMENT);
	}

	private static Content getPollViewContent(Long id,
			Form<AnswerPoll.PollComment> formComment) {
		Question question = QuestionService.getQuestion(id);
		return questionAnswer.render(SessionUtil.currentUser(), question,
				getPollResults(id), formComment);
	}

	private static PollResults getPollResults(Long pollId) {
		Question poll = QuestionService.getQuestion(pollId);

		PollResults results = new PollResults();
		for (QuestionAnswer ans : poll.answers) {
			results.registerUser(ans.user);
			for (QuestionAnswerDetail detail : ans.details) {
				results.addAnswer(ans.user.username, detail.choice.id);
			}
		}
		return results;
	}

	public static Result answer(Long id) {
		DynamicForm form = form().bindFromRequest();
		Set<Long> choices = new HashSet<Long>();
		for (Entry<String, String> entry : form.data().entrySet()) {
			if (!isUsername(entry.getKey())) {
				choices.add(Long.valueOf(entry.getValue()));
			}
		}
		if (SessionUtil.isAuthenticated()) {
			QuestionService.answerQuestionAuthenticated(id, choices);
		} else {
			String username = form.data().get(USERNAME_KEY);
			if (Strings.isNullOrEmpty(username)) {
				error("Choose a user name.");
				return badRequest(getPollViewContent(id));
			}
			try {
				QuestionService.answerQuestionAnonymous(username, id, choices);
			} catch (AnonymousUserAlreadyAnsweredPoll e) {
				error("Your user name has already been used by someone else. If you want to be able to modifiy your answers, you have to be registered.");
				return badRequest(getPollViewContent(id));
			}
		}
		info("Thank you for answering!");
		return view(id);
	}

	private static boolean isUsername(String key) {
		return USERNAME_KEY.equals(key);
	}
}
