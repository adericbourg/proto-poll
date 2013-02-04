package ui.util;

import models.User;
import play.Play;
import scala.Option;
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
		if (SessionUtil.currentUser().isEmpty()) {
			return false;
		}
		return !Strings.isNullOrEmpty(SessionUtil.currentUser().get().email);
	}

	public static String getGravatarUrl(Option<User> user, int size) {
		if (user.isEmpty()) {
			return null;
		}
		return String.format(
				Play.application().configuration()
						.getString(GRAVATAR_PICTURE_URL),
				MD5HexUtil.md5Hex(user.get().email), size);
	}

	public static String getGravatarUrl() {
		return getGravatarUrl(SessionUtil.currentUser(), GRAVATAR_DEFAULT_SIZE);
	}
}
