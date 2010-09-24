package edu.american.weiss.lafayette.chamber;

public interface Hopper extends Runnable {
	
	public void destroy();
	
	public boolean activateHopper(long duration);
	public void deactivateHopper();
	
	public boolean isActive();
	public void setActive(boolean isActive);
	
	public long getDuration();
	public void setDuration(long duration);
	
	public long getStartTime();
	public void setStartTime(long startTime);
	
}
