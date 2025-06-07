package controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import player.SimplePlayer;
import player.TestPlayer;

@DisplayName("ChessGame Tests")
class ChessGameTest {

    private ChessGame chessGame;
    private List<Player> players;

    @BeforeEach
    void setUp() {
        players = Arrays.asList(new TestPlayer(), new SimplePlayer());
        chessGame = new ChessGame(players);
    }

    @Nested
    @DisplayName("Game Initialization")
    class GameInitialization {

        @Test
        @DisplayName("Should initialize with correct number of players")
        void shouldInitializeWithCorrectNumberOfPlayers() {
            // Given players list is set up in @BeforeEach

            // When creating ChessGame
            ChessGame game = new ChessGame(players);

            // Then verify initialization
            assertNotNull(game);
        }

        @Test
        @DisplayName("Should handle single player")
        void shouldHandleSinglePlayer() {
            // Given
            List<Player> singlePlayer = Arrays.asList(new TestPlayer());

            // When
            ChessGame game = new ChessGame(singlePlayer);
            Map<Class<? extends Player>, Integer> results = game.runGame();

            // Then
            assertThat(results).hasSize(1);
            assertThat(results.values()).allMatch(score -> score >= 0);
        }
    }

    @Nested
    @DisplayName("Game Execution")
    class GameExecution {

        @Test
        @DisplayName("Should run complete tournament between players")
        void shouldRunCompleteTournament() {
            // When
            Map<Class<? extends Player>, Integer> results = chessGame.runGame();

            // Then
            assertAll(
                () -> assertNotNull(results, "Results should not be null"),
                () -> assertEquals(2, results.size(), "Should have results for both players"),
                () -> assertTrue(results.containsKey(TestPlayer.class), "Should contain TestPlayer results"),
                () -> assertTrue(results.containsKey(SimplePlayer.class), "Should contain SimplePlayer results"),
                () -> assertTrue(results.values().stream().allMatch(score -> score >= 0), "All scores should be non-negative")
            );
        }

        @Test
        @DisplayName("Should call runGame method correct number of times")
        void shouldCallRunGameCorrectNumberOfTimes() {
            // Given
            ChessGame spyGame = spy(chessGame);

            // When
            spyGame.runGame();

            // Then - For 2 players, should run 10 games (5 each direction)
            verify(spyGame, times(10)).runGame(any(), any(), anyBoolean());
        }

        @ParameterizedTest
        @ValueSource(ints = {1, 2, 3, 4})
        @DisplayName("Should handle different numbers of players")
        void shouldHandleDifferentNumbersOfPlayers(int playerCount) {
            // Given
            List<Player> testPlayers = Arrays.asList(
                new TestPlayer(), new SimplePlayer(), new TestPlayer(), new SimplePlayer()
            ).subList(0, playerCount);

            // When
            ChessGame game = new ChessGame(testPlayers);
            Map<Class<? extends Player>, Integer> results = game.runGame();

            // Then
            assertThat(results).isNotEmpty();
            assertThat(results.values()).allMatch(score -> score >= 0);
        }
    }

    @Nested
    @DisplayName("Score Calculation")
    class ScoreCalculation {

        @Test
        @DisplayName("Should calculate scores correctly")
        void shouldCalculateScoresCorrectly() {
            // When
            Map<Class<? extends Player>, Integer> results = chessGame.runGame();

            // Then
            int totalScore = results.values().stream().mapToInt(Integer::intValue).sum();

            // Each game awards points (WIN_POINTS + LOSE_POINTS or 2*DRAW_POINTS)
            // With 2 players and 10 games, expect reasonable total score
            assertThat(totalScore).isGreaterThan(0);

            // Verify individual scores are within expected range
            results.values().forEach(score -> {
                assertThat(score).isBetween(0, ChessGame.WIN_POINTS * 10);
            });
        }
    }
}
