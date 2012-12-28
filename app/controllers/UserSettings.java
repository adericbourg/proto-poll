package controllers;

import static ui.tags.Messages.info;
import static ui.tags.Messages.warning;
import static ui.tags.MessagesHelper.invalidForm;
import play.data.Form;
import play.data.validation.Constraints.Required;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import services.UserService;
import util.security.SessionUtil;
import views.html.userProfile;

import com.google.common.base.Strings;

@Security.Authenticated(Secured.class)
public class UserSettings extends Controller {

	public static class UserProfile {
		@Required(message = "You cannot remove your email address")
		public String email;
	}

	public static class UserSecurity {
		@Required(message = "Enter your current password")
		public String oldPassword;
		@Required(message = "Enter your new password")
		public String password1;
		@Required(message = "Enter your new password confirmation")
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
			return invalidForm(userProfile.render(formProfile,
					FORM_USER_SECURITY));
		}
		UserService.updateUserEmail(formProfile.get().email);
		return profile();
	}

	public static Result updatePassword() {
		Form<UserSecurity> formSecurity = FORM_USER_SECURITY.bindFromRequest();

		// Check form.
		if (formSecurity.hasErrors()) {
			return invalidForm(userProfile.render(getFormProfile(),
					formSecurity));
		}

		UserSecurity security = formSecurity.get();
		boolean hasError = false;

		// Check new passwords match.
		if (!security.password1.equals(security.password2)) {
			hasError = true;
			formSecurity.reject("password1", "Passwords do not match");
			formSecurity.reject("password2", "");
		}

		if (!hasError) {
			if (!UserService.updateUserPassword(security.oldPassword,
					security.password1)) {
				hasError = true;
			}
		}

		if (hasError) {
			return invalidForm(userProfile.render(getFormProfile(),
					formSecurity));
		}

		if (!Strings.isNullOrEmpty(security.oldPassword)
				&& security.oldPassword.equals(security.password1)) {
			warning("New password is the same as the old one. You might not want to do that.");
		}
		info("Password changed successfully");
		return profile();
	}

	private static Form<UserProfile> getFormProfile() {
		UserProfile userProfileData = new UserProfile();
		userProfileData.email = SessionUtil.currentUser().email;

		return FORM_USER_PROFILE.fill(userProfileData);
	}
}