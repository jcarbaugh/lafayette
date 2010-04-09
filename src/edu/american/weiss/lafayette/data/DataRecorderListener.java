package edu.american.weiss.lafayette.data;

import edu.american.huntsberry.experiment.Test1;
import edu.american.weiss.lafayette.Application;
import edu.american.weiss.lafayette.composite.Composite;
import edu.american.weiss.lafayette.event.ChamberEvent;
import edu.american.weiss.lafayette.event.CompositeTransitionEvent;
import edu.american.weiss.lafayette.event.ReinforcerEvent;
import edu.american.weiss.lafayette.event.ResponseEvent;
import edu.american.weiss.lafayette.event.listener.ChamberEventListener;
import edu.american.weiss.lafayette.experiment.Experiment;

public class DataRecorderListener implements ChamberEventListener {

	private long startTime;
	private Experiment exp;
	
	public DataRecorderListener() {
		startTime = System.currentTimeMillis();
		exp = Application.getCompositeController().getExperiment();
	}
	
	public void handleChamberEvent(ChamberEvent ce) {
		
		long duration = ce.getEventTime() - startTime;
		
		if (!(exp instanceof Test1) ||
				duration > Application.getIntProperty("warmup_duration")) {

			if (ce instanceof CompositeTransitionEvent) {
				
				CompositeTransitionEvent cte = (CompositeTransitionEvent) ce;
				Composite comp = cte.getPreviousComposite();
				
				if (comp != null) {
					DataRecorder.recordDuration(
							comp,
							comp.getEndTime() - comp.getStartTime());
				}
				
			} else if (ce instanceof ReinforcerEvent) {
				
				ReinforcerEvent re = (ReinforcerEvent) ce;
				Composite comp = re.getActiveComposite();
				
				if (comp != null) {
					DataRecorder.recordReinforcer(comp);
				}
				
			} else if (ce instanceof ResponseEvent) {
				
				ResponseEvent re = (ResponseEvent) ce;
				Composite comp = re.getActiveComposite();
				
				DataRecorder.recordResponse(comp, re.getX(), re.getY());
				
			}
		
		}

	}

}