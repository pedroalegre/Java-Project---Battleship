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
			repository.save(new Player("Jack", "Bauer", "jbauer@test.com"));
			repository.save(new Player("Chloe", "O'Brian", "chloe@test.com"));
			repository.save(new Player("Kim", "Bauer", "kim@test.com"));
			repository.save(new Player("David", "Palmer", "david@test.com"));
			repository.save(new Player("Michelle", "Dessler", "michelle@test.com"));
		};
	}
}
