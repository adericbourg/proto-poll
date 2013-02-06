package controllers;

import static play.data.Form.form;
import static ui.tags.Messages.info;
import static ui.tags.MessagesHelper.invalidForm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import models.Question;
import models.QuestionChoice;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import services.PollService;
import services.QuestionService;
import util.binders.UuidBinder;
import views.html.question.questionAddChoices;
import views.html.question.questionNew;

import com.google.common.base.Strings;

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

	public static Result newQuestion() {
		return ok(questionNew.render(QUESTION_FORM));
	}

	public static Result createQuestion() {
		Form<Question> filledForm = QUESTION_FORM.bindFromRequest();

		if (filledForm.hasErrors()) {
			// Error handling.
			return invalidForm(questionNew.render(filledForm));
		}

		Question question = filledForm.get();
		question = QuestionService.createQuestion(question);
		return redirect(routes.CreateQuestion
				.setChoices(question.poll.bindId()));
	}

	public static Result setChoices(UuidBinder uuid) {
		Question question = PollService.getQuestion(uuid.uuid());
		return ok(questionAddChoices.render(question, QUESTION_FORM));
	}

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
		QuestionService.saveChoices(uuid.uuid(), choices);
		info("Question successfully created.");
		return redirect(routes.CreatePoll.confirmCreation(uuid));
	}

	private static int extractSortOrder(String key) {
		Matcher m = CHOICE_ORDER.matcher(key);
		while (m.find()) {
			return Integer.parseInt(m.group(1));
		}
		return 0;
	}
}
