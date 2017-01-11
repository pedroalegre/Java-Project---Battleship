package edu.example;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Pedro on 10-Jan-17.
 */

@Entity
public class GameScore {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	private Date finishDate;
	private double score;


	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="game_id")
	private Game game;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="player_id")
	private Player player;

	public GameScore() {}

	public GameScore(Player player, Game game, long time, double score) {
		this.game = game;
		this.player = player;
		this.finishDate = new Date();
		Date newDate = Date.from(finishDate.toInstant().plusSeconds(time));
		finishDate = newDate;
		this.score = score;
	}

	public long getId() {
		return id;
	}

	public Long getGameId() {
		return game.getId();
	}

	public Long getPlayerId() {
		return player.getId();
	}

	public double getScore() {
		return score;
	}

	public Game getGame() {
		return game;
	}
}
