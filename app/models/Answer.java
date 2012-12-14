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
@Table(name = "ANSWER", uniqueConstraints = @UniqueConstraint(columnNames = {
		"POLL_ID", "USER_ID" }))
public class Answer extends Model {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ID")
	public Long id;

	@ManyToOne
	@JoinColumn(name = "USER_ID")
	public User user;

	@ManyToOne
	@JoinColumn(name = "POLL_ID")
	public Poll poll;

	@OneToMany(cascade = CascadeType.ALL)
	public List<AnswerDetail> details;
}
