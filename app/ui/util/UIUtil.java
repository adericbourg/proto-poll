package ui.util;

import play.Play;

public final class UIUtil {

	private static final String ROOT_URL = "application.base.url";

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
}
