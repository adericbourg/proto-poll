package controllers;

import static play.data.Form.form;
import static util.user.message.Messages.error;

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
import play.db.ebean.Transactional;
import play.mvc.Content;
import play.mvc.Controller;
import play.mvc.Result;
import services.PollService;
import services.exception.NoAnswerFoundException;
import services.exception.NoAuthenfiedUserInSessionException;
import util.binders.UuidBinder;
import util.security.SessionUtil;
import util.user.message.Messages;
import views.html.event.eventAnswer;
import views.html.question.questionAnswer;

import com.google.common.base.Strings;

import controllers.message.ControllerMessage;

public class AnswerPoll extends Controller {

	public static class PollComment {
		public Long userId;
		public String username;
		public String comment;
	}

	static final Form<PollComment> FORM_COMMENT = form(PollComment.class);

	@Transactional
	public static Result viewPoll(UuidBinder uuidBinder) {
		UUID uuid = uuidBinder.uuid();
		Poll poll = PollService.getPoll(uuid);
		if (poll.isEvent()) {
			return ok(getEventViewContent(uuid));
		} else if (poll.isQuestion()) {
			return ok(getQuestionViewContent(uuid));
		} else {
			error(ControllerMessage.POLL_DOES_NOT_EXIST);
			return Application.index();
		}
	}

	@Transactional
	public static Result viewPoll(UuidBinder uuidBinder,
			Form<PollComment> formComment) {
		UUID uuid = uuidBinder.uuid();
		Poll poll = PollService.getPoll(uuid);
		if (poll.isEvent()) {
			return badRequest(getEventViewContent(uuid, formComment));
		} else if (poll.isQuestion()) {
			return badRequest(getQuestionViewContent(uuid, formComment));
		} else {
			error(ControllerMessage.POLL_DOES_NOT_EXIST);
			return Application.index();
		}
	}

	@Transactional
	public static Result comment(UuidBinder uuidBinder) {
		UUID uuid = uuidBinder.uuid();
		Form<PollComment> submittedForm = FORM_COMMENT.bindFromRequest();

		if (submittedForm.hasErrors()) {
			return viewPoll(uuidBinder, submittedForm);
		}

		PollComment pollComment = submittedForm.get();

		if (SessionUtil.isAuthenticated()) {
			PollService.postCommentRegistered(uuid, pollComment.comment);
		} else {
			if (Strings.isNullOrEmpty(pollComment.username)) {
				submittedForm.reject("username", "Please fill your name");
				return viewPoll(uuidBinder, submittedForm);
			}
			PollService.postCommentAnonymous(uuid, pollComment.comment,
					pollComment.username);
		}

		return redirect(routes.AnswerPoll.viewPoll(uuidBinder));
	}

	@Transactional
	public static Result removeUserAnswer(UuidBinder uuidBinder) {
		try {
			PollService.removeCurrentUserAnswer(uuidBinder.uuid());
			Messages.info(ControllerMessage.POLL_ANSWER_DELETED);
		} catch (NoAuthenfiedUserInSessionException e) {
			Messages.error(ControllerMessage.ACCESS_FORBIDDEN_NOT_AUTHENTIFIED);
		} catch (NoAnswerFoundException e) {
			Messages.error(ControllerMessage.POLL_ANSWER_DOES_NOT_EXIST);
		}
		return redirect(routes.AnswerPoll.viewPoll(uuidBinder));
	}

	static Html getEventViewContent(UUID uuid) {
		return getEventViewContent(uuid, FORM_COMMENT);
	}

	static Html getEventViewContent(UUID uuid, Form<PollComment> formComment) {
		Poll poll = PollService.getPoll(uuid);
		return eventAnswer.render(SessionUtil.currentUser(), poll,
				getPollResults(poll.event), formComment);
	}

	static Content getQuestionViewContent(UUID uuid) {
		return getQuestionViewContent(uuid, AnswerPoll.FORM_COMMENT);
	}

	static Content getQuestionViewContent(UUID uuid,
			Form<AnswerPoll.PollComment> formComment) {
		Poll poll = PollService.getPoll(uuid);
		return questionAnswer.render(SessionUtil.currentUser(), poll,
				getPollResults(poll.question), formComment);
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
