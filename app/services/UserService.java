package services;

import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import models.User;
import models.reference.ThirdPartySource;
import play.db.ebean.Transactional;
import play.libs.F.Option;
import play.libs.OpenID.UserInfo;
import services.exception.poll.NoAuthenticatedUserInSessionException;
import services.exception.user.AlreadyRegisteredUser;
import services.messages.ServiceMessage;
import services.openid.OpenIdAttributes;
import util.security.CurrentUser;
import util.security.PasswordUtil;
import util.user.message.Messages;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.ExpressionList;
import com.google.common.base.Strings;

/**
 * User management service.
 * 
 * @author adericbourg
 * 
 */
public final class UserService {

	private UserService() {
		throw new AssertionError();
	}

	@Transactional
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

	@Transactional
	public static void updateUserProfile(User user) {
		User currentUser = getCheckedCurrentUser();
		currentUser.avatarEmail = user.avatarEmail;
		currentUser.displayName = user.displayName;
		currentUser.preferredLocale = user.preferredLocale;
		if (!Strings.isNullOrEmpty(user.email)) {
			currentUser.email = user.email;
		}
		currentUser.update();
		CurrentUser.setUser(currentUser);
	}

	@Transactional
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

	@Transactional
	public static Option<User> getUser(Long userId) {
		ExpressionList<User> el = Ebean.find(User.class).where().idEq(userId);
		if (el.findRowCount() == 0) {
			return Option.None();
		}
		return Option.Some(el.findUnique());
	}

	@Transactional
	public static Option<User> authenticate(String login, String password) {
		String trimmedLogin = login.trim();
		ExpressionList<User> el = Ebean
				.find(User.class)
				.where()
				.ieq("username", trimmedLogin)
				.eq("registered", Boolean.TRUE)
				.ieq("password_hash",
						PasswordUtil.hashPassword(login, password));
		if (el.findRowCount() == 0) {
			return Option.None();
		}
		return Option.Some(el.findUnique());
	}

	@Transactional
	public static Option<User> findByUsername(String name) {
		String trimmedUsername = name.trim();
		ExpressionList<User> el = Ebean.find(User.class).where()
				.ieq("username", trimmedUsername)
				.eq("registered", Boolean.TRUE);
		if (el.findRowCount() == 0) {
			return Option.None();
		}
		return Option.Some(el.findUnique());
	}

	@Transactional
	public static User authenticateOpenId(UserInfo userInfo,
			ThirdPartySource source) {
		Option<User> optUser = findByOpenId(userInfo.id, source);
		User user;
		if (!optUser.isDefined()) {
			// Create new user.
			user = new User();
			user.registered = true;
			user.thirdPartySource = source;
			user.thirdPartyIdentifier = userInfo.id;
			user.username = userInfo.id;
			if (userInfo.attributes.containsKey(OpenIdAttributes.LANGUAGE
					.getKey())) {
				user.preferredLocale = new Locale(
						userInfo.attributes.get(OpenIdAttributes.LANGUAGE
								.getKey()));
			}
		} else {
			// Get user.
			user = optUser.get();
		}

		// Update user data.
		user.displayName = buildDisplayName(userInfo.attributes);
		if (userInfo.attributes.containsKey(OpenIdAttributes.EMAIL.getKey())) {
			user.email = userInfo.attributes.get(OpenIdAttributes.EMAIL
					.getKey());
		}

		user.save();

		return user;
	}

	private static String buildDisplayName(Map<String, String> attributes) {
		// If OpenId service returned a friendly name: use it.
		if (attributes.containsKey(OpenIdAttributes.DISPLAY_NAME.getKey())) {
			return attributes.get(OpenIdAttributes.DISPLAY_NAME.getKey());
		}
		// Else build it from real name.
		StringBuilder sb = new StringBuilder();
		if (attributes.containsKey(OpenIdAttributes.FIRST_NAME.getKey())) {
			sb.append(attributes.get(OpenIdAttributes.FIRST_NAME.getKey()));
		}
		if (attributes.containsKey(OpenIdAttributes.FIRST_NAME.getKey())
				&& attributes.containsKey(OpenIdAttributes.LAST_NAME.getKey())) {
			sb.append(" ");
		}
		if (attributes.containsKey(OpenIdAttributes.LAST_NAME.getKey())) {
			sb.append(attributes.get(OpenIdAttributes.LAST_NAME.getKey()));
		}
		// If nothing was supplied, generate a random name.
		if (sb.length() == 0) {
			sb.append(UUID.randomUUID().toString());
		}
		return sb.toString();
	}

	private static Option<User> findByOpenId(String openIdIdentifier,
			ThirdPartySource source) {
		ExpressionList<User> el = Ebean.find(User.class).where()
				.ieq("thirdPartyIdentifier", openIdIdentifier)
				.eq("thirdPartySource", source);
		if (el.findRowCount() == 0) {
			return Option.None();
		}
		return Option.Some(el.findUnique());
	}

	@Transactional
	public static User registerAnonymousUser(String name) {
		User user = new User();
		user.username = name.trim();
		user.registered = false;
		user.save();
		return user;
	}

	private static User getCheckedCurrentUser() {
		Option<User> currentUser = CurrentUser.currentUser();
		if (!currentUser.isDefined()) {
			throw new NoAuthenticatedUserInSessionException();
		}
		return currentUser.get();
	}
}
