package edu.american.weiss.lafayette.event;

import edu.american.weiss.lafayette.composite.Composite;

public class BaseChamberEventImpl implements ChamberEvent {

	private Composite activeComposite;
	private long eventTime;
	
	public BaseChamberEventImpl(Composite activeComposite) {
		this.activeComposite = activeComposite;
		this.eventTime = System.currentTimeMillis(); 
	}
	
	public Composite getActiveComposite() {
		return activeComposite;
	}
	
	public long getEventTime() {
		return eventTime;
	}

}