package edu.american.weiss.lafayette.chamber;

public class MockHopper extends AbstractHopper {

	private static MockHopper hopper;
	private boolean isActive;
	
	public static synchronized Hopper getInstance() {
		if (hopper == null) {
    		hopper = new MockHopper();
    	}
        return hopper;
	}
	
	public boolean activateHopper(long duration) {
		isActive = true;
		return isActive;
	}
	
	public void deactivateHopper() {
		isActive = false;
	}

}
