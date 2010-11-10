package edu.american.weiss.lafayette.composite;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.american.weiss.lafayette.Application;
import edu.american.weiss.lafayette.chamber.UserInterface;
import edu.american.weiss.lafayette.composite.CompositeElement;
import edu.american.weiss.lafayette.schedule.Schedule;
import edu.american.weiss.lafayette.schedule.ScheduleRepository;

/**
 * @author jeremy
 */
public abstract class BaseComposite implements Composite {
	
    private int type;
    private int diagonal;
    private long duration;
    private long startTime;
    private long endTime;
    private boolean isCompound;
    private boolean isDestroyed;
	
	private String id;
	private String name;
	private String groupName;

	private CompositeAction globalAction = null;
	private Schedule globalSchedule;
	private String globalScheduleId;
	protected List<CompositeElement> compositeElements;
		
	protected UserInterface ui;
    protected Graphics2D g2d;
    protected BufferedImage bi;
    protected Dimension d;
	
	public BaseComposite(UserInterface ui) {
		compositeElements = new ArrayList<CompositeElement>(5);
		this.ui = ui;
		id = this.getClass().getName();
		groupName = DEFAULT_GROUP_NAME;
    	d = ui.getResponseSize();
		bi = new BufferedImage((int) d.getWidth(), (int) d.getHeight(), BufferedImage.TYPE_INT_RGB);
		g2d = bi.createGraphics();
	}
	
	public void init() {
	    if (globalScheduleId != null) {
	    	globalSchedule = ScheduleRepository.getInstance().getSchedule(this.getClass(), globalScheduleId);
	    }
	    for (Iterator<CompositeElement> it = compositeElements.iterator(); it.hasNext();) {
	    	((CompositeElement) it.next()).init(this);
	    }
	}
	
	public CompositeElement getActiveCompositeElement(int x, int y) {
		
		int zIndex = -1;
		CompositeElement activeCompElement = null;
		CompositeElement ce;
		
		for (int i = 0; i < compositeElements.size(); i++) {
			
			ce = (CompositeElement) compositeElements.get(i);
			
			if (ce.contains(x, y) && ce.getZIndex() > zIndex) {
				activeCompElement = ce;
				zIndex = ce.getZIndex();
			}
			
		}
		
		return activeCompElement;
		
	}

    /**
     * @see edu.american.weiss.lafayette.composite.Composite#getActions(int, int)
     */
    public List<CompositeAction> getActions(int x, int y) {
		
		ArrayList<CompositeAction> al = new ArrayList<CompositeAction>(10);
		CompositeElement ce = getActiveCompositeElement(x, y);
		
		if (ce != null &&
				ce.getCompositeActions() != null &&
				ce.isActive(x, y)) {
			al.addAll(ce.getCompositeActions());
		}
		
		if (globalAction != null) {
			globalAction.setCompositeElement(ce);
		    if (globalSchedule == null) {
				al.add(globalAction);
		    } else if (!globalSchedule.isInInterval(System.currentTimeMillis())) {
				al.add(globalAction);
				globalSchedule.reset(Application.getIntProperty("reinforcement_duration"));
		    }
		}
		
		return al;
		
    }

    /**
     * @see edu.american.weiss.lafayette.composite.Composite#addComponentElement(edu.american.weiss.composite.CompositeElement)
     */
    public boolean addCompositeElement(CompositeElement ce) {
		return compositeElements.add(ce);
    }

    /**
     * @see edu.american.weiss.lafayette.composite.Composite#clearComponentElements()
     */
    public void clearCompositeElements() {
		compositeElements.clear();
    }

	/**
     * @see edu.american.weiss.lafayette.composite.Composite#getComponentElements()
     */
    public List<CompositeElement> getCompositeElements() {
		return compositeElements;
    }

    /**
     * @see edu.american.weiss.lafayette.composite.Composite#setComponentElements(java.util.List)
     */
    public void setCompositeElements(List<CompositeElement> componentElements) {
		this.compositeElements = componentElements;
    }

    /**
     * @see edu.american.weiss.lafayette.composite.Composite#getGlobalAction()
     */
    public CompositeAction getGlobalAction() {
		return globalAction;
    }

    /**
     * @see edu.american.weiss.lafayette.composite.Composite#setGlobalAction(edu.american.weiss.composite.CompositeAction)
     */
    public void setGlobalAction(CompositeAction globalAction) {
		this.globalAction = globalAction;
    }

    /**
     * @see edu.american.weiss.lafayette.composite.Composite#getId()
     */
    public String getId() {
		return id;
    }

    /**
     * @see edu.american.weiss.lafayette.composite.Composite#setId(java.lang.String)
     */
    public void setId(String id) {
		this.id = id;
    }

    /**
     * @see edu.american.weiss.lafayette.composite.Composite#getName()
     */
    public String getName() {
		return name;
    }

    /**
     * @see edu.american.weiss.lafayette.composite.Composite#setName(java.lang.String)
     */
    public void setName(String name) {
		this.name = name;
    }

    public void start() {
    	startTime = System.currentTimeMillis();
        if (globalSchedule != null) {
        	globalSchedule.start();
        }
        Iterator<CompositeElement> it = getCompositeElements().iterator();
        while (it.hasNext()) {
            ((CompositeElement) it.next()).start();
        }
    }
    
    public void destroy() {
    	endTime = System.currentTimeMillis();
        if (globalSchedule != null) {
        	globalSchedule.pause(System.currentTimeMillis());
        }
        Iterator<CompositeElement> it = getCompositeElements().iterator();
        while (it.hasNext()) {
            ((CompositeElement) it.next()).destroy();
        }
    }
    

    public long getDuration() {
        return duration;
    }
    public void setDuration(long duration) {
        this.duration = duration;
    }
    public int getType() {
        return type;
    }
    public void setType(int type) {
        this.type = type;
    }

	public String getGlobalScheduleId() {
		return globalScheduleId;
	}

	public void setGlobalScheduleId(String globalScheduleId) {
		this.globalScheduleId = globalScheduleId;
	}
	
	public long getStartTime() {
		return startTime;
	}
	
	public long getEndTime() {
		return endTime;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	
	public boolean isCompound() {
		return isCompound;
	}
	
	public void setCompound(boolean isCompound) {
		this.isCompound = isCompound;
	}

	public int getDiagonal() {
		return diagonal;
	}

	public void setDiagonal(int diagonal) {
		this.diagonal = diagonal;
	}
        
}