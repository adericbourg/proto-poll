package controllers;

import java.util.Locale;

import org.codehaus.jackson.node.ObjectNode;

import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import services.ReferentialService;
import util.security.CurrentUser;
import util.user.message.Messages;

public class I18nController extends Controller {
	/*
	 * public static Result javascriptRoutes() {
	 * response().setContentType("text/javascript"); return
	 * ok(Routes.javascriptRouter("jsRoutes", // Routes
	 * controllers.routes.javascript.I18nController .questionAddChoices())); }
	 */

	public static Result questionAddChoices() {
		ObjectNode data = Json.newObject();
		data.put("choice_placeholder",
				Messages.resolve("question.add_choices.placeholder.choice"));
		return returnLocalizedData(data);
	}

	public static Result eventAddDates() {
		ObjectNode data = Json.newObject();
		data.put("language", currentLanguage());
		return returnLocalizedData(data);
	}

	private static String currentLanguage() {
		return CurrentUser.preferredLang().isDefined() ? CurrentUser
				.preferredLang().get().code() : defaultLang();
	}

	private static String defaultLang() {
		for (Locale locale : ReferentialService.getLanguages()) {
			return locale.getCountry();
		}
		return "en";
	}

	private static Result returnLocalizedData(ObjectNode data) {
		// FIXME Templatize this.
		return ok("var i18n = " + Json.stringify(data));
	}

}
