package models;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.joda.time.LocalDate;

import play.data.validation.Constraints.Required;
import play.db.ebean.Model;

@Entity
@Table(name = "event_choice", uniqueConstraints = @UniqueConstraint(columnNames = {
		"date", "event_id" }))
public class EventChoice extends Model {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id")
	public Long id;

	@Required(message = "Enter date")
	@Column(name = "date", nullable = false)
	public LocalDate date;

	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "event_id")
	public Event event;
}
