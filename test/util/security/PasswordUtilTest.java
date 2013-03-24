package util.security;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PasswordUtilTest {

	@Test
	public void testHashPassword() {
		// Prepare.
		String password = "password";
		String username = "username";

		String expectedHash = "fb25559a0542a67818bc2b76c846955df51c5919"; // sha1(username|sha1(password))

		// Act.
		String result = PasswordUtil.hashPassword(username, password);

		// Assert.
		assertEquals(expectedHash, result);
	}

	@Test(expected = NullPointerException.class)
	public void testHashNullPassword() {
		PasswordUtil.hashPassword("", null);
	}

	@Test
	public void testHashNullUsername() {
		// Prepare.
		String password = "password";

		String expectedHash = "048efa83591a4f983aec474843d6ed842c05be57"; // sha1(null|sha1(password))

		// Act.
		String result = PasswordUtil.hashPassword(null, password);

		// Assert.
		assertEquals(expectedHash, result);
	}
}
