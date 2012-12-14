package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import play.data.validation.Constraints.Required;
import play.db.ebean.Model;

/**
 * Application user.
 * 
 * @author adericbourg
 * 
 */
@Entity
@Table(name = "USER")
public class User extends Model {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ID")
	public Long id;

	@Required
	@Column(name = "USERNAME", unique = true, nullable = false)
	public String username;

	@Required
	@Column(name = "IS_REGISTERED", nullable = false)
	public Boolean registered = false;
}
