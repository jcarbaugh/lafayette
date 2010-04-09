package edu.american.weiss.lafayette.actions;

import edu.american.weiss.lafayette.Application;
import edu.american.weiss.lafayette.composite.BaseCompositeAction;
import edu.american.weiss.lafayette.event.ReinforcerEvent;

/**
 * @author jeremy
 */
public class HopperAction extends BaseCompositeAction implements Reinforcer {

	private long duration;
	
	public HopperAction(long duration) {
		this.duration = duration;
	}
	
	public void run() {
		
		ReinforcerEvent re = new ReinforcerEvent(composite, compositeElement, duration);
		Application.getEventController().notifyListeners(re);
		
	}
	
	public boolean runAsThread() {
	    return false;
	}

}