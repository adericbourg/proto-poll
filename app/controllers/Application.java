package controllers;

import org.apache.commons.lang3.LocaleUtils;

import play.mvc.Controller;
import play.mvc.Result;
import ui.util.UIUtil;
import util.security.SessionUtil;
import views.html.index;

/**
 * Default controller.
 * 
 * @author adericbourg
 * 
 */
public class Application extends Controller {

	public static Result index() {
		return ok(index.render());
	}

	public static Result changeLanguage(String lang, String backUrl) {
		SessionUtil.setPreferredLocale(LocaleUtils.toLocale(lang));
		return redirect(UIUtil.fullUrlDecode(backUrl));
	}
}
