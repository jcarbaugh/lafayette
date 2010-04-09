package edu.american.weiss.lafayette.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.american.weiss.lafayette.event.ChamberEvent;
import edu.american.weiss.lafayette.event.listener.ChamberEventListener;

public abstract class CumulativeRecorder implements ChamberEventListener, Runnable {

	private boolean isRunning;
	private List eventQueue;
	private Map pinMap; 

	protected long startTime;
	protected long stopTime;
	
	public CumulativeRecorder() {
		eventQueue = new ArrayList(100);
		pinMap = new HashMap();
	}
	
	public void run() {
	
		startTime = System.currentTimeMillis();
		isRunning = true;
		
		while (isRunning) {
			
			if (eventQueue.size() > 0) {
				
				processChamberEvent(
						(ChamberEvent) eventQueue.remove(0));
				
			}
	
			try {
				Thread.sleep(1000);
			} catch (InterruptedException ie) { }
			
		}
		
		while (eventQueue.size() > 0) {
			processChamberEvent(
					(ChamberEvent) eventQueue.remove(0));
		}
		
		stopTime = System.currentTimeMillis();
		
	}
	
	public void addPinAssignment(String compositeGroup, int pin) {
		pinMap.put(compositeGroup, new Integer(pin));
	}
	
	public void removePinAssignment(String compositeGroup) {
		pinMap.remove(compositeGroup);
	}
	
	public int getPinAssignment(String compositeGroup) {
		return ((Integer) pinMap.get(compositeGroup)).intValue();
	}

	public boolean isRunning() {
		return isRunning;
	}

	public void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}

	public void handleChamberEvent(ChamberEvent ce) {
		
		eventQueue.add(ce);
		
	}
	
	protected void destroy() {
		
		isRunning = false;
		
		destroyChild();
		
	}
	
	protected abstract void destroyChild();
	protected abstract void processChamberEvent(ChamberEvent ce);

}