package services;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import models.Event;
import models.EventChoice;
import models.Poll;
import models.User;
import models.reference.PollStatus;

import org.joda.time.LocalDate;
import org.junit.Test;

import services.exception.poll.NoChoiceException;
import util.UserTestUtil;
import util.security.CurrentUser;

public class EventServiceTest extends ProtoPollTest {

	private static final int CHOICE_NUMBER = 3;

	@Test
	public void testInstanciation() throws Exception {
		for (Constructor<?> constructor : EventService.class
				.getDeclaredConstructors()) {
			constructor.setAccessible(true);
			try {
				constructor.newInstance();
			} catch (InvocationTargetException e) {
				assertTrue(e.getCause() instanceof AssertionError);
			}
		}
	}

	@Test
	public void testCreateEventAnonymousUser() {
		// Act.
		final Event event = createEvent();

		// Assert.
		final Event loadedEvent = PollService.getEvent(event.uuid());
		assertNotNull(loadedEvent);
		assertNotNull(event.poll);
		assertNull(loadedEvent.poll.userCreator);
		assertEquals(0, loadedEvent.dates.size());
	}

	@Test
	public void testCreateEventRegistered() {
		// Prepare.
		final User user = UserTestUtil.getAuthenticatedUser();
		assertNotNull(CurrentUser.currentUser());

		// Act.
		final Event event = createEvent();

		// Assert.
		final Event loadedEvent = PollService.getEvent(event.uuid());
		assertNotNull("id", loadedEvent.id);
		assertNotNull("poll", loadedEvent.poll);
		assertNotNull("user", loadedEvent.poll.userCreator);
		assertEquals("user id", user.id, loadedEvent.poll.userCreator.id);
		assertEquals("size", 0, loadedEvent.dates.size());
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
		EventService.saveDates(event.uuid(), dates);

		// Assert.
		final Event loadedEvent = PollService.getEvent(event.uuid());
		assertEquals(dates.size(), loadedEvent.dates.size());
	}

	@Test(expected = NoChoiceException.class)
	public void testNoDateSelectedNull() {
		// Prepare.
		final Event event = createEvent();

		// Act.
		EventService.saveDates(event.uuid(), null);
	}

	@Test(expected = NoChoiceException.class)
	public void testNoDateSelectedEmpty() {
		// Prepare.
		final Event event = createEvent();

		// Act.
		EventService.saveDates(event.uuid(), new ArrayList<EventChoice>());
	}

	@Test
	public void testStatusCreated() {
		// Prepare / Act.
		Event event = createEvent();

		// Assert.
		assertEquals(PollStatus.DRAFT, event.poll.status);
	}

	@Test
	public void testStatusCreatedWithChoices() {
		// Prepare.
		Event event = createEvent();

		// Act.
		event = addDates(event);

		// Assert.
		assertEquals(PollStatus.COMPLETE, event.poll.status);
	}

	private Event createEvent() {
		Poll poll = Poll.initEvent();
		poll.title = "event title";
		PollService.createPoll(poll);
		return PollService.getPoll(poll.uuid).event;
	}

	private static Event addDates(Event event) {
		List<EventChoice> choices = new ArrayList<EventChoice>();
		EventChoice choice;
		for (int i = 0; i < CHOICE_NUMBER; i++) {
			choice = new EventChoice();
			choice.date = LocalDate.now().plusDays(i);
			choice.event = event;
			choices.add(choice);
		}
		EventService.saveDates(event.uuid(), choices);
		return PollService.getEvent(event.uuid());
	}
}
