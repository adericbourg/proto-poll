package controllers;

import java.util.List;

import models.Poll;
import play.mvc.Controller;
import play.mvc.Result;
import services.PollService;
import views.html.listPolls;
import views.html.message;

public class ViewPolls extends Controller {
	public static Result polls() {
		List<Poll> polls = PollService.polls();
		if (polls.isEmpty()) {
			return ok(message.render("Polls", "No poll available yet."));
		}
		return ok(listPolls.render(polls));
	}
}
