package util;

import models.User;
import services.UserService;
import util.security.PasswordUtil;
import util.security.CurrentUser;

public class UserTestUtil {

	public static final String USER_LOGIN = "t_usr_dmy_login";
	public static final String USER_PASSWORD = "t_usr_dmy_password";

	public static User createUser() {
		User user = new User();
		user.username = USER_LOGIN;
		user.passwordHash = PasswordUtil
				.hashPassword(USER_LOGIN, USER_PASSWORD);
		UserService.registerUser(user);
		return user;
	}

	public static User getAuthenticatedUser() {
		User user = createUser();
		UserService.authenticate(USER_LOGIN, USER_PASSWORD);
		CurrentUser.setUser(user);
		return user;
	}
}
