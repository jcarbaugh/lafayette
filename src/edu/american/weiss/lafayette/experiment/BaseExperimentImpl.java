package edu.american.weiss.lafayette.experiment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import edu.american.weiss.lafayette.Application;
import edu.american.weiss.lafayette.actions.Reinforcer;
import edu.american.weiss.lafayette.composite.Composite;
import edu.american.weiss.lafayette.composite.CompositeAction;
import edu.american.weiss.lafayette.event.ChamberEvent;
import edu.american.weiss.lafayette.event.ResponseEvent;
import edu.american.weiss.lafayette.event.listener.ChamberEventListener;

public abstract class BaseExperimentImpl implements Experiment {
	
	protected Collection<ChamberEventListener> eventListeners;
	
	public BaseExperimentImpl() {
		eventListeners = new ArrayList<ChamberEventListener>();
	}

	public long getCorrectionDuration() {
		return Long.parseLong(Application.getProperty("response_correction_duration"));
	}

	public long getReinforcementDuration() {
		return Long.parseLong(Application.getProperty("reinforcement_duration"));
	}

	public long getRestDuration() {
		return Long.parseLong(Application.getProperty("rest_duration"));
	}
	
	public boolean isReinforcementWaiting() {
		return true;
	}
	
	public boolean isPseudoRandom() {
		return false;
	}
	
	public int getRestProbability() {
		return 100;
	}
	
	public Collection<ChamberEventListener> getEventListeners() {
		return eventListeners;
	}
	
	public void handleChamberEvent(ChamberEvent ce) {
		if (ce instanceof ResponseEvent) {
			ResponseEvent re = (ResponseEvent) ce;
			Composite comp = re.getActiveComposite();
			List<CompositeAction> as = comp.getActions(re.getX(), re.getY());
			Iterator<CompositeAction> it = as.iterator();
			while (it.hasNext()) {
				CompositeAction ca = it.next();
				if (ca instanceof Reinforcer) {
					((Reinforcer) ca).setComposite(comp);
				}
				if (ca.runAsThread()) {
					Thread t = new Thread(ca);
					t.start();
				} else {
					ca.run();
				}
			}
		}
	}
	
}