package services;

import models.User;
import scala.Option;
import services.exception.AlreadyRegisteredUser;
import services.exception.NoAuthenfiedUserInSessionException;
import services.messages.ServiceMessage;
import util.security.PasswordUtil;
import util.security.SessionUtil;
import util.user.message.Messages;

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
			throw new IllegalArgumentException("user already has an id");
		}
		if (findByUsername(user.username).isDefined()) {
			throw new AlreadyRegisteredUser(user.username);
		}
		user.registered = true;
		user.save();
	}

	public static void updateUserProfile(User user) {
		User currentUser = getCheckedCurrentUser();
		currentUser.email = user.email;
		currentUser.displayName = user.displayName;
		currentUser.preferredLocale = user.preferredLocale;
		currentUser.save();
		SessionUtil.setUser(currentUser);
	}

	public static boolean updateUserPassword(String oldPassword,
			String newPassword) {
		User currentUser = getCheckedCurrentUser();

		if (!currentUser.passwordHash.equals(PasswordUtil.hashPassword(
				currentUser.username, oldPassword))) {
			Messages.error(ServiceMessage.LOGIN_WRONG_OLD_PASSWORD);
			return false;
		}

		currentUser.passwordHash = PasswordUtil.hashPassword(
				currentUser.username, newPassword);
		currentUser.save();
		return true;
	}

	public static Option<User> authenticate(String login, String password) {
		String trimmedLogin = login.trim();
		ExpressionList<User> el = Ebean
				.find(User.class)
				.where()
				.ieq("username", trimmedLogin)
				.ieq("password_hash",
						PasswordUtil.hashPassword(login, password));
		if (el.findRowCount() == 0) {
			return Option.empty();
		}
		return Option.apply(el.findUnique());
	}

	public static Option<User> findByUsername(String name) {
		String trimmedUsername = name.trim();
		ExpressionList<User> el = Ebean.find(User.class).where()
				.ieq("username", trimmedUsername).ieq("registered", "true");
		if (el.findRowCount() == 0) {
			return Option.empty();
		}
		return Option.apply(el.findUnique());
	}

	public static User registerAnonymousUser(String name) {
		User user = new User();
		user.username = name.trim();
		user.registered = false;
		user.save();
		return user;
	}

	private static User getCheckedCurrentUser() {
		Option<User> currentUser = SessionUtil.currentUser();
		if (currentUser.isEmpty()) {
			throw new NoAuthenfiedUserInSessionException();
		}
		return currentUser.get();
	}
}
