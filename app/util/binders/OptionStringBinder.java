package util.binders;

import play.mvc.PathBindable;
import scala.Option;

public class OptionStringBinder implements PathBindable<OptionStringBinder> {

	private Option<String> option;

	public static OptionStringBinder create(Option<String> option) {
		OptionStringBinder binder = new OptionStringBinder();
		binder.option = option;
		return binder;
	}

	@Override
	public OptionStringBinder bind(String key, String txt) {
		option = Option.apply(txt);
		return this;
	}

	@Override
	public String unbind(String key) {
		return option.isDefined() ? option.get() : null;
	}

	@Override
	public String javascriptUnbind() {
		return null;
	}

	public Option<String> option() {
		return option;
	}
}
