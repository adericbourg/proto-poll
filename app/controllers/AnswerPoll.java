package controllers;

import static play.data.Form.form;
import static util.user.message.Messages.error;
import static util.user.message.Messages.info;

import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import models.Poll;
import play.api.templates.Html;
import play.data.DynamicForm;
import play.data.Form;
import play.db.ebean.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import services.PollService;
import services.exception.poll.NoAnswerFoundException;
import services.exception.poll.NoAuthenfiedUserInSessionException;
import services.exception.user.AnonymousUserAlreadyAnsweredPoll;
import util.binders.UuidBinder;
import util.security.SessionUtil;
import util.user.message.Messages;
import views.html.poll.pollResults;

import com.google.common.base.Strings;

import controllers.message.ControllerMessage;
import controllers.model.poll.PollResults;
import controllers.model.poll.PollResultsFactory;

public class AnswerPoll extends Controller {

	private static final String USERNAME_KEY = "username";

	public static class PollComment {
		public Long userId;
		public String username;
		public String comment;
	}

	static final Form<PollComment> FORM_COMMENT = form(PollComment.class);

	@Transactional
	public static Result viewPoll(UuidBinder uuidBinder) {
		Poll poll = PollService.getPoll(uuidBinder.uuid());

		if (poll == null) {
			error(ControllerMessage.POLL_DOES_NOT_EXIST);
			return Application.index();
		}

		return ok(getPollViewContent(poll, FORM_COMMENT));
	}

	@Transactional
	public static Result viewPoll(UuidBinder uuidBinder,
			Form<PollComment> formComment) {
		Poll poll = PollService.getPoll(uuidBinder.uuid());

		if (poll == null) {
			error(ControllerMessage.POLL_DOES_NOT_EXIST);
			return Application.index();
		}

		return ok(getPollViewContent(poll, formComment));
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

	@Transactional
	public static Result answer(UuidBinder uuid) {

		Poll poll = PollService.getPoll(uuid.uuid());
		if (poll == null) {
			error(ControllerMessage.POLL_DOES_NOT_EXIST);
			return Application.index();
		}

		DynamicForm form = form().bindFromRequest();
		Set<Long> choices = new HashSet<Long>();
		for (Entry<String, String> entry : form.data().entrySet()) {
			if (!isUsername(entry.getKey())) {
				choices.add(Long.valueOf(entry.getValue()));
			}
		}
		if (SessionUtil.isAuthenticated()) {
			PollService.answerPollRegistered(uuid.uuid(), choices);
		} else {
			String username = form.data().get(USERNAME_KEY);
			if (Strings.isNullOrEmpty(username)) {
				error(ControllerMessage.POLL_CHOOSE_USER_NAME);
				return badRequest(getPollViewContent(poll, FORM_COMMENT));
			}
			try {
				PollService.answerPollAnonymous(username, uuid.uuid(), choices);
			} catch (AnonymousUserAlreadyAnsweredPoll e) {
				error(ControllerMessage.POLL_USERNAME_ALREADY_TAKEN);
				return badRequest(getPollViewContent(poll, FORM_COMMENT));
			}
		}
		info(ControllerMessage.POLL_ANSWER_SUCCESS);
		return redirect(routes.AnswerPoll.viewPoll(uuid));
	}

	private static boolean isUsername(String key) {
		return USERNAME_KEY.equals(key);
	}

	private static Html getPollViewContent(Poll poll,
			Form<PollComment> formComment) {
		PollResults results = PollResultsFactory.build(poll);
		return pollResults.render(results, poll.comments, formComment);
	}
}
