package edu.american.weiss.lafayette.chamber;

import edu.american.weiss.lafayette.Application;
import edu.american.weiss.lafayette.event.ReinforcerCompleteEvent;
import edu.american.weiss.lafayette.io.jni.Opto22Controller;

/**
 * @author jeremy
 */
public class Opto22Hopper extends AbstractHopper {
    
    private Opto22Controller opto;
    
    private Opto22Hopper() {
    	try {
    		opto = Opto22Controller.getInstance();
    	} catch (Exception e) {
    		System.err.println("!!! Hopper not found!");
    	}
        Thread t = new Thread(this);
        t.start();
    }
    
    public static synchronized Hopper getInstance() {
    	if (hopper == null) {
    		hopper = new Opto22Hopper();
    	}
        return hopper;
    }
    
    public void destroy() {
    	try {
    		opto.closeAllHandles();
    	} catch (NullPointerException npe) { }
    }
    
    public boolean activateHopper(long duration) {

    	boolean activated = false;
    	
        if (!activated) {
            
        	try {
        		opto.activateHopper();
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
    		opto.deactivateHopper();	
    	} catch (NullPointerException npe) {
    	} finally {
    		Application.getEventController().notifyListeners(new ReinforcerCompleteEvent());
            this.isActive = false;
    	}      
        
    }

}