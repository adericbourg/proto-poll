package services;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Locale;

import models.User;

import org.junit.Test;

import play.libs.F.Option;
import services.exception.poll.NoAuthenticatedUserInSessionException;
import services.exception.user.AlreadyRegisteredUser;
import util.security.CurrentUser;
import util.security.PasswordUtil;

public class UserServiceTest extends ProtoPollTest {

	private static final String DUMMY_USERNAME = "tst-dmy-name";
	private static final String DUMMY_PASSWORD = "tst-dmy-name";
	private static final String DUMMY_PASSWORD_HASHED = PasswordUtil
			.hashPassword(DUMMY_USERNAME, DUMMY_PASSWORD);
	private static final String DUMMY_EMAIL = "test@localhost.net";

	@Test
	public void testInstanciation() throws Exception {
		for (Constructor<?> constructor : UserService.class
				.getDeclaredConstructors()) {
			constructor.setAccessible(true);
			try {
				constructor.newInstance();
			} catch (InvocationTargetException e) {
				assertTrue(e.getCause() instanceof AssertionError);
			}
		}
	}

	@Test
	public void testRegisterAnonymousUser() {
		// Act.
		User user = UserService.registerAnonymousUser(DUMMY_USERNAME);

		// Assert.
		assertNotNull(user.id);
		assertEquals(DUMMY_USERNAME, user.username);
		assertFalse(user.registered);
	}

	@Test
	public void testRegisterMultipleAnonymousUserSameUsername() {
		// Act.
		User user1 = UserService.registerAnonymousUser(DUMMY_USERNAME);
		User user2 = UserService.registerAnonymousUser(DUMMY_USERNAME);

		// Assert.
		assertNotNull(user1.id);
		assertNotNull(user2.id);
		assertFalse(user1.id.equals(user2.id));
		assertEquals(DUMMY_USERNAME, user1.username);
		assertEquals(DUMMY_USERNAME, user2.username);
	}

	@Test
	public void testGetUser() {
		// Prepare.
		User user = new User();
		user.username = DUMMY_USERNAME;
		UserService.registerUser(user);

		// Act.
		Option<User> loadedUser = UserService.getUser(user.id);

		// Assert.
		assertTrue(loadedUser.isDefined());
		assertEquals(user.id, loadedUser.get().id);
	}

	@Test
	public void testGetUserNonExisting() {
		// Act.
		Option<User> loadedUser = UserService.getUser(-1L);

		// Assert.
		assertFalse(loadedUser.isDefined());
	}

	@Test
	public void testFindByUsernameRegisteredUser() {
		// Prepare.
		User user = new User();
		user.username = DUMMY_USERNAME;
		UserService.registerUser(user);

		// Act.
		Option<User> foundUser = UserService.findByUsername(user.username);

		// Assert.
		assertNotNull(foundUser);
		assertTrue(foundUser.isDefined());
		assertEquals(DUMMY_USERNAME, foundUser.get().username);
		assertEquals(user.id, foundUser.get().id);
	}

	@Test
	public void testFindByUsernameAnonymousUser() {
		// Prepare.
		User user = UserService.registerAnonymousUser(DUMMY_USERNAME);

		// Act.
		Option<User> foundUser = UserService.findByUsername(user.username);

		// Assert.
		assertNotNull(foundUser);
		assertFalse(foundUser.isDefined());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAlreadySavedUser() {
		// Prepare.
		User user = new User();
		user.username = DUMMY_USERNAME;
		UserService.registerUser(user);

		// Act.
		UserService.registerUser(user);
	}

	@Test(expected = AlreadyRegisteredUser.class)
	public void testAlreadyRegisteredUsername() {
		// Prepare.
		User user = new User();
		user.username = DUMMY_USERNAME;
		UserService.registerUser(user);

		User duplicateUser = new User();
		duplicateUser.username = DUMMY_USERNAME;

		// Act.
		UserService.registerUser(duplicateUser);
	}

	@Test
	public void testAuthenticateUser() {
		// Prepare.
		User user = new User();
		user.username = DUMMY_USERNAME;
		user.passwordHash = DUMMY_PASSWORD_HASHED;
		UserService.registerUser(user);

		// Act.
		Option<User> authentifiedUser = UserService.authenticate(
				DUMMY_USERNAME, DUMMY_PASSWORD);

		// Assert.
		assertNotNull(authentifiedUser);
		assertTrue(authentifiedUser.isDefined());
		assertEquals(user.id, authentifiedUser.get().id);
	}

	@Test
	public void testAuthenticateNonExistentUser() {
		// Act.
		Option<User> user = UserService.authenticate(DUMMY_USERNAME,
				DUMMY_PASSWORD);

		// Assert.
		assertNotNull(user);
		assertFalse(user.isDefined());
	}

	@Test(expected = NoAuthenticatedUserInSessionException.class)
	public void testtestUpdateUserProfileNotAuthentified() {
		UserService.updateUserProfile(new User());
	}

	@Test
	public void testUpdateUserProfile() {
		// Prepare.
		User initUser = new User();
		initUser.username = DUMMY_USERNAME;
		initUser.passwordHash = DUMMY_PASSWORD_HASHED;
		initUser.email = DUMMY_EMAIL;
		initUser.avatarEmail = "avatar" + DUMMY_EMAIL;
		initUser.preferredLocale = Locale.CANADA;
		UserService.registerUser(initUser);

		CurrentUser.setUser(initUser);

		User modifiedUser = UserService.findByUsername(initUser.username).get();
		modifiedUser.preferredLocale = Locale.FRANCE;
		modifiedUser.email = DUMMY_EMAIL + "mod";
		modifiedUser.avatarEmail = "avatar" + DUMMY_EMAIL + "mod";
		modifiedUser.displayName = initUser.displayName + "mod";
		modifiedUser.id = -1L;

		// Act.
		UserService.updateUserProfile(modifiedUser);

		// Assert.
		User sessionUser = CurrentUser.currentUser().get();
		assertEquals(initUser.id, sessionUser.id);
		assertEquals(initUser.registered, sessionUser.registered);
		assertEquals(initUser.username, sessionUser.username);
		assertEquals(initUser.passwordHash, sessionUser.passwordHash);
		assertEquals(initUser.email, initUser.email);
		assertEquals(modifiedUser.displayName, sessionUser.displayName);
		assertEquals(modifiedUser.preferredLocale, sessionUser.preferredLocale);
		assertEquals(modifiedUser.avatarEmail, sessionUser.avatarEmail);

		User loadedUser = UserService.findByUsername(initUser.username).get();
		assertEquals(initUser.id, loadedUser.id);
		assertEquals(initUser.registered, loadedUser.registered);
		assertEquals(initUser.username, loadedUser.username);
		assertEquals(initUser.passwordHash, loadedUser.passwordHash);
		assertEquals(initUser.email, loadedUser.email);
		assertEquals(modifiedUser.displayName, loadedUser.displayName);
		assertEquals(modifiedUser.preferredLocale, loadedUser.preferredLocale);
		assertEquals(modifiedUser.avatarEmail, loadedUser.avatarEmail);
	}

	@Test
	public void testUpdateUserPassword() {
		// Prepare.
		User user = new User();
		user.username = DUMMY_USERNAME;
		user.passwordHash = DUMMY_PASSWORD_HASHED;
		UserService.registerUser(user);

		CurrentUser.setUser(UserService.authenticate(DUMMY_USERNAME,
				DUMMY_PASSWORD).get());

		String newPassword = DUMMY_PASSWORD + DUMMY_PASSWORD;

		// Act.
		boolean result = UserService.updateUserPassword(DUMMY_PASSWORD,
				newPassword);

		// Assert.
		User loadedUser = UserService.findByUsername(DUMMY_USERNAME).get();
		assertTrue(result);
		assertFalse(DUMMY_PASSWORD_HASHED.equals(loadedUser.passwordHash));
		assertEquals(PasswordUtil.hashPassword(DUMMY_USERNAME, newPassword),
				loadedUser.passwordHash);
	}

	@Test
	public void testUpdateUserPasswordWrongOldPassword() {
		// Prepare.
		User user = new User();
		user.username = DUMMY_USERNAME;
		user.passwordHash = DUMMY_PASSWORD_HASHED;
		UserService.registerUser(user);

		CurrentUser.setUser(UserService.authenticate(DUMMY_USERNAME,
				DUMMY_PASSWORD).get());

		String newPassword = DUMMY_PASSWORD + DUMMY_PASSWORD;

		// Act.
		boolean result = UserService.updateUserPassword(newPassword,
				DUMMY_PASSWORD);

		// Assert.
		User loadedUser = UserService.findByUsername(DUMMY_USERNAME).get();
		assertFalse(result);
		assertEquals(DUMMY_PASSWORD_HASHED, loadedUser.passwordHash);
		assertFalse(PasswordUtil.hashPassword(DUMMY_USERNAME, newPassword)
				.equals(loadedUser.passwordHash));
	}

	@Test(expected = NoAuthenticatedUserInSessionException.class)
	public void testUpdateUserPasswordNotAuthentified() {
		UserService.updateUserPassword(null, null);
	}
}
