package console;

import static java.lang.System.out;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import player.SimplePlayer;
import player.TestPlayer;
import controller.Board;
import controller.ChessGame;
import controller.Move;
import controller.Player;

public class WatchGameBetween {
	private static final List<Board> board = new ArrayList<Board>();

	public static void main(String[] args) {
		Player testPlayer = new TestPlayer();
		WatchablePlayerBlack watchablePlayerBlack = new WatchablePlayerBlack();
		ChessGame chessGame = new ChessGame(Arrays.asList(testPlayer, watchablePlayerBlack));
		chessGame.runGame(testPlayer.getClass(), watchablePlayerBlack.getClass(), false);

		for (Board b : board) {
			sleep();
			out.println(b.toString());
		}
	}

	public static class WatchablePlayerBlack extends SimplePlayer {
		@Override
		public Move getMove(Board board, Player enemy) {
			WatchGameBetween.board.add(board.copy());
			Move next = super.getMove(board, enemy);
			return next;
		}
	}

	private static void sleep() {
		try {
			Thread.sleep(0);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
