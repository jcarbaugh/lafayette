package edu.american.weiss.lafayette.data;

import java.util.ArrayList;
import java.util.List;

import edu.american.weiss.lafayette.event.ChamberEvent;
import edu.american.weiss.lafayette.event.listener.ChamberEventListener;

public abstract class QueuedRecorder implements ChamberEventListener, Runnable {

	private boolean isRunning;
	private List<ChamberEvent> eventQueue;

	protected long startTime;
	protected long stopTime;
	
	public QueuedRecorder() {
		eventQueue = new ArrayList<ChamberEvent>(100);
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
