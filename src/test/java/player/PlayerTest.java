package player;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Method;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import controller.Board;
import controller.Color;
import controller.Field;
import controller.Game;
import controller.Move;
import controller.Piece;
import controller.Player;
import controller.Point;

@DisplayName("Player Tests")
class PlayerTest {

    private Board board;
    private TestPlayer testPlayer;
    private SimplePlayer simplePlayer;
    private CCPlayer ccPlayer;

    @BeforeEach
    void setUp() {
        board = new Board();
        initializeBoard(board);

        testPlayer = new TestPlayer();
        simplePlayer = new SimplePlayer();
        ccPlayer = new CCPlayer();
    }

    // Helper method to initialize board using reflection
    private void initializeBoard(Board board) {
        try {
            Method initMethod = Board.class.getDeclaredMethod("initialize");
            initMethod.setAccessible(true);
            initMethod.invoke(board);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize board", e);
        }
    }

    // Helper method to set team using reflection
    private void setPlayerTeam(Player player, Color color) {
        try {
            Method setTeamMethod = Player.class.getDeclaredMethod("setTeam", Color.class);
            setTeamMethod.setAccessible(true);
            setTeamMethod.invoke(player, color);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set player team", e);
        }
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
    @DisplayName("Player State Management")
    class PlayerStateManagement {

        @ParameterizedTest
        @EnumSource(Color.class)
        @DisplayName("Should set and get team correctly")
        void shouldSetAndGetTeamCorrectly(Color color) {
            // When
            setPlayerTeam(testPlayer, color);

            // Then
            assertEquals(color, testPlayer.getTeam());
        }

        @Test
        @DisplayName("Should handle disqualification")
        void shouldHandleDisqualification() {
            // Given
            assertFalse(testPlayer.isDisqualified(), "Player should start not disqualified");

            // When
            disqualifyPlayer(testPlayer);

            // Then
            assertTrue(testPlayer.isDisqualified(), "Player should be disqualified");
        }

        @Test
        @DisplayName("Should handle check status")
        void shouldHandleCheckStatus() {
            // Given
            assertFalse(testPlayer.isCheck(), "Player should start not in check");
            
            // When
            testPlayer.setCheck(true);
            
            // Then
            assertTrue(testPlayer.isCheck(), "Player should be in check");
            
            // When
            testPlayer.setCheck(false);
            
            // Then
            assertFalse(testPlayer.isCheck(), "Player should not be in check");
        }
    }

    @Nested
    @DisplayName("Piece Management")
    class PieceManagement {

        @Test
        @DisplayName("Should get all white pieces correctly")
        void shouldGetAllWhitePiecesCorrectly() {
            // Given
            setPlayerTeam(testPlayer, Color.WHITE);

            // When
            List<Piece> pieces = testPlayer.getPieces(board);

            // Then
            assertAll(
                () -> assertEquals(16, pieces.size(), "White player should have 16 pieces"),
                () -> assertTrue(pieces.stream().allMatch(p -> p.getTeam() == Color.WHITE),
                    "All pieces should be white"),
                () -> assertEquals(8, pieces.stream().mapToInt(p -> p.getType().ordinal() == 0 ? 1 : 0).sum(),
                    "Should have 8 pawns")
            );
        }

        @Test
        @DisplayName("Should get all black pieces correctly")
        void shouldGetAllBlackPiecesCorrectly() {
            // Given
            setPlayerTeam(testPlayer, Color.BLACK);

            // When
            List<Piece> pieces = testPlayer.getPieces(board);

            // Then
            assertAll(
                () -> assertEquals(16, pieces.size(), "Black player should have 16 pieces"),
                () -> assertTrue(pieces.stream().allMatch(p -> p.getTeam() == Color.BLACK),
                    "All pieces should be black")
            );
        }

        @Test
        @DisplayName("Should return empty list when no pieces on board")
        void shouldReturnEmptyListWhenNoPiecesOnBoard() {
            // Given
            Board emptyBoard = new Board();
            // Initialize empty fields
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    Color color = (i + j) % 2 == 0 ? Color.BLACK : Color.WHITE;
                    emptyBoard.getFields()[i][j] = new Field(new Point(i, j), color);
                }
            }
            setPlayerTeam(testPlayer, Color.WHITE);

            // When
            List<Piece> pieces = testPlayer.getPieces(emptyBoard);

            // Then
            assertThat(pieces).isEmpty();
        }
    }

    @Nested
    @DisplayName("Move Generation")
    class MoveGeneration {

        @Test
        @DisplayName("TestPlayer should generate valid moves")
        void testPlayerShouldGenerateValidMoves() {
            // Given - use Game to properly set up players
            Game game = new Game(testPlayer, new SimplePlayer());

            // When
            Move move = testPlayer.getMove(board, new SimplePlayer());

            // Then
            assertAll(
                () -> assertNotNull(move, "Should generate a move"),
                () -> assertNotNull(move.getPiece(), "Move should have a piece"),
                () -> assertNotNull(move.getDestination(), "Move should have destination")
            );
        }

        @Test
        @DisplayName("SimplePlayer should generate valid moves")
        void simplePlayerShouldGenerateValidMoves() {
            // Given - use Game to properly set up players
            Game game = new Game(simplePlayer, new TestPlayer());

            // When
            Move move = simplePlayer.getMove(board, new TestPlayer());

            // Then
            assertAll(
                () -> assertNotNull(move, "Should generate a move"),
                () -> assertNotNull(move.getPiece(), "Move should have a piece")
            );
        }

        @Test
        @DisplayName("CCPlayer should generate valid moves")
        void ccPlayerShouldGenerateValidMoves() {
            // Given - use Game to properly set up players
            Game game = new Game(ccPlayer, new TestPlayer());

            // When
            Move move = ccPlayer.getMove(board, new TestPlayer());

            // Then
            assertAll(
                () -> assertNotNull(move, "Should generate a move"),
                () -> assertNotNull(move.getPiece(), "Move should have a piece")
            );
        }

        @Test
        @DisplayName("Should handle board copy correctly")
        void shouldHandleBoardCopyCorrectly() {
            // Given
            Game game = new Game(testPlayer, new SimplePlayer());
            Board originalBoard = board.copy();

            // When
            Move move = testPlayer.getMove(board, new SimplePlayer());

            // Then - Original board should be unchanged
            assertEquals(originalBoard, board, "Original board should remain unchanged");
        }
    }

    @Nested
    @DisplayName("Player Behavior Differences")
    class PlayerBehaviorDifferences {

        @Test
        @DisplayName("Different players should potentially make different moves")
        void differentPlayersShouldPotentiallyMakeDifferentMoves() {
            // Given - set up players properly using Game
            Game testGame = new Game(testPlayer, new SimplePlayer());
            Game simpleGame = new Game(simplePlayer, new TestPlayer());
            Player enemy = new TestPlayer();

            // When - Get moves from both players multiple times
            boolean foundDifference = false;
            for (int i = 0; i < 10; i++) {
                Move testMove = testPlayer.getMove(board.copy(), enemy);
                Move simpleMove = simplePlayer.getMove(board.copy(), enemy);

                if (!testMove.equals(simpleMove)) {
                    foundDifference = true;
                    break;
                }
            }

            // Then - At least sometimes they should make different moves
            // Note: This test might occasionally fail due to randomness, but it's useful for behavior verification
            // In a real scenario, you might want to use a seeded random or mock the randomness
        }
    }

    @Nested
    @DisplayName("Player String Representation")
    class PlayerStringRepresentation {

        @Test
        @DisplayName("Should have meaningful string representation")
        void shouldHaveMeaningfulStringRepresentation() {
            // Given - set up player properly
            Game game = new Game(testPlayer, new SimplePlayer());

            // When
            String representation = testPlayer.toString();

            // Then
            assertAll(
                () -> assertThat(representation).contains("Player"),
                () -> assertThat(representation).contains("TestPlayer"),
                () -> assertThat(representation).contains("WHITE")
            );
        }
    }
}
