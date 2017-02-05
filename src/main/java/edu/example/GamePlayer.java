package edu.example;

/**
 * Created by Pedro on 15-Dec-16.
 */

import javax.persistence.*;
import java.util.*;

@Entity
public class GamePlayer {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	private Date creationDate;
	private boolean first;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="player_id")
	private Player player;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="game_id")
	private Game game;

	@OneToMany(mappedBy = "gamePlayer", fetch = FetchType.EAGER)
	private Set<Ship>ships;

	@OneToMany(mappedBy = "gamePlayer", fetch = FetchType.EAGER)
	private Set<Salvo> salvoes;


	public GamePlayer() {}

	public GamePlayer(Player player, Game game) {
		this.creationDate = new Date();
		this.player = player;
		this.game = game;
		this.ships = new LinkedHashSet<>();
		this.salvoes = new LinkedHashSet<>();
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

	public long getId() {
		return id;
	}

	public void addShip(Ship ship) {
		ship.setGamePlayer(this);
		this.ships.add(ship);
	}

	public void addSalvo(Salvo salvo) {
		salvo.setGamePlayer(this);
		this.salvoes.add(salvo);
	}

	public Set<Ship> getShips() {
		return ships;
	}

	public Set<Salvo> getSalvoes() {
		return salvoes;
	}

	public GameScore getGameScore() {
		return player.getGameScore(this.game);
	}

	public long getLastTurn() {
		if (!this.getSalvoes().isEmpty()) {
			return this.getSalvoes()
					.stream()
					.map(s -> s.getTurn())
					.max((x, y) -> Long.compare(x, y)).get();

		} else {
			return 0;
		}
	}

	public boolean isFirst() {
		return first;
	}

	public void setFirst(boolean first) {
		this.first = first;
	}
}
