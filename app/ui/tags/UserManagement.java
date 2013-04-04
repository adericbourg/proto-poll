package ui.tags;

import models.User;
import play.api.templates.Html;
import play.libs.F.Option;
import util.security.CurrentUser;

public class UserManagement {

	public static boolean isLoggedIn() {
		return CurrentUser.currentUser().isDefined();
	}

	public static Html getUserDisplay() {
		Option<User> user = CurrentUser.currentUser();
		return user.isDefined() ? Html.apply(user.get().getDisplay()) : Html
				.apply("");
	}

	public static Html currentUser() {
		Option<User> user = CurrentUser.currentUser();

		if (!user.isDefined()) {
			return Html
					.apply("<a class=\"btn btn-inverse btn-mini\" href=\"/login\">Login</a>");
		}
		return Html
				.apply(user.get().getDisplay()
						+ "<a class=\"btn btn-inverse btn-mini\" href=\"/logout\">Logout</a>");
	}
}
