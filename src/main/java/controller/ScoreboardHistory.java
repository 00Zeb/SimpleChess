package controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class ScoreboardHistory {

	private static final String SCOREBOARD_HISTORY_FILE = "ScoreboardHistory";
	private List<List<Score>> myScoreboards = new ArrayList<>();

	public ScoreboardHistory() {

	}

	public void addScoreboard(List<Score> score) {
		myScoreboards.add(0, score);
	}

	public List<List<Score>> getScoreboards() {
		return myScoreboards.subList(1, myScoreboards.size());
	}

	public List<Entry<String, Integer>> getPlayersWithTotalScore() {
		Map<String, Integer> players = new HashMap<>();

		for (List<Score> scoreboard : myScoreboards) {
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

	private List<Entry<String, Integer>> getMapAsSortedList(Map<String, Integer> players)
	{
		List<Entry<String, Integer>> list = new ArrayList<Entry<String, Integer>>(players.entrySet());
		list.sort(new EntryComparator());

		return list;
	}

	public void saveToDisk() {
		ObjectOutputStream objectOutputStream = null;
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(SCOREBOARD_HISTORY_FILE);
			objectOutputStream = new ObjectOutputStream(fileOutputStream);
			objectOutputStream.writeObject(myScoreboards);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				objectOutputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void loadFromDisk() {
		ObjectInputStream objectInputStream = null;
		try {
			File file = new File(SCOREBOARD_HISTORY_FILE);
			if (file.exists()) {
				FileInputStream fileInputStream = new FileInputStream(file);
				if (fileInputStream != null) {
					objectInputStream = new ObjectInputStream(fileInputStream);
					myScoreboards = (List<List<Score>>) objectInputStream.readObject();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (objectInputStream != null) {
					objectInputStream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private class EntryComparator implements Comparator<Entry<String, Integer>> {

		public int compare(Entry<String, Integer> arg0,Entry<String, Integer> arg1) {
			return arg1.getValue().compareTo(arg0.getValue());
		}
	};
}
