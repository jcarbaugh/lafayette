package edu.american.huntsberry.test;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Polygon;

import edu.american.weiss.lafayette.actions.HopperAction;
import edu.american.weiss.lafayette.chamber.UserInterface;
import edu.american.weiss.lafayette.composite.BaseComposite;
import edu.american.weiss.lafayette.composite.BaseCompositeElement;
import edu.american.weiss.lafayette.composite.CompositeController;
import edu.american.weiss.lafayette.composite.CompositeElement;

/**
 * @author jeremy
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
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
		p.addPoint(topLeftX + 100, topLeftY + 2);
		p.addPoint(topLeftX + 100, topLeftY + 100);
		p.addPoint(topLeftX + 2, topLeftY + 100);

		ce = new BaseCompositeElement();
		ce.init(this);
		ce.setUserInterface(ui);
		ce.setShape(p);
		ce.setBackgroundColor(Color.RED);
		ce.setOutlineColor(Color.BLACK);
		ce.addCompositeAction(new HopperAction(4000));

		g2.setPaint(ce.getBackgroundColor());
		g2.fill(ce.getShape());
		g2.setPaint(ce.getOutlineColor());
		g2.draw(ce.getShape());
		
		addCompositeElement(ce);
	}

}