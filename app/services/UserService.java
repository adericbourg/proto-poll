package services;

import models.User;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.ExpressionList;

/**
 * User management service.
 * 
 * @author adericbourg
 * 
 */
public final class UserService {

	private UserService() {
		// No instance.
		throw new AssertionError();
	}

	public static User getUserOrRegisterByName(String name) {
		String trimmedUsername = name.trim();
		ExpressionList<User> el = Ebean.find(User.class).where()
				.ieq("username", trimmedUsername);
		if (el.findRowCount() == 0) {
			return registerAnonymousUser(trimmedUsername);
		}
		return el.findUnique();
	}

	private static User registerAnonymousUser(String name) {
		User user = new User();
		user.username = name;
		user.registered = false;
		user.save();
		return user;
	}
}
