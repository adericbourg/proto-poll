package services;

import models.User;
import util.security.PasswordUtil;

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

	public static void registerUser(User user) {
		if (user.id != null) {
			throw new RuntimeException("user already registered");
		}
		if (findByUsername(user.username) != null) {
			throw new RuntimeException("username already exists");
		}
		user.save();
	}

	public static User authenticate(String login, String password) {
		String trimmedLogin = login.trim();
		ExpressionList<User> el = Ebean
				.find(User.class)
				.where()
				.ieq("username", trimmedLogin)
				.ieq("password_hash",
						PasswordUtil.hashPassword(login, password));
		if (el.findRowCount() == 0) {
			return null;
		}
		return el.findUnique();
	}

	public static User getUserOrRegisterByName(String name) {
		User user = findByUsername(name);
		if (user == null) {
			return registerAnonymousUser(name);
		}
		return user;
	}

	public static User findByUsername(String name) {
		String trimmedUsername = name.trim();
		ExpressionList<User> el = Ebean.find(User.class).where()
				.ieq("username", trimmedUsername);
		if (el.findRowCount() == 0) {
			return null;
		}
		return el.findUnique();
	}

	private static User registerAnonymousUser(String name) {
		User user = new User();
		user.username = name.trim();
		user.registered = false;
		user.save();
		return user;
	}
}
