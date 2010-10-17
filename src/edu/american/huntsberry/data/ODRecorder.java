package edu.american.huntsberry.data;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import edu.american.huntsberry.composite.ObjectDiscriminationComposite;
import edu.american.weiss.lafayette.Application;
import edu.american.weiss.lafayette.composite.Composite;
import edu.american.weiss.lafayette.data.QueuedRecorder;
import edu.american.weiss.lafayette.event.ChamberEvent;
import edu.american.weiss.lafayette.event.CompositeTransitionEvent;
import edu.american.weiss.lafayette.event.DestroyEvent;

public class ODRecorder extends QueuedRecorder {

	private FileOutputStream fos;
	private int trialCounter = 0;
	private long lastTransitionTime;
	
	private int correctCounter = 0;
	private int incorrectCounter = 0;
	private int refusedCounter = 0;
	
	public ODRecorder() {
		
		super();
		
		long now = System.currentTimeMillis();
		
		try {
			fos = new FileOutputStream(
				Application.getProperty("log_path") + "od_" + now + ".log");
			
		} catch (Exception e) { }
		
		lastTransitionTime = startTime;

		StringBuffer header = new StringBuffer();
		header.append("Date:          " + now + "\n");
		header.append("Monkey ID:     \n");
		header.append("Task:          Object Discrimination\n");
		header.append("Tested By:     \n");
		header.append("ITI:           " + Application.getProperty("iti") + "\n");
		header.append("\n");
		header.append("Trials\n");
		
		write(header.toString());
		
	}
	
	private void write(String entry) {
		try {
			fos.write(entry.getBytes());
		} catch (Exception e) { }
	}

	@Override
	protected void processChamberEvent(ChamberEvent ce) {
		if (ce instanceof CompositeTransitionEvent) {
			
			Composite prev = ((CompositeTransitionEvent) ce).getPreviousComposite();
			
			if (prev instanceof ObjectDiscriminationComposite) {
				
				ObjectDiscriminationComposite odc = (ObjectDiscriminationComposite) prev;
				
				String status = odc.getStatus();
				
				if (status == "correct") {
					correctCounter++;
				} else if (status == "incorrect") {
					incorrectCounter++;
				} else {
					refusedCounter++;
				}
				
				trialCounter++;
				
				StringBuffer row = new StringBuffer();
				if (trialCounter < 10) {
					row.append(trialCounter).append(" ");
				} else {
					row.append(trialCounter);
				}
				row.append("\t");
				row.append(status);
				row.append("\t");
				row.append(ce.getEventTime() - lastTransitionTime);
				row.append("\n");
				
				write(row.toString());
				
			}
			
			if (ce.getActiveComposite().getType() == Composite.FINAL_COMPOSITE) {
				
				StringBuffer footer = new StringBuffer();
				footer.append("\n");
				footer.append("Summary\n");
				footer.append("Trials:             " + trialCounter + "\n");
				footer.append("Number Refused:     " + refusedCounter + "\n");
				footer.append("Number Correct:     " + correctCounter + "\n");
				footer.append("Number Incorrect:   " + incorrectCounter + "\n");
				footer.append("% Correct:          " + (((float) correctCounter / (float) trialCounter) * 100) + "\n");
				footer.append("Mean Response Time: \n");
				
				write(footer.toString());
				
			}
			
			lastTransitionTime = ce.getEventTime();
			
		} else if (ce instanceof DestroyEvent) {
			try {
				fos.close();
			} catch (Exception e) { }
		}
		
	}

	@Override
	protected void destroyChild() {
		
	}

}
