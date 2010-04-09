package edu.american.weiss.lafayette.event;

import edu.american.weiss.lafayette.composite.Composite;

public interface ChamberEvent {

	long getEventTime();
	Composite getActiveComposite();
	
}