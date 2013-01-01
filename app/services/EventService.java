package services;

import java.util.ArrayList;
import java.util.Collection;

import models.Event;
import models.EventChoice;
import play.db.ebean.Model.Finder;
import util.security.SessionUtil;

public class EventService {

	private static final Finder<Long, Event> EVENT_FINDER = new Finder<Long, Event>(
			Long.class, Event.class);
	private static final Finder<Long, EventChoice> EVENT_CHOICE_FINDER = new Finder<Long, EventChoice>(
			Long.class, EventChoice.class);

	public static Long createEvent(Event event) {
		event.userCreator = SessionUtil.currentUser();
		event.save();
		return event.id;
	}

	public static Event getEvent(Long id) {
		return EVENT_FINDER.byId(id);
	}

	public static void saveDates(Long eventId, Collection<EventChoice> dates) {
		Event event = getEvent(eventId);
		event.dates = new ArrayList<EventChoice>(dates);
	}
}
