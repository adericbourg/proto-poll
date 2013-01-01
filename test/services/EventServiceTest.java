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
		Event event = testCreateEvent();

		// Assert.
		assertNotNull(event.id);
		assertNull(event.userCreator);
	}

	@Test
	public void testCreateEventRegistered() {
		// Prepare.
		User user = UserTestUtil.getauthenticatedUser();

		// Act.
		Event event = testCreateEvent();

		// Assert.
		assertNotNull(event.id);
		assertNotNull(event.userCreator);
		assertEquals(user.id, event.userCreator.id);
	}

	private Event testCreateEvent() {
		Event event = new Event();
		event.label = "event label";
		EventService.createEvent(event);
		return event;
	}
}
