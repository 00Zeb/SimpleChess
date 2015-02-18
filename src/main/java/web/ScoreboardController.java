package web;

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

@Controller
public class ScoreboardController {
	private final ScoreFormatter scoreFormatter;
	private final ChessGame chessGame;
	private String timestamp;
	private List<Score> scoreboard;

	@Autowired
	public ScoreboardController(List<Player> players) {
		chessGame = new ChessGame(players);
		scoreFormatter = new ScoreFormatter();
	}
	
	@PostConstruct
	public void runSimulator() {
		Map<Class<? extends Player>, Integer> results = chessGame.runGame();
		scoreboard = scoreFormatter.score(results);
		timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
		.format(Calendar.getInstance().getTime());
	}

	@RequestMapping("/*")
	public String scoreboard(Model model) {
		model.addAttribute("timestamp", timestamp);
		model.addAttribute("results", scoreboard);
		return "scoreboard";
	}
}
