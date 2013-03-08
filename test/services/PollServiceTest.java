package services;

import static org.junit.Assert.assertNull;

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
