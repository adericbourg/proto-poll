package controllers;

import static play.data.Form.form;
import static util.user.message.Messages.error;
import static util.user.message.Messages.info;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import models.Poll;
import models.Question;
import models.QuestionChoice;
import play.api.templates.Html;
import play.data.DynamicForm;
import play.data.Form;
import play.db.ebean.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import services.PollService;
import services.QuestionService;
import services.exception.poll.NoChoiceException;
import util.binders.UuidBinder;
import views.html.question.questionAddChoices;

import com.google.common.base.Strings;

import controllers.message.ControllerMessage;

/**
 * Managing poll controller. Create, edit, add choices.
 * 
 * @author adericbourg
 * 
 */
public class CreateQuestion extends Controller {
	private static final Form<Question> QUESTION_FORM = form(Question.class)
			.fill(new Question());
	private static final Pattern CHOICE_ORDER = Pattern
			.compile("choice\\[(.*?)\\]");

	@Transactional
	public static Result setChoices(UuidBinder uuid) {
		return ok(prepareChoiceData(uuid));
	}

	@Transactional
	public static Result saveChoices(UuidBinder uuid) {
		DynamicForm dynamicForm = form().bindFromRequest();
		QuestionChoice choice;
		List<QuestionChoice> choices = new ArrayList<QuestionChoice>();
		String value;
		for (Entry<String, String> entry : dynamicForm.data().entrySet()) {
			value = entry.getValue();
			if (!Strings.isNullOrEmpty(value)) {
				choice = new QuestionChoice();
				choice.label = entry.getValue();
				choice.sortOrder = extractSortOrder(entry.getKey());
				choices.add(choice);
			}
		}
		try {
			QuestionService.saveChoices(uuid.uuid(), choices);
			info(ControllerMessage.QUESTION_SUCCESSFULLY_CREATED);
			return redirect(routes.CreatePoll.confirmCreation(uuid));
		} catch (NoChoiceException e) {
			error(ControllerMessage.NO_CHOICE_ON_POLL);
			return badRequest(prepareChoiceData(uuid));
		}
	}

	private static Html prepareChoiceData(UuidBinder uuid) {
		Poll poll = PollService.getPoll(uuid.uuid());
		return questionAddChoices.render(poll, QUESTION_FORM);
	}

	private static int extractSortOrder(String key) {
		Matcher m = CHOICE_ORDER.matcher(key);
		while (m.find()) {
			return Integer.parseInt(m.group(1));
		}
		return 0;
	}
}
