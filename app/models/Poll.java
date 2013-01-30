package models;

import java.util.List;

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

import play.db.ebean.Model;

@Entity
@Table(name = "poll")
public class Poll extends Model {

	private static final long serialVersionUID = 1L;

	public Poll(Event event) {
		this(null, event);
	}

	public Poll(Question question) {
		this(question, null);
	}

	private Poll(Question question, Event event) {
		super();
		this.question = question;
		this.event = event;
	}

	@Id
	@Column(name = "id")
	public Long id;

	@Column(name = "creation_date", nullable = false)
	public DateTime creationDate;

	@OneToOne
	public final Question question;

	@OneToOne
	public final Event event;

	@ManyToOne(optional = true)
	public User userCreator;

	@OneToMany
	@OrderBy("submitDate ASC")
	public List<Comment> comments;

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
		if (isEvent()) {
			return event.hasDescription();
		} else if (isQuestion()) {
			return question.hasDescription();
		} else {
			return false;
		}
	}

	@Transient
	public String getDescription() {
		if (isEvent()) {
			return event.description;
		} else if (isQuestion()) {
			return question.description;
		}
		return null;
	}

	@Transient
	public String getTitle() {
		if (isEvent()) {
			return event.title;
		} else if (isQuestion()) {
			return question.title;
		}
		return null;
	}
}
