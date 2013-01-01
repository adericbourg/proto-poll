package services;

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
}
