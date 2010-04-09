package edu.american.huntsberry.composite;

import java.awt.Dimension;
import java.awt.Graphics2D;

import edu.american.weiss.lafayette.chamber.UserInterface;
import edu.american.weiss.lafayette.composite.BaseComposite;
import edu.american.weiss.lafayette.composite.CompositeController;


public class EmptyComposite extends BaseComposite {
    
    private Dimension d;
    private UserInterface ui;
	
	public EmptyComposite(UserInterface ui, Dimension d) {
		super(ui);
		this.d = d;
		this.ui = ui;
	}
	
	public void init(Graphics2D g2, CompositeController cc) { 
	    
        if (ui != null) {
            Dimension d = ui.getResponseSize();
            ui.getGraphics().clearRect(0, 0, (int) d.getWidth(), (int) d.getHeight());            
        }
	    
	}

}