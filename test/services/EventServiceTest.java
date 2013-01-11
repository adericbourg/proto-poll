package services;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

import models.Event;
import models.EventChoice;
import models.User;

import org.joda.time.LocalDate;
import org.junit.Test;

import util.UserTestUtil;

public class EventServiceTest extends ProtoPollTest {

	@Test
	public void testCreateEventAnonymousUser() {
		// Act.
		final Event event = createEvent();

		// Assert.
		final Event loadedEvent = EventService.getEvent(event.id);
		assertNotNull(loadedEvent);
		assertNull(loadedEvent.userCreator);
		assertEquals(0, loadedEvent.dates.size());
	}

	@Test
	public void testCreateEventRegistered() {
		// Prepare.
		final User user = UserTestUtil.getauthenticatedUser();

		// Act.
		final Event event = createEvent();

		// Assert.
		final Event loadedEvent = EventService.getEvent(event.id);
		assertNotNull(loadedEvent.id);
		assertNotNull(loadedEvent.userCreator);
		assertEquals(user.id, loadedEvent.userCreator.id);
		assertEquals(0, loadedEvent.dates.size());
	}

	@Test
	public void testSetDates() {
		// Prepare.
		final Event event = createEvent();

		final List<EventChoice> dates = new ArrayList<EventChoice>();

		final EventChoice date1 = new EventChoice();
		date1.date = LocalDate.now();
		dates.add(date1);

		final EventChoice date2 = new EventChoice();
		date2.date = LocalDate.now().plusWeeks(1);
		dates.add(date2);

		// Act.
		EventService.saveDates(event.id, dates);

		// Assert.
		final Event loadedEvent = EventService.getEventWithChoices(event.id);
		assertEquals(dates.size(), loadedEvent.dates.size());
	}

	private Event createEvent() {
		final Event event = new Event();
		event.title = "event title";
		EventService.createEvent(event);
		return event;
	}
}
