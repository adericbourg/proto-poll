package models;

import java.util.Locale;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import models.reference.ThirdPartySource;
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

	@Column(name = "avatar_email")
	public String avatarEmail;

	@Required
	@Column(name = "is_registered", nullable = false)
	public Boolean registered = false;

	@Required
	@Column(name = "third_party_source", nullable = false)
	public ThirdPartySource thirdPartySource = ThirdPartySource.NONE;

	@Column(name = "third_party_identifier")
	public String thirdPartyIdentifier;

	@Column(name = "locale")
	public Locale preferredLocale;

	@Transient
	public String getDisplay() {
		return Strings.isNullOrEmpty(displayName) ? username : displayName;
	}
}
