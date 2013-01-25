package models;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.Valid;

import play.db.ebean.Model;

import com.google.common.base.Strings;

@Entity
@Table(name = "event")
public class Event extends Model {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id")
	public Long id;

	@Column(name = "title", nullable = false)
	public String title;

	@Column(name = "description")
	public String description;

	@Valid
	@OneToMany(cascade = CascadeType.ALL)
	@OrderBy("date ASC")
	public List<EventChoice> dates;

	@ManyToOne(optional = true)
	public User userCreator;

	// ---

	@Transient
	public final boolean hasDescription() {
		return !Strings.isNullOrEmpty(description);
	}

	@Transient
	public final boolean hasRegisteredCreator() {
		return userCreator != null;
	}

}
