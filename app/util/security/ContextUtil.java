package util.security;

import models.User;
import play.mvc.Http.Context;
import services.UserService;

public final class ContextUtil {
	private ContextUtil() {
		throw new AssertionError();
	}

	public static User getCurrentUser(Context context) {
		if (context == null) {
			return null;
		}

		if (!context.session().containsKey(SessionUtil.USERNAME_KEY)) {
			return null;
		}
		return UserService.findByUsername(context.session().get(
				SessionUtil.USERNAME_KEY));

	}
}
