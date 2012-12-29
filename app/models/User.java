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
@Table(name = "user")
public class User extends Model {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id")
	public Long id;

	@Required
	@Column(name = "username", nullable = false)
	public String username;

	@Column(name = "display_name")
	public String displayName;

	@Column(name = "password_hash")
	public String passwordHash;

	@Column(name = "email")
	public String email;

	@Required
	@Column(name = "is_registered", nullable = false)
	public Boolean registered = false;

	@Transient
	public String getDisplay() {
		if (!Strings.isNullOrEmpty(displayName)) {
			return String.format("%s (%s)", displayName, username);
		}
		if (!Strings.isNullOrEmpty(email)) {
			return String.format("%s (%s)", username, email);
		}
		return username;
	}
}
