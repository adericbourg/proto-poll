package services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import models.Event;
import models.EventChoice;
import models.Poll;
import models.Question;

import org.joda.time.LocalDate;
import org.junit.Test;

import services.exception.poll.NoAuthenticatedUserInSessionException;
import util.UserTestUtil;

public class PollServiceTest extends ProtoPollTest {

	@Test
	public void testInstanciation() throws Exception {
		for (Constructor<?> constructor : PollService.class
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
	public void testGetPollNonExistent() {
		// Prepare.
		UUID uuid = UUID.randomUUID();

		// Act.
		Poll poll = PollService.getPoll(uuid);
		// Assert.
		assertNull(poll);
	}

	@Test
	public void testGetQuestionWherePollIsEvent() {
		// Prepare.
		Event event = createEvent();

		// Act.
		Question question = PollService.getQuestion(event.uuid());

		// Assert.
		assertNull(question);
	}

	@Test
	public void testGetQuestionNonExistent() {
		// Prepare.
		UUID uuid = UUID.randomUUID();

		// Act.
		Question question = PollService.getQuestion(uuid);
		// Assert.
		assertNull(question);
	}

	@Test
	public void testGetEventPollIsQuestion() {
		// Prepare.
		Question question = createQuestion();

		// Act.
		Event event = PollService.getEvent(question.uuid());

		// Assert.
		assertNull(event);
	}

	@Test
	public void testGetEventNonExistent() {
		// Prepare.
		UUID uuid = UUID.randomUUID();

		// Act.
		Event event = PollService.getEvent(uuid);
		// Assert.
		assertNull(event);
	}

	@Test
	public void testPollsNoPoll() {
		// No prepare.

		// Act.
		List<Poll> polls = PollService.polls();

		// Assert.
		assertNotNull(polls);
		assertTrue(polls.isEmpty());
	}

	@Test
	public void testPollsWithPolls() {
		// Prepare.
		Map<UUID, Poll> index = new HashMap<UUID, Poll>();

		Event event = createEvent();
		index.put(event.uuid(), event.poll);

		Question question = createQuestion();
		index.put(question.uuid(), question.poll);

		// Act.
		List<Poll> polls = PollService.polls();
		assertFalse(polls.isEmpty());
		for (Poll poll : polls) {
			assertTrue(index.containsKey(poll.uuid));
			assertEquals(index.get(poll.uuid).isEvent(), poll.isEvent());
			assertEquals(index.get(poll.uuid).isQuestion(), poll.isQuestion());
		}
	}

	@Test
	public void testListUserPollWithChoice() {
		// Prepare.
		UserTestUtil.getAuthenticatedUser();

		Event event = createEvent();
		EventChoice choice = new EventChoice();
		choice.date = LocalDate.now();
		EventService.saveDates(event.uuid(), Arrays.asList(choice));

		// Act.
		List<Poll> polls = PollService.listUserPolls();

		// Assert.
		assertEquals(1, polls.size());
		assertEquals(event.uuid(), polls.get(0).uuid);
	}

	@Test
	public void testListUserPollNoChoice() {
		// Prepare.
		UserTestUtil.getAuthenticatedUser();
		createEvent();

		// Act.
		List<Poll> polls = PollService.listUserPolls();

		// Assert.
		assertTrue(polls.isEmpty());
	}

	@Test(expected = NoAuthenticatedUserInSessionException.class)
	public void testListUserPollsNotAuthenticated() {
		PollService.listUserPolls();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateAlreadySavedPoll() {
		// Prepare.
		Poll poll = createQuestion().poll;

		// Act.
		PollService.createPoll(poll);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreatePollNotEventNotQuestion() throws Exception {
		// Prepare.
		Poll poll = Poll.class.newInstance();

		// Act.
		PollService.createPoll(poll);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreatePollBothEventAndQuestion() throws Exception {
		// Prepare.
		Poll poll = Poll.class.getDeclaredConstructor(Question.class,
				Event.class).newInstance(new Question(), new Event());

		// Act.
		PollService.createPoll(poll);
	}

	private Event createEvent() {
		Poll poll = Poll.initEvent();
		poll.title = "event title";
		PollService.createPoll(poll);
		return PollService.getPoll(poll.uuid).event;
	}

	private static Question createQuestion() {
		Poll poll = Poll.initQuestion();
		poll.title = "question title";
		PollService.createPoll(poll);
		return PollService.getPoll(poll.uuid).question;
	}
}
