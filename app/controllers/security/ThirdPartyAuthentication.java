package controllers.security;

import play.mvc.Result;
import services.openid.OpenIdProvider;

public class ThirdPartyAuthentication {

	public static Result google(String redirectUrl) {
		return OpenIdAuthentication.auth(OpenIdProvider.GOOGLE, redirectUrl);
	}
}
