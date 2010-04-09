package edu.american.huntsberry.composite;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.List;

import edu.american.weiss.lafayette.Application;
import edu.american.weiss.lafayette.actions.HopperAction;
import edu.american.weiss.lafayette.chamber.UserInterface;
import edu.american.weiss.lafayette.composite.BaseComposite;
import edu.american.weiss.lafayette.composite.CompositeController;
import edu.american.weiss.lafayette.event.ReinforcerEvent;

public abstract class AutoComposite extends BaseComposite {

	protected CompositeController cc;
	protected Dimension d;
	protected UserInterface ui;
	protected boolean activated;

	protected Rectangle boundary;
	protected HopperAction ha;
    
	private int topLeftX;
	private int topLeftY;

	public AutoComposite(UserInterface ui, Dimension d) {
		super(ui);
		this.ui = ui;
		this.d = d;
		this.activated = false;
	    
	    topLeftX = (int) (ui.getResponseSize().getWidth() / 2) - 50;
	    topLeftY = (int) (ui.getResponseSize().getHeight() / 2) - 50;
	    
	    boundary = new Rectangle(topLeftX, topLeftY, 100, 100);
	    
	}
	
	public void init(Graphics2D g2, CompositeController cc) {

		this.cc = cc;

        if (ui != null) {
            Dimension d = ui.getResponseSize();
            ui.getGraphics().clearRect(0, 0, (int) d.getWidth(), (int) d.getHeight());
        }
        
        initialize();

	}
	
	public abstract void initialize();
	
    public List getActions(int x, int y) {
    	activated = true;
    	cc.setCompositeDuration(1);
    	return super.getActions(x, y);
    }
    
    public void destroy() {
    	if (!activated) {
    		Application.getEventController().notifyListeners(new ReinforcerEvent(this, 4000));
    	}
    	super.destroy();
    }

}