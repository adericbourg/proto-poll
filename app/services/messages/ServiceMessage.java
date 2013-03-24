package services.messages;

import util.user.message.MessageKey;

public enum ServiceMessage implements MessageKey {
	/***/
	LOGIN_WRONG_OLD_PASSWORD("login.wrong_old_password"),
	/***/
	ALREADY_REGISTERED_USER("register.username_already_exists");

	private String resourceCode;

	private ServiceMessage(String resourceCode) {
		this.resourceCode = resourceCode;
	}

	/** {@inheritDoc} */
	@Override
	public String getCode() {
		return resourceCode;
	}
}
