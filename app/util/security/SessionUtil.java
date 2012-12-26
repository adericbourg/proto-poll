package util.security;

import models.User;
import play.mvc.Http.Context;
import play.mvc.Http.Session;
import services.UserService;

public final class SessionUtil {

	private static final String USERNAME_KEY = "k_username";

	private SessionUtil() {
		throw new AssertionError();
	}

	public static void setUser(User user) {
		getCurrentSession().put(USERNAME_KEY, user.username);
	}

	public static User getCurrentUser(Context context) {
		if (context == null) {
			return null;
		}

		if (!context.session().containsKey(USERNAME_KEY)) {
			return null;
		}
		return UserService.findByUsername(context.session().get(USERNAME_KEY));

	}

	public static User currentUser() {
		if (!getCurrentSession().containsKey(USERNAME_KEY)) {
			return null;
		}
		return UserService
				.findByUsername(getCurrentSession().get(USERNAME_KEY));

	}

	public static void removeCurrentUser() {
		if (getCurrentSession().containsKey(USERNAME_KEY)) {
			getCurrentSession().remove(USERNAME_KEY);
		}
	}

	private static Session getCurrentSession() {
		return Context.current().session();
	}
}
