package edu.example;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pedro on 05-Jan-17.
 */

@Entity
public class Salvo {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	private long turn;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="game_player_id")
	private GamePlayer gamePlayer;

	@ElementCollection
	@Column(name = "salvo_location")
	private List<String> salvoLocations = new ArrayList<>();

	public Salvo() {}

	public Salvo(long turn, List<String> salvoLocations) {
		this.turn = turn;
		this.salvoLocations = salvoLocations;
	}

	private long getId() { return id; }

	public long getTurn() {
		return turn;
	}

	public void setTurn(long turn) {
		this.turn = turn;
	}

	public GamePlayer getGamePlayer() { return gamePlayer; }

	public void setGamePlayer(GamePlayer gamePlayer) { this.gamePlayer = gamePlayer; }

	public List<String> getSalvoLocations() { return this.salvoLocations; }
}
