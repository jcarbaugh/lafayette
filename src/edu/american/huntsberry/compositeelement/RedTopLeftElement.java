package edu.american.huntsberry.compositeelement;

import java.awt.Color;
import java.awt.Polygon;

import edu.american.weiss.lafayette.composite.BaseCompositeElement;

public class RedTopLeftElement extends BaseCompositeElement {
	
	public RedTopLeftElement(int topLeftX, int topLeftY, int zIndex) {
		
		Polygon p = new Polygon();
		p.addPoint(topLeftX, topLeftY);
		p.addPoint(topLeftX + 100, topLeftY);
		p.addPoint(topLeftX, topLeftY + 100);
		
		setShape(p);
		setBackgroundColor(Color.RED);
		setOutlineColor(Color.BLACK);
		setZIndex(zIndex);
		
		setGroupName("red");
		
	}

}