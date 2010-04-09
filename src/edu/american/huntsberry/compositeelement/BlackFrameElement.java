package edu.american.huntsberry.compositeelement;

import java.awt.Color;
import java.awt.Polygon;

import edu.american.weiss.lafayette.composite.BaseCompositeElement;

public class BlackFrameElement extends BaseCompositeElement {

	public BlackFrameElement(int topLeftX, int topLeftY, int zIndex) {

		Polygon p = new Polygon(); 
		p.addPoint(topLeftX - 5, topLeftY - 5);
		p.addPoint(topLeftX - 5, topLeftY + 105);
		p.addPoint(topLeftX + 105, topLeftY + 105);
		p.addPoint(topLeftX + 105, topLeftY - 5);
		
		setShape(p);
		setBackgroundColor(Color.WHITE);
		setOutlineColor(Color.WHITE);
		setZIndex(zIndex);
		
		setGroupName("white");
		
	}
	
}