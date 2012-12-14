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
@Table(name = "ANSWER_DETAIL", uniqueConstraints = @UniqueConstraint(columnNames = {
		"CHOICE_ID", "ANSWER_ID" }))
public class AnswerDetail extends Model {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ID")
	public Long id;

	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "CHOICE_ID")
	public Choice choice;
}
