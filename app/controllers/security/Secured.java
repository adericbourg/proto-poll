package controllers.security;

import controllers.routes;
import models.User;
import play.mvc.Http.Context;
import play.mvc.Result;
import play.mvc.Security;
import util.security.ContextUtil;

public class Secured extends Security.Authenticator {

	@Override
	public String getUsername(Context ctx) {
		User user = ContextUtil.getCurrentUser(ctx);
		if (user == null) {
			return null;
		}
		return user.username;
	}

	@Override
	public Result onUnauthorized(Context ctx) {
		return redirect(routes.Authentication.login());
	}
}
