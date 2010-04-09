package edu.american.huntsberry.compositeelement;

import java.awt.Color;
import java.awt.Polygon;

import edu.american.weiss.lafayette.composite.BaseCompositeElement;

public class BlueBottomRightElement extends BaseCompositeElement {
	
	public BlueBottomRightElement(int topLeftX, int topLeftY, int zIndex) {
		
		Polygon p = new Polygon();
		p.addPoint(topLeftX, topLeftY + 100);
		p.addPoint(topLeftX + 100, topLeftY);
		p.addPoint(topLeftX + 100, topLeftY + 100);
		
		setShape(p);
		setBackgroundColor(Color.BLUE);
		setOutlineColor(Color.BLACK);
		setZIndex(zIndex);
		
		setGroupName("blue");
		
	}

}