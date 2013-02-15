package ui.util;

import java.util.List;
import java.util.Locale;

import services.ReferentialService;
import util.security.SessionUtil;

public class Language {

	public static boolean displayLanguageSelection() {
		return availableLanguages().size() > 1;
	}

	public static List<Locale> availableLanguages() {
		return ReferentialService.getLanguages();
	}

	public static boolean isCurrentLanguage(String language) {
		return SessionUtil.preferredLocale().isDefined()
				&& SessionUtil.preferredLocale().get().getLanguage()
						.equals(language);
	}
}
