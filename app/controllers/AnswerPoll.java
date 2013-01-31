package controllers;

import models.Poll;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import services.PollService;
import util.security.SessionUtil;

import com.google.common.base.Strings;

public class AnswerPoll extends Controller {

	public static class PollComment {
		public Long userId;
		public String username;
		public String comment;
	}

	static final Form<PollComment> FORM_COMMENT = form(PollComment.class);

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

	public static Result viewPoll(Long id, Form<PollComment> formComment) {
		Poll poll = PollService.getPoll(id);
		if (poll.isEvent()) {
			return AnswerEvent.view(poll.event.id, formComment);
		} else if (poll.isQuestion()) {
			return AnswerQuestion.view(poll.question.id);
		} else {
			ui.tags.Messages.error("Poll does not exist.");
			return Application.index();
		}
	}

	public static Result comment(Long id) {
		Form<PollComment> submittedForm = FORM_COMMENT.bindFromRequest();

		if (submittedForm.hasErrors()) {
			return viewPoll(id, submittedForm);
		}

		PollComment pollComment = submittedForm.get();

		if (SessionUtil.isAuthenticated()) {
			PollService.postComment(id, pollComment.comment);
		} else {
			if (Strings.isNullOrEmpty(pollComment.username)) {
				submittedForm.reject("username", "Please fill your name");
				return viewPoll(id, submittedForm);
			}
			PollService.postComment(id, pollComment.comment,
					pollComment.username);
		}

		return viewPoll(id);
	}
}
