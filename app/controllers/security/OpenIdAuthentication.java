package controllers.security;

import static util.user.message.Messages.info;

import java.util.HashMap;
import java.util.Map;

import models.User;
import play.libs.F.Promise;
import play.libs.OpenID;
import play.libs.OpenID.UserInfo;
import play.mvc.Controller;
import play.mvc.Result;
import services.UserService;
import services.openid.OpenIdAttributes;
import services.openid.OpenIdProvider;
import ui.util.UIUtil;
import util.security.SessionUtil;

import com.google.common.base.Strings;

import controllers.message.ControllerMessage;

public class OpenIdAuthentication extends Controller {

	static Result auth(OpenIdProvider provider, String url) {
		if (provider == null) {
			return badRequest("No provider supplied");
		}
		String providerUrl = provider.url();
		String returnToUrl = UIUtil.getFullUrl(routes.OpenIdAuthentication
				.verify().url());
		Map<String, String> attributes = new HashMap<String, String>();
		for (OpenIdAttributes attribute : OpenIdAttributes.values()) {
			attributes.put(attribute.getKey(), attribute.getAttribute());
		}

		Promise<String> redirectUrl = OpenID.redirectURL(providerUrl,
				returnToUrl, attributes);
		return redirect(redirectUrl.get());
	}

	public static Result verify() {
		return verifyAndRedirect(null);
	}

	public static Result verifyAndRedirect(String url) {
		Promise<UserInfo> userInfoPromise = OpenID.verifiedId();

		UserInfo userInfo = userInfoPromise.get();
		User user = UserService.authenticateOpenId(userInfo);

		SessionUtil.setUser(user);

		// Redirect to page.
		info(ControllerMessage.APPLICATION_WELCOME, user.getDisplay());
		if (!Strings.isNullOrEmpty(url)) {
			return redirect(UIUtil.fullUrlDecode(url));
		}
		return redirect(controllers.routes.Application.index());
	}
}
