package controller;

public class Score {
	private final String name;
	private final int score;
	private final int place;

	public Score(String name, int score, int place) {
		this.name = name;
		this.score = score;
		this.place = place;
	}

	public String getName() {
		return name;
	}

	public int getScore() {
		return score;
	}

	public int getPlace() {
		return place;
	}

	@Override
	public String toString() {
		return place + ") " + name + ": " + score;
	}
}
