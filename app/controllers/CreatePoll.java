package controllers;

import static play.data.Form.form;
import static ui.tags.MessagesHelper.invalidForm;
import models.Poll;
import play.data.Form;
import play.db.ebean.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import services.PollService;
import util.binders.UuidBinder;
import views.html.event.eventCreated;
import views.html.event.eventNew;
import views.html.question.questionCreated;
import views.html.question.questionNew;

public class CreatePoll extends Controller {

	private static final Form<Poll> FORM_QUESTION_POLL = form(Poll.class).fill(
			Poll.initQuestion());
	private static final Form<Poll> FORM_EVENT_POLL = form(Poll.class).fill(
			Poll.initEvent());

	@Transactional
	public static Result newQuestion() {
		return ok(questionNew.render(FORM_QUESTION_POLL));
	}

	@Transactional
	public static Result createQuestion() {
		Form<Poll> filledForm = FORM_QUESTION_POLL.bindFromRequest();

		if (filledForm.hasErrors()) {
			// Error handling.
			return invalidForm(questionNew.render(filledForm));
		}

		Poll poll = Poll.initQuestion();
		fillPoll(poll, filledForm);
		poll = PollService.createPoll(poll);
		return redirect(routes.CreateQuestion.setChoices(poll.bindId()));
	}

	private static void fillPoll(Poll poll, Form<Poll> filledForm) {
		Poll filled = filledForm.get();
		poll.title = filled.title;
		poll.description = filled.title;
		poll.singleAnswer = filled.singleAnswer;
	}

	@Transactional
	public static Result newEvent() {
		return ok(eventNew.render(FORM_EVENT_POLL));
	}

	@Transactional
	public static Result createEvent() {
		Form<Poll> filledForm = FORM_EVENT_POLL.bindFromRequest();

		if (filledForm.hasErrors()) {
			// Error handling.
			return invalidForm(eventNew.render(filledForm));
		}

		Poll poll = Poll.initEvent();
		fillPoll(poll, filledForm);
		poll = PollService.createPoll(poll);
		return redirect(routes.CreateEvent.setDates(poll.bindId()));
	}

	@Transactional
	public static Result confirmCreation(UuidBinder uuidBinder) {
		Poll poll = PollService.getPoll(uuidBinder.uuid());
		Result result;
		if (poll == null) {
			result = notFound();
		} else if (poll.isEvent()) {
			return ok(eventCreated.render(poll.event));
		} else if (poll.isQuestion()) {
			return ok(questionCreated.render(poll.question));
		} else {
			result = badRequest();
		}
		return result;
	}
}
