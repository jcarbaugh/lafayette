package edu.american.weiss.lafayette.data;

import java.util.HashMap;
import java.util.Iterator;

import edu.american.weiss.lafayette.composite.Composite;
import edu.american.weiss.lafayette.composite.CompositeElement;

public class DataRecorder {

	private static HashMap dataMap = new HashMap();
	private static long durationTotal;
	private static long reinforcerTotal;
	private static long responseTotal;
	
	public synchronized static void recordResponse(Composite composite, int x, int y) {
		String id = "";
		CompositeElement ce = composite.getActiveCompositeElement(x, y);
		if (ce == null) {
			id = "black";
		} else if (composite.getId().equals("rest")) {
			id = "rest";
		} else if (ce != null && ce.getGroupName() != null && ce.getGroupName().equals("white")) {
			id = "white";
		} else {
			id = composite.getId();
		}
		getResultData(id).addResponses(1);
		responseTotal++;
	}
	
	public synchronized static long getResponses(Composite composite) {
		return getResultData(composite.getId()).getResponses();
	}
	
	public synchronized static void recordReinforcer(Composite composite) {
		getResultData(composite.getId()).addReinforcers(1);
		reinforcerTotal++;
	}
	
	public synchronized static long getReinforcers(Composite composite) {
		return getResultData(composite.getId()).getReinforcers();
	}
	
	public synchronized static void recordDuration(Composite composite, long duration) {
		getResultData(composite.getId()).addDuration(duration);
		durationTotal += duration;
	}
	
	public synchronized static long getDuration(Composite composite) {
		return getResultData(composite.getId()).getDuration();
	}
	
	public static long getReinforcerTotal() {
		return reinforcerTotal;
	}
	
	public static long getResponseTotal() {
		return responseTotal;
	}
	
	public static long getDurationTotal() {
		return durationTotal;
	}
	
	private static DataElement getResultData(Object key) {
		
		DataElement rd = (DataElement) dataMap.get(key);
		
		if (rd == null) {
			rd = new DataElement();
			dataMap.put(key, rd);
		}
		
		return rd;
		
	}
	
	public synchronized void reset() {
		dataMap.clear();
		durationTotal = 0;
		reinforcerTotal = 0;
		responseTotal = 0;
	}
	
	public synchronized static void debug() {
		
		Iterator it = dataMap.keySet().iterator();
		
		while (it.hasNext()) {
			
			String key = (String) it.next();
			DataElement rd = (DataElement) dataMap.get(key);

			StringBuffer output = new StringBuffer();
			
			output.append("[");
			output.append(key);
			output.append("] d:");
			output.append(rd.getDuration());
			output.append(" resp:");
			output.append(rd.getResponses());
			output.append(" rein:");
			output.append(rd.getReinforcers());
			
			System.out.println(output);
			
		}
 	}
	
}