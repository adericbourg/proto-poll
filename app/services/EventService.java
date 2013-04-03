package services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import models.Event;
import models.EventAnswer;
import models.EventAnswerDetail;
import models.EventChoice;
import models.User;
import models.reference.PollStatus;
import play.db.ebean.Transactional;
import scala.Option;
import services.exception.poll.NoChoiceException;
import services.exception.user.AnonymousUserAlreadyAnsweredPoll;
import util.security.CurrentUser;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.ExpressionList;

public class EventService {

	private EventService() {
		throw new AssertionError();
	}

	@Transactional
	public static void saveDates(UUID uuid, Collection<EventChoice> dates) {

		if (dates == null || dates.isEmpty()) {
			throw new NoChoiceException();
		}

		Event event = PollService.getEvent(uuid);
		event.dates = new ArrayList<EventChoice>(dates);
		event.save();

		PollService.updateStatus(uuid, PollStatus.COMPLETE);
	}

	static void answerEventRegistered(UUID uuid, Collection<Long> choiceIds) {
		answerEvent(CurrentUser.currentUser(), uuid, choiceIds);
	}

	static void answerEventAnonymous(String username, UUID uuid,
			Collection<Long> choiceIds) {
		Event event = PollService.getEvent(uuid); // With answers

		// Check if a user with same name has already answered.
		for (EventAnswer answer : event.answers) {
			if (answer.user.username.equals(username)) {
				throw new AnonymousUserAlreadyAnsweredPoll();
			}
		}

		// Create new unregistered user with same login.
		User user = UserService.registerAnonymousUser(username);

		answerEvent(Option.apply(user), uuid, choiceIds);
	}

	private static void answerEvent(Option<User> user, UUID uuid,
			Collection<Long> choiceIds) {
		if (user.isEmpty()) {
			throw new RuntimeException("User cannot be null");
		}

		Event event = PollService.getEvent(uuid);
		EventAnswer answer = getOrCreateAnswer(user.get(), event);

		// Clear all previous answers.
		for (EventAnswerDetail detail : answer.details) {
			detail.delete();
		}

		// Map choices.
		Map<Long, EventChoice> choices = new HashMap<Long, EventChoice>();
		for (EventChoice choice : event.dates) {
			choices.put(choice.id, choice);
		}

		// Save current choices.
		EventChoice choice;
		EventAnswerDetail detail;
		List<EventAnswerDetail> details = new ArrayList<EventAnswerDetail>();
		for (Long choiceId : choiceIds) {
			choice = choices.get(choiceId);
			detail = new EventAnswerDetail();
			detail.choice = choice;
			details.add(detail);
		}
		answer.details = details;
		answer.save();
	}

	private static EventAnswer getOrCreateAnswer(User user, Event event) {
		ExpressionList<EventAnswer> el = Ebean.find(EventAnswer.class)
				.fetch("details").where().eq("user.id", user.id)
				.eq("event.id", event.id);
		if (el.findRowCount() == 0) {
			EventAnswer answer = new EventAnswer();
			answer.user = user;
			answer.event = event;
			return answer;
		}
		return el.findUnique();
	}
}
