package models;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
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
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "poll_id",
		"user_id" }))
public class Answer extends Model {
	@Id
	public Long id;

	@ManyToOne
	public User user;

	@ManyToOne
	public Poll poll;

	@OneToMany(cascade = CascadeType.ALL)
	public List<AnswerDetail> details;
}
