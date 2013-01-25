package controllers;

import models.Event;
import models.EventAnswer;
import models.EventAnswerDetail;
import models.PollResults;
import play.mvc.Controller;
import play.mvc.Result;
import services.EventService;
import util.security.SessionUtil;
import views.html.eventAnswer;

public class AnswerEvent extends Controller {

	public static Result view(Long id) {
		// TODO Gather results.
		return ok(eventAnswer.render(SessionUtil.currentUser(),
				EventService.getEvent(id), getPollResults(id)));
	}

	public static Result answer(Long id) {
		// TODO
		return ok();
	}

	private static PollResults getPollResults(Long pollId) {
		Event event = EventService.getEvent(pollId);

		PollResults results = new PollResults();
		for (EventAnswer ans : event.answers) {
			results.registerUser(ans.user.username);
			for (EventAnswerDetail detail : ans.details) {
				results.addAnswer(ans.user.username, detail.choice.id);
			}
		}
		return results;
	}

}
