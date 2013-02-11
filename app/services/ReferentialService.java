package services;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.LocaleUtils;

import play.Play;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

public class ReferentialService {

	private static final String APPLICATION_LANGS = "application.langs";
	private static final String LANG_SEPARATOR = ",";

	private ReferentialService() {
		// No instance allowed.
		throw new AssertionError();
	}

	public static List<Locale> getLanguages() {
		String langsParam = Play.application().configuration()
				.getString(APPLICATION_LANGS);

		if (Strings.isNullOrEmpty(langsParam)) {
			return Collections.emptyList();
		}

		List<String> langs = Arrays.asList(langsParam.split(LANG_SEPARATOR));
		List<Locale> locales = Lists.newArrayList();
		for (String localeCode : langs) {
			locales.add(LocaleUtils.toLocale(localeCode.replace("-", "_")));
		}
		return locales;
	}
}
