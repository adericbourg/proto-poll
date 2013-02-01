package util.security;

import static util.security.SessionParameters.USERNAME;

import java.util.UUID;

import models.User;
import play.cache.Cache;
import play.mvc.Http.Context;
import play.mvc.Http.Session;
import services.UserService;

public final class SessionUtil {

	private static final String SESSION_KEY = "SESSION_ID";

	private SessionUtil() {
		throw new AssertionError();
	}

	public static boolean isAuthenticated() {
		return getSessionParameter(USERNAME) != null;
	}

	public static void setUser(User user) {
		setSessionParameter(USERNAME, user.username);
		Cache.set(USERNAME.getKey(), user);
	}

	public static User currentUser() {
		if (!isAuthenticated()) {
			return null;
		}

		User user = (User) Cache.get(USERNAME.getKey());
		if (user == null) {
			user = UserService.findByUsername(getSessionParameter(USERNAME));
			Cache.set(USERNAME.getKey(), user);
		}
		return user;
	}

	public static void clear() {
		if (isAuthenticated()) {
			removeSessionParameter(USERNAME);
		}
		getCurrentSession().clear();
	}

	static String sessionId() {
		if (getCurrentSession().containsKey(SESSION_KEY)) {
			return getCurrentSession().get(SESSION_KEY);
		}

		String sessionId = UUID.randomUUID().toString();
		getCurrentSession().put(SESSION_KEY, sessionId);
		return sessionId();
	}

	private static String getSessionParameter(SessionParameters parameter) {
		return getCurrentSession().get(parameter.getKey());
	}

	private static void setSessionParameter(SessionParameters parameter,
			String value) {
		getCurrentSession().put(parameter.getKey(), value);
	}

	private static void removeSessionParameter(SessionParameters parameter) {
		getCurrentSession().remove(parameter.getKey());
	}

	private static Session getCurrentSession() {
		return Context.current().session();
	}

}
