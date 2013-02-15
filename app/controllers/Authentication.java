package controllers;

import static play.data.Form.form;
import static ui.tags.MessagesHelper.invalidForm;
import static util.user.message.Messages.info;
import models.User;
import play.data.Form;
import play.data.validation.Constraints.Required;
import play.db.ebean.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import scala.Option;
import services.UserService;
import services.exception.AlreadyRegisteredUser;
import util.security.PasswordUtil;
import util.security.SessionUtil;
import util.user.message.Messages;
import controllers.message.ControllerMessage;

public class Authentication extends Controller {

	public static class Registration {
		@Required(message = "authentication.registration.username.mandatory")
		public String username;
		@Required(message = "authentication.registration.password.mandatory")
		public String password;
		@Required(message = "authentication.registration.password_confirm.mandatory")
		public String passwordConfirm;
		public String email;
	}

	public static class Login {
		@Required(message = "authentication.login.username.mandatory")
		public String username;
		@Required(message = "authentication.login.password.mandatory")
		public String password;
	}

	private static Form<Registration> FORM_REGISTRATION = form(Registration.class);
	private static Form<Login> FORM_LOGIN = form(Login.class);

	@Transactional
	public static Result register() {
		return ok(views.html.register.render(FORM_REGISTRATION));
	}

	@Transactional
	public static Result saveRegistration() {
		Form<Registration> filledForm = FORM_REGISTRATION.bindFromRequest();

		if (filledForm.hasErrors()) {
			return invalidForm(views.html.register.render(filledForm));
		}

		// Register user.
		Registration registration = filledForm.get();
		if (!registration.password.equals(registration.passwordConfirm)) {
			filledForm.reject("password", "Passwords do not match");
			filledForm.reject("passwordConfirm", "");
			return invalidForm(views.html.register.render(filledForm));
		}
		User user = getUserFromRegistration(registration);

		try {
			UserService.registerUser(user);
		} catch (AlreadyRegisteredUser e) {
			Messages.error(e.getMessageKey(), e.getParams());
			return invalidForm(views.html.register.render(filledForm));
		}

		// Automatically log user in.
		SessionUtil.setUser(user);

		// Redirect to home page.
		info(ControllerMessage.REGISTRATION_THANKS, user.getDisplay());
		return redirect(routes.Application.index());
	}

	private static User getUserFromRegistration(Registration registration) {
		User user = new User();
		user.registered = true;
		user.username = registration.username;
		user.email = registration.email;
		user.passwordHash = PasswordUtil.hashPassword(registration.username,
				registration.password);
		return user;
	}

	@Transactional
	public static Result login() {
		return ok(views.html.login.render(FORM_LOGIN));
	}

	@Transactional
	public static Result authenticate() {
		Form<Login> formLogin = FORM_LOGIN.bindFromRequest();
		if (formLogin.hasErrors()) {
			return invalidForm(views.html.login.render(formLogin));
		}

		Login login = formLogin.get();
		Option<User> optUser = UserService.authenticate(login.username,
				login.password);

		if (optUser.isEmpty()) {
			formLogin.reject("username", "User name and password do not match");
			formLogin.reject("password", "");
			return invalidForm(views.html.login.render(formLogin));
		}

		User user = optUser.get();
		SessionUtil.setUser(user);

		// Redirect to main page.
		info(ControllerMessage.APPLICATION_WELCOME, user.getDisplay());
		return redirect(routes.Application.index());
	}

	@Transactional
	public static Result logout() {
		SessionUtil.clear();
		return redirect(routes.Application.index());
	}
}
