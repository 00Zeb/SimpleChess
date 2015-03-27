package controller;

import java.io.Serializable;

public class Score implements Serializable {

	private static final long serialVersionUID = 1L;
	private final Class<?> player;
	private final int score;
	private final int place;
	private String reflection;

	public Score(Class<?> player, int score, int place) {
		this.player = player;
		this.score = score;
		this.place = place;
		this.reflection = "";
	}

	public String getName() {
		return player.getSimpleName();
	}

	public int getScore() {
		return score;
	}

	public int getPlace() {
		return place;
	}

	public Class<?> getPlayer() {
		return this.player;
	}

	public void setReflection(String reflection) {
		this.reflection = reflection;
	}

	public String getReflection() {
		return this.reflection;
	}

	@Override
	public String toString() {
		return place + ") " + getName() + getReflection() + ": " + score;
	}
}
