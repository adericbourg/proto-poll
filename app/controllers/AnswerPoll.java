package controllers;

import static ui.tags.Messages.error;
import static ui.tags.Messages.info;

import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import models.Answer;
import models.AnswerDetail;
import models.Choice;
import models.Poll;
import models.PollResults;
import play.data.DynamicForm;
import play.mvc.Content;
import play.mvc.Controller;
import play.mvc.Result;
import services.PollService;
import services.exception.AnonymousUserAlreadyAnsweredPoll;
import util.security.SessionUtil;
import views.html.answerPoll;

import com.google.common.base.Strings;

/**
 * Poll answer controller.
 * 
 * @author adericbourg
 * 
 */
public class AnswerPoll extends Controller {

	private static final String USERNAME_KEY = "data[username]";

	public static Result view(Long id) {
		return ok(getPollViewContent(id));
	}

	private static PollResults getPollResults(Long pollId) {
		Poll poll = PollService.getPollWithAnswers(pollId);

		PollResults results = new PollResults();
		for (Answer ans : poll.answers) {
			results.registerUser(ans.user.username);
			for (AnswerDetail detail : ans.details) {
				results.addAnswer(ans.user.username, detail.choice.id);
			}
		}
		return results;
	}

	private static Content getPollViewContent(Long id) {
		Poll poll = PollService.getPoll(id);
		List<Choice> choices = PollService.getChoicesByPoll(id);

		return answerPoll.render(SessionUtil.currentUser(), poll, choices,
				getPollResults(id));
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
			PollService.answerPoll(id, choices);
		} else {
			String username = form.data().get(USERNAME_KEY);
			if (Strings.isNullOrEmpty(username)) {
				error("Choose a user name.");
				return badRequest(getPollViewContent(id));
			}
			try {
				PollService.answerPoll(username, id, choices);
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
