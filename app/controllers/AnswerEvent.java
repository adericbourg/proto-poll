package controllers;

import play.mvc.Controller;
import play.mvc.Result;
import services.EventService;
import util.security.SessionUtil;
import views.html.eventAnswer;

public class AnswerEvent extends Controller {

	public static Result view(Long id) {
		// TODO Gather results.
		return ok(eventAnswer.render(SessionUtil.currentUser(),
				EventService.getEvent(id)));
	}

	public static Result answer(Long id) {
		// TODO
		return ok();
	}

}
