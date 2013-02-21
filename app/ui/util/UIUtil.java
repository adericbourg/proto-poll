package ui.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import models.User;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import play.Play;
import play.mvc.Http.Request;
import scala.Option;
import util.security.SessionUtil;
import util.user.message.Messages;

import com.google.common.base.Strings;

public final class UIUtil {

	private static final String ENCODING = "UTF-8";
	private static final String ROOT_URL = "application.base.url";
	private static final String GRAVATAR_PICTURE_URL = "gravatar.picture.url";
	private static final String DATE_FORMAT_PARAMETER = "date.format";
	private static final String DATE_TIME_FORMAT_PARAMETER = "datetime.format";
	private static final int GRAVATAR_DEFAULT_SIZE = 25;

	private UIUtil() {
		throw new AssertionError();
	}

	public static String formatDate(LocalDate date) {
		DateTimeFormatter formatter;
		String formatPattern = Messages.resolve(DATE_FORMAT_PARAMETER);
		if (Strings.isNullOrEmpty(formatPattern)) {
			formatter = DateTimeFormat.mediumDate();
		} else {
			formatter = DateTimeFormat.forPattern(formatPattern);
		}
		return formatter.print(date);
	}

	public static String formatDateTime(DateTime dateTime) {
		DateTimeFormatter formatter;
		String formatPattern = Messages.resolve(DATE_TIME_FORMAT_PARAMETER);
		if (Strings.isNullOrEmpty(formatPattern)) {
			formatter = DateTimeFormat.mediumDate();
		} else {
			formatter = DateTimeFormat.forPattern(formatPattern);
		}
		return formatter.print(dateTime);
	}

	public static String urlEncode(Request request) {
		try {
			return URLEncoder.encode(request.uri(), ENCODING);
		} catch (UnsupportedEncodingException e) {
			return "";
		}
	}

	public static String fullUrlDecode(String encodedUrl) {
		try {
			return getFullUrl(URLDecoder.decode(encodedUrl, ENCODING));
		} catch (UnsupportedEncodingException e) {
			return "";
		}
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

	public static String getGravatarUrl(Option<User> optUser, int size) {
		if (optUser.isEmpty()) {
			return null;
		}

		User user = optUser.get();
		String email = Strings.isNullOrEmpty(user.avatarEmail) ? user.email
				: user.avatarEmail;
		return String.format(
				Play.application().configuration()
						.getString(GRAVATAR_PICTURE_URL),
				MD5HexUtil.md5Hex(email), size);
	}

	public static String getGravatarUrl() {
		return getGravatarUrl(SessionUtil.currentUser(), GRAVATAR_DEFAULT_SIZE);
	}
}
