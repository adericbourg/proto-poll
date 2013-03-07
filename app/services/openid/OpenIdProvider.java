package services.openid;

import models.reference.ThirdPartySource;

public enum OpenIdProvider {

	GOOGLE("https://www.google.com/accounts/o8/id", ThirdPartySource.GOOGLE);

	private final String url;
	private final ThirdPartySource source;

	private OpenIdProvider(String url, ThirdPartySource source) {
		this.url = url;
		this.source = source;
	}

	public String url() {
		return url;
	}

	public ThirdPartySource getSource() {
		return source;
	}
}
