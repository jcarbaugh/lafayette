package edu.american.weiss.lafayette.data;

import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import edu.american.weiss.lafayette.Application;
import edu.american.weiss.lafayette.composite.Composite;
import edu.american.weiss.lafayette.composite.CompositeElement;
import edu.american.weiss.lafayette.event.ChamberEvent;
import edu.american.weiss.lafayette.event.DestroyEvent;
import edu.american.weiss.lafayette.event.ResponseEvent;
import edu.american.weiss.lafayette.event.listener.ChamberEventListener;

public class ResponseSummaryListenerTB2 implements ChamberEventListener {

	private Map summaryMap;
	
	public ResponseSummaryListenerTB2() {
		
		summaryMap = new HashMap();
		
		Map purpleMap = new HashMap();
		purpleMap.put("purple", new Integer(0));
		purpleMap.put("white", new Integer(0));
		purpleMap.put("white-purple", new Integer(0));
		purpleMap.put("black", new Integer(0));
		
		Map yellowMap = new HashMap();
		yellowMap.put("yellow", new Integer(0));
		yellowMap.put("white", new Integer(0));
		yellowMap.put("white-yellow", new Integer(0));
		yellowMap.put("black", new Integer(0));
		
		Map controlMap = new HashMap();
		controlMap.put("green", new Integer(0));
		controlMap.put("red", new Integer(0));
		controlMap.put("black", new Integer(0));
		
		Map compoundMap = new HashMap();
		compoundMap.put("purple", new Integer(0));
		compoundMap.put("yellow", new Integer(0));
		compoundMap.put("black", new Integer(0));

		summaryMap.put("purple", purpleMap);
		summaryMap.put("yellow", yellowMap);
		summaryMap.put("compound", compoundMap);
		summaryMap.put("control", controlMap);
		
	}
	
	public void handleChamberEvent(ChamberEvent ce) {

		if (ce instanceof ResponseEvent) {
			
			ResponseEvent re = (ResponseEvent) ce;
			
			Composite c = re.getActiveComposite();
			CompositeElement cElem = c.getActiveCompositeElement(re.getX(), re.getY());
			
			if (c != null) {
				
				String cName = c.getGroupName();
				String ceName;
				
				if (cElem == null) {
					ceName = "black";
				} else {
					ceName = cElem.getGroupName();
					if (ceName == null) {
						ceName = "black";
					}
				}
				
				try {
				
					Map m = (Map) summaryMap.get(cName);
					Integer i = (Integer) m.get(ceName);
					
					m.put(ceName, new Integer(i.intValue() + 1));
					
				} catch (Exception e) { }
				
			}
			
		} else if (ce instanceof DestroyEvent) {
			
			FileOutputStream fos = null;
			
			try {
				
				Map m;
				StringBuffer sb = new StringBuffer();
								
				sb.append(System.currentTimeMillis());
				sb.append("\n\n");
				
				appendMapDisplay(sb, "purple");
				appendMapDisplay(sb, "yellow");
				appendMapDisplay(sb, "control");
				appendMapDisplay(sb, "compound");

				fos = new FileOutputStream (
						Application.getProperty("log_path") + "/response_summary.log");
				
				fos.write(sb.toString().getBytes());
								
			} catch (Exception e) { 
				
			} finally {
				try { fos.close(); } catch (Exception e) { }
			}
			
		}

	}
	
	private void appendMapDisplay(StringBuffer sb, String id) {
		
		Map m = (Map) summaryMap.get(id);
		Iterator keyIterator = m.keySet().iterator();
		
		sb.append(id).append("\n");

		while (keyIterator.hasNext()) {
			String key = (String) keyIterator.next();
			sb.append("\t").append(key).append(": ").append(m.get(key)).append("\n");
		}
		
	}
	
}