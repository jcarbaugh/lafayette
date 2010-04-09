package edu.american.weiss.lafayette.data;

import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

import edu.american.weiss.lafayette.Application;
import edu.american.weiss.lafayette.composite.Composite;
import edu.american.weiss.lafayette.composite.CompositeElement;
import edu.american.weiss.lafayette.event.ChamberEvent;
import edu.american.weiss.lafayette.event.DestroyEvent;
import edu.american.weiss.lafayette.event.ResponseEvent;
import edu.american.weiss.lafayette.event.listener.ChamberEventListener;

public class ResponseSummaryListener implements ChamberEventListener {

	private Map summaryMap;
	
	public ResponseSummaryListener() {
		
		summaryMap = new HashMap();
		
		Map blueMap = new HashMap();
		blueMap.put("red", new Integer(0));
		blueMap.put("blue", new Integer(0));
		blueMap.put("white", new Integer(0));
		blueMap.put("black", new Integer(0));
		
		Map redMap = new HashMap();
		redMap.put("red", new Integer(0));
		redMap.put("blue", new Integer(0));
		redMap.put("white", new Integer(0));
		redMap.put("black", new Integer(0));
		
		Map compoundMap = new HashMap();
		compoundMap.put("red", new Integer(0));
		compoundMap.put("blue", new Integer(0));
		compoundMap.put("white", new Integer(0));
		compoundMap.put("black", new Integer(0));
		
		summaryMap.put("red", redMap);
		summaryMap.put("blue", blueMap);
		summaryMap.put("compound", compoundMap);
		
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
				m = (Map) summaryMap.get("blue");
				sb.append("blue\n");
				sb.append("\tred: ").append(m.get("red")).append("\n");
				sb.append("\tblue: ").append(m.get("blue")).append("\n");
				sb.append("\twhite: ").append(m.get("white")).append("\n");
				sb.append("\tblack: ").append(m.get("black")).append("\n");
				sb.append("\n");
				m = (Map) summaryMap.get("red");
				sb.append("red\n");
				sb.append("\tred: ").append(m.get("red")).append("\n");
				sb.append("\tblue: ").append(m.get("blue")).append("\n");
				sb.append("\twhite: ").append(m.get("white")).append("\n");
				sb.append("\tblack: ").append(m.get("black")).append("\n");
				sb.append("\n");
				m = (Map) summaryMap.get("compound");
				sb.append("compound\n");
				sb.append("\tred: ").append(m.get("red")).append("\n");
				sb.append("\tblue: ").append(m.get("blue")).append("\n");
				sb.append("\twhite: ").append(m.get("white")).append("\n");
				sb.append("\tblack: ").append(m.get("black")).append("\n");

				fos = new FileOutputStream (
						Application.getProperty("log_path") + "/response_summary.log");
				
				fos.write(sb.toString().getBytes());
								
			} catch (Exception e) { 
				
			} finally {
				try { fos.close(); } catch (Exception e) { }
			}
			
		}

	}
}