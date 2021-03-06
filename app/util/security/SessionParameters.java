package util.security;

enum SessionParameters {
	USERNAME, LOCALE;

	String getKey() {
		return String.format("__%s_-_%s__", CurrentUser.sessionId(), name()
				.toLowerCase());
	}

}
