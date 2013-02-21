package models;

import java.util.List;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.joda.time.DateTime;

import play.data.validation.Constraints.Required;
import play.db.ebean.Model;
import util.binders.UuidBinder;

import com.google.common.base.Strings;

@Entity
@Table(name = "poll")
public class Poll extends Model {

	private static final long serialVersionUID = 1L;

	private Poll(Event event) {
		this(null, event);
	}

	private Poll(Question question) {
		this(question, null);
	}

	private Poll(Question question, Event event) {
		super();
		this.question = question;
		this.event = event;
	}

	@Id
	@Column(name = "uuid")
	public UUID uuid;

	@Required(message = "poll.title.mandatory")
	@Column(name = "title", nullable = false)
	public String title;

	@Column(name = "description")
	public String description;

	@Column(name = "single_answer", nullable = false)
	public boolean singleAnswer = false;

	@Column(name = "creation_date", nullable = false)
	public DateTime creationDate;

	@ManyToOne(optional = true)
	public User userCreator;

	@OneToOne
	public final Question question;

	@OneToOne
	public final Event event;

	@OneToMany
	@OrderBy("submitDate ASC")
	public List<Comment> comments;

	// ---

	public static Poll initQuestion() {
		return new Poll(new Question());
	}

	public static Poll initEvent() {
		return new Poll(new Event());
	}

	// ---

	@Transient
	public UuidBinder bindId() {
		return UuidBinder.create(uuid);
	}

	// ---

	@Transient
	public final boolean hasRegisteredCreator() {
		return userCreator != null;
	}

	@Transient
	public boolean isEvent() {
		return event != null;
	}

	@Transient
	public boolean isQuestion() {
		return question != null;
	}

	@Transient
	public boolean hasDescription() {
		return !Strings.isNullOrEmpty(description);
	}

	@Transient
	public boolean hasSingleChoice() {
		if (isEvent()) {
			return event.hasSingleChoice();
		}
		if (isQuestion()) {
			return question.hasSingleChoice();
		}
		return false;
	}
}
