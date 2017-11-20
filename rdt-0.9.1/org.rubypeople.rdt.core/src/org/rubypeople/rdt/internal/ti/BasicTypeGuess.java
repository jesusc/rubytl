package org.rubypeople.rdt.internal.ti;

public class BasicTypeGuess implements ITypeGuess {
	private String type;
	private int confidence;
	public int getConfidence() {
		return confidence;
	}
	public void setConfidence(int confidence) {
		this.confidence = confidence;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public BasicTypeGuess(String type, int confidence) {
		this.type = type;
		this.confidence = confidence;
	}
	
}
