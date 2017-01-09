package edu.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
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

	@RequestMapping("/game_view/{gamePlayerId}")
	private Map<String, Object> getGameView(@PathVariable Long gamePlayerId) {
		Map<String, Object> gameViewMap = new LinkedHashMap<>();
		GamePlayer gamePlayer = gamePlayerRepository.findOne(gamePlayerId);
		Set<GamePlayer> gamePlayers = gamePlayer.getGame().getGamePlayers();

		gameViewMap.put("id", gamePlayer.getGame().getId());
		gameViewMap.put("created", gamePlayer.getGame().getCreationDate());
		gameViewMap.put("GamePlayers", gamePlayers.stream().map(gp -> makeGamePlayerGameViewDTO(gp)).collect(Collectors.toList()));

		gameViewMap.put("ships", gamePlayer.getShips()
				.stream().map(ship -> makeShipsDTO(ship)).collect(Collectors.toList()));

		gameViewMap.put("salvoes", gamePlayer.getGame().getGamePlayers().stream().map(gp -> makeSalvoDTO(gp.getSalvoes())).collect(Collectors.toList()));
		//gameViewMap.put("salvoes", gamePlayerRepository.findOne(gamePlayerId).salvoes
		//		.stream().map(salvo -> makeSalvoesDTO(salvo)).collect(Collectors.toList()));

		return gameViewMap;
	}

	private List<Map<String,Object>> makeSalvoDTO(Set<Salvo> salvoes) {

		List<Map<String,Object>> result= new ArrayList<>();

		for (Salvo salvo : salvoes) {
			Map<String, Object> salvoMap = new LinkedHashMap<>();
			long turn = salvo.getTurn();
			List<String> salvoLocations = salvo.getSalvoLocations();
			long id = salvo.getGamePlayer().getPlayer().getId();

			salvoMap.put("turn", turn);
			salvoMap.put("player", id);
			salvoMap.put("locations", salvoLocations);

			result.add(salvoMap);
		}
		return result;
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

	/*private Map<String, Object> makeSalvoesDTO(GamePlayer salvo) {
		Map<String, Object> salvoesMap = new LinkedHashMap<>();

		salvoesMap.put("turn", salvo.getTurn());
		salvoesMap.put("player", salvo.getGamePlayer().getId());
		salvoesMap.put("locations", salvo.getSalvoLocations());

		return salvoesMap;
	}
	*/
}
