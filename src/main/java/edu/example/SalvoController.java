package edu.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by Pedro on 16-Dec-16.
 */
@RestController
public class SalvoController {

	@RequestMapping("/api")
	public List<Game> getAll() {
		return game.findAll();
	}

	@Autowired
	private GameRepository game;
}
