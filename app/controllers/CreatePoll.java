package controllers;

import models.Poll;
import play.mvc.Controller;
import play.mvc.Result;
import services.PollService;
import util.binders.UuidBinder;
import views.html.event.eventCreated;
import views.html.question.questionCreated;

public class CreatePoll extends Controller {

	public static Result confirmCreation(UuidBinder id) {
		Poll poll = PollService.getPoll(id.uuid());
		if (poll.isEvent()) {
			return ok(eventCreated.render(poll.event));
		} else if (poll.isQuestion()) {
			return ok(questionCreated.render(poll.question));
		}
		return notFound();
	}

}
