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
import play.db.ebean.Model.Finder;
import services.exception.AnonymousUserAlreadyAnsweredPoll;
import util.security.SessionUtil;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.ExpressionList;

public class EventService {

	private static final Finder<Long, Event> EVENT_FINDER = new Finder<Long, Event>(
			Long.class, Event.class);

	public static Long createEvent(Event event) {
		event.save();
		PollService.initPoll(event);
		return event.id;
	}

	public static Event getEvent(Long id) {
		return EVENT_FINDER.fetch("poll.userCreator").fetch("poll.event")
				.fetch("poll.comments").fetch("poll.comments.user").where()
				.eq("id", id).findUnique();
	}

	public static void saveDates(Long eventId, Collection<EventChoice> dates) {
		Event event = getEvent(eventId);
		event.dates = new ArrayList<EventChoice>(dates);
		event.save();
	}

	public static void answerEvent(UUID uuid, Collection<Long> choiceIds) {
		answerEvent(SessionUtil.currentUser(), uuid, choiceIds);
	}

	public static void answerEvent(String username, UUID uuid,
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

		answerEvent(user, uuid, choiceIds);
	}

	private static void answerEvent(User user, UUID uuid,
			Collection<Long> choiceIds) {
		if (user == null) {
			throw new RuntimeException("User cannot be null");
		}

		Event event = PollService.getEvent(uuid);
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
