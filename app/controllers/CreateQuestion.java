package controllers;

import static ui.tags.Messages.info;
import static ui.tags.MessagesHelper.invalidForm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import models.Choice;
import models.Question;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import services.QuestionService;
import views.html.newQuestion;
import views.html.addChoices;
import views.html.questionCreated;
import com.google.common.base.Strings;

/**
 * Managing poll controller. Create, edit, add choices.
 * 
 * @author adericbourg
 * 
 */
public class CreateQuestion extends Controller {
	private static final Form<Question> QUESTION_FORM = form(Question.class);
	private static final Pattern CHOICE_ORDER = Pattern
			.compile("choice\\[(.*?)\\]");

	public static Result newQuestion() {
		return ok(newQuestion.render(QUESTION_FORM));
	}

	public static Result createQuestion() {
		Form<Question> filledForm = QUESTION_FORM.bindFromRequest();

		if (filledForm.hasErrors()) {
			// Error handling.
			return invalidForm(newQuestion.render(QUESTION_FORM));
		}

		Question question = filledForm.get();
		Long questionId = QuestionService.createQuestion(question);
		return setChoices(questionId);
	}

	public static Result setChoices(Long questionId) {
		Question poll = QuestionService.getQuestion(questionId);
		return ok(addChoices.render(poll, QUESTION_FORM));
	}

	public static Result saveChoices(Long questionId) {
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
		QuestionService.saveChoices(questionId, choices);
		info("Question successfully created.");
		return confirmQuestionCreation(questionId);

	}

	public static Result confirmQuestionCreation(Long questionId) {
		Question question = QuestionService.getQuestion(questionId);
		return ok(questionCreated.render(question));
	}

	private static int extractSortOrder(String key) {
		Matcher m = CHOICE_ORDER.matcher(key);
		while (m.find()) {
			return Integer.parseInt(m.group(1));
		}
		return 0;
	}
}
