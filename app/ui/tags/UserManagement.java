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
}
