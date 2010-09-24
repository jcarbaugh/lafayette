package edu.american.weiss.lafayette.chamber;

import edu.american.weiss.lafayette.Application;
import edu.american.weiss.lafayette.event.ReinforcerCompleteEvent;

public abstract class AbstractHopper implements Hopper {

    protected static Hopper hopper;
    protected boolean isActive = false;
    protected long duration;
    protected long startTime;
    
    public long getDuration() {
    	return duration;
    }
    
    public void setDuration(long duration) {
    	this.duration = duration;
    }
    
    public long getStartTime() {
    	return this.startTime;
    }
    
    public void setStartTime(long startTime) {
    	this.startTime = startTime;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean isActive) {
    	this.isActive = isActive;
    }
    
    public void run() {
        
        while (true) {
            
            try {
        
                if (hopper.isActive()) {
                    
                    if (System.currentTimeMillis() > (hopper.getStartTime() + hopper.getDuration()) &&
                    		hopper.getDuration() > 0) {
                        
                    	try {
                    		hopper.deactivateHopper();
                    	} catch (NullPointerException npe) {
                    		npe.printStackTrace();
                    	}

                		Application.getEventController().notifyListeners(new ReinforcerCompleteEvent());
                        
                        hopper.setActive(false);
                        
                    }
                    
                }
                
                Thread.sleep(50);
                
            } catch (Exception e) { }
            
        }
        
    }

	public void destroy() { }

}
