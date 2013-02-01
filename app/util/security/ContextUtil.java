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
		Context.current.set(context);
		if (!context.session().containsKey(SessionParameters.USERNAME.getKey())) {
			return null;
		}
		return UserService.findByUsername(context.session().get(
				SessionParameters.USERNAME.getKey()));
	}
}
