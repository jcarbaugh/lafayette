package edu.american.weiss.lafayette.experiment.test;

import edu.american.huntsberry.composite.BlackComposite;
import edu.american.huntsberry.composite.BlueBLComposite;
import edu.american.huntsberry.composite.EmptyComposite;
import edu.american.huntsberry.composite.RedTRComposite;
import edu.american.weiss.lafayette.chamber.UserInterface;
import edu.american.weiss.lafayette.chamber.UserInterfaceFactory;
import edu.american.weiss.lafayette.composite.BaseCompositeElement;
import edu.american.weiss.lafayette.composite.Composite;
import edu.american.weiss.lafayette.experiment.BaseExperimentImpl;
import edu.american.weiss.lafayette.schedule.FixedInterval;
import edu.american.weiss.lafayette.schedule.ScheduleRepository;
import edu.american.weiss.lafayette.schedule.VariableInterval;

/**
 * @author jeremy
 */
public class TestExperimentImpl extends BaseExperimentImpl {
    
    private UserInterface ui;
    private long compositeCount;
    
    
    public TestExperimentImpl() {
	    ScheduleRepository sr = ScheduleRepository.getInstance();
	    sr.addSchedule(BlueBLComposite.class, "fi", new FixedInterval(5000));
	    sr.addSchedule(RedTRComposite.class, "vi", new VariableInterval(5000, 15000));
	    ui = UserInterfaceFactory.getUserInterfaceInstance();
    }



    public Composite getInitialComposite() {
        Composite comp = new BlackComposite(ui, ui.getResponseSize());
        comp.setType(Composite.INITIAL_COMPOSITE);
        comp.setDuration(5000);
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
        if (compositeCount < 6) {
            if (compositeCount % 2 == 0) {
                comp = new BlueBLComposite(ui, ui.getResponseSize());
            } else {
                comp = new RedTRComposite(ui, ui.getResponseSize());
            }
            comp.setType(Composite.ACTIVE_COMPOSITE);
            comp.setDuration(10000);
            compositeCount++;
        } else {
            comp = null;
        }
        return comp;
    }



    public Composite getRestComposite() {
        Composite comp = new EmptyComposite(ui, ui.getResponseSize());
        comp.setType(Composite.REST_COMPOSITE);
        comp.setDuration(3000);
        //comp.setGlobalAction()
        return comp;
    }

	public long getCorrectionDuration() {
		return 30000;
	}

	public boolean isCorrecting() {
		return true;
	}    

}