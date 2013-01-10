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
@Table(name = "answer", uniqueConstraints = @UniqueConstraint(columnNames = {
		"poll_id", "user_id" }))
public class Answer extends Model {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id")
	public Long id;

	@ManyToOne
	@JoinColumn(name = "user_id")
	public User user;

	@ManyToOne
	@JoinColumn(name = "poll_id")
	public Poll poll;

	@OneToMany(cascade = CascadeType.ALL)
	public List<AnswerDetail> details;
}
