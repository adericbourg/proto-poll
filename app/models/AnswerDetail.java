package models;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
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
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "choice_id",
		"answer_id" }))
public class AnswerDetail extends Model {
	@Id
	public Long id;
	@ManyToOne(cascade = CascadeType.REFRESH)
	public Choice choice;
}
