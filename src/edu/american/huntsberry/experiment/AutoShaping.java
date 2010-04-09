package edu.american.huntsberry.experiment;

import java.awt.Color;
import java.util.Random;

import edu.american.huntsberry.composite.BlackComposite;
import edu.american.huntsberry.composite.BlackFrameComposite;
import edu.american.huntsberry.composite.BlueAutoComposite;
import edu.american.huntsberry.composite.RedAutoComposite;
import edu.american.weiss.lafayette.Application;
import edu.american.weiss.lafayette.actions.HopperAction;
import edu.american.weiss.lafayette.chamber.UserInterface;
import edu.american.weiss.lafayette.chamber.UserInterfaceFactory;
import edu.american.weiss.lafayette.composite.Composite;
import edu.american.weiss.lafayette.experiment.BaseExperimentImpl;

public class AutoShaping extends BaseExperimentImpl {
	
	private UserInterface ui;
    private Random rand;
    
    public AutoShaping() {
	    ui = UserInterfaceFactory.getUserInterfaceInstance();
	    rand = new Random();
    }

    public Composite getInitialComposite() {
        Composite comp = new BlackFrameComposite(ui, ui.getResponseSize());
        comp.setType(Composite.INITIAL_COMPOSITE);
        comp.setDuration(2000);
        return comp;
    }

	public long getReinforcementDuration() {
		return 4000;
	}

    public Composite getFinalComposite() {
        Composite comp = new BlackComposite(ui, ui.getResponseSize());
        comp.setType(Composite.FINAL_COMPOSITE);
        return comp;
    }
    
    public Composite getNextComposite() {
        Composite comp;
        if (rand.nextInt(2) % 2 == 0) {
        	comp = new BlueAutoComposite(ui, ui.getResponseSize());
        } else {
        	comp = new RedAutoComposite(ui, ui.getResponseSize());
        }
        comp.setGlobalAction(new HopperAction(getReinforcementDuration()));
        comp.setType(Composite.ACTIVE_COMPOSITE);
        comp.setDuration(10000);
        return comp;
    }

    public Composite getRestComposite() {
        Composite comp = new BlackFrameComposite(ui, ui.getResponseSize());
        comp.setType(Composite.REST_COMPOSITE);
        comp.setDuration(
        		Application.getRandomLong(
        				Integer.parseInt(Application.getProperty("rest_min")),
        				Integer.parseInt(Application.getProperty("rest_max"))));
        return comp;
    }

	public boolean isCorrecting() {
		return false;
	}
	
	public boolean isReinforcementWaiting() {
		return false;
	}    

}