package services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import models.Event;
import models.Poll;
import models.Question;

import org.junit.Test;

public class PollServiceTest extends ProtoPollTest {

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
