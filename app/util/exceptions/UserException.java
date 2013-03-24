package util.exceptions;

import util.user.message.MessageKey;
import util.user.message.Messages;

/**
 * Exception thrown from user bad usage.
 * 
 * @author adericbourg
 * 
 */
public abstract class UserException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private final MessageKey messageKey;
	private final Object[] params;

	public UserException() {
		super();
		messageKey = null;
		params = null;
	}

	public UserException(MessageKey messageKey, Object... params) {
		super(Messages.resolve(messageKey, params));
		this.messageKey = messageKey;
		this.params = params;
	}

	public final MessageKey getMessageKey() {
		return messageKey;
	}

	public Object[] getParams() {
		return params;
	}
}
