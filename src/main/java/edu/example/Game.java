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
	private String creationDate;

	public Game(String date) {
		creationDate = date;
	}

	public String getCreationDate() { return creationDate; }

	public void setCreationDate(String creationDate) { this.creationDate = creationDate; }

}
