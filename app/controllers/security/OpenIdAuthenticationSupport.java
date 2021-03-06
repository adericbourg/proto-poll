package controllers.security;

import static util.user.message.Messages.info;

import java.util.HashMap;
import java.util.Map;

import models.User;
import models.reference.ThirdPartySource;
import play.libs.F.Promise;
import play.libs.OpenID;
import play.libs.OpenID.UserInfo;
import play.mvc.Call;
import play.mvc.Controller;
import play.mvc.Result;
import services.UserService;
import services.openid.OpenIdAttributes;
import services.openid.OpenIdProvider;
import ui.util.UIUtil;
import util.security.CurrentUser;

import com.google.common.base.Strings;

import controllers.message.ControllerMessage;

public class OpenIdAuthenticationSupport extends Controller {

	protected static Result auth(OpenIdProvider provider, Call verifyCall) {
		if (provider == null) {
			return badRequest("No provider supplied");
		}
		String providerUrl = provider.url();
		String returnToFullUrl = UIUtil.getFullUrl(verifyCall.url());

		Map<String, String> attributes = new HashMap<String, String>();
		for (OpenIdAttributes attribute : OpenIdAttributes.values()) {
			attributes.put(attribute.getKey(), attribute.getAttribute());
		}

		Promise<String> redirectUrl = OpenID.redirectURL(providerUrl,
				returnToFullUrl, attributes);
		return redirect(redirectUrl.get());
	}

	protected static Result verify(ThirdPartySource source, String returnToUrl) {
		Promise<UserInfo> userInfoPromise = OpenID.verifiedId();

		UserInfo userInfo = userInfoPromise.get();
		User user = UserService.authenticateOpenId(userInfo, source);

		CurrentUser.setUser(user);

		// Redirect to page.
		info(ControllerMessage.APPLICATION_WELCOME, user.getDisplay());
		if (Strings.isNullOrEmpty(returnToUrl)) {
			return redirect(controllers.routes.Application.index());
		}
		return redirect(UIUtil.getFullUrl(returnToUrl));
	}
}
