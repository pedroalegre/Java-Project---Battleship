package edu.example;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Date;

@SpringBootApplication
public class SalvoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SalvoApplication.class, args);
	}


	@Bean
	public CommandLineRunner initData(PlayerRepository player, GameRepository game) {
		return (args) -> {
			// save a couple of players
			player.save(new Player("jbauer@test.com"));
			player.save(new Player("chloe@test.com"));
			player.save(new Player("kim@test.com"));
			player.save(new Player("david@test.com"));
			player.save(new Player("michelle@test.com"));
			//game.save(new Game("14-12-2016"));
			//game.save(new Game("12-12-2016"));
			game.save(new Game());
		};
	}
}
