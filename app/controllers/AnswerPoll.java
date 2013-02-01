package controllers;

import java.util.UUID;

import models.Event;
import models.EventAnswer;
import models.EventAnswerDetail;
import models.Poll;
import models.PollResults;
import models.Question;
import models.QuestionAnswer;
import models.QuestionAnswerDetail;
import play.api.templates.Html;
import play.data.Form;
import play.mvc.Content;
import play.mvc.Controller;
import play.mvc.Result;
import services.EventService;
import services.PollService;
import util.binders.UuidBinder;
import util.security.SessionUtil;
import views.html.event.eventAnswer;
import views.html.question.questionAnswer;

import com.google.common.base.Strings;

public class AnswerPoll extends Controller {

	public static class PollComment {
		public Long userId;
		public String username;
		public String comment;
	}

	static final Form<PollComment> FORM_COMMENT = form(PollComment.class);

	public static Result viewPoll(UuidBinder id) {
		UUID uuid = id.uuid();
		Poll poll = PollService.getPoll(uuid);
		if (poll.isEvent()) {
			return ok(getEventViewContent(poll.event.id));
		} else if (poll.isQuestion()) {
			return ok(getQuestionViewContent(uuid));
		} else {
			ui.tags.Messages.error("Poll does not exist.");
			return Application.index();
		}
	}

	public static Result viewPoll(UuidBinder id, Form<PollComment> formComment) {
		UUID uuid = id.uuid();
		Poll poll = PollService.getPoll(uuid);
		if (poll.isEvent()) {
			return badRequest(getEventViewContent(poll.event.id, formComment));
		} else if (poll.isQuestion()) {
			return badRequest(getQuestionViewContent(uuid, formComment));
		} else {
			ui.tags.Messages.error("Poll does not exist.");
			return Application.index();
		}
	}

	public static Result comment(UuidBinder id) {
		UUID uuid = id.uuid();
		Form<PollComment> submittedForm = FORM_COMMENT.bindFromRequest();

		if (submittedForm.hasErrors()) {
			return viewPoll(id, submittedForm);
		}

		PollComment pollComment = submittedForm.get();

		if (SessionUtil.isAuthenticated()) {
			PollService.postComment(uuid, pollComment.comment);
		} else {
			if (Strings.isNullOrEmpty(pollComment.username)) {
				submittedForm.reject("username", "Please fill your name");
				return viewPoll(id, submittedForm);
			}
			PollService.postComment(uuid, pollComment.comment,
					pollComment.username);
		}

		return viewPoll(id);
	}

	static Html getEventViewContent(Long id) {
		return getEventViewContent(id, FORM_COMMENT);
	}

	static Html getEventViewContent(Long id, Form<PollComment> formComment) {
		Event event = EventService.getEvent(id);
		return eventAnswer.render(SessionUtil.currentUser(), event,
				getPollResults(event), formComment);
	}

	static Content getQuestionViewContent(UUID uuid) {
		return getQuestionViewContent(uuid, AnswerPoll.FORM_COMMENT);
	}

	static Content getQuestionViewContent(UUID uuid,
			Form<AnswerPoll.PollComment> formComment) {
		Question question = PollService.getQuestion(uuid);
		return questionAnswer.render(SessionUtil.currentUser(), question,
				getPollResults(question), formComment);
	}

	private static PollResults getPollResults(Event event) {
		PollResults results = new PollResults();
		for (EventAnswer ans : event.answers) {
			results.registerUser(ans.user);
			for (EventAnswerDetail detail : ans.details) {
				results.addAnswer(ans.user.username, detail.choice.id);
			}
		}
		return results;
	}

	private static PollResults getPollResults(Question question) {
		PollResults results = new PollResults();
		for (QuestionAnswer ans : question.answers) {
			results.registerUser(ans.user);
			for (QuestionAnswerDetail detail : ans.details) {
				results.addAnswer(ans.user.username, detail.choice.id);
			}
		}
		return results;
	}
}
