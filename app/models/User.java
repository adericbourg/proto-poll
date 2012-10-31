package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import play.data.validation.Constraints.Required;
import play.db.ebean.Model;

/**
 * Application user.
 * 
 * @author adericbourg
 * 
 */
@Entity
public class User extends Model {
	@Id
	public Long id;
	@Required
	@Column(unique = true, nullable = false)
	public String username;
	@Required
	@Column(nullable = false)
	public Boolean registered = false;
}
