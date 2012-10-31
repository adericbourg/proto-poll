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
import services.PollService;
import views.html.answerPoll;

/**
 * Poll answer controller.
 * 
 * @author adericbourg
 * 
 */
public class AnswerPoll extends Controller {

	private static final String USERNAME_KEY = "data[username]";

	public static Result view(Long id) {
		Poll poll = PollService.getPoll(id);
		List<Choice> choices = PollService.getChoicesByPoll(id);

		return ok(answerPoll.render(poll, choices, getPollResults(id)));
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
		String username = form.data().get(USERNAME_KEY);
		Set<Long> choices = new HashSet<Long>();
		for (Entry<String, String> entry : form.data().entrySet()) {
			if (!isUsername(entry.getKey())) {
				choices.add(Long.valueOf(entry.getValue()));
			}
		}
		PollService.answerPoll(username, id, choices);
		return view(id);
	}

	private static boolean isUsername(String key) {
		return USERNAME_KEY.equals(key);
	}
}
