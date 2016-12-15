package edu.example;

/**
 * Created by Pedro on 15-Dec-16.
 */

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.Date;

@Entity
public class GamePlayer {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	private Date creationDate;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="player_id")
	private Player player;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="game_id")
	private Game game;

	public GamePlayer() {}

	public GamePlayer(Player player, Game game) {
		this.creationDate = new Date();
		this.player = player;
		this.game = game;

	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date date) {
		this.creationDate = date;
	}

	public Player getPlayer() { return player; }

	public void setPlayer(Player player) { this.player = player; }

	public Game getGame() { return game; }

	public void setGame(Game game) { this.game = game; }
}
