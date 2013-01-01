package models;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import play.db.ebean.Model;

/**
 * Submitted answer.
 * 
 * @author adericbourg
 * 
 */
@Entity
@Table(name = "question_answer", uniqueConstraints = @UniqueConstraint(columnNames = {
		"question_id", "user_id" }))
public class QuestionAnswer extends Model {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id")
	public Long id;

	@ManyToOne
	@JoinColumn(name = "user_id")
	public User user;

	@ManyToOne
	@JoinColumn(name = "question_id")
	public Question question;

	@OneToMany(cascade = CascadeType.ALL)
	public List<QuestionAnswerDetail> details;
}
