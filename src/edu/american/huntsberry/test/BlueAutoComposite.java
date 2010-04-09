package edu.american.huntsberry.test;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.util.List;

import edu.american.weiss.lafayette.actions.HopperAction;
import edu.american.weiss.lafayette.chamber.Opto22Hopper;
import edu.american.weiss.lafayette.chamber.UserInterface;
import edu.american.weiss.lafayette.composite.BaseComposite;
import edu.american.weiss.lafayette.composite.BaseCompositeElement;
import edu.american.weiss.lafayette.composite.CompositeController;
import edu.american.weiss.lafayette.composite.CompositeElement;

public class BlueAutoComposite extends BaseComposite {

	private CompositeController cc;
	private Dimension d;
    private UserInterface ui;
    private boolean activated;
	
	public BlueAutoComposite(UserInterface ui, Dimension d) {
		
		super(ui);
		this.d = d;
		this.ui = ui;
		this.activated = false;
		
	}
	
	public void init(Graphics2D g2, CompositeController cc) {

		this.cc = cc;

        if (ui != null) {
            Dimension d = ui.getResponseSize();
            ui.getGraphics().clearRect(0, 0, (int) d.getWidth(), (int) d.getHeight());
        }
		 
		CompositeElement ce;
		Polygon p;
		
		int topLeftY = (int) (d.getHeight() / 2) - 50;
		int topLeftX = (int) (d.getWidth() / 2) - 50;
		 
		p = new Polygon();
		p.addPoint(0, 0);
		p.addPoint(0, d.height);
		p.addPoint(d.width, d.height);
		p.addPoint(d.width, 0);
		
		g2.setPaint(Color.BLACK);
		g2.fill(p);
		g2.setPaint(Color.BLACK);
		g2.draw(p);
		 
		p = new Polygon();
		p.addPoint(topLeftX - 5, topLeftY - 5);
		p.addPoint(topLeftX - 5, topLeftY + 105);
		p.addPoint(topLeftX + 105, topLeftY + 105);
		p.addPoint(topLeftX + 105, topLeftY - 5);
		
		g2.setPaint(Color.WHITE);
		g2.fill(p);
		g2.setPaint(Color.WHITE);
		g2.draw(p);
		
		p = new Polygon();
		p.addPoint(topLeftX, topLeftY);
		p.addPoint(topLeftX + 98, topLeftY);
		p.addPoint(topLeftX, topLeftY + 98);
		
		ce = new BaseCompositeElement();
		ce.init(this);
		ce.setUserInterface(ui);
		ce.setShape(p);
		ce.setBackgroundColor(Color.BLUE);
		ce.setOutlineColor(Color.BLACK);
		
		//g2.clearRect(topLeftX, topLeftY, topLeftX + 100, topLeftY + 100);
		g2.setPaint(ce.getBackgroundColor());
		g2.fill(ce.getShape());
		g2.setPaint(ce.getOutlineColor());
		g2.draw(ce.getShape());
		
		addCompositeElement(ce);
	    
	}
	
    public List getActions(int x, int y) {
    	activated = true;
    	cc.setCompositeDuration(1);
    	return super.getActions(x, y);
    }
    
    public void destroy() {
    	if (!activated) {
    		Opto22Hopper.getInstance().activateHopper(4000);
    	}
    	super.destroy();
    }

}