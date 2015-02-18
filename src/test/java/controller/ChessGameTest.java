package controller;

import static org.mockito.Mockito.verify;

import java.util.Arrays;

import org.junit.Test;
import org.mockito.Mockito;

import player.SimplePlayer;
import player.TestPlayer;

public class ChessGameTest {

	@Test
	public void runGame() {
		ChessGame chessGame = Mockito.spy(new ChessGame(Arrays.asList(new TestPlayer(), new SimplePlayer())));
		chessGame.runGame();
		verify(chessGame, Mockito.times(10)).runGame(anyplayer(), anyplayer(),
				Mockito.anyBoolean());
		;
	}

	private Class<Player> anyplayer() {
		return Mockito.anyObject();
	}
}
