package controllers;

import java.util.List;

import models.Poll;
import play.db.ebean.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import services.PollService;
import util.user.message.Messages;
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

	@Transactional
	public static Result polls() {
		List<Poll> polls = PollService.polls();
		if (polls.isEmpty()) {
			return noPoll();
		}
		return ok(listPolls.render(polls));
	}

	protected static Result noPoll() {
		return ok(message.render(Messages.resolve("listpolls.name"),
				Messages.resolve("listpolls.no_poll_available")));
	}
}
