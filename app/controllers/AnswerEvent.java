package controllers;

import static ui.tags.Messages.error;
import static ui.tags.Messages.info;

import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import models.Event;
import models.EventAnswer;
import models.EventAnswerDetail;
import models.PollResults;
import play.api.templates.Html;
import play.data.DynamicForm;
import play.mvc.Controller;
import play.mvc.Result;
import services.EventService;
import services.exception.AnonymousUserAlreadyAnsweredPoll;
import util.security.SessionUtil;
import views.html.event.eventAnswer;

import com.google.common.base.Strings;

public class AnswerEvent extends Controller {

	private static final String USERNAME_KEY = "data[username]";

	public static Result view(Long id) {
		return ok(getEventViewContent(id));
	}

	private static Html getEventViewContent(Long id) {
		return eventAnswer.render(SessionUtil.currentUser(),
				EventService.getEvent(id), getPollResults(id));
	}

	public static Result answer(Long id) {
		DynamicForm form = form().bindFromRequest();
		Set<Long> choices = new HashSet<Long>();
		for (Entry<String, String> entry : form.data().entrySet()) {
			if (!isUsername(entry.getKey())) {
				choices.add(Long.valueOf(entry.getValue()));
			}
		}
		if (SessionUtil.isAuthenticated()) {
			EventService.answerEvent(id, choices);
		} else {
			String username = form.data().get(USERNAME_KEY);
			if (Strings.isNullOrEmpty(username)) {
				error("Choose a user name.");
				return badRequest(getEventViewContent(id));
			}
			try {
				EventService.answerEvent(username, id, choices);
			} catch (AnonymousUserAlreadyAnsweredPoll e) {
				error("Your user name has already been used by someone else. If you want to be able to modifiy your answers, you have to be registered.");
				return badRequest(getEventViewContent(id));
			}
		}
		info("Thank you for answering!");
		return view(id);
	}

	private static PollResults getPollResults(Long pollId) {
		Event event = EventService.getEvent(pollId);

		PollResults results = new PollResults();
		for (EventAnswer ans : event.answers) {
			results.registerUser(ans.user);
			for (EventAnswerDetail detail : ans.details) {
				results.addAnswer(ans.user.username, detail.choice.id);
			}
		}
		return results;
	}

	private static boolean isUsername(String key) {
		return USERNAME_KEY.equals(key);
	}
}
