package controllers;

import static play.data.Form.form;
import static ui.tags.MessagesHelper.invalidForm;
import static util.user.message.Messages.info;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import models.Event;
import models.EventChoice;

import org.joda.time.LocalDate;

import play.data.DynamicForm;
import play.data.Form;
import play.db.ebean.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import scala.Option;
import services.EventService;
import services.PollService;
import util.binders.UuidBinder;
import util.security.SessionUtil;
import views.html.event.eventAddDates;
import views.html.event.eventNew;

import com.google.common.base.Strings;

import controllers.message.ControllerMessage;

public class CreateEvent extends Controller {
	private static final Form<Event> FORM_EVENT = form(Event.class).fill(
			new Event());

	@Transactional
	public static Result newEvent() {
		return ok(eventNew.render(FORM_EVENT));
	}

	@Transactional
	public static Result createEvent() {
		Form<Event> filledForm = FORM_EVENT.bindFromRequest();

		if (filledForm.hasErrors()) {
			// Error handling.
			return invalidForm(eventNew.render(FORM_EVENT));
		}

		Event event = filledForm.get();
		event = EventService.createEvent(event);
		return redirect(routes.CreateEvent.setDates(event.bindId()));
	}

	@Transactional
	public static Result setDates(UuidBinder uuid) {
		Event event = PollService.getEvent(uuid.uuid());
		Option<String> locale;
		if (SessionUtil.preferredLang().isDefined()) {
			locale = Option.apply(SessionUtil.preferredLang().get().language());
		} else {
			locale = Option.empty();
		}
		return ok(eventAddDates.render(event, FORM_EVENT, locale));
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
		EventService.saveDates(uuid.uuid(), dates);
		info(ControllerMessage.EVENT_SUCCESSFULLY_CREATED);
		return redirect(routes.CreatePoll.confirmCreation(uuid));
	}
}
