package edu.american.weiss.lafayette.data;

import java.io.File;
import java.io.FileOutputStream;

import edu.american.weiss.lafayette.Application;
import edu.american.weiss.lafayette.composite.Composite;
import edu.american.weiss.lafayette.composite.CompositeElement;
import edu.american.weiss.lafayette.event.ChamberEvent;
import edu.american.weiss.lafayette.event.CompositeTransitionEvent;
import edu.american.weiss.lafayette.event.DestroyEvent;
import edu.american.weiss.lafayette.event.ResponseEvent;
import edu.american.weiss.lafayette.event.listener.ChamberEventListener;

public class ResponseRecorderListener implements ChamberEventListener {

	private long startTime;
	private FileOutputStream fos;
	
	public ResponseRecorderListener() {
		startTime = System.currentTimeMillis();
		try {
			fos = new FileOutputStream(
					Application.getProperty("log_path") + "/responselog_" + startTime + ".log");
			write("Elapsed\t\tPoint\t\tComp\t\tComp Elem\n");
		} catch (Exception e) { }
	}
	
	public void handleChamberEvent(ChamberEvent ce) {
		
		if (ce instanceof ResponseEvent) {
			
			ResponseEvent re = (ResponseEvent) ce;			
			StringBuffer logEntry = new StringBuffer();
			
			Composite comp = re.getActiveComposite();
			CompositeElement compElem = comp.getActiveCompositeElement(re.getX(), re.getY());
						
			logEntry.append(re.getEventTime() - startTime);
			logEntry.append("ms\t\t(");
			logEntry.append(re.getX());
			logEntry.append(",");
			logEntry.append(re.getY());
			logEntry.append(")\t");
			logEntry.append(comp.getGroupName());
			if (compElem != null) {
				logEntry.append("\t\t");
				logEntry.append(compElem.getGroupName());
			}
			logEntry.append("\n");
				
			write(logEntry.toString());
			
		} else if (ce instanceof CompositeTransitionEvent) {
			
			StringBuffer logEntry = new StringBuffer();
			
			logEntry.append(ce.getEventTime() - startTime);
			logEntry.append("ms\t\tcomposite: ");
			logEntry.append(ce.getActiveComposite().getId());
			logEntry.append("\n");
			
			write(logEntry.toString());
			
		} else if (ce instanceof DestroyEvent) {
			try {
				fos.close();
			} catch (Exception e) { }
		}
		
	}
	
	private void write(String entry) {
		try {
			fos.write(entry.getBytes());
		} catch (Exception e) { }
	}

}