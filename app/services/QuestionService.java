package services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
		List<QuestionChoice> deduplicatedChoices = deduplicateChoices(choices);
		Question question = getQuestion(questionId);
		question.choices = deduplicatedChoices;
		question.save();
	}

	private static List<QuestionChoice> deduplicateChoices(
			List<QuestionChoice> choices) {
		Set<String> keys = new HashSet<String>();
		List<QuestionChoice> deduplicated = new ArrayList<QuestionChoice>();
		for (QuestionChoice questionChoice : choices) {
			if (!keys.contains(questionChoice.label)) {
				deduplicated.add(questionChoice);
				keys.add(questionChoice.label);
			}
		}
		return deduplicated;
	}

	public static Question getQuestion(Long questionId) {
		return QUESTION_FINDER.fetch("poll.userCreator").fetch("poll.question")
				.where().eq("id", questionId).findUnique();
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