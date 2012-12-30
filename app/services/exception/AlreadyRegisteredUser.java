package services.exception;

/**
 * Exception thrown when trying to register a user whose username is already
 * used by another registered user.
 * 
 * @author adericbourg
 * 
 */
public class AlreadyRegisteredUser extends RuntimeException {

	private static final long serialVersionUID = 1L;

	/**
	 * @param username
	 *            User name.
	 */
	public AlreadyRegisteredUser(String username) {
		super("User with that username already exists: " + username);
	}
}
