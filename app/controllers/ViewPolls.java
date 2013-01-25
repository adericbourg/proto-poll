package controllers;

import java.util.List;

import models.Event;
import models.Question;
import play.mvc.Controller;
import play.mvc.Result;
import services.EventService;
import services.QuestionService;
import views.html.listPolls;
import views.html.message;

/**
 * Temporary view polls controller. Once the application will publish / send by
 * e-mail the URL of the poll, this controller might be removed. Or maybe
 * replaced by user's polls list (when authentification will be implemented). Or
 * anything else. We'll see, that's not the point at the moment.
 * 
 * @author adericbourg
 * 
 */
public class ViewPolls extends Controller {
	public static Result polls() {
		List<Question> polls = QuestionService.questions();
		List<Event> events = EventService.events();
		if (polls.isEmpty() && events.isEmpty()) {
			return ok(message.render("Polls", "No poll available yet."));
		}
		return ok(listPolls.render(polls, events));
	}
}
