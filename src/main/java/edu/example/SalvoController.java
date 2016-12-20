package edu.example;

import org.springframework.beans.factory.annotation.Autowired;
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
}
