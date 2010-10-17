package edu.american.weiss.lafayette.composite;

import java.awt.Graphics2D;
import java.util.List;
import edu.american.weiss.lafayette.composite.CompositeElement;

/**
 * DOCUMENT ME!
 *
 * @author jeremy
 */
public interface Composite {
	
	public static final String DEFAULT_GROUP_NAME = "default";
	
    public static final int INITIAL_COMPOSITE = 0;
    public static final int ACTIVE_COMPOSITE = 1;
    public static final int REST_COMPOSITE = 2;
    public static final int FINAL_COMPOSITE = 3;

    public boolean addCompositeElement(CompositeElement ce);
    public void clearCompositeElements();
    public List<CompositeElement> getCompositeElements();
    public void setCompositeElements(List<CompositeElement> componentElements);

    public void init(Graphics2D g2, CompositeController cc);

    public void start();

    public void destroy();
    
    public List<CompositeAction> getActions(int x, int y);
    public CompositeElement getActiveCompositeElement(int x, int y);

    public long getEndTime();
    
    public long getStartTime();

    public long getDuration();
    public void setDuration(long duration);

    public CompositeAction getGlobalAction();
    public void setGlobalAction(CompositeAction globalAction);

    public String getGlobalScheduleId();
    public void setGlobalScheduleId(String globalScheduleId);

    public String getId();
    public void setId(String id);

    public String getName();
    public void setName(String name);
    
    public String getGroupName();
    public void setGroupName(String groupName);

    public int getType();
    public void setType(int type);
    
    public boolean isCompound();
    public void setCompound(boolean isCompound);

    public int getDiagonal();
    public void setDiagonal(int id);
    
}