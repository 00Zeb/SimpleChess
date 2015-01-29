package controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;

public class ChessGame {
	public static final int WIN_POINTS = 3;
	public static final int DRAW_POINTS = 1;
	public static final int LOSE_POINTS = 0;
	private static final int GAMES_PER_PAIR = 10;
	private final Class<Player>[] classes;
	private final Map<Class<? extends Player>, Integer> scores = new HashMap<>();

	public ChessGame() {
		Set<Class<? extends Player>> subTypesOf = new Reflections("player")
				.getSubTypesOf(Player.class);
		classes = subTypesOf.toArray(new Class[subTypesOf.size()]);
		for (Class player : classes) {
			scores.put(player, 0);
		}
	}
	
	public List<String> runGame() {
		generateResult();
		return score();
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

	private void runGame(Class<? extends Player> class1, Class<? extends Player> class2,
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

	private List<String> score() {
		int place = 1;
		List<String> scoringChart = new ArrayList<String>();
		while (scores.size() > 0) {
			StringBuilder playerResult = new StringBuilder();
			int bestScore = 0;
			Class<?> currPlayer = null;
			for (Class player : scores.keySet()) {
				int playerScore = scores.get(player);
				if (scores.get(player) >= bestScore) {
					bestScore = playerScore;
					currPlayer = player;
				}
			}
			playerResult.append(String.format("%02d", place++)).append(") ")
			.append(currPlayer.getSimpleName()).append(": ").append(bestScore);
			scoringChart.add(playerResult.toString());
			scores.remove(currPlayer);
		}
		return scoringChart;
	}
}
