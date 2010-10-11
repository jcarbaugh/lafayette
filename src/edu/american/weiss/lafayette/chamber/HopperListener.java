package edu.american.weiss.lafayette.chamber;

import edu.american.weiss.lafayette.Application;
import edu.american.weiss.lafayette.event.ChamberEvent;
import edu.american.weiss.lafayette.event.ReinforcerEvent;
import edu.american.weiss.lafayette.event.listener.ChamberEventListener;

public class HopperListener implements ChamberEventListener {

	public void handleChamberEvent(ChamberEvent ce) {

		if (ce instanceof ReinforcerEvent) {
		
			int duration = Application.getIntProperty("reinforcement_duration");
			
			Hopper h = Chamber.getHopper();
			
			if (h != null) {
				h.activateHopper(duration);
			}
			
		}

	}

}