package edu.american.huntsberry.experiment;

import edu.american.huntsberry.composite.EmptyComposite;
import edu.american.huntsberry.composite.HabituationComposite;
import edu.american.weiss.lafayette.chamber.UserInterface;
import edu.american.weiss.lafayette.chamber.UserInterfaceFactory;
import edu.american.weiss.lafayette.composite.Composite;
import edu.american.weiss.lafayette.experiment.BaseExperimentImpl;

public class Habituation extends BaseExperimentImpl {
	
    private UserInterface ui;
    
    public Habituation() {
	    ui = UserInterfaceFactory.getUserInterfaceInstance();
    }

	public Composite getInitialComposite() {
        Composite comp = new HabituationComposite(ui, ui.getResponseSize());
        comp.setType(Composite.ACTIVE_COMPOSITE);
        comp.setDuration(0);
        return comp;
	}

	public Composite getFinalComposite() {
        Composite comp = new EmptyComposite(ui, ui.getResponseSize());
        comp.setType(Composite.FINAL_COMPOSITE);
        return comp;
	}

	public Composite getNextComposite() {
        Composite comp = new HabituationComposite(ui, ui.getResponseSize());
        comp.setType(Composite.ACTIVE_COMPOSITE);
        comp.setDuration(0);
        return comp;
	}

	public Composite getRestComposite() {
        Composite comp = new EmptyComposite(ui, ui.getResponseSize());
        comp.setType(Composite.REST_COMPOSITE);
        comp.setDuration(-1);
        return comp;
	}

	public boolean isCorrecting() {
		return false;
	}

}