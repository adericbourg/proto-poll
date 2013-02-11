package controllers.message;

import util.user.message.MessageKey;

public enum ControllerMessage implements MessageKey {
	/***/
	APPLICATION_WELCOME("authentication.welcome"),
	/***/
	REGISTRATION_THANKS("registration.thanks"),
	/***/
	POLL_CHOOSE_USER_NAME("poll.answer.choose_user_name"),
	/***/
	POLL_USERNAME_ALREADY_TAKEN("poll.answer.username_already_taken"),
	/***/
	POLL_ANSWER_SUCCESS("poll.answer.thanks"),
	/***/
	POLL_DOES_NOT_EXIST("poll.does_not_exist"),
	/***/
	EVENT_SUCCESSFULLY_CREATED("event.creation.successful"),
	/***/
	QUESTION_SUCCESSFULLY_CREATED("question.creation.successful"),
	/***/
	PASSWORD_SUCCESSFULLY_CHANGED("password.change.successful"),
	/***/
	PASSWORD_CHANGE_SAME_AS_OLD("password.change.same_as_old"),
	/***/
	ACCESS_FORBIDDEN_NOT_AUTHENTIFIED("access.forbidden.not_authentified");

	private String messageKey;

	private ControllerMessage(String messageKey) {
		this.messageKey = messageKey;
	}

	/** {@inheritDoc} */
	@Override
	public String getCode() {
		return messageKey;
	}
}
