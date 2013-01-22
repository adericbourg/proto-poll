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

@Entity
@Table(name = "event_answer_detail", uniqueConstraints = @UniqueConstraint(columnNames = {
		"event_choice_id", "event_answer_id" }))
public class EventAnswerDetail extends Model {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id")
	public Long id;

	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "event_choice_id")
	public EventChoice choice;

}
