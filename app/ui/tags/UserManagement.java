package ui.tags;

import models.User;
import play.api.templates.Html;
import util.security.SessionUtil;

public class UserManagement {

	public static boolean isLoggedIn() {
		return SessionUtil.currentUser() != null;
	}

	public static Html getUserDisplay() {
		User user = SessionUtil.currentUser();
		return user == null ? new Html("") : new Html(user.getDisplay());
	}

	public static Html currentUser() {
		User user = SessionUtil.currentUser();

		if (user == null) {
			return new Html(
					"<a class=\"btn btn-inverse btn-mini\" href=\"/login\">Login</a>");
		}

		return new Html(
				user.getDisplay()
						+ "<a class=\"btn btn-inverse btn-mini\" href=\"/logout\">Logout</a>");
	}
}
