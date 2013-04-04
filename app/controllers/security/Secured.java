package controllers.security;

import models.User;
import play.libs.F.Option;
import play.mvc.Http.Context;
import play.mvc.Result;
import play.mvc.Security;
import ui.util.UIUtil;
import util.security.ContextUtil;

public class Secured extends Security.Authenticator {

	@Override
	public String getUsername(Context ctx) {
		Option<User> user = ContextUtil.getCurrentUser(ctx);
		if (!user.isDefined()) {
			return null;
		}
		return user.get().username;
	}

	@Override
	public Result onUnauthorized(Context ctx) {
		return redirect(controllers.routes.Authentication.login(UIUtil
				.urlEncode(ctx.request())));
	}
}
