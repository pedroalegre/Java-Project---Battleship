package edu.example;

/**
 * Created by Pedro on 08-Dec-16.
 */

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Entity
public class Game {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	private Date creationDate;

	public Game() {}

	public Game(long time) {
		this.creationDate = new Date();
		Date newDate = Date.from(creationDate.toInstant().plusSeconds(time));
		creationDate = newDate;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date date) {
		this.creationDate = date;
	}

}
