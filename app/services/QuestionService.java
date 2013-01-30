package services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Question;
import models.QuestionAnswer;
import models.QuestionAnswerDetail;
import models.QuestionChoice;
import models.User;
import play.db.ebean.Model.Finder;
import services.exception.AnonymousUserAlreadyAnsweredPoll;
import util.security.SessionUtil;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.ExpressionList;

/**
 * Question management service.
 * 
 * @author adericbourg
 * 
 */
public class QuestionService {

	private static final Finder<Long, Question> QUESTION_FINDER = new Finder<Long, Question>(
			Long.class, Question.class);
	private static final Finder<Long, QuestionChoice> CHOICE_FINDER = new Finder<Long, QuestionChoice>(
			Long.class, QuestionChoice.class);

	private QuestionService() {
		// No instance.
		throw new AssertionError();
	}

	public static Long createQuestion(Question question) {
		question.save();
		PollService.initPoll(question);
		return question.id;
	}

	public static void saveChoices(Long questionId, List<QuestionChoice> choices) {
		Question question = getQuestion(questionId);
		question.choices = choices;
		question.save();
	}

	public static Question getQuestion(Long id) {
		return QUESTION_FINDER.fetch("poll.userCreator").fetch("poll.question")
				.where().eq("id", id).findUnique();
	}

	public static QuestionChoice getChoice(Long id) {
		return CHOICE_FINDER.byId(id);
	}

	public static List<Question> questions() {
		return Ebean.find(Question.class).findList();
	}

	public static void answerQuestionAuthenticated(Long questionId,
			Collection<Long> choiceIds) {
		answerQuestion(SessionUtil.currentUser(), questionId, choiceIds);
	}

	public static void answerQuestionAnonymous(String username,
			Long questionId, Collection<Long> choiceIds) {
		Question question = getQuestion(questionId);

		// Check if a user with same name has already answered.
		for (QuestionAnswer answer : question.answers) {
			if (answer.user.username.equals(username)) {
				throw new AnonymousUserAlreadyAnsweredPoll();
			}
		}

		// Create new unregistered user with same login.
		User user = UserService.registerAnonymousUser(username);

		answerQuestion(user, questionId, choiceIds);
	}

	private static void answerQuestion(User user, Long questionId,
			Collection<Long> choiceIds) {
		if (user == null) {
			throw new RuntimeException("User cannot be null");
		}

		Question question = getQuestion(questionId);
		QuestionAnswer answer = getOrCreateAnswer(user, question);

		// Clear all previous answers.
		for (QuestionAnswerDetail detail : answer.details) {
			detail.delete();
		}

		// Map choices.
		Map<Long, QuestionChoice> choices = new HashMap<Long, QuestionChoice>();
		for (QuestionChoice choice : question.choices) {
			choices.put(choice.id, choice);
		}

		// Save current choices.
		QuestionChoice choice;
		QuestionAnswerDetail detail;
		List<QuestionAnswerDetail> details = new ArrayList<QuestionAnswerDetail>();
		for (Long choiceId : choiceIds) {
			choice = choices.get(choiceId);
			detail = new QuestionAnswerDetail();
			detail.choice = choice;
			details.add(detail);
		}
		answer.details = details;
		answer.save();
	}

	private static QuestionAnswer getOrCreateAnswer(User user, Question question) {
		ExpressionList<QuestionAnswer> el = Ebean.find(QuestionAnswer.class)
				.fetch("details").where().eq("user.id", user.id)
				.eq("question.id", question.id);
		if (el.findRowCount() == 0) {
			QuestionAnswer answer = new QuestionAnswer();
			answer.user = user;
			answer.question = question;
			return answer;
		}
		return el.findUnique();
	}
}
