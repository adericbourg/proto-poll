package models;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import play.db.ebean.Model;

/**
 * Detail of a sumitted answer: links the answer to one of the selected choices.
 * 
 * @author adericbourg
 * 
 */
@Entity
@Table(name = "answer_detail", uniqueConstraints = @UniqueConstraint(columnNames = {
		"choice_id", "answer_id" }))
public class AnswerDetail extends Model {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id")
	public Long id;

	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "choice_id")
	public Choice choice;
}
