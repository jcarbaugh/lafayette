package edu.american.weiss.lafayette.composite;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Map;

import edu.american.huntsberry.composite.GenericComposite;
import edu.american.huntsberry.compositeelement.TriangleCompositeElement;
import edu.american.weiss.lafayette.actions.RestAction;
import edu.american.weiss.lafayette.chamber.UserInterface;

public class TriangleCompositeUtil {

	public static final Color COLOR_BLACK = Color.BLACK;
	public static final Color COLOR_GREEN = Color.getHSBColor(0.33f, 1.00f, 0.65f);
	public static final Color COLOR_ORANGE = Color.getHSBColor(0.08f, 1.00f, 0.96f);
	public static final Color COLOR_PURPLE = Color.getHSBColor(0.83f, 0.70f, 1.00f);
	public static final Color COLOR_RED = Color.getHSBColor(0.00f, 0.56f, 1.00f);
	public static final Color COLOR_WHITE = Color.getHSBColor(0.00f, 0.00f, 0.65f);
	public static final Color COLOR_YELLOW = Color.getHSBColor(0.14f, 1.00f, 0.76f);

	public static final int DIAGONAL_UNDEFINED = -1;
	public static final int DIAGONAL_BACK = 0;
	public static final int DIAGONAL_FORWARD = 1;

	public static final int TYPE_UNDEFINED = -1;
	public static final int TYPE_TOP_LEFT = 0;
	public static final int TYPE_TOP_RIGHT = 1;
	public static final int TYPE_BOTTOM_LEFT = 2;
	public static final int TYPE_BOTTOM_RIGHT = 3;

	private Map colorNameMap;
	private Map typeNameMap;
	private Map shortTypeNameMap;
	
	private Rectangle boundary;
	private Rectangle boundaryActive;
	private RestAction ra;
	private UserInterface ui;
	
	public TriangleCompositeUtil(UserInterface ui, Rectangle boundary, Rectangle boundaryActive) {
		
		this.ui = ui;
		this.boundary = boundary;
		this.boundaryActive = boundaryActive;

		ra = new RestAction();
		
		typeNameMap = new HashMap();
		typeNameMap.put(Integer.valueOf(TYPE_TOP_LEFT), "top_left");
		typeNameMap.put(Integer.valueOf(TYPE_TOP_RIGHT), "top_right");
		typeNameMap.put(Integer.valueOf(TYPE_BOTTOM_LEFT), "bottom_left");
		typeNameMap.put(Integer.valueOf(TYPE_BOTTOM_RIGHT), "bottom_right");
		
		shortTypeNameMap = new HashMap();
		shortTypeNameMap.put(Integer.valueOf(TYPE_TOP_LEFT), "tl");
		shortTypeNameMap.put(Integer.valueOf(TYPE_TOP_RIGHT), "tr");
		shortTypeNameMap.put(Integer.valueOf(TYPE_BOTTOM_LEFT), "bl");
		shortTypeNameMap.put(Integer.valueOf(TYPE_BOTTOM_RIGHT), "br");
		
		colorNameMap = new HashMap();
		colorNameMap.put(COLOR_BLACK, "black");
		colorNameMap.put(COLOR_GREEN, "green");
		colorNameMap.put(COLOR_ORANGE, "orange");
		colorNameMap.put(COLOR_PURPLE, "purple");
		colorNameMap.put(COLOR_RED, "red");
		colorNameMap.put(COLOR_WHITE, "white");
		colorNameMap.put(COLOR_YELLOW, "yellow");
		
	}
	
	public Composite generateComposite(Color c1, Color c2, String groupName, String scheduleId,
			int type, CompositeAction ca, boolean isPunishing) {
		
		Composite comp = new GenericComposite(ui, ui.getResponseSize());
		CompositeElement compElem;
		int diagonal = -1;
		
		if (type == TYPE_TOP_LEFT || type == TYPE_BOTTOM_RIGHT) {
			diagonal = DIAGONAL_FORWARD;
		} else if (type == TYPE_BOTTOM_LEFT || type == TYPE_TOP_RIGHT) {
			diagonal = DIAGONAL_BACK;
		} else {
			diagonal = DIAGONAL_UNDEFINED;
		}
		comp.setDiagonal(diagonal);
		
		if (c1 == null) {
			
			comp.setId("rest");
        	compElem = new TriangleCompositeElement(boundary, type, 5, COLOR_WHITE);
    		compElem.setGroupName("white");
        	comp.addCompositeElement(compElem);
        	compElem = new TriangleCompositeElement(boundary, getOppositeType(type), 5, COLOR_WHITE);
    		compElem.setGroupName("white");
        	comp.addCompositeElement(compElem);
        	
		} else {
			
			String color1Name = getColorName(c1);
			String color2Name = getColorName(c2);
			int oppositeType = getOppositeType(type);
			
			if (groupName != null) {
				comp.setGroupName(groupName);
			}
			
			compElem = new TriangleCompositeElement(boundary, type, 5, c1);
			compElem.setZIndex(0);
			comp.addCompositeElement(compElem);
			compElem = new TriangleCompositeElement(boundaryActive, type, 5, null);
			compElem.setZIndex(1);
			compElem.setGroupName(color1Name);
			if (scheduleId != null) {
				compElem.setScheduleId(scheduleId);
			}
			if (ca != null) {
				compElem.addCompositeAction(ca);
			}
			comp.addCompositeElement(compElem);
			
			if (c2 == null) {
				
				comp.setId(color1Name + "_" + getTypeName(type));

				compElem = new TriangleCompositeElement(boundary, oppositeType, 5, COLOR_WHITE);
				compElem.setZIndex(0);
				comp.addCompositeElement(compElem);
				compElem = new TriangleCompositeElement(boundaryActive, oppositeType, 5, null);
				compElem.setZIndex(1);
				compElem.setGroupName("white");
				if (isPunishing) {
					compElem.addCompositeAction(ra);
				}
				comp.addCompositeElement(compElem);				
				
			} else {
				
				comp.setId(color1Name + "_" + getShortTypeName(type) + "_" +
						color2Name + "_" + getShortTypeName(oppositeType));
				
				compElem = new TriangleCompositeElement(boundary, oppositeType, 5, c2);
				compElem.setZIndex(0);
				comp.addCompositeElement(compElem);
				compElem = new TriangleCompositeElement(boundaryActive, oppositeType, 5, null);
				compElem.setZIndex(1);
				compElem.setGroupName(color2Name);
				if (scheduleId != null) {
					compElem.setScheduleId(scheduleId);
				}
				if (ca != null) {
					compElem.addCompositeAction(ca);
				}
				comp.addCompositeElement(compElem);
								
			}			
			
		}
		
		return comp;
		
	}
	
	private String getShortTypeName(int type) {
		return (String) shortTypeNameMap.get(Integer.valueOf(type));
	}
	
	private String getTypeName(int type) {
		return (String) typeNameMap.get(Integer.valueOf(type));
	}
	
	private String getColorName(Color c) {
		return (String) colorNameMap.get(c);
	}
	
	private int getOppositeType(int type) {
		int oppositeType = TYPE_UNDEFINED;
		switch (type) {
		case TYPE_TOP_LEFT:
			oppositeType = TYPE_BOTTOM_RIGHT;
			break;
		case TYPE_TOP_RIGHT:
			oppositeType = TYPE_BOTTOM_LEFT;
			break;
		case TYPE_BOTTOM_LEFT:
			oppositeType = TYPE_TOP_RIGHT;
			break;
		case TYPE_BOTTOM_RIGHT:
			oppositeType = TYPE_TOP_LEFT;
			break;
		}
		return oppositeType;
	}
	
	public static int getDiagonal(int type) {
		int diagonal = DIAGONAL_UNDEFINED;
		if (type == TYPE_TOP_LEFT || type == TYPE_BOTTOM_RIGHT) {
			diagonal = DIAGONAL_FORWARD;
		} else if (type == TYPE_BOTTOM_LEFT || type == TYPE_TOP_RIGHT) {
			diagonal = DIAGONAL_BACK;
		}
		return diagonal;
	}

}