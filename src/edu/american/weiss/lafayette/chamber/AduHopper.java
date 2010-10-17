package edu.american.weiss.lafayette.chamber;

import edu.american.weiss.lafayette.Application;
import edu.american.weiss.lafayette.event.ReinforcerCompleteEvent;

public class AduHopper extends AbstractHopper {
	
	private AduHopper() {
		
	}

	public static synchronized Hopper getInstance() {
		if (hopper == null) {
    		hopper = new AduHopper();
            Thread t = new Thread(hopper);
            t.start();
    	}
        return hopper;
	}
	
	public boolean activateHopper(long duration) {
		
		boolean activated = false;
    	
        if (!activated) {
        		
        	this.duration = duration;
        	this.startTime = System.currentTimeMillis();
        		        	
        	this.isActive = true;
        	activated = true;
        	            
        }
        
        return activated;
        
	}

	public void deactivateHopper() {
		Application.getEventController().notifyListeners(new ReinforcerCompleteEvent());
		this.isActive = false;
	}

}
