package fr.ldu.android.floodit.image.model;

import java.io.Serializable;

public class Score implements Serializable {

	private static final long serialVersionUID = -4693460487721160925L;
	
	int score, position;
	String name;
	
	public Score(int score, String name, int position) {
		this.score = score;
		this.name = name;
		this.position = position;
	}
	
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getPosition() {
		return position;
	}
	public void setPosition(int p) {
		position = p;
	}
}
