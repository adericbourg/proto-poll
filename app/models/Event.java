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

@Entity
@Table(name = "event")
public class Event extends Model {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id")
	public Long id;

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
	public UUID uuid() {
		return poll == null ? null : poll.uuid;
	}

	@Transient
	public UuidBinder bindId() {
		return poll == null ? null : poll.bindId();
	}

	// --

	@Transient
	public boolean hasSingleChoice() {
		return dates.size() <= 1;
	}
}
