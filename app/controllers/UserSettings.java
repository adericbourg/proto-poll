package controllers;

import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import services.UserService;
import ui.tags.Messages;
import util.security.SessionUtil;
import views.html.userProfile;

import com.google.common.base.Strings;

@Security.Authenticated(Secured.class)
public class UserSettings extends Controller {

	public static class UserProfile {
		public String email;
	}

	public static class UserSecurity {
		public String oldPassword;
		public String password1;
		public String password2;
	}

	private static final Form<UserProfile> FORM_USER_PROFILE = form(UserProfile.class);
	private static final Form<UserSecurity> FORM_USER_SECURITY = form(UserSecurity.class);

	public static Result profile() {
		return ok(userProfile.render(getFormProfile(), FORM_USER_SECURITY));
	}

	public static Result updateProfile() {
		Form<UserProfile> formProfile = getFormProfile().bindFromRequest(
				"email");
		if (formProfile.hasErrors()) {
			return badRequest(userProfile.render(formProfile,
					FORM_USER_SECURITY));
		}
		UserService.updateUserEmail(formProfile.get().email);
		return profile();
	}

	public static Result updatePassword() {
		Form<UserSecurity> formSecurity = FORM_USER_SECURITY.bindFromRequest();

		// Check form.
		if (formSecurity.hasErrors()) {
			return badRequest(userProfile
					.render(getFormProfile(), formSecurity));
		}

		UserSecurity security = formSecurity.get();
		boolean hasError = false;

		// Check new passwords match.
		if (Strings.isNullOrEmpty(security.password1)) {
			hasError = true;
			Messages.error("New password is required");
		}
		if (Strings.isNullOrEmpty(security.password2)) {
			hasError = true;
			Messages.error("New password confirmation is required");
		}
		if (!security.password1.equals(security.password2)) {
			hasError = true;
			Messages.error("New password and confirmation password do not match");
		}

		if (!hasError) {
			if (!UserService.updateUserPassword(security.oldPassword,
					security.password1)) {
				hasError = true;
			}
		}

		if (hasError) {
			return badRequest(userProfile
					.render(getFormProfile(), formSecurity));
		}

		Messages.info("Password changed successfully");
		return profile();
	}

	private static Form<UserProfile> getFormProfile() {
		UserProfile userProfileData = new UserProfile();
		userProfileData.email = SessionUtil.currentUser().email;

		return FORM_USER_PROFILE.fill(userProfileData);
	}
}
