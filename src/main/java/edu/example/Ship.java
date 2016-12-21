package edu.example;

import javax.persistence.*;
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

	public Ship() {}
}
