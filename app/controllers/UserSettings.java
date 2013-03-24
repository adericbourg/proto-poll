package controllers;

import static play.data.Form.form;
import static ui.tags.MessagesHelper.invalidForm;
import static util.user.message.Messages.info;
import static util.user.message.Messages.warning;

import java.util.ArrayList;
import java.util.List;

import models.User;

import org.apache.commons.lang3.LocaleUtils;

import play.data.Form;
import play.data.validation.Constraints.Email;
import play.data.validation.Constraints.Required;
import play.db.ebean.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import scala.Option;
import services.ReferentialService;
import services.UserService;
import services.exception.poll.NoAuthenfiedUserInSessionException;
import ui.util.Language;
import util.security.SessionUtil;
import views.html.user.userProfile;

import com.google.common.base.Strings;

import controllers.message.ControllerMessage;
import controllers.model.user.UserProfileLayout;
import controllers.security.Secured;

@Security.Authenticated(Secured.class)
public class UserSettings extends Controller {

	public static class UserProfile {
		public String displayName;
		public String localeCode;
		@Email(message = "user.constraint.email.format")
		public String email;
		@Email(message = "user.constraint.email.format")
		public String avatarEmail;
	}

	public static class UserSecurity {
		@Required(message = "user.security.old_password.mandatory")
		public String oldPassword;
		@Required(message = "user.security.new_password.mandatory")
		public String password1;
		@Required(message = "user.security.new_password_confirm.mandatory")
		public String password2;
	}

	private static final Form<UserProfile> FORM_USER_PROFILE = form(UserProfile.class);
	private static final Form<UserSecurity> FORM_USER_SECURITY = form(UserSecurity.class);

	@Transactional
	public static Result profile() {

		return ok(userProfile.render(ReferentialService.getLanguages(),
				getFormProfile(), FORM_USER_SECURITY, getLayout()));
	}

	@Transactional
	public static Result updateProfile() {
		Form<UserProfile> formProfile = bindProfileUpdateFromRequest();
		if (formProfile.hasErrors()) {
			return invalidForm(userProfile.render(
					ReferentialService.getLanguages(), formProfile,
					FORM_USER_SECURITY, getLayout()));
		}
		UserService
				.updateUserProfile(getUserFromUserProfile(formProfile.get()));
		return redirect(routes.UserSettings.profile());
	}

	private static Form<UserProfile> bindProfileUpdateFromRequest() {
		String[] updatableFields = getBindableFieldsForProfileUpdate();
		Form<UserProfile> formProfile = getFormProfile().bindFromRequest(
				updatableFields);
		return formProfile;
	}

	private static String[] getBindableFieldsForProfileUpdate() {
		List<String> updatableFields = new ArrayList<String>();
		updatableFields.add("displayName");
		if (Language.displayLanguageSelection()) {
			updatableFields.add("localeCode");
		}
		if (SessionUtil.currentUser().isDefined()
				&& SessionUtil.currentUser().get().isLocalUser()) {
			updatableFields.add("email");
		}
		updatableFields.add("avatarEmail");
		return updatableFields.toArray(new String[updatableFields.size()]);
	}

	private static User getUserFromUserProfile(UserProfile profile) {
		User user = new User();
		if (!Strings.isNullOrEmpty(profile.email)) {
			user.email = profile.email;
		}
		user.avatarEmail = profile.avatarEmail;
		user.displayName = profile.displayName;
		user.preferredLocale = Strings.isNullOrEmpty(profile.localeCode) ? null
				: LocaleUtils.toLocale(profile.localeCode);
		return user;
	}

	@Transactional
	public static Result updatePassword() {
		Form<UserSecurity> formSecurity = FORM_USER_SECURITY.bindFromRequest();

		// Check form.
		if (formSecurity.hasErrors()) {
			return invalidForm(userProfile.render(
					ReferentialService.getLanguages(), getFormProfile(),
					formSecurity, getLayout()));
		}

		UserSecurity security = formSecurity.get();
		boolean hasError = false;

		// Check new passwords match.
		if (!security.password1.equals(security.password2)) {
			hasError = true;
			formSecurity
					.reject("password1", "password.change.new_do_not_match");
			formSecurity.reject("password2",
					"password.change.new_do_not_match_confirmation");
		}

		if (!hasError) {
			if (!UserService.updateUserPassword(security.oldPassword,
					security.password1)) {
				hasError = true;
			}
		}

		if (hasError) {
			return invalidForm(userProfile.render(
					ReferentialService.getLanguages(), getFormProfile(),
					formSecurity, getLayout()));
		}

		if (!Strings.isNullOrEmpty(security.oldPassword)
				&& security.oldPassword.equals(security.password1)) {
			warning(ControllerMessage.PASSWORD_CHANGE_SAME_AS_OLD);
		}
		info(ControllerMessage.PASSWORD_SUCCESSFULLY_CHANGED);
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
		userProfileData.localeCode = currentUser.preferredLocale == null ? null
				: currentUser.preferredLocale.toString();
		userProfileData.avatarEmail = currentUser.avatarEmail;
		return FORM_USER_PROFILE.fill(userProfileData);
	}

	private static UserProfileLayout getLayout() {
		return new UserProfileLayout(SessionUtil.currentUser().get());
	}
}
