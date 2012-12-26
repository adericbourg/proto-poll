package views.tags;

import models.User;
import play.api.templates.Html;
import util.security.SessionUtil;

public class UserTags {

	public static Html currentUser() {
		User user = SessionUtil.getCurrentUser();

		if (user == null) {
			return new Html(
					"<a class=\"btn btn-inverse btn-mini\" href=\"/login\">Login</a>");
		}

		return new Html(
				user.getDisplay()
						+ "<a class=\"btn btn-inverse btn-mini\" href=\"/logout\">Logout</a>");
	}
}
