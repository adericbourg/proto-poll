package controllers;

import java.util.List;

import models.Poll;
import play.db.ebean.Transactional;
import play.mvc.Result;
import play.mvc.Security;
import services.PollService;
import util.security.SessionUtil;
import util.user.message.Messages;
import views.html.index;
import views.html.listPolls;
import controllers.message.ControllerMessage;
import controllers.security.Secured;

@Security.Authenticated(Secured.class)
public class UserViewPolls extends ViewPolls {

	@Transactional
	public static Result userPolls() {
		if (!SessionUtil.isAuthenticated()) {
			Messages.error(ControllerMessage.ACCESS_FORBIDDEN_NOT_AUTHENTIFIED);
			return badRequest(index.render());
		}

		List<Poll> polls = PollService.listUserPolls();
		if (polls.isEmpty()) {
			return noPoll();
		}
		return ok(listPolls.render(polls));
	}
}
