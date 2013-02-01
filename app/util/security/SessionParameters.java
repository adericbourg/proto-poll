package util.security;

public enum SessionParameters {
	USERNAME;

	public String getKey() {
		return String.format("__%s_-_%s__", SessionUtil.sessionId(), name()
				.toLowerCase());
	}

}
