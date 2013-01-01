package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import play.db.ebean.Model;

import com.google.common.base.Strings;

@Entity
@Table(name = "event")
public class Event extends Model {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id")
	public Long id;

	@Column(name = "label", nullable = false)
	public String label;

	@Column(name = "description")
	public String description;

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
