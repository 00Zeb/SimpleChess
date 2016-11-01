package controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class HistoryData implements Serializable{

	private static final long serialVersionUID = 1L;
	private List<List<Score>> scoreboards = new ArrayList<>();
	private List<String> timetamps = new ArrayList<>();
	Map<String, Map<String, Integer>> mapTotal = new HashMap<>();
	
	public void addScoreboard(List<Score> score) {
		scoreboards.add(0, score);
	}
	
	public List<List<Score>> getScore()
	{
		return scoreboards;
	}
	
	public List<String> getMyTimetamps() {
		return timetamps;
	}
	public void addTimestamp(String timestamp) {
		this.timetamps.add(timestamp);
	}
	public List<List<Score>> getPreviousScoreboards() {
		return scoreboards.subList(1, scoreboards.size());
	}
	
	public void saveTotalScore(String timestamp)
	{
		Map<String, Integer> players = new HashMap<>();

		for (List<Score> scoreboard : scoreboards) {
			for (Score score : scoreboard) {
				if (players.get(score.getName()) == null) {
					players.put(score.getName(), score.getScore());
				} else {
					players.put(score.getName(),players.get(score.getName())
							+ score.getScore());
				}
			}
		}
		mapTotal.put(timestamp, players);		
	}
	
	public Map<String, Map<String,Integer>> getTotalScoreChart()
	{
		return mapTotal;
	}

	public List<Entry<String, Integer>> getTotalScoreWithoutCybercomPlayers() {
		Map<String, Integer> players = new HashMap<>();

		for (List<Score> scoreboard : scoreboards) {
			for (Score score : scoreboard) {
				if (players.get(score.getName()) == null) {
					if (!(score.getPlayer().getSuperclass() == CybercomPlayer.class)) {
						players.put(score.getName(), score.getScore());
					}
				} else {
					if (!(score.getPlayer().getSuperclass() == CybercomPlayer.class)) {
						players.put(score.getName(),players.get(score.getName())
								+ score.getScore());
					}
				}
			}
		}
		return getMapAsSortedList(players);
	}

	public List<Entry<String, Integer>> getTotalScore() {
		Map<String, Integer> players = new HashMap<>();

		for (List<Score> scoreboard : scoreboards) {
			for (Score score : scoreboard) {
				if (players.get(score.getName()) == null) {
					players.put(score.getName(), score.getScore());
				} else {
					players.put(score.getName(),players.get(score.getName())
							+ score.getScore());
				}
			}
		}
		return getMapAsSortedList(players);
	}


	private List<Entry<String, Integer>> getMapAsSortedList(Map<String, Integer> map){
		List<Entry<String, Integer>> list = new ArrayList<Entry<String, Integer>>(map.entrySet());
		list.sort(new EntryComparator());
		return list;
	}
	
	private class EntryComparator implements Comparator<Entry<String, Integer>> {
		public int compare(Entry<String, Integer> arg0,Entry<String, Integer> arg1) {
			return arg1.getValue().compareTo(arg0.getValue());
		}
	};
}
