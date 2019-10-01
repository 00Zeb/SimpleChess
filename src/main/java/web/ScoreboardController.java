package web;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import controller.ChessGame;
import controller.FileHandler;
import controller.HistoryData;
import controller.Player;
import controller.Score;
import controller.ScoreFormatter;

@Controller
public class ScoreboardController {
	private final ScoreFormatter scoreFormatter;
	private final ChessGame chessGame;
	private String timestamp;
	private List<Score> scoreboard;
	private HistoryData historyData;

	@Autowired
	public ScoreboardController(List<Player> players) {
		chessGame = new ChessGame(players);
		scoreFormatter = new ScoreFormatter();
		if((historyData = (HistoryData) FileHandler.get("HistoryData")) == null)
		{
			historyData = new HistoryData();
		}
	}
	
	@PostConstruct
	public void runSimulator() {
		Map<Class<? extends Player>, Integer> results = chessGame.runGame();
		scoreboard = scoreFormatter.score(results);
		timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
		.format(Calendar.getInstance().getTime());

		historyData.addScoreboard(scoreboard);
		historyData.addTimestamp(timestamp);
		FileHandler.set(historyData, "HistoryData");
	}

//	@RequestMapping("/*")
//	public String scoreboard(Model model) {
//		model.addAttribute("otherScoreboard", "/cc");
//		model.addAttribute("timestamp", timestamp);
//		model.addAttribute("totalScore", historyData.getTotalScoreWithoutCybercomPlayers());
//		model.addAttribute("currentScoreboard", scoreboard);
//		model.addAttribute("previousScoreboards", historyData.getPreviousScoreboards());
//		return "scoreboard";
//	}
//
//	@RequestMapping("/cc")
//	public String scoreboardCc(Model model) {
//		model.addAttribute("otherScoreboard", "/*");
//		model.addAttribute("timestamp", timestamp);
//		model.addAttribute("totalScore", historyData.getTotalScore());
//		model.addAttribute("currentScoreboard", scoreboard);
//		model.addAttribute("previousScoreboards", historyData.getPreviousScoreboards());
//		return "scoreboard";
//	} 
}
