package edu.american.weiss.lafayette.event;

import java.awt.Point;

import edu.american.weiss.lafayette.composite.Composite;

public class ResponseEvent extends BaseChamberEventImpl {

	private Point responsePoint;
	
	public ResponseEvent(Composite activeComposite, int x, int y) {
		super(activeComposite);
		this.responsePoint = new Point(x, y);
	}
	
	public Point getResponsePoint() {
		return responsePoint;
	}
	
	public int getX() {
		return (int) responsePoint.getX();
	}
	
	public int getY() {
		return (int) responsePoint.getY();
	}

}