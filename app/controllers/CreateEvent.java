package controllers;

import static play.data.Form.form;
import static util.user.message.Messages.error;
import static util.user.message.Messages.info;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import models.Event;
import models.EventChoice;
import models.Poll;

import org.joda.time.LocalDate;

import play.api.templates.Html;
import play.data.DynamicForm;
import play.data.Form;
import play.db.ebean.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import scala.Option;
import services.EventService;
import services.PollService;
import services.exception.poll.NoChoiceException;
import util.binders.UuidBinder;
import util.security.CurrentUser;
import views.html.event.eventAddDates;

import com.google.common.base.Strings;

import controllers.message.ControllerMessage;

public class CreateEvent extends Controller {
	private static final Form<Event> FORM_EVENT = form(Event.class).fill(
			new Event());

	@Transactional
	public static Result setDates(UuidBinder uuid) {
		return ok(prepareDatesData(uuid));
	}

	@Transactional
	public static Result saveDates(UuidBinder uuid) {
		DynamicForm dynamicForm = form().bindFromRequest();
		EventChoice date;
		List<EventChoice> dates = new ArrayList<EventChoice>();
		String value;
		for (Entry<String, String> entry : dynamicForm.data().entrySet()) {
			value = entry.getValue();
			if (!Strings.isNullOrEmpty(value)) {
				date = new EventChoice();
				date.date = LocalDate.parse(entry.getValue());
				dates.add(date);
			}
		}
		try {
			EventService.saveDates(uuid.uuid(), dates);
			info(ControllerMessage.EVENT_SUCCESSFULLY_CREATED);
			return redirect(routes.CreatePoll.confirmCreation(uuid));
		} catch (NoChoiceException e) {
			error(ControllerMessage.NO_CHOICE_ON_POLL);
			return badRequest(prepareDatesData(uuid));
		}
	}

	private static Html prepareDatesData(UuidBinder uuid) {
		Poll poll = PollService.getPoll(uuid.uuid());
		Option<String> locale;
		if (CurrentUser.preferredLang().isDefined()) {
			locale = Option.apply(CurrentUser.preferredLang().get().language());
		} else {
			locale = Option.empty();
		}
		return eventAddDates.render(poll, FORM_EVENT, locale);
	}
}
