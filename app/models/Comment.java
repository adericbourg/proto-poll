package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.joda.time.DateTime;

import play.db.ebean.Model;

@Entity
@Table(name = "comment")
public class Comment extends Model {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id")
	public Long id;

	@ManyToOne
	@JoinColumn(name = "user_id")
	public User user;

	@Column(name = "submit_date", nullable = false)
	public DateTime submitDate;

	@Column(name = "last_edit_date")
	public DateTime lastEditDate;

	@Column(name = "content", nullable = false)
	public String content;

	@ManyToOne
	public Poll poll;
}
