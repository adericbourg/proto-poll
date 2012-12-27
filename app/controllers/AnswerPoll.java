package controllers;

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
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import services.PollService;
import ui.tags.Messages;
import util.security.SessionUtil;
import views.html.answerPoll;

/**
 * Poll answer controller.
 * 
 * @author adericbourg
 * 
 */
@Security.Authenticated(Secured.class)
public class AnswerPoll extends Controller {

	private static final String USERNAME_KEY = "data[username]";

	public static Result view(Long id) {
		Poll poll = PollService.getPoll(id);
		List<Choice> choices = PollService.getChoicesByPoll(id);

		return ok(answerPoll.render(SessionUtil.currentUser(), poll, choices,
				getPollResults(id)));
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

	public static Result answer(Long id) {
		DynamicForm form = form().bindFromRequest();
		Set<Long> choices = new HashSet<Long>();
		for (Entry<String, String> entry : form.data().entrySet()) {
			if (!isUsername(entry.getKey())) {
				choices.add(Long.valueOf(entry.getValue()));
			}
		}
		PollService.answerPoll(SessionUtil.currentUser(), id, choices);
		Messages.pushInfo("Thank you for answering!");
		return view(id);
	}

	private static boolean isUsername(String key) {
		return USERNAME_KEY.equals(key);
	}
}
