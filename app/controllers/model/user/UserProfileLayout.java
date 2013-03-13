package controllers.model.user;

import models.User;

public class UserProfileLayout {

	public UserProfileLayout(User user) {
		super();

		boolean isLocalUser = user.isLocalUser();

		// Tabs.
		displayTabPasswordChange = isLocalUser;

		// User informations.
		canChangeFieldEmailAddress = isLocalUser;
	}

	// Tabs.
	public final boolean displayTabInformations = true;
	public final boolean displayTabPasswordChange;

	// User informations fields.
	public final boolean canChangeFieldEmailAddress;
}
