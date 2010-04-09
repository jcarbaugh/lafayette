package edu.american.huntsberry.composite;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Polygon;

import edu.american.weiss.lafayette.actions.HopperAction;
import edu.american.weiss.lafayette.chamber.UserInterface;
import edu.american.weiss.lafayette.composite.BaseComposite;
import edu.american.weiss.lafayette.composite.Composite;
import edu.american.weiss.lafayette.composite.BaseCompositeElement;
import edu.american.weiss.lafayette.composite.CompositeController;
import edu.american.weiss.lafayette.composite.CompositeElement;

public class RedTRComposite extends BaseComposite {
	
    private Dimension d;
    private UserInterface ui;
	
	public RedTRComposite(UserInterface ui, Dimension d) {
		
		super(ui);
		this.d = d;
		this.ui = ui;
		
	}
	
	public void init(Graphics2D g2, CompositeController cc) {

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
		ce.addCompositeAction(new HopperAction(4000));
		
		//g2.clearRect(topLeftX, topLeftY, topLeftX + 100, topLeftY + 100);
		g2.setPaint(ce.getBackgroundColor());
		g2.fill(ce.getShape());
		g2.setPaint(ce.getOutlineColor());
		g2.draw(ce.getShape());
		
		addCompositeElement(ce);
	    
	}
}
