package util.security;

import org.apache.commons.codec.digest.DigestUtils;

public final class PasswordUtil {
	private PasswordUtil() {
		// Utilitary class.
		throw new AssertionError();
	}

	public static String hashPassword(String login, String password) {
		// Hash(login|hash(password))
		return DigestUtils.sha1Hex(String.format("%s|%s", login,
				DigestUtils.sha1Hex(password)));
	}
}
