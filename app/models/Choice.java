package models;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
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
@Table(name = "CHOICE", uniqueConstraints = @UniqueConstraint(columnNames = {
		"LABEL", "POLL_ID" }))
public class Choice extends Model {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ID")
	public Long id;

	@Required
	@Column(name = "LABEL", nullable = false)
	public String label;

	@Required
	@Column(name = "SORT_ORDER", nullable = false)
	public Integer sortOrder;

	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "POLL_ID")
	public Poll poll;
}
