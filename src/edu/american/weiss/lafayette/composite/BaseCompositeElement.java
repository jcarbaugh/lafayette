package edu.american.weiss.lafayette.composite;

import java.awt.Color;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.List;

import edu.american.weiss.lafayette.Application;
import edu.american.weiss.lafayette.chamber.UserInterface;
import edu.american.weiss.lafayette.schedule.Interval;
import edu.american.weiss.lafayette.schedule.Ratio;
import edu.american.weiss.lafayette.schedule.Schedule;
import edu.american.weiss.lafayette.schedule.ScheduleRepository;

public class BaseCompositeElement implements CompositeElement {

	private Composite c;
	private List actions;
	
	private Color bgColor;
	private Color olColor;
	private Shape s;
	private Schedule sched;
	private String schedId;
	private String groupName;
	private UserInterface ui;
	private int zIndex;
	
	public BaseCompositeElement() {
	    actions = new ArrayList();
	    groupName = DEFAULT_GROUP_NAME;
	}
	
	public void init(Composite c) {
	    this.c = c;
	    if (schedId != null) {
	    	sched = ScheduleRepository.getInstance().getSchedule(c.getClass(), getScheduleId());
	    }
	}

    /**
     * @see edu.american.weiss.lafayette.composite.CompositeElement#isActive(int, int)
     */
    public boolean isActive(int x, int y) {
        long intervalValue;
        boolean isActive = false;
		if (s.contains(x, y)) {
		    if (sched instanceof Interval) {
		        intervalValue = System.currentTimeMillis();
		    } else if (sched instanceof Ratio) {
		        intervalValue = System.currentTimeMillis();
		    } else {
		        intervalValue = -1;
		    }
		    if (sched == null) { 
		        isActive = true;
		    } else if (!sched.isInInterval(intervalValue)) {
		        isActive = true;
		        sched.reset(Application.getIntProperty("reinforcement_duration"));
		    } else {
		        //System.out.println("NOT ACTIVE");
		        //UserInterfaceFactory.getUserInterfaceInstance().writeMessage("NOT ACTIVE");
		    }
		} else {
		    //System.out.println("not contained");
	        //UserInterfaceFactory.getUserInterfaceInstance().writeMessage("not contained");
		}
		return isActive;
    }
    
    public void start() {
        if (sched != null) {
            sched.start();
        }
    }
    
    public void destroy() {
        if (sched != null) {
            sched.pause(System.currentTimeMillis());
        }
    }

    /**
     * @see edu.american.weiss.lafayette.composite.CompositeElement#getBackgroundColor()
     */
    public Color getBackgroundColor() {
		return bgColor;
    }

    /**
     * @see edu.american.weiss.lafayette.composite.CompositeElement#setBackgroundColor(java.awt.Color)
     */
    public void setBackgroundColor(Color c) {
		this.bgColor = c;

    }

    /**
     * @see edu.american.weiss.lafayette.composite.CompositeElement#getComponentAction()
     */
    public List getCompositeActions() {
		return actions;
    }

    /**
     * @see edu.american.weiss.lafayette.composite.CompositeElement#setComponentAction(edu.american.weiss.lafayette.composite.CompositeAction)
     */
    public void setCompositeActions(List actions) {
    	if (actions != null) {
    		this.actions = actions;
    	}
    }

    
    public void addCompositeAction(CompositeAction ca) {
    	ca.setCompositeElement(this);
        actions.add(ca);
    }
    
    public void removeCompositeAction(CompositeAction ca) {
        actions.remove(ca);
    }
    
    /**
     * @see edu.american.weiss.lafayette.composite.CompositeElement#getOutlineColor()
     */
    public Color getOutlineColor() {
		return olColor;
    }

    /**
     * @see edu.american.weiss.lafayette.composite.CompositeElement#setOutlineColor(java.awt.Color)
     */
    public void setOutlineColor(Color c) {
		this.olColor = c;
    }
    
    public void setUserInterface(UserInterface ui) {
        this.ui = ui;
    }

    /**
     * @see edu.american.weiss.lafayette.composite.CompositeElement#getShape()
     */
    public Shape getShape() {
		return s;
    }

    /**
     * @see edu.american.weiss.lafayette.composite.CompositeElement#setShape(java.awt.Shape)
     */
    public void setShape(Shape s) {
		this.s = s;
    }
    
    public void setSchedule(Schedule s) {
        sched = s;
    }
    
    public String getScheduleId() {
    	return schedId;
    }
    
    public void setScheduleId(String schedId) {
    	this.schedId = schedId;
    }

	public int getZIndex() {
		return zIndex;
	}

	public void setZIndex(int index) {
		zIndex = index;
	}
	
	public boolean contains(int x, int y) {
		boolean isContained = false;
		if (getShape() != null) {
			isContained = getShape().contains(x, y);
		}
		return isContained;		
	}
	
	public String getGroupName() {
		return groupName;
	}
	
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
    
}