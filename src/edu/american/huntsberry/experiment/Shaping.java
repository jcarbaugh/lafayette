package edu.american.huntsberry.experiment;

import edu.american.huntsberry.composite.EmptyComposite;
import edu.american.huntsberry.composite.GrayComposite;
import edu.american.weiss.lafayette.Application;
import edu.american.weiss.lafayette.actions.HopperAction;
import edu.american.weiss.lafayette.chamber.UserInterface;
import edu.american.weiss.lafayette.chamber.UserInterfaceFactory;
import edu.american.weiss.lafayette.composite.Composite;
import edu.american.weiss.lafayette.experiment.BaseExperimentImpl;

public class Shaping extends BaseExperimentImpl {

private UserInterface ui;
    
    public Shaping() {
	    ui = UserInterfaceFactory.getUserInterfaceInstance();
    }

	public Composite getInitialComposite() {
        Composite comp = new GrayComposite(ui, ui.getResponseSize());
        comp.setType(Composite.ACTIVE_COMPOSITE);
        comp.setDuration(0);
        comp.setGlobalAction(new HopperAction(getReinforcementDuration()));
        return comp;
	}

	public Composite getFinalComposite() {
        Composite comp = new EmptyComposite(ui, ui.getResponseSize());
        comp.setType(Composite.FINAL_COMPOSITE);
        return comp;
	}

	public Composite getNextComposite() {
        Composite comp = new GrayComposite(ui, ui.getResponseSize());
        comp.setType(Composite.ACTIVE_COMPOSITE);
        comp.setDuration(0);
        comp.setGlobalAction(new HopperAction(getReinforcementDuration()));
        return comp;
	}

	public Composite getRestComposite() {
        Composite comp = new EmptyComposite(ui, ui.getResponseSize());
        comp.setType(Composite.REST_COMPOSITE);
        comp.setDuration(-1);
        return comp;
	}

	public long getCorrectionDuration() {
		return 0;
	}

	public long getReinforcementDuration() {
		return Integer.parseInt(Application.getProperty("reinforcement_duration"));
	}

	public boolean isCorrecting() {
		return false;
	}

}