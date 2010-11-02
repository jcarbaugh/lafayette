package edu.american.weiss.lafayette.chamber;

import edu.american.weiss.lafayette.Application;
import edu.american.weiss.lafayette.event.ReinforcerCompleteEvent;
import edu.american.weiss.lafayette.io.jni.ADUController;

public class AduHopper extends AbstractHopper {
	
	private ADUController adu;
	
	private AduHopper() {
		try {
			adu = ADUController.getInstance();
		} catch (Exception e) {
    		System.err.println("!!! Hopper not found!");
			System.out.println(e.getMessage());
		}
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
        	
        	try {
        		adu.activateHopper();
        	} catch (NullPointerException npe) { }
        		
        	this.duration = duration;
        	this.startTime = System.currentTimeMillis();
        		        	
        	this.isActive = true;
        	activated = true;
        	            
        }
        
        return activated;
        
	}

	public void deactivateHopper() {
		
		try {
			adu.deactivateHopper();
		} catch (NullPointerException npe) {
		} finally {
			Application.getEventController().notifyListeners(new ReinforcerCompleteEvent());
			this.isActive = false;
		}
		
	}

}
