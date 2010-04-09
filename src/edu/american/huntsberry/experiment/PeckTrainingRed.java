package edu.american.huntsberry.experiment;

import java.util.Random;

import edu.american.huntsberry.composite.BlackComposite;
import edu.american.huntsberry.composite.BlackFrameComposite;
import edu.american.huntsberry.composite.BlueBLComposite;
import edu.american.huntsberry.composite.RedTRComposite;
import edu.american.weiss.lafayette.Application;
import edu.american.weiss.lafayette.actions.HopperRatioAction;
import edu.american.weiss.lafayette.chamber.UserInterface;
import edu.american.weiss.lafayette.chamber.UserInterfaceFactory;
import edu.american.weiss.lafayette.composite.Composite;
import edu.american.weiss.lafayette.experiment.BaseExperimentImpl;

public class PeckTrainingRed extends BaseExperimentImpl {

	private UserInterface ui;
    private Random rand;
    private HopperRatioAction hra;
    
	public PeckTrainingRed() {
		hra = new HopperRatioAction(getReinforcementDuration(), Application.getIntProperty("ratio"));
	    ui = UserInterfaceFactory.getUserInterfaceInstance();
	    rand = new Random();
	}

	public Composite getInitialComposite() {
        Composite comp = new BlackComposite(ui, ui.getResponseSize());
        comp.setType(Composite.INITIAL_COMPOSITE);
        comp.setDuration(5000);
        return comp;
	}

	public Composite getFinalComposite() {
        Composite comp = new BlackComposite(ui, ui.getResponseSize());
        comp.setType(Composite.FINAL_COMPOSITE);
        return comp;
	}

	public Composite getNextComposite() {
        Composite comp = new RedTRComposite(ui, ui.getResponseSize());
        comp.setGlobalAction(hra);
        comp.setType(Composite.ACTIVE_COMPOSITE);
        comp.setDuration(
        		Application.getRandomLong(
        				Integer.parseInt(Application.getProperty("composite_min")),
        				Integer.parseInt(Application.getProperty("composite_max"))));
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

}