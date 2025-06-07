package controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import player.SimplePlayer;
import player.TestPlayer;
import web.ScoreboardController;

@WebMvcTest(ScoreboardController.class)
@ContextConfiguration
@DisplayName("ScoreboardController Tests")
class ScoreboardControllerTest {

    @Configuration
    @ComponentScan({ "player", "controller", "web" })
    static class TestConfig {
        // Test configuration
    }

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private List<Player> players;

    @BeforeEach
    void setUp() {
        // Setup mock players if needed
    }

    @Nested
    @DisplayName("Main Scoreboard Endpoint")
    class MainScoreboardEndpoint {

        @Test
        @DisplayName("Should return scoreboard view for root path")
        void shouldReturnScoreboardViewForRootPath() throws Exception {
            mockMvc.perform(get("/legacy/"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("scoreboard"))
                    .andExpect(model().attributeExists("otherScoreboard"))
                    .andExpect(model().attributeExists("timestamp"))
                    .andExpect(model().attributeExists("totalScore"))
                    .andExpect(model().attributeExists("currentScoreboard"))
                    .andExpect(model().attributeExists("previousScoreboards"));
        }

        @Test
        @DisplayName("Should have correct other scoreboard link")
        void shouldHaveCorrectOtherScoreboardLink() throws Exception {
            mockMvc.perform(get("/legacy/"))
                    .andExpect(status().isOk())
                    .andExpect(model().attribute("otherScoreboard", "/cc"));
        }

        @Test
        @DisplayName("Should have non-null timestamp")
        void shouldHaveNonNullTimestamp() throws Exception {
            mockMvc.perform(get("/legacy/"))
                    .andExpect(status().isOk())
                    .andExpect(model().attribute("timestamp", notNullValue()));
        }
    }

    @Nested
    @DisplayName("CC Scoreboard Endpoint")
    class CCScoreboardEndpoint {

        @Test
        @DisplayName("Should return scoreboard view for CC path")
        void shouldReturnScoreboardViewForCCPath() throws Exception {
            mockMvc.perform(get("/legacy/cc"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("scoreboard"))
                    .andExpect(model().attributeExists("otherScoreboard"))
                    .andExpect(model().attributeExists("timestamp"))
                    .andExpect(model().attributeExists("totalScore"))
                    .andExpect(model().attributeExists("currentScoreboard"))
                    .andExpect(model().attributeExists("previousScoreboards"));
        }

        @Test
        @DisplayName("Should have correct other scoreboard link for CC")
        void shouldHaveCorrectOtherScoreboardLinkForCC() throws Exception {
            mockMvc.perform(get("/legacy/cc"))
                    .andExpect(status().isOk())
                    .andExpect(model().attribute("otherScoreboard", "/*"));
        }
    }

    @Nested
    @DisplayName("Model Attributes")
    class ModelAttributes {

        @Test
        @DisplayName("Should include all required model attributes")
        void shouldIncludeAllRequiredModelAttributes() throws Exception {
            mockMvc.perform(get("/legacy/"))
                    .andExpect(status().isOk())
                    .andExpect(model().attributeExists(
                            "otherScoreboard",
                            "timestamp",
                            "totalScore",
                            "currentScoreboard",
                            "previousScoreboards"
                    ));
        }

        @Test
        @DisplayName("Should have valid scoreboard data structure")
        void shouldHaveValidScoreboardDataStructure() throws Exception {
            mockMvc.perform(get("/legacy/"))
                    .andExpect(status().isOk())
                    .andExpect(model().attribute("currentScoreboard", notNullValue()))
                    .andExpect(model().attribute("previousScoreboards", notNullValue()));
        }
    }

    @Nested
    @DisplayName("Error Handling")
    class ErrorHandling {

        @Test
        @DisplayName("Should handle wildcard paths")
        void shouldHandleWildcardPaths() throws Exception {
            // Since the controller uses "/*" mapping, any path should work
            mockMvc.perform(get("/legacy/any-path"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("scoreboard"));
        }
    }
}
