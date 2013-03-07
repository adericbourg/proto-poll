package controllers.security;

import models.reference.ThirdPartySource;
import play.mvc.Result;
import services.openid.OpenIdProvider;

public class GoogleAuthenticationController extends
		OpenIdAuthenticationController {

	public static Result auth() {
		return auth(OpenIdProvider.GOOGLE,
				routes.GoogleAuthenticationController.verify());
	}

	public static Result verify() {
		return verify(ThirdPartySource.GOOGLE);
	}
}
