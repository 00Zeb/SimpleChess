package controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ScoreFormatter {

	public List<Score> score(Map<Class<? extends Player>, Integer> scores) {
		int place = 1;
		List<Score> scoringChart = new ArrayList<Score>();
		while (scores.size() > 0) {
			int bestScore = 0;
			Class<?> currPlayer = null;
			for (Class<?> player : scores.keySet()) {
				int playerScore = scores.get(player);
				if (scores.get(player) >= bestScore) {
					bestScore = playerScore;
					currPlayer = player;
				}
			}
			scoringChart.add(new Score(currPlayer.getSimpleName(), bestScore, place++));
			scores.remove(currPlayer);
		}
		return scoringChart;
	}

}
