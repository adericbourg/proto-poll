package models;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
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
public class Poll extends Model {

	@Id
	public Long id;

	@Required
	@Column(nullable = false)
	public String title;
	public String description;

	@Valid
	@OneToMany(cascade = CascadeType.ALL)
	public List<Choice> choices;

	@OneToMany(cascade = CascadeType.ALL)
	public List<Answer> answers;
}
