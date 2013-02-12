package controllers;

import org.apache.commons.lang3.LocaleUtils;

import play.db.ebean.Transactional;
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

	@Transactional
	public static Result index() {
		return ok(index.render());
	}

	@Transactional
	public static Result changeLanguage(String lang, String backUrl) {
		SessionUtil.setPreferredLocale(LocaleUtils.toLocale(lang));
		return redirect(UIUtil.fullUrlDecode(backUrl));
	}
}
