package console;

import static java.lang.System.out;

import java.util.Arrays;

import player.TestPlayer;
import player.Zeb;
import controller.Board;
import controller.ChessGame;
import controller.Move;
import controller.Player;

public class WatchGameBetween {

	public static void main(String[] args) {
		WatchablePlayerWhite watchablePlayerWhite = new WatchablePlayerWhite();
		WatchablePlayerBlack watchablePlayerBlack = new WatchablePlayerBlack();
		ChessGame chessGame = new ChessGame(Arrays.asList(watchablePlayerWhite,watchablePlayerBlack));
		chessGame.runGame(watchablePlayerWhite.getClass(), watchablePlayerBlack.getClass(),
				false);
	}

	public static class WatchablePlayerWhite extends TestPlayer {
		@Override
		public Move getMove(Board board, Player enemy) {
			sleep();
			out.println(board.toString());
			return super.getMove(board, enemy);
		}
	}
	
	public static class WatchablePlayerBlack extends Zeb {
		@Override
		public Move getMove(Board board, Player enemy) {
			sleep();
			out.println(board.toString());
			Move next = super.getMove(board, enemy);
			return next;
		}
	}

	private static void sleep() {
		try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
