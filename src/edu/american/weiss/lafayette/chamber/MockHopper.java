package edu.american.weiss.lafayette.chamber;

public class MockHopper implements Hopper {

	private static MockHopper hopper;
	
	public static synchronized Hopper getInstance() {
		if (hopper == null) {
    		hopper = new MockHopper();
    	}
        return hopper;
	}
	
	public void run() {
		// TODO Auto-generated method stub
		
	}
	
	public void destroy() {
		
	}

}
