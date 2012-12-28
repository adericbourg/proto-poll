package models;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
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
@Table(name = "poll")
public class Poll extends Model {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id")
	public Long id;

	@Required(message = "Title is mandatory")
	@Column(name = "title", nullable = false)
	public String title;

	@Column(name = "description")
	public String description;

	@Valid
	@OneToMany(cascade = CascadeType.ALL)
	public List<Choice> choices;

	@OneToMany(cascade = CascadeType.ALL)
	public List<Answer> answers;

	@ManyToOne(optional = true)
	public User userCreator;
}
