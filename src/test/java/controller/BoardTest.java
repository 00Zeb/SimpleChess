package controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import pieces.King;
import pieces.Pawn;
import pieces.Queen;
import pieces.Rook;
import player.TestPlayer;

@DisplayName("Board Tests")
class BoardTest {

    private Board board;
    private Player whitePlayer;
    private Player blackPlayer;

    @BeforeEach
    void setUp() {
        board = new Board();
        board.initialize();
        
        whitePlayer = new TestPlayer();
        whitePlayer.setTeam(Color.WHITE);
        
        blackPlayer = new TestPlayer();
        blackPlayer.setTeam(Color.BLACK);
    }

    @Nested
    @DisplayName("Board Initialization")
    class BoardInitialization {

        @Test
        @DisplayName("Should initialize 8x8 board")
        void shouldInitialize8x8Board() {
            // Given - board is initialized in @BeforeEach
            
            // Then
            Field[][] fields = board.getFields();
            assertAll(
                () -> assertNotNull(fields, "Fields should not be null"),
                () -> assertEquals(8, fields.length, "Board should be 8 wide"),
                () -> assertEquals(8, fields[0].length, "Board should be 8 tall")
            );
        }

        @Test
        @DisplayName("Should place all pieces in starting positions")
        void shouldPlaceAllPiecesInStartingPositions() {
            // Then verify white pieces
            assertThat(board.getFields()[0][0].getPiece()).isInstanceOf(Rook.class);
            assertThat(board.getFields()[4][0].getPiece()).isInstanceOf(King.class);
            assertThat(board.getFields()[3][0].getPiece()).isInstanceOf(Queen.class);
            
            // Verify black pieces
            assertThat(board.getFields()[0][7].getPiece()).isInstanceOf(Rook.class);
            assertThat(board.getFields()[4][7].getPiece()).isInstanceOf(King.class);
            assertThat(board.getFields()[3][7].getPiece()).isInstanceOf(Queen.class);
            
            // Verify pawns
            for (int i = 0; i < 8; i++) {
                assertThat(board.getFields()[i][1].getPiece()).isInstanceOf(Pawn.class);
                assertThat(board.getFields()[i][6].getPiece()).isInstanceOf(Pawn.class);
            }
        }

        @Test
        @DisplayName("Should have empty middle squares")
        void shouldHaveEmptyMiddleSquares() {
            // Then verify middle rows are empty
            for (int i = 0; i < 8; i++) {
                for (int j = 2; j <= 5; j++) {
                    assertFalse(board.getFields()[i][j].hasPiece(), 
                        String.format("Square [%d][%d] should be empty", i, j));
                }
            }
        }
    }

    @Nested
    @DisplayName("Piece Movement")
    class PieceMovement {

        @Test
        @DisplayName("Should move piece successfully")
        void shouldMovePieceSuccessfully() {
            // Given
            Piece pawn = board.getFields()[0][1].getPiece(); // White pawn
            Move move = new Move(pawn, new Point(0, 2));
            
            // When
            boolean captured = board.movePiece(move);
            
            // Then
            assertAll(
                () -> assertFalse(captured, "Should not capture anything"),
                () -> assertNull(board.getFields()[0][1].getPiece(), "Original square should be empty"),
                () -> assertEquals(pawn, board.getFields()[0][2].getPiece(), "Piece should be at new position"),
                () -> assertEquals(new Point(0, 2), pawn.getPos(), "Piece position should be updated")
            );
        }

        @Test
        @DisplayName("Should detect capture when moving to occupied square")
        void shouldDetectCaptureWhenMovingToOccupiedSquare() {
            // Given - place a white pawn where it can capture a black piece
            Piece whitePawn = new Pawn(Color.WHITE, new Point(3, 4));
            board.getFields()[3][4].setPiece(whitePawn);
            
            Piece blackPawn = new Pawn(Color.BLACK, new Point(4, 5));
            board.getFields()[4][5].setPiece(blackPawn);
            
            Move captureMove = new Move(whitePawn, new Point(4, 5));
            
            // When
            boolean captured = board.movePiece(captureMove);
            
            // Then
            assertAll(
                () -> assertTrue(captured, "Should detect capture"),
                () -> assertEquals(whitePawn, board.getFields()[4][5].getPiece(), "Capturing piece should be at target"),
                () -> assertNull(board.getFields()[3][4].getPiece(), "Original square should be empty")
            );
        }

        @ParameterizedTest
        @CsvSource({
            "0, 1, 0, 3", // Pawn two squares forward
            "1, 0, 3, 0", // Knight move
            "0, 0, 0, 4"  // Rook move
        })
        @DisplayName("Should handle various piece movements")
        void shouldHandleVariousPieceMovements(int fromX, int fromY, int toX, int toY) {
            // Given
            Piece piece = board.getFields()[fromX][fromY].getPiece();
            if (piece != null) {
                Move move = new Move(piece, new Point(toX, toY));
                
                // When
                board.movePiece(move);
                
                // Then
                assertEquals(new Point(toX, toY), piece.getPos());
            }
        }
    }

    @Nested
    @DisplayName("Check Detection")
    class CheckDetection {

        @Test
        @DisplayName("Should detect check correctly")
        void shouldDetectCheckCorrectly() {
            // Given - create a simple check scenario
            Board testBoard = new Board();

            // Initialize the fields first
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    Color color = (i + j) % 2 == 0 ? Color.BLACK : Color.WHITE;
                    testBoard.getFields()[i][j] = new Field(new Point(i, j), color);
                }
            }

            // Place white king
            King whiteKing = new King(Color.WHITE, new Point(4, 0));
            testBoard.getFields()[4][0].setPiece(whiteKing);

            // Place black rook that attacks the king
            Rook blackRook = new Rook(Color.BLACK, new Point(4, 7));
            testBoard.getFields()[4][7].setPiece(blackRook);

            // When
            boolean isCheck = testBoard.isCheck(whitePlayer, blackPlayer);

            // Then
            assertTrue(isCheck, "Should detect check when king is under attack");
        }

        @Test
        @DisplayName("Should not detect check when king is safe")
        void shouldNotDetectCheckWhenKingIsSafe() {
            // Given - standard starting position
            
            // When
            boolean isCheck = board.isCheck(whitePlayer, blackPlayer);
            
            // Then
            assertFalse(isCheck, "Should not detect check in starting position");
        }
    }

    @Nested
    @DisplayName("Board Copy")
    class BoardCopy {

        @Test
        @DisplayName("Should create independent copy of board")
        void shouldCreateIndependentCopyOfBoard() {
            // When
            Board copy = board.copy();
            
            // Then
            assertAll(
                () -> assertNotNull(copy, "Copy should not be null"),
                () -> assertEquals(board, copy, "Copy should be equal to original"),
                () -> assertThat(copy).isNotSameAs(board),
                () -> assertThat(copy.getFields()).isNotSameAs(board.getFields())
            );
        }

        @Test
        @DisplayName("Should maintain independence after modification")
        void shouldMaintainIndependenceAfterModification() {
            // Given
            Board copy = board.copy();
            Piece originalPawn = board.getFields()[0][1].getPiece();
            
            // When - move piece on original board
            Move move = new Move(originalPawn, new Point(0, 2));
            board.movePiece(move);
            
            // Then - copy should remain unchanged
            assertNotNull(copy.getFields()[0][1].getPiece(), "Copy should still have piece at original position");
            assertNull(copy.getFields()[0][2].getPiece(), "Copy should not have piece at new position");
        }
    }

    @Nested
    @DisplayName("King Finding")
    class KingFinding {

        @Test
        @DisplayName("Should find white king")
        void shouldFindWhiteKing() {
            // When
            Piece whiteKing = board.getKing(whitePlayer);
            
            // Then
            assertAll(
                () -> assertNotNull(whiteKing, "Should find white king"),
                () -> assertEquals(PieceType.KING, whiteKing.getType(), "Should be a king"),
                () -> assertEquals(Color.WHITE, whiteKing.getTeam(), "Should be white"),
                () -> assertEquals(new Point(4, 0), whiteKing.getPos(), "Should be at correct position")
            );
        }

        @Test
        @DisplayName("Should find black king")
        void shouldFindBlackKing() {
            // When
            Piece blackKing = board.getKing(blackPlayer);
            
            // Then
            assertAll(
                () -> assertNotNull(blackKing, "Should find black king"),
                () -> assertEquals(PieceType.KING, blackKing.getType(), "Should be a king"),
                () -> assertEquals(Color.BLACK, blackKing.getTeam(), "Should be black"),
                () -> assertEquals(new Point(4, 7), blackKing.getPos(), "Should be at correct position")
            );
        }

        @Test
        @DisplayName("Should return null when king is captured")
        void shouldReturnNullWhenKingIsCaptured() {
            // Given - remove the white king
            board.getFields()[4][0].setPiece(null);
            
            // When
            Piece king = board.getKing(whitePlayer);
            
            // Then
            assertNull(king, "Should return null when king is not found");
        }
    }
}
