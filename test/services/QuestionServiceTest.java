package services;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

import models.Question;
import models.QuestionChoice;
import models.User;

import org.junit.Test;

import util.UserTestUtil;
import util.security.SessionUtil;

public class QuestionServiceTest extends ProtoPollTest {

	@Test
	public void testCreateQuestionAnonymousUser() {
		// Act.
		final Question question = createQuestion();

		// Assert.
		final Question loadedQuestion = QuestionService
				.getQuestion(question.id);
		assertNotNull(loadedQuestion);
		assertNotNull(question.poll);
		assertNull(loadedQuestion.poll.userCreator);
		assertEquals(0, loadedQuestion.choices.size());
	}

	@Test
	public void testCreateQuestionRegistered() {
		// Prepare.
		final User user = UserTestUtil.getAuthenticatedUser();
		assertNotNull(SessionUtil.currentUser());

		// Act.
		final Question question = createQuestion();

		// Assert.
		final Question loadedQuestion = QuestionService
				.getQuestion(question.id);
		assertNotNull("id", loadedQuestion.id);
		assertNotNull("poll", loadedQuestion.poll);
		assertNotNull("user", loadedQuestion.poll.userCreator);
		assertEquals("user id", user.id, loadedQuestion.poll.userCreator.id);
		assertEquals("size", 0, loadedQuestion.choices.size());
	}

	@Test
	public void testSetChoices() {
		// Prepare.
		final Question question = createQuestion();

		final List<QuestionChoice> choices = new ArrayList<QuestionChoice>();

		final QuestionChoice choice1 = new QuestionChoice();
		choice1.label = "Choice 1";
		choice1.sortOrder = 2;
		choices.add(choice1);

		final QuestionChoice choice2 = new QuestionChoice();
		choice2.label = "Choice 2";
		choice2.sortOrder = 1;
		choices.add(choice2);

		// Act.
		QuestionService.saveChoices(question.id, choices);

		// Assert.
		final Question loadedQuestion = QuestionService
				.getQuestion(question.id);
		assertEquals(choices.size(), loadedQuestion.choices.size());
		assertEquals(choices.get(0).label, loadedQuestion.choices.get(1).label);
		assertEquals(choices.get(1).label, loadedQuestion.choices.get(0).label);
	}

	private Question createQuestion() {
		final Question question = new Question();
		question.title = "question title";
		QuestionService.createQuestion(question);
		return question;
	}
}
