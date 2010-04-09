package edu.american.weiss.lafayette.chamber;

import edu.american.weiss.lafayette.Application;
import edu.american.weiss.lafayette.EventController;
import edu.american.weiss.lafayette.event.ReinforcerCompleteEvent;
import edu.american.weiss.lafayette.io.jni.Opto22Controller;

/**
 * @author jeremy
 */
public class Opto22Hopper implements Hopper {
    
    private static Opto22Hopper hopper;
    
    private boolean isActive = false;
    private long duration;
    private long startTime;
    
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
        		        	
        	isActive = true;
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
            hopper.isActive = false;
    	}      
        
    }
    
    public void run() {
        
        while (true) {
            
            try {
        
                if (hopper.isActive) {
                    
                    if (System.currentTimeMillis() > (hopper.startTime + hopper.duration) &&
                    		hopper.duration > 0) {
                        
                    	try {
                    		opto.deactivateHopper();
                    	} catch (NullPointerException npe) {
                    		npe.printStackTrace();
                    	}

                		Application.getEventController().notifyListeners(new ReinforcerCompleteEvent());
                        
                        hopper.isActive = false;
                        
                    }
                    
                }
                
                Thread.sleep(50);
                
            } catch (Exception e) { }
            
        }
        
    }
    
    public boolean isActive() {
        return isActive;
    }

}