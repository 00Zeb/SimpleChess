package controller;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import web.ScoreboardController;

@ExtendWith(SpringExtension.class)
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

        @BeforeEach
        public void create() throws Exception {
                mockMvc = MockMvcBuilders.standaloneSetup(scoreboardController).build();
        }

        @Test
        public void performGet() throws Exception {
                MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.get("/");
                mockMvc.perform(mockRequest).andExpected(MockMvcResultMatchers.status().isOk())
                                .andExpected(MockMvcResultMatchers.model().attributeExists("totalScore"));
        }

}
