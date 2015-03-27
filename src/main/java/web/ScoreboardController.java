package web;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import controller.ChessGame;
import controller.Player;
import controller.Score;
import controller.ScoreFormatter;
import controller.ScoreboardHistory;

@Controller
public class ScoreboardController {
	private final ScoreFormatter scoreFormatter;
	private final ChessGame chessGame;
	private String timestamp;
	private List<Score> scoreboard;
	private ScoreboardHistory scoreboardHistory;

	@Autowired
	public ScoreboardController(List<Player> players) {
		chessGame = new ChessGame(players);
		scoreFormatter = new ScoreFormatter();
		scoreboardHistory = new ScoreboardHistory();
	}
	
	@PostConstruct
	public void runSimulator() {
		Map<Class<? extends Player>, Integer> results = chessGame.runGame();
		scoreboard = scoreFormatter.score(results);
		markPlayersUsingReflection();
		scoreboardHistory.loadFromDisk();
		scoreboardHistory.addScoreboard(scoreboard);
		scoreboardHistory.saveToDisk();
		timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
		.format(Calendar.getInstance().getTime());
	}

	@RequestMapping("/*")
	public String scoreboard(Model model) {
		model.addAttribute("timestamp", timestamp);
		model.addAttribute("total", scoreboardHistory.getPlayersWithTotalScore());
		model.addAttribute("results", scoreboard);
		model.addAttribute("history", scoreboardHistory.getScoreboards());
		return "scoreboard";
	}

	private void markPlayersUsingReflection() {
		for (Score score : scoreboard) {
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
