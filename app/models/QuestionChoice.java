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
@Table(name = "question_choice", uniqueConstraints = @UniqueConstraint(columnNames = {
		"label", "question_id" }))
public class QuestionChoice extends Model {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id")
	public Long id;

	@Required(message = "question_choice.label.mandatory")
	@Column(name = "label", nullable = false)
	public String label;

	@Required
	@Column(name = "sort_order", nullable = false)
	public Integer sortOrder;

	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "question_id")
	public Question question;
}
