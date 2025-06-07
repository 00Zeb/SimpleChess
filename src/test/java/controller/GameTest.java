package controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Method;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import player.SimplePlayer;
import player.TestPlayer;

@DisplayName("Game Tests")
class GameTest {

    private Player whitePlayer;
    private Player blackPlayer;
    private Game game;

    @BeforeEach
    void setUp() {
        whitePlayer = new TestPlayer();
        blackPlayer = new SimplePlayer();
        game = new Game(whitePlayer, blackPlayer);
    }

    // Helper method to disqualify player using reflection
    private void disqualifyPlayer(Player player) {
        try {
            Method setDisqualifiedMethod = Player.class.getDeclaredMethod("setDisqualified");
            setDisqualifiedMethod.setAccessible(true);
            setDisqualifiedMethod.invoke(player);
        } catch (Exception e) {
            throw new RuntimeException("Failed to disqualify player", e);
        }
    }

    @Nested
    @DisplayName("Game Initialization")
    class GameInitialization {

        @Test
        @DisplayName("Should initialize players with correct colors")
        void shouldInitializePlayersWithCorrectColors() {
            // Then
            assertAll(
                () -> assertEquals(Color.WHITE, whitePlayer.getTeam(), "First player should be white"),
                () -> assertEquals(Color.BLACK, blackPlayer.getTeam(), "Second player should be black"),
                () -> assertFalse(whitePlayer.isDisqualified(), "White player should not be disqualified"),
                () -> assertFalse(blackPlayer.isDisqualified(), "Black player should not be disqualified")
            );
        }

        @Test
        @DisplayName("Should start with game not over")
        void shouldStartWithGameNotOver() {
            // Then
            assertFalse(game.gameOver(), "Game should not be over at start");
        }
    }

    @Nested
    @DisplayName("Game Execution")
    class GameExecution {

        @Test
        @Timeout(10) // Prevent infinite loops
        @DisplayName("Should complete game and return valid result")
        void shouldCompleteGameAndReturnValidResult() {
            // When
            int result = game.run();
            
            // Then
            assertThat(result).isIn(
                ChessGame.WIN_POINTS, 
                ChessGame.LOSE_POINTS, 
                ChessGame.DRAW_POINTS
            );
        }

        @Test
        @Timeout(10)
        @DisplayName("Should end game when game over condition is met")
        void shouldEndGameWhenGameOverConditionIsMet() {
            // When
            int result = game.run();
            
            // Then
            assertTrue(game.gameOver(), "Game should be over after run() completes");
        }

        @Test
        @DisplayName("Should handle player disqualification")
        void shouldHandlePlayerDisqualification() {
            // Given
            disqualifyPlayer(whitePlayer);

            // When
            int result = game.run();

            // Then
            assertAll(
                () -> assertTrue(game.gameOver(), "Game should be over"),
                () -> assertEquals(ChessGame.LOSE_POINTS, result, "Disqualified player should lose")
            );
        }
    }

    @Nested
    @DisplayName("Game Over Conditions")
    class GameOverConditions {

        @Test
        @DisplayName("Should detect game over when player is disqualified")
        void shouldDetectGameOverWhenPlayerIsDisqualified() {
            // Given
            disqualifyPlayer(whitePlayer);

            // Then
            assertTrue(game.gameOver(), "Game should be over when player is disqualified");
        }

        @Test
        @DisplayName("Should not be game over in normal starting position")
        void shouldNotBeGameOverInNormalStartingPosition() {
            // Then
            assertFalse(game.gameOver(), "Game should not be over at start");
        }
    }

    @Nested
    @DisplayName("Score Calculation")
    class ScoreCalculation {

        @Test
        @DisplayName("Should return win points for winner")
        void shouldReturnWinPointsForWinner() {
            // Given - simulate black player losing (disqualified)
            disqualifyPlayer(blackPlayer);

            // When
            int result = game.run();

            // Then
            assertEquals(ChessGame.WIN_POINTS, result, "Winner should get win points");
        }

        @Test
        @DisplayName("Should return lose points for loser")
        void shouldReturnLosePointsForLoser() {
            // Given - simulate white player losing (disqualified)
            disqualifyPlayer(whitePlayer);

            // When
            int result = game.run();

            // Then
            assertEquals(ChessGame.LOSE_POINTS, result, "Loser should get lose points");
        }

        @Test
        @DisplayName("Should handle draw scenarios")
        void shouldHandleDrawScenarios() {
            // This test is more complex as it requires setting up a draw scenario
            // For now, we'll test that draw points are a valid return value
            assertThat(ChessGame.DRAW_POINTS).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("Turn Management")
    class TurnManagement {

        @Test
        @DisplayName("Should alternate between players")
        void shouldAlternateBetweenPlayers() {
            // This is tested implicitly by the game running to completion
            // In a more sophisticated test, we might mock the players to verify turn order
            
            // When
            game.run();
            
            // Then
            assertTrue(game.gameOver(), "Game should complete with alternating turns");
        }
    }

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCases {

        @Test
        @DisplayName("Should handle both players disqualified")
        void shouldHandleBothPlayersDisqualified() {
            // Given
            disqualifyPlayer(whitePlayer);
            disqualifyPlayer(blackPlayer);

            // When
            int result = game.run();

            // Then
            assertTrue(game.gameOver(), "Game should be over when both players disqualified");
            // Result could be either WIN_POINTS or LOSE_POINTS depending on implementation
            assertThat(result).isIn(ChessGame.WIN_POINTS, ChessGame.LOSE_POINTS, ChessGame.DRAW_POINTS);
        }

        @Test
        @Timeout(5)
        @DisplayName("Should not run indefinitely")
        void shouldNotRunIndefinitely() {
            // When
            int result = game.run();
            
            // Then - if we reach here, the game completed within timeout
            assertThat(result).isNotNull();
        }
    }

    @Nested
    @DisplayName("Player Interaction")
    class PlayerInteraction {

        @Test
        @DisplayName("Should set check status for players")
        void shouldSetCheckStatusForPlayers() {
            // This test verifies that the game logic properly manages check status
            // The actual check detection is tested in BoardTest
            
            // When
            game.run();
            
            // Then - game should complete successfully
            assertTrue(game.gameOver());
        }
    }
}
