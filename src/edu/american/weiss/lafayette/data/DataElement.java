package edu.american.weiss.lafayette.data;

public class DataElement {

	private long duration;
	private long responses;
	private long reinforcers;
	
	public long addDuration(long duration) {
		this.duration += duration;
		return duration;
	}
	public long getDuration() {
		return duration;
	}
	public void setDuration(long duration) {
		this.duration = duration;
	}
	
	public long addReinforcers(long reinforcers) {
		this.reinforcers += reinforcers;
		return reinforcers;
	}
	public long getReinforcers() {
		return reinforcers;
	}
	public void setReinforcers(long reinforcers) {
		this.reinforcers = reinforcers;
	}
	
	public long addResponses(long responses) {
		this.responses += responses;
		return responses;
	}
	public long getResponses() {
		return responses;
	}
	public void setResponses(long responses) {
		this.responses = responses;
	}
	
}