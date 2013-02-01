package util.binders;

import java.util.UUID;

import play.mvc.PathBindable;

public class UuidBinder implements PathBindable<UuidBinder> {

	private UUID uuid;

	public static UuidBinder create(final UUID uuid) {
		final UuidBinder u = new UuidBinder();
		u.uuid = uuid;
		return u;
	}

	/** {@inheritDoc} */
	@Override
	public UuidBinder bind(String key, String txt) {
		uuid = UUID.fromString(txt);
		return this;
	}

	/** {@inheritDoc} */
	@Override
	public String unbind(String key) {
		return uuid.toString();
	}

	/** {@inheritDoc} */
	@Override
	public String javascriptUnbind() {
		return null;
	}

	public UUID uuid() {
		return uuid;
	}
}
