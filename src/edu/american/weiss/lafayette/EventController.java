package edu.american.weiss.lafayette;

import java.util.ArrayList;
import java.util.List;

import edu.american.weiss.lafayette.event.ChamberEvent;
import edu.american.weiss.lafayette.event.listener.ChamberEventListener;

public class EventController {

	private List<ChamberEventListener> listeners;
	
	public EventController() {
		this.listeners = new ArrayList<ChamberEventListener>();
	}
	
	public void registerEventListener(ChamberEventListener cel) {
		this.listeners.add(cel);
	}
	
	public void notifyListeners(ChamberEvent ce) {
		for (ChamberEventListener cel : this.listeners) {
			cel.handleChamberEvent(ce);
		}
	}
	
	public void destroy() {
		this.listeners.clear();
		this.listeners = null;
	}
	
}
