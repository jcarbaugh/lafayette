package edu.american.huntsberry.experiment;

import java.util.Random;

import edu.american.huntsberry.composite.BlackFrameComposite;
import edu.american.huntsberry.composite.GenericComposite;
import edu.american.huntsberry.compositeelement.BlackFrameElement;
import edu.american.huntsberry.compositeelement.BlueBottomLeftElement;
import edu.american.huntsberry.compositeelement.BlueBottomRightElement;
import edu.american.huntsberry.compositeelement.BlueTopLeftElement;
import edu.american.huntsberry.compositeelement.BlueTopRightElement;
import edu.american.huntsberry.compositeelement.RedBottomLeftElement;
import edu.american.huntsberry.compositeelement.RedBottomRightElement;
import edu.american.huntsberry.compositeelement.RedTopLeftElement;
import edu.american.huntsberry.compositeelement.RedTopRightElement;
import edu.american.weiss.lafayette.Application;
import edu.american.weiss.lafayette.actions.HopperAction;
import edu.american.weiss.lafayette.chamber.UserInterface;
import edu.american.weiss.lafayette.chamber.UserInterfaceFactory;
import edu.american.weiss.lafayette.composite.Composite;
import edu.american.weiss.lafayette.experiment.BaseExperimentImpl;
import edu.american.weiss.lafayette.schedule.ScheduleRepository;
import edu.american.weiss.lafayette.schedule.VariableInterval;

public class TerminalBaseline extends BaseExperimentImpl {

    private UserInterface ui;
    private Random rand;
    private HopperAction ha;
    private String previousCompositeId = "";

	private int topLeftX;
	private int topLeftY;
    
	public TerminalBaseline() {
		
		ha = new HopperAction(getReinforcementDuration());
	    ui = UserInterfaceFactory.getUserInterfaceInstance();
	    rand = new Random();
	    topLeftX = (int) (ui.getResponseSize().getWidth() / 2) - 50;
	    topLeftY = (int) (ui.getResponseSize().getHeight() / 2) - 50;
	    
	    ScheduleRepository sr = ScheduleRepository.getInstance();
	    sr.addSchedule(GenericComposite.class, "blue", new VariableInterval(
	    		Integer.parseInt(Application.getProperty("interval_min", "blue")),
	    		Integer.parseInt(Application.getProperty("interval_max", "blue"))));
	    sr.addSchedule(GenericComposite.class, "red", new VariableInterval(
	    		Integer.parseInt(Application.getProperty("interval_min", "red")),
	    		Integer.parseInt(Application.getProperty("interval_max", "red"))));
	    
	}

	public Composite getInitialComposite() {
        Composite comp = new BlackFrameComposite(ui, ui.getResponseSize());
        comp.setType(Composite.INITIAL_COMPOSITE);
        comp.setDuration(5000);
        comp.setId("initial");
        return comp;
	}

	public Composite getFinalComposite() {
        Composite comp = new BlackFrameComposite(ui, ui.getResponseSize());
        comp.setType(Composite.FINAL_COMPOSITE);
        comp.setId("final");
        return comp;
	}
	
	private Composite generateComposite() {
		
        Composite comp = new GenericComposite(ui, ui.getResponseSize());
		comp.addCompositeElement(new BlackFrameElement(topLeftX, topLeftY, 0));
		
        int compType = rand.nextInt(8);

        switch (compType) {
        	case 0:
        		comp.setId("blue_top_left");
        		comp.setGlobalScheduleId("blue");
        		comp.setGroupName("blue");
        		comp.addCompositeElement(new BlueTopLeftElement(topLeftX, topLeftY, 1));
        		break;
        	case 1:
        		comp.setId("blue_top_right");
        		comp.setGlobalScheduleId("blue");
        		comp.setGroupName("blue");
        		comp.addCompositeElement(new BlueTopRightElement(topLeftX, topLeftY, 1));
        		break;
        	case 2:
        		comp.setId("blue_bottom_left");
        		comp.setGlobalScheduleId("blue");
        		comp.setGroupName("blue");
        		comp.addCompositeElement(new BlueBottomLeftElement(topLeftX, topLeftY, 1));
        		break;
        	case 3:
        		comp.setId("blue_bottom_right");
        		comp.setGlobalScheduleId("blue");
        		comp.setGroupName("blue");
        		comp.addCompositeElement(new BlueBottomRightElement(topLeftX, topLeftY, 1));
        		break;
        	case 4:
        		comp.setId("red_top_left");
        		comp.setGlobalScheduleId("red");
        		comp.setGroupName("red");
        		comp.addCompositeElement(new RedTopLeftElement(topLeftX, topLeftY, 1));
        		break;
        	case 5:
        		comp.setId("red_top_right");
        		comp.setGlobalScheduleId("red");
        		comp.setGroupName("red");
        		comp.addCompositeElement(new RedTopRightElement(topLeftX, topLeftY, 1));
        		break;
        	case 6:
        		comp.setId("red_bottom_left");
        		comp.setGlobalScheduleId("red");
        		comp.setGroupName("red");
        		comp.addCompositeElement(new RedBottomLeftElement(topLeftX, topLeftY, 1));
        		break;
        	case 7:
        		comp.setId("red_bottom_right");
        		comp.setGlobalScheduleId("red");
        		comp.setGroupName("red");
        		comp.addCompositeElement(new RedBottomRightElement(topLeftX, topLeftY, 1));
        		break;
        }

        comp.setGlobalAction(ha);
        
        comp.setType(Composite.ACTIVE_COMPOSITE);
        
        comp.setDuration(
        		Application.getRandomLong(
        				Integer.parseInt(Application.getProperty("composite_min", comp.getId())),
        				Integer.parseInt(Application.getProperty("composite_max", comp.getId()))));
        
        return comp;
        
	}

	public Composite getNextComposite() {
		
		Composite c = null;
		
		do {
			c = generateComposite();
		} while (previousCompositeId.equals(c.getId()) && isPseudoRandom());
			
		previousCompositeId = c.getId();
			
        return c;
        
	}

	public Composite getRestComposite() {
        Composite comp = new BlackFrameComposite(ui, ui.getResponseSize());
        comp.setType(Composite.REST_COMPOSITE);
        comp.setDuration(
        		Application.getRandomLong(
        				Integer.parseInt(Application.getProperty("rest_min")),
        				Integer.parseInt(Application.getProperty("rest_max"))));
        comp.setId("rest");
        return comp;
	}

	public boolean isCorrecting() {
		return true;
	}
	
	public boolean isPseudoRandom() {
		String pr = Application.getProperty("pseudo_random");
		if (pr != null && pr.equals("true")) {
			return true;
		} else {
			return false;
		}
	}
	
	public int getRestProbability() {
		return Integer.parseInt(Application.getProperty("rest_probability"));
	}

}