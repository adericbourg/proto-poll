package services;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import models.Event;
import models.User;

import org.junit.Test;

import util.UserTestUtil;

public class EventServiceTest extends ProtoPollTest {

	@Test
	public void testCreateEventAnonymousUser() {
		// Act.
		Event event = createEvent();

		// Assert.
		Event loadedEvent = EventService.getEvent(event.id);
		assertNotNull(loadedEvent);
		assertNull(loadedEvent.userCreator);
		assertEquals(0, loadedEvent.dates.size());
	}

	@Test
	public void testCreateEventRegistered() {
		// Prepare.
		User user = UserTestUtil.getauthenticatedUser();

		// Act.
		Event event = createEvent();

		// Assert.
		Event loadedEvent = EventService.getEvent(event.id);
		assertNotNull(loadedEvent.id);
		assertNotNull(loadedEvent.userCreator);
		assertEquals(user.id, loadedEvent.userCreator.id);
		assertEquals(0, loadedEvent.dates.size());
	}

	// FIXME org.springframework.beans.InvalidPropertyException: Invalid
	// property 'id' of bean class [models.Event]: No property 'id' found
	// @Test
	// public void testSetDates() {
	// // Prepare.
	// Event event = createEvent();
	//
	// List<EventChoice> dates = new ArrayList<EventChoice>();
	//
	// EventChoice date1 = new EventChoice();
	// date1.date = LocalDate.now();
	// dates.add(date1);
	//
	// EventChoice date2 = new EventChoice();
	// date2.date = LocalDate.now().plusWeeks(1);
	// dates.add(date2);
	//
	// // Act.
	// EventService.saveDates(event.id, dates);
	//
	// // Assert.
	// Event loadedEvent = EventService.getEventWithChoices(event.id);
	// assertEquals(dates.size(), loadedEvent.dates.size());
	// }

	private Event createEvent() {
		Event event = new Event();
		event.title = "event title";
		EventService.createEvent(event);
		return event;
	}
}
