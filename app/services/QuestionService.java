package services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import models.Question;
import models.QuestionAnswer;
import models.QuestionAnswerDetail;
import models.QuestionChoice;
import models.User;
import play.db.ebean.Model.Finder;
import play.db.ebean.Transactional;
import scala.Option;
import services.exception.user.AnonymousUserAlreadyAnsweredPoll;
import util.security.SessionUtil;

import com.avaje.ebean.ExpressionList;

/**
 * Question management service.
 * 
 * @author adericbourg
 * 
 */
public class QuestionService {

	private static final Finder<Long, QuestionAnswer> ANSWER_FINDER = new Finder<Long, QuestionAnswer>(
			Long.class, QuestionAnswer.class);

	private QuestionService() {
		// No instance.
		throw new AssertionError();
	}

	@Transactional
	public static void saveChoices(UUID uuid, List<QuestionChoice> choices) {
		List<QuestionChoice> deduplicatedChoices = deduplicateChoices(choices);
		Question question = PollService.getQuestion(uuid);
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

	@Transactional
	public static void answerQuestionAuthenticated(UUID uuid,
			Collection<Long> choiceIds) {
		answerQuestion(SessionUtil.currentUser(), uuid, choiceIds);
	}

	@Transactional
	public static void answerQuestionAnonymous(String username, UUID uuid,
			Collection<Long> choiceIds) {
		// FIXME It makes poll being loaded twice.
		Question question = PollService.getQuestion(uuid);

		// Check if a user with same name has already answered.
		for (QuestionAnswer answer : question.answers) {
			if (answer.user.username.equals(username)) {
				throw new AnonymousUserAlreadyAnsweredPoll();
			}
		}

		// Create new unregistered user with same login.
		User user = UserService.registerAnonymousUser(username);

		answerQuestion(Option.apply(user), uuid, choiceIds);
	}

	private static void answerQuestion(Option<User> user, UUID uuid,
			Collection<Long> choiceIds) {
		if (user.isEmpty()) {
			throw new RuntimeException("User must be defined");
		}

		Question question = PollService.getQuestion(uuid);
		QuestionAnswer answer = getOrCreateAnswer(user.get(), question);

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
		ExpressionList<QuestionAnswer> el = ANSWER_FINDER.fetch("details")
				.where().eq("user.id", user.id).eq("question.id", question.id);
		if (el.findRowCount() == 0) {
			QuestionAnswer answer = new QuestionAnswer();
			answer.user = user;
			answer.question = question;
			answer.details = new ArrayList<QuestionAnswerDetail>();
			return answer;
		}
		return el.findUnique();
	}
}
