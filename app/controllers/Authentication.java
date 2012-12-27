package controllers;

import models.User;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import services.UserService;
import ui.tags.Messages;
import util.security.PasswordUtil;
import util.security.SessionUtil;

public class Authentication extends Controller {

	public static class Registration {
		public String username;
		public String password;
		public String passwordConfirm;
		public String email;
	}

	public static class Login {
		public String username;
		public String password;
	}

	private static Form<Registration> FORM_REGISTRATION = form(Registration.class);
	private static Form<Login> FORM_LOGIN = form(Login.class);

	public static Result register() {
		return ok(views.html.register.render(FORM_REGISTRATION));
	}

	public static Result saveRegistration() {
		Form<Registration> filledForm = FORM_REGISTRATION.bindFromRequest();
		if (filledForm.hasErrors()) {
			return badRequest(views.html.register.render(filledForm));
		}

		// Register user.
		Registration registration = filledForm.get();
		if (registration.password == null
				|| registration.passwordConfirm == null) {
			Messages.pushError("Password is mandatory. Think about your security.");
		}
		if (!registration.password.equals(registration.passwordConfirm)) {
			Messages.pushError("Passwords do not match.");
		}
		User user = getUserFromRegistration(registration);
		UserService.registerUser(user);

		// Redirect to login page.
		return login();
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

	public static Result login() {
		return ok(views.html.login.render(FORM_LOGIN));
	}

	public static Result authenticate() {
		Form<Login> formLogin = FORM_LOGIN.bindFromRequest();
		if (formLogin.hasErrors()) {
			return badRequest(views.html.login.render(formLogin));
		}

		Login login = formLogin.get();
		User user = UserService.authenticate(login.username, login.password);
		if (user == null) {
			Messages.pushError("The user name or password you provided is invalid.");
			return badRequest(views.html.login.render(formLogin));
		}
		SessionUtil.setUser(user);

		// Redirect to main page.
		Messages.pushInfo("Welcome " + user.username + "!");
		return Application.index();
	}

	public static Result logout() {
		SessionUtil.removeCurrentUser();
		return Application.index();

	}
}
