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
	public CommandLineRunner initData(PlayerRepository player, GameRepository game, GamePlayerRepository gamePlayer, ShipRepository ship) {
		return (args) -> {
			Player p1 = new Player("jbauer@test.com");
			Player p2 = new Player("chloe@test.com");
			Player p3 = new Player("kim@test.com");

			Game g1 = new Game(0);
			Game g2 = new Game(3600);
			Game g3 = new Game(7200);

			Ship s1 = new Ship("Destroyer");
			Ship s2 = new Ship("Carrier");
			Ship s3 = new Ship("Battleship");
			Ship s4 = new Ship("Submarine");
			Ship s5 = new Ship("Patrol Boat");

			GamePlayer gp1 = new GamePlayer(p1, g1);
			GamePlayer gp2 = new GamePlayer(p2, g1);
			GamePlayer gp3 = new GamePlayer(p2, g2);
			GamePlayer gp4 = new GamePlayer(p3, g1);

			gp1.addShip(s4);
			gp1.addShip(s3);
			gp2.addShip(s1);
			gp2.addShip(s2);


			// save a few of players
			player.save(p1);
			player.save(p2);
			player.save(p3);

			// save a few of games
			game.save(g1);
			game.save(g2);
			game.save(g3);

			// save a few gamePlayers
			gamePlayer.save(gp1);
			gamePlayer.save(gp2);
			gamePlayer.save(gp3);
			gamePlayer.save(gp4);

			// save a few ships
			ship.save(s1);
			ship.save(s2);
			ship.save(s3);
			ship.save(s4);
			ship.save(s5);

		};
	}
}
