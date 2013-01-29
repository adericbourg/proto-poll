package controllers;

import models.Poll;
import play.mvc.Controller;
import play.mvc.Result;
import services.PollService;

public class AnswerPoll extends Controller {

	public static Result viewPoll(Long id) {
		Poll poll = PollService.getPoll(id);
		if (poll.isEvent()) {
			return AnswerEvent.view(poll.event.id);
		} else if (poll.isQuestion()) {
			return AnswerQuestion.view(poll.question.id);
		} else {
			ui.tags.Messages.error("Poll does not exist.");
			return Application.index();
		}
	}
}
