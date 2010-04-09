package edu.american.weiss.lafayette.data;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
//import javax.media.TransitionEvent;
import edu.american.weiss.lafayette.Application;
import edu.american.weiss.lafayette.event.ChamberEvent;
import edu.american.weiss.lafayette.event.CompositeTransitionEvent;
import edu.american.weiss.lafayette.event.DestroyEvent;
//import edu.american.weiss.lafayette.event.ResponseEvent;
import edu.american.weiss.lafayette.event.listener.ChamberEventListener;

public class EventRecorderListener implements ChamberEventListener {

	private BufferedWriter br;
	private FileOutputStream fos;
	private long startTime;
	
	public EventRecorderListener() {
		try {
			fos = new FileOutputStream(
					Application.getProperty("log_path") + "/composite.log");
			br = new BufferedWriter(new OutputStreamWriter(fos));
		} catch (Exception e) { }
		startTime = System.currentTimeMillis();
	}
	
	public void handleChamberEvent(ChamberEvent ce) {
		if (ce instanceof CompositeTransitionEvent) {
			StringBuffer sb = new StringBuffer();
			sb.append(System.currentTimeMillis() - startTime);
			sb.append("\t");
			sb.append(ce.getActiveComposite().getId());
			sb.append("\n");
			try {
				br.write(sb.toString());
			} catch (Exception e) { }
		} else if (ce instanceof DestroyEvent) {
			try { br.close(); } catch (Exception e) { }
			try { fos.close(); } catch (Exception e) { }
		}
	}

}