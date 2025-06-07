package web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"player","controller","web"})
public class Application {

	public static String[] ARGS = new String[0];

    public static void main(String[] args) {
        ARGS = args;
        SpringApplication.run(Application.class, args);
    }

}
