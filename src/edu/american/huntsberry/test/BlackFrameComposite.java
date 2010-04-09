package edu.american.huntsberry.test;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Polygon;

import edu.american.weiss.lafayette.chamber.UserInterface;
import edu.american.weiss.lafayette.composite.BaseComposite;
import edu.american.weiss.lafayette.composite.BaseCompositeElement;
import edu.american.weiss.lafayette.composite.CompositeController;
import edu.american.weiss.lafayette.composite.CompositeElement;

public class BlackFrameComposite extends BaseComposite {
    
    private Dimension d;
    private UserInterface ui;
	
	public BlackFrameComposite(UserInterface ui, Dimension d) {
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
	    
	}

}