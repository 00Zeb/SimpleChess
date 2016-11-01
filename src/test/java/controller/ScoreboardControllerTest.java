package controller;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import web.ScoreboardController;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class ScoreboardControllerTest {
	@Configuration
	@ComponentScan({ "player", "controller", "web" })
	public static class Config {

	}

	@Autowired
	private List<Player> players;
	@Autowired
	private ScoreboardController scoreboardController;
	private MockMvc mockMvc;

	@Before
	public void create() throws Exception {
		mockMvc = MockMvcBuilders.standaloneSetup(scoreboardController).build();
	}

	@Test
	public void performGet() throws Exception {
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.get("/");
		mockMvc.perform(mockRequest).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.model().attributeExists("totalScore"));
	}

}
