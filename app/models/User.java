package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import play.data.validation.Constraints.Required;
import play.db.ebean.Model;

import com.google.common.base.Strings;

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
	@Column(name = "USERNAME", nullable = false)
	public String username;

	@Column(name = "PASSWORD_HASH")
	public String passwordHash;

	@Column(name = "EMAIL")
	public String email;

	@Required
	@Column(name = "IS_REGISTERED", nullable = false)
	public Boolean registered = false;

	@Transient
	public String getDisplay() {
		if (Strings.isNullOrEmpty(email)) {
			return username;
		}
		return String.format("%s (%s)", username, email);
	}
}
