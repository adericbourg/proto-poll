package services.openid;

public enum OpenIdAttributes {
	/***/
	EMAIL("email", "http://schema.openid.net/contact/email"),
	/***/
	FIRST_NAME("firstName", "http://schema.openid.net/namePerson/first"),
	/***/
	LAST_NAME("lastName", "http://schema.openid.net/namePerson/last"),
	/***/
	DISPLAY_NAME("displayName", "http://openid.net/schema/namePerson/friendly"),
	/***/
	LANGUAGE("lang", "http://openid.net/schema/pref/language");

	private String key;
	private String attribute;

	private OpenIdAttributes(String key, String attribute) {
		this.key = key;
		this.attribute = attribute;
	}

	public String getKey() {
		return key;
	}

	public String getAttribute() {
		return attribute;
	}
}
