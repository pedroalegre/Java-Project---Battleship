package edu.example;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class SalvoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SalvoApplication.class, args);
	}


	@Bean
	public CommandLineRunner initData(PlayerRepository player, GameRepository game, GamePlayerRepository gamePlayer, ShipRepository ship, SalvoRepository salvo) {
		return (args) -> {
			Player p1 = new Player("j.bauer@ctu.gov");
			Player p2 = new Player("c.hloe@ctu.gov");
			Player p3 = new Player("kim_bauer@gmail.com");
			Player p4 = new Player("t.almeida@ctu.gov");

			Game g1 = new Game(0);
			Game g2 = new Game(3600);
			Game g3 = new Game(7200);
			Game g4 = new Game(9800);

			List<String> loc1 = Arrays.asList("A2", "B2", "C2", "D2");
			List<String> loc2 = Arrays.asList("F5", "F6", "F7");
			List<String> loc3 = Arrays.asList("C1", "C2");
			List<String> loc4 = Arrays.asList("B3", "B4", "B5", "B6", "B7");
			List<String> loc5 = Arrays.asList("G4", "H4");


			Ship s1 = new Ship("Destroyer", loc2);
			Ship s2 = new Ship("Carrier", loc4);
			Ship s3 = new Ship("Battleship", loc1);
			Ship s4 = new Ship("Submarine", loc3);
			Ship s5 = new Ship("Patrol Boat", loc5);

			GamePlayer gp1 = new GamePlayer(p1, g1);
			GamePlayer gp2 = new GamePlayer(p2, g1);
			GamePlayer gp3 = new GamePlayer(p2, g2);
			GamePlayer gp4 = new GamePlayer(p3, g2);
			GamePlayer gp5 = new GamePlayer(p4, g3);
			GamePlayer gp6 = new GamePlayer(p1, g3);

			gp1.addShip(s2);
			gp1.addShip(s4);
			gp1.addShip(s5);
			gp2.addShip(s1);
			gp2.addShip(s3);

			List<String> sloc1 = Arrays.asList("A2", "C3", "B7");
			List<String> sloc2 = Arrays.asList("G3", "F6", "H8");
			List<String> sloc3 = Arrays.asList("B4", "B5", "G4");

			Salvo sal1 = new Salvo(1, sloc1);
			Salvo sal2 = new Salvo(2, sloc2);
			Salvo sal3 = new Salvo(3, sloc3);
			Salvo sal4 = new Salvo(1, loc1);
			Salvo sal5 = new Salvo(2, loc2);
			Salvo sal6 = new Salvo(3, loc3);
			Salvo sal7 = new Salvo(4, loc4);
			Salvo sal8 = new Salvo(4, loc5);

			gp1.addSalvo(sal1);
			gp1.addSalvo(sal2);
			gp1.addSalvo(sal3);
			gp1.addSalvo(sal7);
			gp2.addSalvo(sal4);
			gp2.addSalvo(sal5);
			gp2.addSalvo(sal6);
			gp2.addSalvo(sal8);

			// save a few of players
			player.save(p1);
			player.save(p2);
			player.save(p3);
			player.save(p4);

			// save a few of games
			game.save(g1);
			game.save(g2);
			game.save(g3);
			game.save(g4);

			// save a few gamePlayers
			gamePlayer.save(gp1);
			gamePlayer.save(gp2);
			gamePlayer.save(gp3);
			gamePlayer.save(gp4);
			gamePlayer.save(gp5);
			gamePlayer.save(gp6);

			// save a few ships
			ship.save(s1);
			ship.save(s2);
			ship.save(s3);
			ship.save(s4);
			ship.save(s5);

			// save a few salvoes
			salvo.save(sal1);
			salvo.save(sal2);
			salvo.save(sal3);
			salvo.save(sal4);
			salvo.save(sal5);
			salvo.save(sal6);
			salvo.save(sal7);
			salvo.save(sal8);

		};
	}
}
