package util.security;

enum SessionParameters {
	USERNAME;

	String getKey() {
		return String.format("__%s_-_%s__", SessionUtil.sessionId(), name()
				.toLowerCase());
	}

}
