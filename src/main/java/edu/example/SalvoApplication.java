package edu.example;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SalvoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SalvoApplication.class, args);
	}


	@Bean
	public CommandLineRunner initData(PlayerRepository player, GameRepository game, GamePlayerRepository gamePlayer) {
		return (args) -> {
			Player p1 = new Player("jbauer@test.com");
			Player p2 = new Player("chloe@test.com");
			Player p3 = new Player("kim@test.com");
			Game g1 = new Game(0);
			Game g2 = new Game(3600);
			Game g3 = new Game(7200);

			// save a few of players
			player.save(p1);
			player.save(p2);
			player.save(p3);

			// save a few of games
			game.save(g1);
			game.save(g2);
			game.save(g3);

			// save a few gamePlayers
			gamePlayer.save(new GamePlayer(p1, g1));
			gamePlayer.save(new GamePlayer(p2, g2));
			gamePlayer.save(new GamePlayer(p3, g3));
		};
	}
}
