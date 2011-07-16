package edu.american.huntsberry.experiment;

import java.awt.Color;

import edu.american.huntsberry.composite.ColorComposite;
import edu.american.huntsberry.composite.StartComposite;
import edu.american.weiss.lafayette.Application;
import edu.american.weiss.lafayette.chamber.UserInterface;
import edu.american.weiss.lafayette.chamber.UserInterfaceFactory;
import edu.american.weiss.lafayette.composite.Composite;
import edu.american.weiss.lafayette.experiment.BaseExperimentImpl;

public class MTS extends BaseExperimentImpl {
	
	private static int SAMPLE_COMPOSITE = -1;
	private static int MATCH_COMPOSITE = 1;
	
	private UserInterface ui;
	private int iti;
	
	private int currentComposite = MATCH_COMPOSITE;
	private boolean lastResponseWasCorrect = false;
	
	public MTS() {
		ui = UserInterfaceFactory.getUserInterfaceInstance();
		iti = Application.getIntProperty("iti");
	}

	public Composite getInitialComposite() {
		Composite comp = new StartComposite(ui, ui.getResponseSize());
		comp.setType(Composite.INITIAL_COMPOSITE);
        comp.setId("initial");
		return comp;
	}

	public Composite getFinalComposite() {
		// TODO Auto-generated method stub
		return null;
	}

	public Composite getNextComposite() {
		
		currentComposite *= -1;	// swap composite
		
		if (currentComposite == SAMPLE_COMPOSITE) {
			
			// show sample
			
		} else if (currentComposite == MATCH_COMPOSITE) {
			
		}
		
		// TODO Auto-generated method stub
		return null;
	}

	public Composite getRestComposite() {
		Color color;
		String id;
		if (lastResponseWasCorrect) {
			color = Color.BLACK;
			id = "rest (black)";
		} else {
			color = Color.BLUE;
			id = "rest (blue)";
		}
		Composite comp = new ColorComposite(ui, ui.getResponseSize(), color);
        comp.setType(Composite.REST_COMPOSITE);
		comp.setDuration(iti);
		comp.setId(id);
        return comp;
	}

	public boolean isCorrecting() {
		return false;
	}
	
	public boolean isReinforcementWaiting() {
		return false;
	}

}
