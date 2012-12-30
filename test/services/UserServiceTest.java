package services;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import models.User;

import org.junit.Test;

public class UserServiceTest extends ProtoPollTest {

	private static final String DUMMY_USERNAME = "tst-dmy-name";

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
	public void testFindByUsernameRegisteredUser() {
		// Prepare.
		User user = new User();
		user.username = DUMMY_USERNAME;
		UserService.registerUser(user);

		// Act.
		User foundUser = UserService.findByUsername(user.username);

		// Assert.
		assertNotNull(foundUser);
		assertEquals(DUMMY_USERNAME, foundUser.username);
		assertEquals(user.id, foundUser.id);
	}

	@Test
	public void testFindByUsernameAnonymousUser() {
		// Prepare.
		User user = UserService.registerAnonymousUser(DUMMY_USERNAME);

		// Act.
		User foundUser = UserService.findByUsername(user.username);

		// Assert.
		assertNull(foundUser);
	}
}
