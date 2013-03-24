package util.security;

import models.User;
import play.mvc.Http.Context;
import scala.Option;
import services.UserService;

public final class ContextUtil {
	private ContextUtil() {
		throw new AssertionError();
	}

	public static Option<User> getCurrentUser(Context context) {
		if (context == null) {
			return null;
		}
		Context.current.set(context);
		if (!context.session().containsKey(SessionParameters.USERNAME.getKey())) {
			return Option.empty();
		}
		return UserService.findByUsername(context.session().get(
				SessionParameters.USERNAME.getKey()));
	}
}
