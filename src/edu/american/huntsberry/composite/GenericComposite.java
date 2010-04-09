package edu.american.huntsberry.composite;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Iterator;

import edu.american.weiss.lafayette.Application;
import edu.american.weiss.lafayette.chamber.UserInterface;
import edu.american.weiss.lafayette.chamber.UserInterfaceFactory;
import edu.american.weiss.lafayette.composite.BaseComposite;
import edu.american.weiss.lafayette.composite.CompositeController;
import edu.american.weiss.lafayette.composite.CompositeElement;

public class GenericComposite extends BaseComposite {
    
    private UserInterface ui;
    
    public GenericComposite() {
    	super(UserInterfaceFactory.getUserInterfaceInstance());
    	ui = UserInterfaceFactory.getUserInterfaceInstance();
    	compositeElements.clear();
    }
	
	public GenericComposite(UserInterface ui, Dimension d) {
		super(ui);
		this.d = d;
		this.ui = ui;
	}

	public void init(Graphics2D g2, CompositeController cc) {

		CompositeElement ce;
		Iterator it = getCompositeElements().iterator();
		
		while (it.hasNext()) {
			
			ce = (CompositeElement) it.next();
			
			if (ce.getBackgroundColor() != null) {
				g2d.setPaint(ce.getBackgroundColor());
				g2d.fill(ce.getShape());
				g2d.setPaint(ce.getOutlineColor());
				g2d.draw(ce.getShape());
			} else {
				if (Application.getBooleanProperty("show_active_area")) {
					g2d.setPaint(Color.CYAN);
					g2d.draw(ce.getShape());
				}
			}
			
		}
		
		g2.drawImage(bi, null, null);
		
		super.init();

	}

}