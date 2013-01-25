package services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Event;
import models.EventAnswer;
import models.EventAnswerDetail;
import models.EventChoice;
import models.User;
import play.db.ebean.Model.Finder;
import services.exception.AnonymousUserAlreadyAnsweredPoll;
import util.security.SessionUtil;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.ExpressionList;

public class EventService {

	private static final Finder<Long, Event> EVENT_FINDER = new Finder<Long, Event>(
			Long.class, Event.class);

	public static Long createEvent(Event event) {
		event.userCreator = SessionUtil.currentUser();
		event.save();
		return event.id;
	}

	public static Event getEvent(Long id) {
		return EVENT_FINDER.byId(id);
	}

	public static void saveDates(Long eventId, Collection<EventChoice> dates) {
		Event event = getEvent(eventId);
		event.dates = new ArrayList<EventChoice>(dates);
		event.save();
	}

	public static void answerEvent(Long questionId, Collection<Long> choiceIds) {
		answerEvent(SessionUtil.currentUser(), questionId, choiceIds);
	}

	public static void answerEvent(String username, Long eventId,
			Collection<Long> choiceIds) {
		Event event = getEvent(eventId); // With answers

		// Check if a user with same name has already answered.
		for (EventAnswer answer : event.answers) {
			if (answer.user.username.equals(username)) {
				throw new AnonymousUserAlreadyAnsweredPoll();
			}
		}

		// Create new unregistered user with same login.
		User user = UserService.registerAnonymousUser(username);

		answerEvent(user, eventId, choiceIds);
	}

	private static void answerEvent(User user, Long eventId,
			Collection<Long> choiceIds) {
		if (user == null) {
			throw new RuntimeException("User cannot be null");
		}

		Event event = getEvent(eventId); // With answers
		EventAnswer answer = getOrCreateAnswer(user, event);

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

	public static List<Event> events() {
		return Ebean.find(Event.class).findList();
	}
}
