package web;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import controller.Controller;

@SpringBootApplication
public class SimpleChessApplication implements CommandLineRunner {
	private Controller controller = new Controller();

	@Override
	public void run(String... args) {
		System.out.println(this.controller.runGame());
	}

	public static void main(String[] args) throws Exception {
		SpringApplication.run(SimpleChessApplication.class, args);
	}
}