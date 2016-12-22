package edu.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Pedro on 16-Dec-16.
 */
@RestController
@RequestMapping("/api")
public class SalvoController {

	@Autowired
	private GameRepository game;

	@Autowired
	private GamePlayerRepository gamePlayerRepository;

	@RequestMapping("/games")
	public List<Object> getGame() {
		return game.findAll().stream().map(game -> makeGameDTO(game)).collect(Collectors.toList());
	}

	private Map<String, Object> makeGameDTO(Game game) {
		Map<String, Object> gamesMap = new LinkedHashMap<>();

		gamesMap.put("id", game.getId());
		gamesMap.put("created", game.getCreationDate());
		gamesMap.put("GamePlayers", game.games.stream().map(gp -> makeGamePlayerDTO(gp)).collect(Collectors.toList()));

		return gamesMap;
	}

	private Map<String, Object> makeGamePlayerDTO(GamePlayer gp) {
		Map<String, Object> gpMap = new LinkedHashMap<>();

		gpMap.put("id", gp.getId());
		gpMap.put("player", gp.getPlayer());

		return gpMap;
	}

	@RequestMapping("/game_view/{gameId}")
	private Map<String, Object> getGameView(@PathVariable Long gameId) {
		Map<String, Object> gameViewMap = new LinkedHashMap<>();

		gameViewMap.put("id", game.findOne(gameId).getId());
		gameViewMap.put("created", game.findOne(gameId).getCreationDate());
		gameViewMap.put("GamePlayers", gamePlayerRepository.findAll().stream()
				.filter(gpv2 -> gpv2.getGame().getId() == gameId)
				.map(gpv -> makeGamePlayerGameViewDTO(gpv))
				.collect(Collectors.toList()));
		gameViewMap.put("ships", gamePlayerRepository.findOne(gameId).ships
				.stream().map(ship -> makeShipsDTO(ship)).collect(Collectors.toList()));

		return gameViewMap;
	}

	private Map<String, Object> makeGamePlayerGameViewDTO(GamePlayer gp) {
		Map<String, Object> gpvMap = new LinkedHashMap<>();

		gpvMap.put("id", gp.getId());
		gpvMap.put("player", gp.getPlayer());

		return gpvMap;
	}

	private Map<String, Object> makeShipsDTO(Ship ship) {
		Map<String, Object> shipsMap = new LinkedHashMap<>();

		shipsMap.put("type", ship.getShipType());
		shipsMap.put("locations", ship.getShipLocations());

		return shipsMap;
	}
}
