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
	private final String timestamp;
	private final List<String> results;

	public ScoreboardController() {
		results = new ChessGame().runGame();
		timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(Calendar.getInstance().getTime());
	}

	@RequestMapping("/scoreboard")
	public String scoreboard(Model model) {
		model.addAttribute("timestamp", timestamp);
		model.addAttribute("results", results);
		return "scoreboard";
	}
}
