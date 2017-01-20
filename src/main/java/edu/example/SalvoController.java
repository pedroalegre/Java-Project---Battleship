package edu.example;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Pedro on 16-Dec-16.
 */
@RestController
@RequestMapping("/api")
public class SalvoController {

	@Autowired
	private GameRepository gameRepository;

	@Autowired
	private GamePlayerRepository gamePlayerRepository;

	@Autowired
	private PlayerRepository playerRepository;

	@RequestMapping("/games")
	public Map<String, Object> getGame(Authentication authentication) {
		Player players = playerRepository.findByUserName(authentication.getName());

		Map<String, Object> makeDTO = new LinkedHashMap<>();
		if(isGuest(authentication)) {
			makeDTO.put("player", "Guest");
		} else {
			makeDTO.put("player", makePlayerDTO(players));
		}
		makeDTO.put("games", gameRepository.findAll().stream().map(game -> makeGameDTO(game)).collect(Collectors.toList()));

		return makeDTO;
	}

	private Map<String, Object> makePlayerDTO(Player player) {
		Map<String, Object> playerMap = new LinkedHashMap<>();

		playerMap.put("id", player.getId());
		playerMap.put("player", player.getUserName());

		return playerMap;
	}

	private boolean isGuest(Authentication authentication) {
		return authentication == null || authentication instanceof AnonymousAuthenticationToken;
	}

	private Map<String, Object> makeGameDTO(Game game) {
		Map<String, Object> gamesMap = new LinkedHashMap<>();

		gamesMap.put("id", game.getId());
		gamesMap.put("created", game.getCreationDate());
		gamesMap.put("players", game.games.stream().map(gp -> makeGamePlayerDTO(gp)).collect(Collectors.toList()));

		return gamesMap;
	}

	private Map<String, Object> makeGamePlayerDTO(GamePlayer gp) {
		Map<String, Object> gpMap = new LinkedHashMap<>();

		gpMap.put("gpid", gp.getId());
		gpMap.put("id", gp.getPlayer().getId());
		gpMap.put("name", gp.getPlayer().getUserName());
		if( gp.getGameScore() != null) { gpMap.put("score", gp.getGameScore().getScore()); }

		return gpMap;
	}

	@RequestMapping("/game_view/{gamePlayerId}")
	private Object getGameView(@PathVariable Long gamePlayerId, Authentication authentication) {

		GamePlayer gamePlayer = gamePlayerRepository.findOne(gamePlayerId);
		String currentPlayer = gamePlayerRepository.findOne(gamePlayerId).getPlayer().getUserName();

		if(authentication.getName() != currentPlayer) {
			return new ResponseEntity<>(makeMap("name", "Hello"), HttpStatus.UNAUTHORIZED);
		} else {
			Map<String, Object> gameViewMap = new LinkedHashMap<>();
			Set<GamePlayer> gamePlayers = gamePlayer.getGame().getGamePlayers();

			gameViewMap.put("id", gamePlayer.getGame().getId());
			gameViewMap.put("created", gamePlayer
					.getGame().getCreationDate());
			gameViewMap.put("GamePlayers", gamePlayers
					.stream().map(gp -> makeGamePlayerGameViewDTO(gp))
					.collect(Collectors.toList()));

			gameViewMap.put("ships", gamePlayer.getShips()
					.stream().map(ship -> makeShipsDTO(ship))
					.collect(Collectors.toList()));

			gameViewMap.put("salvoes", gamePlayer.getGame().getGamePlayers()
					.stream().map(gp -> makeSalvoDTO(gp.getSalvoes()))
					.collect(Collectors.toList()));

			return gameViewMap;
		}
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

	@RequestMapping(path = "/players", method = RequestMethod.POST)
	private ResponseEntity<Map<String, Object>> createUser(@RequestParam String userName, String password) {
		if(userName.isEmpty()) {
			return new ResponseEntity<>(makeMap("error", "No name given"), HttpStatus.FORBIDDEN);
		}

		Player player = playerRepository.findByUserName(userName);
		if(player != null) {
			return new ResponseEntity<>(makeMap("error", "Already exists"), HttpStatus.CONFLICT);
		}

		player = playerRepository.save(new Player(userName, password));
		return new ResponseEntity<>(makeMap("name", player.getUserName()), HttpStatus.CREATED);
	}

	private Map<String, Object> makeMap(String key, Object value) {
		Map<String, Object> map = new LinkedHashMap<>();
		map.put(key, value);
		return map;
	}

	@RequestMapping(path = "/games", method = RequestMethod.POST)
	private ResponseEntity<Map<String, Object>> createGame(Authentication authentication) {
		Player player = playerRepository.findByUserName(authentication.getName());

		if(authentication == null) {
			return new ResponseEntity<>(makeMap("error", "You are not logged in"), HttpStatus.UNAUTHORIZED);
		}

		Game game = new Game(0);
		gameRepository.save(game);

		GamePlayer gamePlayer = new GamePlayer(player, game);
		gamePlayerRepository.save(gamePlayer);

		return new ResponseEntity<>(makeMap("gpid", gamePlayer.getId()), HttpStatus.CREATED);
	}
}
