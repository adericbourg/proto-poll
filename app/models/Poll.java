package models;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.Valid;

import play.data.validation.Constraints.Required;
import play.db.ebean.Model;

/**
 * Poll.
 * 
 * @author adericbourg
 * 
 */
@Entity
@Table(name = "POLL")
public class Poll extends Model {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ID")
	public Long id;

	@Required
	@Column(name = "TITLE", nullable = false)
	public String title;

	@Column(name = "DESCRIPTION")
	public String description;

	@Valid
	@OneToMany(cascade = CascadeType.ALL)
	public List<Choice> choices;

	@OneToMany(cascade = CascadeType.ALL)
	public List<Answer> answers;
}
