package services;

import java.util.ArrayList;
import java.util.Collection;

import models.Event;
import models.EventChoice;
import play.db.ebean.Model.Finder;
import util.security.SessionUtil;

import com.avaje.ebean.Ebean;

public class EventService {

	private static final Finder<Long, Event> EVENT_FINDER = new Finder<Long, Event>(
			Long.class, Event.class);

	public static Long createEvent(Event event) {
		event.userCreator = SessionUtil.currentUser();
		event.save();
		return event.id;
	}

	public static Event getEvent(Long id) {
		return EVENT_FINDER.byId(id);
	}

	public static Event getEventWithChoices(Long id) {
		return Ebean.find(Event.class).fetch("dates").where().eq("id", id)
				.findUnique();
	}

	public static void saveDates(Long eventId, Collection<EventChoice> dates) {
		Event event = getEvent(eventId);
		event.dates = new ArrayList<EventChoice>(dates);
		event.save();
	}
}
