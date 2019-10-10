package controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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
			scoringChart.add(new Score(currPlayer, bestScore, place++));
			scores.remove(currPlayer);
		}
//		markPlayersUsingReflection(scoringChart);
		return scoringChart;
	}

	private void markPlayersUsingReflection(List<Score> scoringChart) {
		for (Score score : scoringChart) {
			File file = new File("src/main/java/player/"+ score.getPlayer().getSimpleName() + ".java");
			BufferedReader br = null;

			try {
				br = new BufferedReader(new FileReader(file));
				String line = null;
				while ((line = br.readLine()) != null) {
					if (line.contains("java.lang.reflect")) {
						score.setReflection("*");
						break;
					}
				}
			}catch (IOException e) {
				score.setReflection("*");
				e.printStackTrace();
			} finally {
				if (br != null) {
					try {
						br.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

}

