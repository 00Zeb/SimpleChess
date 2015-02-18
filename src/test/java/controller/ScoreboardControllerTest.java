package controller;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class ScoreboardControllerTest {
	@Configuration
	@ComponentScan({"player", "controller"})
	public static class Config {
		
	}
	
	@Autowired
	private List<Player> players;
	
	@Test
	public void testName() throws Exception {
		Assert.assertTrue(players.size() >= 2);
	}
}
