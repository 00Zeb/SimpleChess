package controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChessGame {
	public static final int WIN_POINTS = 3;
	public static final int DRAW_POINTS = 1;
	public static final int LOSE_POINTS = 0;
	private static final int GAMES_PER_PAIR = 10;
	
	private final Class<Player>[] classes;
	private final Map<Class<? extends Player>, Integer> scores = new HashMap<>();

	@SuppressWarnings("unchecked")
	public ChessGame(List<Player> players) {
		classes = new Class[players.size()];
		int i = 0;
		for (Player p : players) {
			Class<Player> class1 = (Class<Player>) p.getClass();
			scores.put(class1, 0);
			classes[i] = class1;
			i++;
		}
	}
	
	
	public Map<Class<? extends Player>, Integer> runGame() {
		generateResult();
		return scores;
	}

	public void generateResult() {
		for (int i = 0; i < classes.length - 1; i++) {
			for (int j = i + 1; j < classes.length; j++) {
				for (int k = 0; k < GAMES_PER_PAIR; k++) {
					runGame(classes[i], classes[j], k >= GAMES_PER_PAIR / 2);
				}
			}
		}
	}

	public void runGame(Class<? extends Player> class1, Class<? extends Player> class2,
			boolean switchSides) {
		if (switchSides) { // switch sides
			Class<? extends Player> tempClass = class2;
			class2 = class1;
			class1 = tempClass;
		}
		try {
			Player player1 = class1.newInstance();
			Player player2 = class2.newInstance();
			int result = new Game(player1, player2).run();

			addResult(class1, result, false);
			addResult(class2, result, true);
		} catch (Exception e) {
			System.out.println("Error in game between " + class1 + " and " + class2);
		}
	}

	private void addResult(Class<? extends Player> player, int result, boolean reverse) {
		if (reverse) {
			if (result == WIN_POINTS) {
				result = LOSE_POINTS;
			} else if (result == LOSE_POINTS) {
				result = WIN_POINTS;
			}
		}
		int newScore = scores.get(player) + result;
		scores.put(player, newScore);
	}
}
