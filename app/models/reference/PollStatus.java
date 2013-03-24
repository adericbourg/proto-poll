package models.reference;

public enum PollStatus {

	/** Poll has been created but no choice has been selected. */
	DRAFT,

	/** Poll has been created and choices have been set. */
	COMPLETE,

	/** Poll is marked as deprecated and can be deleted at any time. */
	DEPRECATED,
}
