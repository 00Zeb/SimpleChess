package web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan({"player","controller","web"})
@EnableAutoConfiguration
public class Application {
	public static String[] ARGS = new String[0];

    public static void main(String[] args) {
    	ARGS = args;
        SpringApplication.run(Application.class, args);
    }

}
