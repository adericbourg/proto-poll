package models;

import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.Valid;

import play.db.ebean.Model;
import util.binders.UuidBinder;

/**
 * Poll.
 * 
 * @author adericbourg
 * 
 */
@Entity
@Table(name = "question")
public class Question extends Model {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id")
	public Long id;

	@Valid
	@OneToMany(cascade = CascadeType.ALL)
	@OrderBy("sortOrder ASC")
	public List<QuestionChoice> choices;

	@OneToMany(cascade = CascadeType.ALL)
	public List<QuestionAnswer> answers;

	@OneToOne(mappedBy = "question")
	public Poll poll;

	// ---

	@Transient
	public UUID uuid() {
		return poll == null ? null : poll.uuid;
	}

	@Transient
	public UuidBinder bindId() {
		return poll == null ? null : poll.bindId();
	}

	// ---

	@Transient
	public boolean hasSingleChoice() {
		return choices.size() <= 1;
	}
}
