package ui.util;

import models.User;
import play.Play;
import util.security.SessionUtil;

import com.google.common.base.Strings;

public final class UIUtil {

	private static final String ROOT_URL = "application.base.url";
	private static final String GRAVATAR_PICTURE_URL = "gravatar.picture.url";
	private static final int GRAVATAR_DEFAULT_SIZE = 25;

	private UIUtil() {
		throw new AssertionError();
	}

	public static String getFullUrl(String relativeUrl) {
		String rootUrl = Play.application().configuration().getString(ROOT_URL);
		if (rootUrl.endsWith("/") && relativeUrl.startsWith("/")) {
			return rootUrl + relativeUrl.substring(1);
		}
		return rootUrl + relativeUrl;
	}

	public static boolean displayGravatar() {
		if (!SessionUtil.isAuthenticated()) {
			return false;
		}
		return !Strings.isNullOrEmpty(SessionUtil.currentUser().email);
	}

	public static String getGravatarUrl(User user, int size) {
		return String.format(
				Play.application().configuration()
						.getString(GRAVATAR_PICTURE_URL),
				MD5HexUtil.md5Hex(user.email), size);
	}

	public static String getGravatarUrl() {
		return getGravatarUrl(SessionUtil.currentUser(), GRAVATAR_DEFAULT_SIZE);
	}
}
