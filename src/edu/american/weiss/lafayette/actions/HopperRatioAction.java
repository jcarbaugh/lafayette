package edu.american.weiss.lafayette.actions;

import edu.american.weiss.lafayette.Application;
import edu.american.weiss.lafayette.composite.BaseCompositeAction;
import edu.american.weiss.lafayette.event.ReinforcerEvent;

public class HopperRatioAction extends BaseCompositeAction implements Reinforcer {
	
	private long duration;
	private long responses;
	private long responseThreshold;
	
	public HopperRatioAction(long duration, long responseThreshold) {
		this.duration = duration;
		this.responseThreshold = responseThreshold;
	}

	public boolean runAsThread() {
		return false;
	}

	public void run() {
		
		responses++;
		
		if (responses >= responseThreshold) {
			
			ReinforcerEvent re = new ReinforcerEvent(composite, compositeElement, duration);
			Application.getEventController().notifyListeners(re);
			responses = 0;
			
		}
		
	}

}