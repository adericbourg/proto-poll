package ui.tags;

import models.User;
import play.api.templates.Html;
import scala.Option;
import util.security.SessionUtil;

public class UserManagement {

	public static boolean isLoggedIn() {
		return SessionUtil.currentUser().isDefined();
	}

	public static Html getUserDisplay() {
		Option<User> user = SessionUtil.currentUser();
		return user.isDefined() ? new Html(user.get().getDisplay()) : new Html(
				"");
	}

	public static Html currentUser() {
		Option<User> user = SessionUtil.currentUser();

		if (user.isEmpty()) {
			return new Html(
					"<a class=\"btn btn-inverse btn-mini\" href=\"/login\">Login</a>");
		}
		return new Html(
				user.get().getDisplay()
						+ "<a class=\"btn btn-inverse btn-mini\" href=\"/logout\">Logout</a>");
	}
}
