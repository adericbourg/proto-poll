package services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Answer;
import models.AnswerDetail;
import models.Choice;
import models.Question;
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
	private static final Finder<Long, Choice> CHOICE_FINDER = new Finder<Long, Choice>(
			Long.class, Choice.class);

	private QuestionService() {
		// No instance.
		throw new AssertionError();
	}

	public static Long createQuestion(Question question) {
		question.userCreator = SessionUtil.currentUser();
		question.save();
		return question.id;
	}

	public static void saveChoices(Long questionId, List<Choice> choices) {
		Question question = getQuestion(questionId);
		question.choices = choices;
		question.save();
	}

	public static Question getQuestion(Long id) {
		return QUESTION_FINDER.byId(id);
	}

	public static Choice getChoice(Long id) {
		return CHOICE_FINDER.byId(id);
	}

	public static List<Question> questions() {
		return Ebean.find(Question.class).findList();
	}

	public static List<Choice> getChoicesByQuestion(Long questionId) {
		List<Choice> choices = Ebean.find(Choice.class).where()
				.eq("question.id", questionId).findList();
		Ebean.sort(choices, "sortOrder");
		return choices;
	}

	public static void answerQuestion(Long questionId,
			Collection<Long> choiceIds) {
		answerQuestion(SessionUtil.currentUser(), questionId, choiceIds);
	}

	public static void answerQuestion(String username, Long questionId,
			Collection<Long> choiceIds) {
		Question question = getQuestionWithAnswers(questionId);

		// Check if a user with same name has already answered.
		for (Answer answer : question.answers) {
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

		Question question = getQuestionWithAnswers(questionId);
		Answer answer = getOrCreateAnswer(user, question);

		// Clear all previous answers.
		for (AnswerDetail detail : answer.details) {
			detail.delete();
		}

		// Map choices.
		Map<Long, Choice> choices = new HashMap<Long, Choice>();
		for (Choice choice : question.choices) {
			choices.put(choice.id, choice);
		}

		// Save current choices.
		Choice choice;
		AnswerDetail detail;
		List<AnswerDetail> details = new ArrayList<AnswerDetail>();
		for (Long choiceId : choiceIds) {
			choice = choices.get(choiceId);
			detail = new AnswerDetail();
			detail.choice = choice;
			details.add(detail);
		}
		answer.details = details;
		answer.save();
	}

	private static Answer getOrCreateAnswer(User user, Question question) {
		ExpressionList<Answer> el = Ebean.find(Answer.class).fetch("details")
				.where().eq("user.id", user.id).eq("question.id", question.id);
		if (el.findRowCount() == 0) {
			Answer answer = new Answer();
			answer.user = user;
			answer.question = question;
			return answer;
		}
		return el.findUnique();
	}

	public static Question getQuestionWithAnswers(Long questionId) {
		return Ebean.find(Question.class).fetch("answers")
				.fetch("answers.user").fetch("answers.details")
				.fetch("answers.details.choice").where().eq("id", questionId)
				.findUnique();
	}
}
