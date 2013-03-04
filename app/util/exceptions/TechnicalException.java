package util.exceptions;

public abstract class TechnicalException extends RuntimeException {

	private static final long serialVersionUID = 5015467853708401587L;

	public TechnicalException() {
		super();
	}

	public TechnicalException(String message) {
		super(message);
	}

	public TechnicalException(String message, Object... params) {
		super(String.format(message, params));
	}

	public TechnicalException(String message, Throwable e) {
		super(message, e);
	}
}
