package ui.util;

import play.Play;
import util.security.SessionUtil;

import com.google.common.base.Strings;

public final class UIUtil {

	private static final String ROOT_URL = "application.base.url";
	private static final String GRAVATAR_PICTURE_URL = "gravatar.picture.url";

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

	public static String getGravatarUrl() {
		return String.format(
				Play.application().configuration()
						.getString(GRAVATAR_PICTURE_URL),
				MD5HexUtil.md5Hex(SessionUtil.currentUser().email));
	}
}
