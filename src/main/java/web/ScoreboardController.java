package web;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import controller.ChessGame;

@Controller
public class ScoreboardController {
	private final ChessGame chessGame = new ChessGame();
	private final String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance()
			.getTime()) + "\n";
	
	@RequestMapping("/scoreboard")
	public String scoreboard(Model model) {
		List<String> results = chessGame.runGame();
		model.addAttribute("timestamp", timestamp);
		model.addAttribute("results", results);
		return "scoreboard";
	}
}
