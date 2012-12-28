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
@Table(name = "choice", uniqueConstraints = @UniqueConstraint(columnNames = {
		"label", "poll_id" }))
public class Choice extends Model {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id")
	public Long id;

	@Required(message = "Enter label for this choice")
	@Column(name = "label", nullable = false)
	public String label;

	@Required
	@Column(name = "sort_order", nullable = false)
	public Integer sortOrder;

	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "poll_id")
	public Poll poll;
}
