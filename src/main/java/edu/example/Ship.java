package edu.example;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;
import javax.persistence.Column;
import javax.persistence.ElementCollection;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pedro on 21-Dec-16.
 */

@Entity
public class Ship {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	private String shipType;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="game_player_id")
	private GamePlayer gamePlayer;

	@ElementCollection
	@Column(name = "ship_location")
	private List<String> shipLocations = new ArrayList<>();

	private boolean sunk = false;

	public Ship() {}

	public Ship(String shipType, List<String> shipLocations) {
		this.shipType = shipType;
		this.shipLocations = shipLocations;
	}

	private long getId() { return id; }

	public String getShipType() {
		return shipType;
	}

	public void setShipType(String shipType) {
		this.shipType = shipType;
	}

	public List<String> getShipLocations() { return this.shipLocations; }

	public void setGamePlayer(GamePlayer gamePlayer) { this.gamePlayer = gamePlayer; }

	public boolean isSunk() {
		return sunk;
	}

	public void setSunk(boolean sunk) {
		this.sunk = sunk;
	}
}
