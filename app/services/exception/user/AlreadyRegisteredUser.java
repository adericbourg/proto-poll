package services.exception.user;

import services.messages.ServiceMessage;
import util.exceptions.UserException;

/**
 * Exception thrown when trying to register a user whose username is already
 * used by another registered user.
 * 
 * @author adericbourg
 * 
 */
public class AlreadyRegisteredUser extends UserException {

	private static final long serialVersionUID = 1L;

	/**
	 * @param username User name.
	 */
	public AlreadyRegisteredUser(String username) {
		super(ServiceMessage.ALREADY_REGISTERED_USER, username);
	}
}
