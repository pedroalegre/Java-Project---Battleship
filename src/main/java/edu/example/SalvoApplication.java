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
	public CommandLineRunner initData(PlayerRepository repository) {
		return (args) -> {
			// save a couple of customers
			repository.save(new Player("jbauer@test.com"));
			repository.save(new Player("chloe@test.com"));
			repository.save(new Player("kim@test.com"));
			repository.save(new Player("david@test.com"));
			repository.save(new Player("michelle@test.com"));
		};
	}
}
