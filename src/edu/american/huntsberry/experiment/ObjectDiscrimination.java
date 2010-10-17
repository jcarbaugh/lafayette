package edu.american.huntsberry.experiment;

import java.awt.Color;
import java.awt.Image;
import java.awt.Toolkit;
import java.util.Random;

import edu.american.huntsberry.composite.BlackComposite;
import edu.american.huntsberry.composite.ColorComposite;
import edu.american.huntsberry.composite.EmptyComposite;
import edu.american.huntsberry.composite.ObjectDiscriminationComposite;
import edu.american.weiss.lafayette.Application;
import edu.american.weiss.lafayette.chamber.UserInterface;
import edu.american.weiss.lafayette.chamber.UserInterfaceFactory;
import edu.american.weiss.lafayette.composite.Composite;
import edu.american.weiss.lafayette.experiment.BaseExperimentImpl;

public class ObjectDiscrimination extends BaseExperimentImpl {
	
	private UserInterface ui;
	private Random rand;
	private int trials;
	private int trialsPerBlock;
	private int currentTrial;
	private int iti;
	
	private Image correctImage;
	private Image incorrectImage;
	
	public ObjectDiscrimination() {
		
		ui = UserInterfaceFactory.getUserInterfaceInstance();
		rand = new Random();
		trials = Application.getIntProperty("trials");
		trialsPerBlock = Application.getIntProperty("trials_per_block");
		iti = Application.getIntProperty("iti");
		currentTrial = 0;
		
		Toolkit tk = Toolkit.getDefaultToolkit();
		
		correctImage = tk.getImage(Application.getProperty("correct_image_path"));
		incorrectImage = tk.getImage(Application.getProperty("incorrect_image_path"));
		
	}

	public Composite getInitialComposite() {
		Composite comp = new BlackComposite(ui, ui.getResponseSize());
		comp.setType(Composite.INITIAL_COMPOSITE);
		comp.setDuration(Application.getIntProperty("initial_duration"));
        comp.setId("initial");
		return comp;
	}

	public Composite getFinalComposite() {
		Composite comp = new BlackComposite(ui, ui.getResponseSize());
        comp.setType(Composite.FINAL_COMPOSITE);
        comp.setId("final");
        return comp;
	}

	public Composite getNextComposite() {
		
		Composite comp = null;
		
		if (currentTrial < trials) {
			
			currentTrial++;
			
			comp = new ObjectDiscriminationComposite(ui, ui.getResponseSize(), correctImage, incorrectImage);
	        comp.setType(Composite.ACTIVE_COMPOSITE);
			comp.setDuration(5000);
			
		}
		
		return comp;
		
	}

	public Composite getRestComposite() {
		Color color;
		String id;
		if (rand.nextInt(2) == 0) {
			color = Color.BLUE;
			id = "rest (blue)";
		} else {
			color = Color.BLACK;
			id = "rest (black)";
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
