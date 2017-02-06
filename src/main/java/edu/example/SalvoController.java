package edu.example;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
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

	@Autowired
	private ShipRepository shipRepository;

	@Autowired
	private SalvoRepository salvoRepository;

	@Autowired
	private GameScoreRepository gameScoreRepository;

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
			gameViewMap.put("gameStatus", gameStatus(gamePlayer));
			gameViewMap.put("GamePlayers", gamePlayers
					.stream().map(gp -> makeGamePlayerGameViewDTO(gp))
					.collect(Collectors.toList()));

			gameViewMap.put("ships", gamePlayer.getShips()
					.stream().map(ship -> makeShipsDTO(ship))
					.collect(Collectors.toList()));

			if(getEnemyPlayer(gamePlayer) != null) {
				gameViewMap.put("enemyShips", getEnemyPlayer(gamePlayer).getShips()
						.stream().map(ship -> makeEnemyShipsDTO(ship))
						.collect(Collectors.toList()));
			}

			gameViewMap.put("salvoes", gamePlayer.getGame().getGamePlayers()
					.stream().map(gp -> makeSalvoDTO(gp.getSalvoes()))
					.collect(Collectors.toList()));

			return gameViewMap;
		}
	}

	private List<Map<String,Object>> makeSalvoDTO(Set<Salvo> salvoes) {

		List<Map<String,Object>> result= new ArrayList<>();

		salvoes.forEach(s -> {
			Map<String, Object> salvoMap = new LinkedHashMap<>();
			salvoMap.put("turn", s.getTurn());
			salvoMap.put("player", s.getGamePlayer().getPlayer().getId());
			salvoMap.put("locations", s.getSalvoLocations());
			salvoMap.put("hits", makeHitsDTO(s));
			result.add(salvoMap);
		});

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
		shipsMap.put("sunk", ship.isSunk());

		return shipsMap;
	}

	private Map<String, Object> makeEnemyShipsDTO(Ship ship) {
		Map<String, Object> shipsMap = new LinkedHashMap<>();

		shipsMap.put("type", ship.getShipType());
		shipsMap.put("sunk", ship.isSunk());

		return shipsMap;
	}

	private List<String> makeHitsDTO(Salvo salvo) {
		GamePlayer enemyPlayer = getEnemyPlayer(salvo.getGamePlayer());

		if(enemyPlayer != null) {
			List<String> salvoLocations = salvo.getSalvoLocations();
			List<String> shipLocations = getShipLocations(enemyPlayer);

			return salvoLocations.stream()
					.filter(s -> shipLocations.contains(s))
					.collect(Collectors.toList());
		} else return null;

	}

	private GamePlayer getEnemyPlayer(GamePlayer gamePlayer) {
		Long playerId = gamePlayer.getId();
		Game game = gamePlayer.getGame();
		Set<GamePlayer> gamePlayers = game.getGamePlayers();

		GamePlayer enemyPlayer = gamePlayers.stream()
				.filter(gp -> gp.getId() != playerId ).findAny().orElse(null);

		return enemyPlayer;
	}

	private List<String> getShipLocations(GamePlayer gamePlayer) {
		Set<Ship> ships = gamePlayer.getShips();

		return ships.stream().map(s -> s.getShipLocations())
				.flatMap(cellId -> cellId.stream())
				.collect(Collectors.toList());
	}

	private void sinkShip (GamePlayer gamePlayer) {
		GamePlayer enemyPlayer = getEnemyPlayer(gamePlayer);

		if(enemyPlayer != null) {
			Set<Ship> enemyShips = enemyPlayer.getShips();
			List<String> playerSalvoes = getSalvoLocations(gamePlayer);

			enemyShips.stream().filter(ship -> !ship.isSunk())
					.forEach(ship -> {
						if(shipIsSunk(playerSalvoes, ship)) {
							ship.setSunk(true);
							shipRepository.save(ship);
						}
			});
		}
	}

	private List<String> getSalvoLocations(GamePlayer gamePlayer) {
		Set<Salvo> salvoes = gamePlayer.getSalvoes();

		return salvoes.stream().map(s -> s.getSalvoLocations())
				.flatMap(cellId -> cellId.stream())
				.collect(Collectors.toList());
	}

	private boolean shipIsSunk(List<String> playerSalvoes, Ship ship) {
		boolean shipIsSunk = ship.getShipLocations().stream()
				.allMatch(location -> playerSalvoes.contains(location));

		return shipIsSunk;
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

		if(isGuest(authentication)) {
			return new ResponseEntity<>(makeMap("error", "You are not logged in"), HttpStatus.UNAUTHORIZED);
		} else {
			Game game = new Game(0);
			gameRepository.save(game);

			GamePlayer gamePlayer = new GamePlayer(player, game);
			gamePlayer.setFirst(true);
			gamePlayerRepository.save(gamePlayer);

			return new ResponseEntity<>(makeMap("gpid", gamePlayer.getId()), HttpStatus.CREATED);
		}
	}

	@RequestMapping(path = "/game/{gameId}/players", method = RequestMethod.POST)
	private ResponseEntity<Map<String, Object>> joinGame(@PathVariable Long gameId, Authentication authentication) {
		if(authentication == null) {
			return new ResponseEntity<>(makeMap("error", "You are not logged in"), HttpStatus.UNAUTHORIZED);
		}

		Player currentPlayer = playerRepository.findByUserName(authentication.getName());

		if(gameId == null) {
			return new ResponseEntity<>(makeMap("error", "There is no game"), HttpStatus.FORBIDDEN);
		}

		Game currentGame = gameRepository.findOne(gameId);

		if(currentGame.getGamePlayers().size() == 2) {
			return new ResponseEntity<>(makeMap("error", "Game is full"), HttpStatus.FORBIDDEN);
		} else {
			GamePlayer gamePlayer = new GamePlayer(currentPlayer, currentGame);
			gamePlayerRepository.save(gamePlayer);

			return new ResponseEntity<>(makeMap("gpid", gamePlayer.getId()), HttpStatus.CREATED);
		}
	}

	@RequestMapping(path = "/games/players/{gamePlayerId}/ships", method = RequestMethod.POST)
	private ResponseEntity addShips(@PathVariable Long gamePlayerId, Authentication authentication, @RequestBody List<Ship> ship) {
		if(authentication == null) {
			return new ResponseEntity<>(makeMap("error", "You are not logged in"), HttpStatus.UNAUTHORIZED);
		}

		GamePlayer currentGamePlayer = gamePlayerRepository.findOne(gamePlayerId);

		if(currentGamePlayer == null) {
			return new ResponseEntity<>(makeMap("error", "There is no game player"), HttpStatus.UNAUTHORIZED);
		}

		Player currentPlayer = playerRepository.findByUserName(authentication.getName());

		if(currentPlayer.getId() != (currentGamePlayer.getPlayer().getId())) {
			return new ResponseEntity<>(makeMap("error", "Wrong player"), HttpStatus.UNAUTHORIZED);
		}

		if(currentGamePlayer.getShips().isEmpty()) {
			ship.forEach(s -> s.setGamePlayer(currentGamePlayer));
			ship.forEach(s -> shipRepository.save(ship));

			return new ResponseEntity(HttpStatus.CREATED);

		} else {
			return new ResponseEntity<>(makeMap("error", "Ships already placed"), HttpStatus.FORBIDDEN);
		}
	}

	@RequestMapping(path = "/games/players/{gamePlayerId}/salvoes", method = RequestMethod.POST)
	private ResponseEntity addSalvoes(@PathVariable Long gamePlayerId, Authentication authentication, @RequestBody Salvo salvo) {
		if(authentication == null) {
			return new ResponseEntity<>(makeMap("error", "You are not logged in"), HttpStatus.UNAUTHORIZED);
		}

		GamePlayer currentGamePlayer = gamePlayerRepository.findOne(gamePlayerId);

		if(currentGamePlayer == null) {
			return new ResponseEntity<>(makeMap("error", "There is no game player"), HttpStatus.UNAUTHORIZED);
		}

		Player currentPlayer = playerRepository.findByUserName(authentication.getName());

		if(currentPlayer.getId() != (currentGamePlayer.getPlayer().getId())) {
			return new ResponseEntity<>(makeMap("error", "Wrong player"), HttpStatus.UNAUTHORIZED);
		}

		if(!isGuest(authentication)) {
			salvo.setTurn(currentGamePlayer.getLastTurn() + 1);
			currentGamePlayer.addSalvo(salvo);
			salvoRepository.save(salvo);
			sinkShip(currentGamePlayer);

			return new ResponseEntity(HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(makeMap("error", "You have already fired your salvoes this turn"), HttpStatus.FORBIDDEN);
		}
	}

	@RequestMapping(path = "/{gamePlayerId}/winner", method = RequestMethod.POST)
	private ResponseEntity<Map<String, Object>> winner(@PathVariable Long gamePlayerId) {

		GamePlayer currentGamePlayer = gamePlayerRepository.findOne(gamePlayerId);
		Game game = currentGamePlayer.getGame();
		setGameOver(game);

		if(allShipsSunk(currentGamePlayer)) {
			return new ResponseEntity<>(makeMap("winner", true), HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(makeMap("winner", false), HttpStatus.CREATED);
		}
	}

	private boolean allShipsSunk(GamePlayer gamePlayer) {
		List<String> allSalvoLocations = getSalvoLocations(gamePlayer);

		return getShipLocations(getEnemyPlayer(gamePlayer))
				.stream().allMatch(l -> allSalvoLocations.contains(l));
	}

	private int gameStatus(GamePlayer gamePlayer) {
		GamePlayer enemyPlayer = getEnemyPlayer(gamePlayer);

		if(enemyPlayer != null) {
			if(enemyPlayer.getShips().isEmpty()) {
				return 1;
			} else if((allShipsSunk(enemyPlayer) && gamePlayer.getSalvoes().size() > 0)
				|| (allShipsSunk(gamePlayer) && gamePlayer.getSalvoes().size() > 0)) {
				if(gamePlayer.getGame().getGameScores().isEmpty()) {
					setScores(gamePlayer);
					return 2;
				} else {
					setScores(gamePlayer);
					return 2;
				}
			} else if(enemyPlayer.getLastTurn() < gamePlayer.getLastTurn()) {
				return 1;
			} else if(enemyPlayer.getLastTurn() == gamePlayer.getLastTurn()) {
				if(gamePlayer.isFirst()) {
					return 0;
				} else {
					return 1;
				}
			} else if(gamePlayer.getLastTurn() < enemyPlayer.getLastTurn()) {
				if(allShipsSunk(enemyPlayer) || allShipsSunk(gamePlayer)) {
					return 2;
				} else {
					return 0;
				}
			}
			return 1;
		} else if(!gamePlayer.getShips().isEmpty()) {
			return 1;
		} else {
			return 1;
		}
	}

	private void setScores(GamePlayer gamePlayer) {
		GamePlayer enemyPlayer = getEnemyPlayer(gamePlayer);

		if(allShipsSunk(enemyPlayer) && allShipsSunk(gamePlayer)) {
			gameScoreRepository.save(new GameScore(gamePlayer.getPlayer(), gamePlayer.getGame(), 0, 0.5, new Date()));
			gameScoreRepository.save(new GameScore(enemyPlayer.getPlayer(), enemyPlayer.getGame(), 0, 0.5, new Date()));
		} else if(allShipsSunk(gamePlayer)) {
			gameScoreRepository.save(new GameScore(gamePlayer.getPlayer(), gamePlayer.getGame(), 0, 1, new Date()));
			gameScoreRepository.save(new GameScore(enemyPlayer.getPlayer(), enemyPlayer.getGame(), 0, 0, new Date()));
		} else if(allShipsSunk(enemyPlayer)) {
			gameScoreRepository.save(new GameScore(gamePlayer.getPlayer(), gamePlayer.getGame(), 0, 0, new Date()));
			gameScoreRepository.save(new GameScore(enemyPlayer.getPlayer(), enemyPlayer.getGame(), 0, 1, new Date()));
		}
	}

	private void setGameOver(Game game) {
		game.setFinished(true);
		gameRepository.save(game);
	}
}
