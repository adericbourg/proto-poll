package models;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import play.data.validation.Constraints.Required;
import play.db.ebean.Model;

/**
 * Selectable choice in a poll.
 * 
 * @author adericbourg
 * 
 */
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "label", "poll_id" }))
public class Choice extends Model {

	@Id
	public Long id;
	@Required
	@Column(nullable = false)
	public String label;
	@Required
	@Column(nullable = false)
	public Integer sortOrder;

	@ManyToOne(cascade = CascadeType.REFRESH)
	public Poll poll;
}
