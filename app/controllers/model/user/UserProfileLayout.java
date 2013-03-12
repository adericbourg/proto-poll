package controllers.model.user;

import models.User;
import models.reference.ThirdPartySource;

public class UserProfileLayout {

	public UserProfileLayout(User user) {
		super();

		boolean isThirdPartyUser = user.thirdPartySource == null
				|| ThirdPartySource.NONE.equals(user.thirdPartySource);

		// Tabs.
		displayTabPasswordChange = isThirdPartyUser;

		// User informations.
		canChangeFieldEmailAddress = !isThirdPartyUser;
	}

	// Tabs.
	public final boolean displayTabInformations = true;
	public final boolean displayTabPasswordChange;

	// User informations fields.
	public final boolean canChangeFieldEmailAddress;
}
