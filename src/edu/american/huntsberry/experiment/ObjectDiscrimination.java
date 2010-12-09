package edu.american.huntsberry.experiment;

import java.awt.Color;
import java.awt.Image;
import java.awt.Toolkit;

import edu.american.huntsberry.composite.BlackComposite;
import edu.american.huntsberry.composite.ColorComposite;
import edu.american.huntsberry.composite.MessageComposite;
import edu.american.huntsberry.composite.ObjectDiscriminationComposite;
import edu.american.huntsberry.composite.StartComposite;
import edu.american.weiss.lafayette.Application;
import edu.american.weiss.lafayette.ImageManager;
import edu.american.weiss.lafayette.chamber.AudioFile;
import edu.american.weiss.lafayette.chamber.AudioPlayer;
import edu.american.weiss.lafayette.chamber.UserInterface;
import edu.american.weiss.lafayette.chamber.UserInterfaceFactory;
import edu.american.weiss.lafayette.composite.Composite;
import edu.american.weiss.lafayette.experiment.BaseExperimentImpl;

public class ObjectDiscrimination extends BaseExperimentImpl {
	
	private static int CORRECT = 1;
	private static int REFUSED = 0;
	private static int INCORRECT = -1;
	
	private UserInterface ui;
	private int trials;
	private int iti;
	private int state;
	
	private int criteriaTrials;
	private int criteriaThreshold;
	private int criteriaCount = 0;
	private int criteriaIndex = 0;
	private int[] criteria;
	
	private int compositeCounter = 0;
	private boolean lastResponseWasCorrect = true;
	
	public ObjectDiscrimination() {
		
		ui = UserInterfaceFactory.getUserInterfaceInstance();
		trials = Application.getIntProperty("trials");
		iti = Application.getIntProperty("iti");
		
		criteriaTrials = Application.getIntProperty("criteria_trials", 20);
		criteriaThreshold = Application.getIntProperty("criteria_threshold", 17);
		criteria = new int[criteriaTrials];
		                      
		AudioPlayer ap = AudioPlayer.getInstance();
		ap.addTrack("od.correct", Application.getProperty("correct_response_wav"));
		ap.addTrack("od.incorrect", Application.getProperty("incorrect_response_wav"));
		
		ImageManager im = ImageManager.getInstance();
		im.loadImage("od.correct", Application.getProperty("correct_image_path"));
		im.loadImage("od.incorrect", Application.getProperty("incorrect_image_path"));
		
	}

	public Composite getInitialComposite() {
		Composite comp = new StartComposite(ui, ui.getResponseSize());
		comp.setType(Composite.INITIAL_COMPOSITE);
        comp.setId("initial");
		return comp;
	}

	public Composite getFinalComposite() {
		Composite comp;
		if (criteriaMet()) {
			String message = "CRITERIA MET";
			comp = new MessageComposite(ui, ui.getResponseSize(), Color.ORANGE, Color.WHITE, message);
		} else {
			comp = new ColorComposite(ui, ui.getResponseSize(), Color.RED);
		}
		comp.setType(Composite.FINAL_COMPOSITE);
        comp.setId("final");
        return comp;
	}

	public Composite getNextComposite() {
		
		if (criteriaMet()) {
			return null;
		}
		
		state = ObjectDiscrimination.REFUSED;
		
		if (compositeCounter >= trials) {
			return null;
		}
				
		Composite comp = new ObjectDiscriminationComposite(ui, ui.getResponseSize());
		comp.setType(Composite.ACTIVE_COMPOSITE);
		comp.setDuration(Application.getIntProperty("max_response_time"));
		comp.setGroupName("od");
		
		lastResponseWasCorrect = false;
		compositeCounter++;
		
		return comp;
		
	}

	public Composite getRestComposite() {
		
		updateCriteria(lastResponseWasCorrect);
		
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
	
	public void setLastResponseWasCorrect(boolean wasCorrect) {
		state = wasCorrect ? ObjectDiscrimination.CORRECT : ObjectDiscrimination.INCORRECT;
		lastResponseWasCorrect = wasCorrect;
	}
	
	private void updateCriteria(boolean wasCorrect) {
		if (criteria[criteriaIndex] == 1) {
			criteriaCount--;
		}
		if (wasCorrect) {
			criteria[criteriaIndex] = 1;
			criteriaCount++;
		} else {
			criteria[criteriaIndex] = 0;
		}
		criteriaIndex = (criteriaIndex + 1) % criteriaTrials;
	}
	
	private boolean criteriaMet() {
		return criteriaCount >= criteriaThreshold;
	}

}
