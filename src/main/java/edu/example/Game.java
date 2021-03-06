package edu.example;

/**
 * Created by Pedro on 08-Dec-16.
 */

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.Date;
import java.util.Set;

@Entity
public class Game {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	private Date creationDate;
	private boolean finished;

	@OneToMany(mappedBy = "game", fetch = FetchType.EAGER)
	Set<GamePlayer> games;

	@OneToMany(mappedBy = "game", fetch = FetchType.EAGER)
	Set<GameScore> gameScores;

	public Game() {}

	public Game(long time) {
		this.creationDate = new Date();
		Date newDate = Date.from(creationDate.toInstant().plusSeconds(time));
		creationDate = newDate;
	}

	public long getId() { return this.id; }

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date date) {
		this.creationDate = date;
	}

	public Set<GamePlayer> getGamePlayers() { return games; }

	public Set<GameScore> getGameScores() {
		return gameScores;
	}

	public boolean isFinished() {
		return finished;
	}

	public void setFinished(boolean finished) {
		this.finished = finished;
	}
}
