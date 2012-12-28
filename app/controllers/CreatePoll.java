package controllers;

import static ui.tags.Messages.info;
import static ui.tags.MessagesHelper.invalidForm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import models.Choice;
import models.Poll;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import services.PollService;
import views.html.addChoices;
import views.html.index;
import views.html.newPoll;

import com.google.common.base.Strings;

/**
 * Managing poll controller. Create, edit, add choices.
 * 
 * @author adericbourg
 * 
 */
public class CreatePoll extends Controller {
	private static final Form<Poll> POLL_FORM = form(Poll.class);
	private static final Pattern CHOICE_ORDER = Pattern
			.compile("choice\\[(.*?)\\]");

	public static Result newPoll() {
		return ok(newPoll.render(POLL_FORM));
	}

	public static Result createPoll() {
		Form<Poll> filledForm = POLL_FORM.bindFromRequest();

		if (filledForm.hasErrors()) {
			// Error handling.
			return invalidForm(newPoll.render(POLL_FORM));
		}

		Poll poll = filledForm.get();
		Long pollId = PollService.createPoll(poll);
		return setChoices(pollId);
	}

	public static Result setChoices(Long pollId) {
		Poll poll = PollService.getPoll(pollId);
		return ok(addChoices.render(poll, POLL_FORM));
	}

	public static Result saveChoices(Long pollId) {
		DynamicForm dynamicForm = form().bindFromRequest();
		Choice choice;
		List<Choice> choices = new ArrayList<Choice>();
		String value;
		for (Entry<String, String> entry : dynamicForm.data().entrySet()) {
			value = entry.getValue();
			if (!Strings.isNullOrEmpty(value)) {
				choice = new Choice();
				choice.label = entry.getValue();
				choice.sortOrder = extractSortOrder(entry.getKey());
				choices.add(choice);
			}
		}
		PollService.saveChoices(pollId, choices);
		info("Poll successfully created.");
		return ok(index.render());

	}

	private static int extractSortOrder(String key) {
		Matcher m = CHOICE_ORDER.matcher(key);
		while (m.find()) {
			return Integer.parseInt(m.group(1));
		}
		return 0;
	}
}
