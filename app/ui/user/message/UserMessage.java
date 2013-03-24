package ui.user.message;

import util.user.message.MessageKey;

public enum UserMessage implements MessageKey {
	FIX_ERRORS("error.fix_errors_below");

	private String resourceCode;

	private UserMessage(String resourceCode) {
		this.resourceCode = resourceCode;
	}

	/** {@inheritDoc} */
	@Override
	public String getCode() {
		return resourceCode;
	}
}
