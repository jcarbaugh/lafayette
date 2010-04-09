package edu.american.huntsberry.test;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.util.List;
import java.util.Random;

import edu.american.weiss.lafayette.chamber.Chamber;
import edu.american.weiss.lafayette.chamber.UserInterface;
import edu.american.weiss.lafayette.composite.BaseComposite;
import edu.american.weiss.lafayette.composite.BaseCompositeElement;
import edu.american.weiss.lafayette.composite.CompositeController;
import edu.american.weiss.lafayette.composite.CompositeElement;
import edu.american.weiss.lafayette.experiment.Experiment;

public class AutoShapingComposite extends BaseComposite implements Runnable {

	private Dimension d;
    private UserInterface ui;
    private Experiment exp;
    private Thread t;
    
    private boolean isRunning;
	
	public AutoShapingComposite(UserInterface ui, Dimension d) {
		super(ui);
		this.d = d;
		this.ui = ui;
	}
	
	public List getActions(int x, int y) {
		if (t != null) {
			t.interrupt();
		}
		return super.getActions(x, y);
	}
	
	public void init(Graphics2D g2, CompositeController cc) { 
	    
        if (ui != null) {
            Dimension d = ui.getResponseSize();
            ui.getGraphics().clearRect(0, 0, (int) d.getWidth(), (int) d.getHeight());            
        }
        
		CompositeElement ce;
		Polygon p;
		 
		p = new Polygon();
		p.addPoint(5, 5);
		p.addPoint(5, d.height - 5);
		p.addPoint(d.width - 5, d.height - 5);
		p.addPoint(d.width - 5, 5);

		ce = new BaseCompositeElement();
		ce.init(this);
		ce.setUserInterface(ui);
		ce.setShape(p);
		ce.setBackgroundColor(Color.DARK_GRAY);
		ce.setOutlineColor(Color.DARK_GRAY);
		
		g2.setPaint(ce.getBackgroundColor());
		g2.fill(ce.getShape());
		g2.setPaint(ce.getOutlineColor());
		g2.draw(ce.getShape());
	    
		exp = cc.getExperiment();
		
	}
	
	public void run() {
		
		Random rand = new Random();
		long waitDuration;
		
		while (isRunning) {
			
			waitDuration = rand.nextInt(30000) + 30000;
			
			try {
				Thread.sleep(waitDuration);
				Chamber.getHopper().activateHopper(exp.getReinforcementDuration());
			} catch (InterruptedException ie) {
			} catch (Exception e) { }
						
		}
		
	}
	
	public void start() {
		
		try {
			
			isRunning = true;
			
			t = new Thread(this);
			t.start();
			
			super.start();
			
		} catch (Exception e) { }
		
	}
	
	public void destroy() {
		
		try {
			
			isRunning = false;
			
			super.destroy();
			
		} catch (Exception e) { }
		
	}

}