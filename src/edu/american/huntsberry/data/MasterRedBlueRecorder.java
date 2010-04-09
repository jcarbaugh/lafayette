package edu.american.huntsberry.data;

import edu.american.weiss.lafayette.composite.Composite;
import edu.american.weiss.lafayette.composite.CompositeElement;
import edu.american.weiss.lafayette.data.MedPCCumulativeRecorder;
import edu.american.weiss.lafayette.event.ChamberEvent;
import edu.american.weiss.lafayette.event.CompositeTransitionEvent;
import edu.american.weiss.lafayette.event.ReinforcerEvent;
import edu.american.weiss.lafayette.event.ResponseEvent;

public class MasterRedBlueRecorder extends MedPCCumulativeRecorder {
	
	private int getDatumIndex(Composite comp) {
		if (comp.getGroupName().equals("blue")) {
			return 1;
		} else if (comp.getGroupName().equals("red")) {
			return 2;
		} else if (comp.getGroupName().equals("white")) {
			return 3;
		} else if (comp.getGroupName().equals("compound")) {
			return 4;
		}
		return 0;
	}
	
	private int getDatumIndex(ResponseEvent re) {
		CompositeElement ce = re.getActiveComposite().getActiveCompositeElement(re.getX(), re.getY());
		if (ce != null) {
			if (ce.getGroupName() != null) {
				if (ce.getGroupName().equals("blue")) {
					return 1;
				} else if (ce.getGroupName().equals("red")) {
					return 2;
				} else if (ce.getGroupName().equals("white")) {
					return 3;
				} else if (ce.getGroupName().equals("compound")) {
					return 4;
				}
			}
		}
		return 0;
	}
	
	private int getDatumIndex(ReinforcerEvent re) {
		CompositeElement ce = re.getCompositeElement();
		if (ce != null) {
			if (ce.getGroupName().equals("blue")) {
				return 1;
			} else if (ce.getGroupName().equals("red")) {
				return 2;
			} else if (ce.getGroupName().equals("compound")) {
				return 3;
			}
		}
		return 0;
	}

	protected void generateEventEntries(ChamberEvent ce) {

		if (ce instanceof CompositeTransitionEvent) {
			processCompositeTransitionEvent((CompositeTransitionEvent) ce);
		} else if (ce instanceof ResponseEvent) {
			processResponseEvent((ResponseEvent) ce);
		} else if (ce instanceof ReinforcerEvent) {
			processReinforcerEvent((ReinforcerEvent) ce);
		}

	}
	
	private void processCompositeTransitionEvent(CompositeTransitionEvent cte) {
		Composite previousComposite = cte.getPreviousComposite();
		Composite activeComposite = cte.getActiveComposite();
		if (previousComposite != null &&
				previousComposite.getType() == Composite.ACTIVE_COMPOSITE) {
			addEventEntry(cte.getEventTime(), 5, getDatumIndex(previousComposite));
		}
		if (activeComposite != null &&
				activeComposite.getType() == Composite.ACTIVE_COMPOSITE) {
			addEventEntry(cte.getEventTime(), 6, getDatumIndex(activeComposite));
		}
	}
	
	private void processResponseEvent(ResponseEvent re) {
		addEventEntry(re.getEventTime(), 1, getDatumIndex(re));
	}
	
	private void processReinforcerEvent(ReinforcerEvent re) {
		addEventEntry(re.getEventTime(), 2, getDatumIndex(re));
	}

	protected String getOutputFileName() {
		return "!medpc_" + startTime + "_rbmaster";
	}

}