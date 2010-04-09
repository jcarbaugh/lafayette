package edu.american.huntsberry.data;

import edu.american.weiss.lafayette.composite.Composite;
import edu.american.weiss.lafayette.data.MedPCCumulativeRecorder;
import edu.american.weiss.lafayette.event.ChamberEvent;
import edu.american.weiss.lafayette.event.CompositeTransitionEvent;
import edu.american.weiss.lafayette.event.DestroyEvent;

public class CompositeGroupCumulativeRecorder extends MedPCCumulativeRecorder {

	private String groupName;
	
	public CompositeGroupCumulativeRecorder(String groupName) {
		this.groupName = groupName;
	}
	
	protected void generateEventEntries(ChamberEvent ce) {

		Composite activeComp = ce.getActiveComposite();
		
		if (ce instanceof DestroyEvent) {
			super.processChamberEvent(ce);
		} else if (activeComp != null && activeComp.getGroupName() != null) {
			if (activeComp instanceof CompositeTransitionEvent) {
				CompositeTransitionEvent cte = (CompositeTransitionEvent) ce;
				Composite previousComp = cte.getPreviousComposite();
				if (activeComp.getGroupName().equals(groupName) ||
						previousComp.getGroupName().equals(groupName)) {
					super.processChamberEvent(ce);
				}
			} else {
				if (activeComp.getGroupName().equals(groupName)) {
					super.processChamberEvent(ce);
				}
			}
		}
		
	}
	
	protected String getOutputFileName() {
		return "!medpc_" + startTime + "_" + groupName;
	}
	
	protected int getDatumIndex(ChamberEvent ce) {
		return 0;
	}

	
}