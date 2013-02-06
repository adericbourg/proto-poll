package controllers;
import static play.data.Form.form;
import static ui.tags.Messages.info;
import static ui.tags.Messages.warning;
import static ui.tags.MessagesHelper.invalidForm;
import models.User;
import play.data.Form;
import play.data.validation.Constraints.Required;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import scala.Option;
import services.UserService;
import services.exception.NoAuthenfiedUserInSessionException;
import util.security.SessionUtil;
import views.html.userProfile;

import com.google.common.base.Strings;

import controllers.security.Secured;

@Security.Authenticated(Secured.class)
public class UserSettings extends Controller {

	public static class UserProfile {
		public String displayName;
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
				"email", "displayName");
		if (formProfile.hasErrors()) {
			return invalidForm(userProfile.render(formProfile,
					FORM_USER_SECURITY));
		}
		UserService
				.updateUserProfile(getUserFromUserProfile(formProfile.get()));
		return redirect(routes.UserSettings.profile());
	}

	private static User getUserFromUserProfile(UserProfile profile) {
		User user = new User();
		user.email = profile.email;
		user.displayName = profile.displayName;
		return user;
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
		return redirect(routes.UserSettings.profile());
	}

	private static Form<UserProfile> getFormProfile() {
		Option<User> optUser = SessionUtil.currentUser();
		if (optUser.isEmpty()) {
			throw new NoAuthenfiedUserInSessionException();
		}

		User currentUser = optUser.get();
		UserProfile userProfileData = new UserProfile();
		userProfileData.displayName = currentUser.displayName;
		userProfileData.email = currentUser.email;

		return FORM_USER_PROFILE.fill(userProfileData);
	}
}
