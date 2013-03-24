package controllers.security;

import models.reference.ThirdPartySource;
import play.mvc.Result;
import services.openid.OpenIdProvider;

public class GoogleAuthenticationController extends OpenIdAuthenticationSupport {

	public static Result auth(String returnUrl) {
		return auth(OpenIdProvider.GOOGLE,
				routes.GoogleAuthenticationController.verify(returnUrl));
	}

	public static Result verify(String url) {
		return verify(ThirdPartySource.GOOGLE, url);
	}
}
