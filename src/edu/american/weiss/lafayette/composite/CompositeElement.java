package edu.american.weiss.lafayette.composite;

import java.awt.Color;
import java.awt.Shape;
import java.util.List;

import edu.american.weiss.lafayette.chamber.UserInterface;

public interface CompositeElement {
	
	public static final String DEFAULT_GROUP_NAME = "default";
    
	public boolean isActive(int x, int y);
	
	public Color getBackgroundColor();
	public void setBackgroundColor(Color c);
	
	public Composite getComposite();
	public List<CompositeAction> getCompositeActions();
	public void setCompositeActions(List<CompositeAction> actions);
	public void addCompositeAction(CompositeAction ca);
	public void removeCompositeAction(CompositeAction ca);
	
	public Color getOutlineColor();
	public void setOutlineColor(Color c);
	
	public Shape getShape();
	public void setShape(Shape s);
	
	public boolean contains(int x, int y);
	
	public int getZIndex();
	public void setZIndex(int zIndex);
	
	public void setUserInterface(UserInterface ui);
	
	public void init(Composite c);
	public void start();
	public void destroy();
	
	public String getGroupName();
	public void setGroupName(String groupName);
	
	public String getScheduleId();
	public void setScheduleId(String schedId);

}