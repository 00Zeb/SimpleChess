package web;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.DatasetUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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
		historyData.saveTotalScore(timestamp);
		FileHandler.set(historyData, "HistoryData");
	}

	@RequestMapping("/*")
	public String scoreboard(Model model) {
		model.addAttribute("otherScoreboard", "/cc");
		model.addAttribute("timestamp", timestamp);
		model.addAttribute("totalScore", historyData.getTotalScoreWithoutCybercomPlayers());
		model.addAttribute("currentScoreboard", scoreboard);
		model.addAttribute("previousScoreboards", historyData.getPreviousScoreboards());
		return "scoreboard";
	}

	@RequestMapping("/cc")
	public String scoreboardCc(Model model) {
		model.addAttribute("otherScoreboard", "/*");
		model.addAttribute("timestamp", timestamp);
		model.addAttribute("totalScore", historyData.getTotalScore());
		model.addAttribute("currentScoreboard", scoreboard);
		model.addAttribute("previousScoreboards", historyData.getPreviousScoreboards());
		return "scoreboard";
	}
	
	private String[] getPlayerNames()
	{
		int numberOfPlayers = scoreboard.size();
		String[] playerNames = new String[numberOfPlayers];

		for(int i = 0; i < numberOfPlayers; i++)
		{
			playerNames[i] = scoreboard.get(i).getName();
		}
		return playerNames;
	}
	
	private String[] getNumberOfTimes()
	{
		int numberOfTimes = historyData.getTotalScoreChart().keySet().size();
		String[] times = new String[numberOfTimes + 1];
		times[0] = "Start";
		int counter = 1;
		for(String key : historyData.getTotalScoreChart().keySet())
		{
			times[counter] = "Round " + counter;
			counter++;
		}
		return times;		
	}
	
	@RequestMapping(value = "/chart", method = RequestMethod.GET)
	public void drawPieChart(HttpServletResponse response)
	{
		response.setContentType("image/png");
	
		String[] playerNames = getPlayerNames();
		String[] times = getNumberOfTimes();
	
		double[][] data = new double[playerNames.length][times.length] ;
		
		for(int i = 0; i < playerNames.length; i++)
		{
			data[i] = getDataForPlayer(playerNames[i]);
		}
		
		CategoryDataset newData = DatasetUtilities.createCategoryDataset(
				playerNames, times, data);

		JFreeChart chart = ChartFactory.createLineChart("Result", "Rounds",  
				"Points", newData, PlotOrientation.VERTICAL, true, true, false);

		try
		{
			ChartUtilities.writeChartAsPNG(response.getOutputStream(), chart, 1600, 600);
			response.getOutputStream().close();
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
		}
	}
	
	private double[] getDataForPlayer(String player)
	{
		double[] scoress = new double[historyData.getScore().size()+1];
		
		List<List<Score>> scoreboards = historyData.getScore();
		
		scoress[0] = 0.0;
		int counter = 1;
		
		for(int i = scoreboards.size()-1; i > -1; i--)
		{
			List<Score> scoreboard = scoreboards.get(i);
			for(int j = 0; j < scoreboard.size(); j++)
			{
				if(scoreboard.get(j).getName().equals(player))
				{
					if(i == scoreboards.size()-1)
					{
						scoress[counter] = scoreboard.get(j).getScore();
					}
					else
					{
						scoress[counter] = scoress[counter -1] + scoreboard.get(j).getScore();
					}	
				}
			}
			counter = counter + 1;
		}
		
		return scoress;
	}
}
