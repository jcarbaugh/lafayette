package edu.american.huntsberry.experiment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import edu.american.huntsberry.composite.BlackComposite;
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

public class Test1 extends BaseExperimentImpl {

	//private final int FIFTEEN_MINUTES = 1800000;
	//private final int TEST_DURATION = 60000;

	private int warmUpDuration;
	private int compositeDuration;
	
    private UserInterface ui;
    private Random rand;
    private String previousCompositeId = "";

    private int testCount = 0;
	private int topLeftX;
	private int topLeftY;
	private long startTime;
	
    private HopperAction ha;
    private List testComposites;
    
	public Test1() {
		
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
	    	    
	    compositeDuration = Application.getIntProperty("composite_duration");
	    warmUpDuration = Application.getIntProperty("warmup_duration");
	    
	    testComposites = new ArrayList();
	    
		ha = new HopperAction(getReinforcementDuration());
	    
	}

	public Composite getInitialComposite() {
		startTime = System.currentTimeMillis();
        Composite comp = new BlackFrameComposite(ui, ui.getResponseSize());
        comp.setType(Composite.INITIAL_COMPOSITE);
        comp.setDuration(5000);
        comp.setId("initial");
        return comp;
	}

	public Composite getFinalComposite() {
        Composite comp = new BlackComposite(ui, ui.getResponseSize());
        comp.setType(Composite.FINAL_COMPOSITE);
        comp.setId("final");
        return comp;
	}
	
	private Composite generateTestComposite() {
		
		if (testComposites.size() == 0) {
			loadComposites(testComposites);
		}
		
		Composite comp = (Composite) testComposites.remove(0);

        comp.setType(Composite.ACTIVE_COMPOSITE);
        comp.setDuration(compositeDuration);
        
        return comp;
        
	}
	
	private Composite generateWarmupComposite() {
		
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
		
		if (System.currentTimeMillis() - startTime < warmUpDuration) {
			do {
				c = generateWarmupComposite();
			} while (previousCompositeId.equals(c.getId()) && isPseudoRandom());
		} else {
			Application.setProperty("rest_probability", "100");
			Application.setProperty("response_correction_duration", "0");
			c = generateTestComposite();
			testCount++;
		}
		
		previousCompositeId = c.getId();
					
        return c;
        
	}

	public Composite getRestComposite() {
		
        Composite comp;
        
        if (testCount >= Application.getIntProperty("block_count")) {
        	comp = null;
        	Application.getCompositeController().setActive(false);
        } else {
        	comp = new BlackFrameComposite(ui, ui.getResponseSize());
        	comp.setType(Composite.REST_COMPOSITE);
        	if (System.currentTimeMillis() - startTime < warmUpDuration) {
        		comp.setDuration(
        				Application.getRandomLong(
        						Integer.parseInt(Application.getProperty("rest_min")),
        						Integer.parseInt(Application.getProperty("rest_max"))));
        	} else {
        		comp.setDuration(compositeDuration);
        	}
        	comp.setId("rest");
        }
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
	
	private void loadComposites(List randomComposites) {
		
		List blueComposites = new ArrayList(4);
		List redComposites = new ArrayList(4);
		List compoundComposites = new ArrayList(4);
		List tempComposites = new ArrayList(3);
		
		randomComposites.clear();
		
        Composite comp;
        		
        /*
         * blue
         */
        comp = new GenericComposite(ui, ui.getResponseSize());
        comp.addCompositeElement(new BlackFrameElement(topLeftX, topLeftY, 0));
        comp.setId("blue_top_left");
        comp.setGroupName("blue");
        comp.addCompositeElement(new BlueTopLeftElement(topLeftX, topLeftY, 1));
        blueComposites.add(comp);
        
        comp = new GenericComposite(ui, ui.getResponseSize());
        comp.addCompositeElement(new BlackFrameElement(topLeftX, topLeftY, 0));
        comp.setId("blue_top_right");
        comp.setGroupName("blue");
        comp.addCompositeElement(new BlueTopRightElement(topLeftX, topLeftY, 1));
        blueComposites.add(comp);
        
        comp = new GenericComposite(ui, ui.getResponseSize());
        comp.addCompositeElement(new BlackFrameElement(topLeftX, topLeftY, 0));
        comp.setId("blue_bottom_left");
        comp.setGroupName("blue");
        comp.addCompositeElement(new BlueBottomLeftElement(topLeftX, topLeftY, 1));
        blueComposites.add(comp);
        
        comp = new GenericComposite(ui, ui.getResponseSize());
        comp.addCompositeElement(new BlackFrameElement(topLeftX, topLeftY, 0));
        comp.setId("blue_bottom_right");
        comp.setGroupName("blue");
        comp.addCompositeElement(new BlueBottomRightElement(topLeftX, topLeftY, 1));
        blueComposites.add(comp);
        
        /*
         * red
         */
        
        comp = new GenericComposite(ui, ui.getResponseSize());
        comp.addCompositeElement(new BlackFrameElement(topLeftX, topLeftY, 0));
        comp.setId("red_top_left");
        comp.setGroupName("red");
        comp.addCompositeElement(new RedTopLeftElement(topLeftX, topLeftY, 1));
        redComposites.add(comp);
        
        comp = new GenericComposite(ui, ui.getResponseSize());
        comp.addCompositeElement(new BlackFrameElement(topLeftX, topLeftY, 0));
        comp.setId("red_top_right");
        comp.setGroupName("red");
        comp.addCompositeElement(new RedTopRightElement(topLeftX, topLeftY, 1));
        redComposites.add(comp);
        
        comp = new GenericComposite(ui, ui.getResponseSize());
        comp.addCompositeElement(new BlackFrameElement(topLeftX, topLeftY, 0));
        comp.setId("red_bottom_left");
        comp.setGroupName("red");
        comp.addCompositeElement(new RedBottomLeftElement(topLeftX, topLeftY, 1));
        redComposites.add(comp);
        
        comp = new GenericComposite(ui, ui.getResponseSize());
        comp.addCompositeElement(new BlackFrameElement(topLeftX, topLeftY, 0));
        comp.setId("red_bottom_right");
        comp.setGroupName("red");
        comp.addCompositeElement(new RedBottomRightElement(topLeftX, topLeftY, 1));
        redComposites.add(comp);
        
        /*
         * compound
         */
        
        comp = new GenericComposite(ui, ui.getResponseSize());
        comp.addCompositeElement(new BlackFrameElement(topLeftX, topLeftY, 0));
        comp.setId("red_top_left_blue_bottom_right");
        comp.setGroupName("compound");
        comp.addCompositeElement(new RedTopLeftElement(topLeftX, topLeftY, 1));
        comp.addCompositeElement(new BlueBottomRightElement(topLeftX, topLeftY, 1));
        compoundComposites.add(comp);
        
        comp = new GenericComposite(ui, ui.getResponseSize());
        comp.addCompositeElement(new BlackFrameElement(topLeftX, topLeftY, 0));
        comp.setId("red_top_right_blue_bottom_left");
        comp.setGroupName("compound");
        comp.addCompositeElement(new RedTopRightElement(topLeftX, topLeftY, 1));
        comp.addCompositeElement(new BlueBottomLeftElement(topLeftX, topLeftY, 1));
        compoundComposites.add(comp);
        
        comp = new GenericComposite(ui, ui.getResponseSize());
        comp.addCompositeElement(new BlackFrameElement(topLeftX, topLeftY, 0));
        comp.setId("red_bottom_left_blue_top_right");
        comp.setGroupName("compound");
        comp.addCompositeElement(new RedBottomLeftElement(topLeftX, topLeftY, 1));
        comp.addCompositeElement(new BlueTopRightElement(topLeftX, topLeftY, 1));
        compoundComposites.add(comp);
        
        comp = new GenericComposite(ui, ui.getResponseSize());
        comp.addCompositeElement(new BlackFrameElement(topLeftX, topLeftY, 0));
        comp.setId("red_bottom_right_blue_top_left");
        comp.setGroupName("compound");
        comp.addCompositeElement(new RedBottomRightElement(topLeftX, topLeftY, 1));
        comp.addCompositeElement(new BlueTopLeftElement(topLeftX, topLeftY, 1));
        compoundComposites.add(comp);
        
		Collections.shuffle(blueComposites);
		Collections.shuffle(redComposites);
		Collections.shuffle(compoundComposites);
		
		for (int i = 0; i < 4; i++) {
			Composite tc = null;
			Composite rc = null;
			tempComposites.clear();
			tempComposites.add(blueComposites.remove(0));
			tempComposites.add(redComposites.remove(0));
			tempComposites.add(compoundComposites.remove(0));
			do {
				Collections.shuffle(tempComposites);
				tc = (Composite) tempComposites.get(0);
				if (randomComposites.size() > 0) {
					rc = (Composite) randomComposites.get(randomComposites.size() - 1);
				}
			} while (rc != null &&
					tc.getGroupName().equals(rc.getGroupName()));
			randomComposites.addAll(tempComposites);
		}
		
	}

}