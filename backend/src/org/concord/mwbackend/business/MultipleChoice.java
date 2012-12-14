package org.concord.mwbackend.business;

import java.util.ArrayList;
import java.util.List;

public class MultipleChoice {

	private List<String> choices;

	private List<String> guesses;

	private String question;

	private String correct;

	private String selection;

	private String address;

	private int id;

	private long timestamp = -1;

	public MultipleChoice() {
		choices = new ArrayList<String>();
		guesses = new ArrayList<String>();
	}

	public void setID(int i) {
		id = i;
	}

	public int getID() {
		return id;
	}

	public void setTimestamp(long t) {
		timestamp = t;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setAddress(String s) {
		address = s;
	}

	public String getAddress() {
		return address;
	}

	public void setQuestion(String s) {
		question = s;
	}

	public String getQuestion() {
		return question;
	}

	public void addChoice(String s) {
		choices.add(s);
	}

	public int getChoiceCount() {
		return choices.size();
	}

	public String getChoice(int i) {
		if (i < 0 || i >= choices.size())
			return null;
		return (String) choices.get(i);
	}

	public void addGuess(String s) {
		guesses.add(s);
	}

	public int getGuessCount() {
		return guesses.size();
	}

	public String getGuess(int i) {
		if (i < 0 || i >= guesses.size())
			return null;
		return (String) guesses.get(i);
	}

	public void setCorrect(String s) {
		correct = s;
	}

	public String getCorrect() {
		return correct;
	}

	public void setSelection(String s) {
		selection = s;
	}

	public String getSelection() {
		return selection;
	}

}