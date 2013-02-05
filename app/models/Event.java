package models;

import java.util.List;

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

import play.data.validation.Constraints.Required;
import play.db.ebean.Model;
import util.binders.UuidBinder;

import com.google.common.base.Strings;

@Entity
@Table(name = "event")
public class Event extends Model {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id")
	public Long id;

	@Required(message = "Title is mandatory")
	@Column(name = "title", nullable = false)
	public String title;

	@Column(name = "description")
	public String description;

	@Column(name = "single_answer", nullable = false)
	public boolean singleAnswer = false;

	@Valid
	@OneToMany(cascade = CascadeType.ALL)
	@OrderBy("date ASC")
	public List<EventChoice> dates;

	@OneToMany(cascade = CascadeType.ALL)
	public List<EventAnswer> answers;

	@OneToOne(mappedBy = "event")
	public Poll poll;

	// ---

	@Transient
	public UuidBinder bindId() {
		return poll == null ? null : poll.bindId();
	}

	@Transient
	public final boolean hasDescription() {
		return !Strings.isNullOrEmpty(description);
	}

	@Transient
	public final boolean hasRegisteredCreator() {
		return poll.hasRegisteredCreator();
	}
}
