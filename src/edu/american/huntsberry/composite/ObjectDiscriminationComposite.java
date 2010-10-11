package edu.american.huntsberry.composite;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.image.ImageObserver;
import java.util.List;

import edu.american.huntsberry.compositeelement.ObjectDiscriminationElement;
import edu.american.weiss.lafayette.Application;
import edu.american.weiss.lafayette.actions.HopperAction;
import edu.american.weiss.lafayette.chamber.UserInterface;
import edu.american.weiss.lafayette.composite.BaseComposite;
import edu.american.weiss.lafayette.composite.BaseCompositeElement;
import edu.american.weiss.lafayette.composite.CompositeAction;
import edu.american.weiss.lafayette.composite.CompositeController;
import edu.american.weiss.lafayette.composite.CompositeElement;

public class ObjectDiscriminationComposite extends BaseComposite implements ImageObserver {
    
	private CompositeController cc;
    private Dimension d;
    private Image lImg;
    private Image rImg;
    private UserInterface ui;
    
    public ObjectDiscriminationComposite(UserInterface ui, Dimension d, Image lImg, Image rImg) {
		super(ui);
		this.d = d;
		this.ui = ui;
		this.lImg = lImg;
		this.rImg = rImg;
	}

	public void init(Graphics2D g2, CompositeController cc) {
		
		this.cc = cc;

        if (ui != null) {
            Dimension d = ui.getResponseSize();
            ui.getGraphics().clearRect(0, 0, (int) d.getWidth(), (int) d.getHeight());            
        }

        Polygon p;
        
        p = new Polygon();
		p.addPoint(0, 0);
		p.addPoint(0, d.height);
		p.addPoint(d.width, d.height);
		p.addPoint(d.width, 0);
		
		g2.setPaint(Color.BLACK);
		g2.fill(p);
		g2.setPaint(Color.BLACK);
		g2.draw(p);
		
		int qWidth = d.width / 4;
		
		drawImage(g2, lImg, -qWidth, 0, true);
		drawImage(g2, rImg, qWidth, 0, true);

	}
	
	private void drawImage(Graphics2D g2, Image img, int xOffset, int yOffset, boolean isCorrect) {
		
		int height = img.getHeight(this);
		int width = img.getWidth(this);
		int x = xOffset + ((d.width - width) / 2);
		int y = yOffset + ((d.height - height) / 2);
		
		Polygon p = new Polygon();
		p.addPoint(x, y);
		p.addPoint(x + width, y);
		p.addPoint(x + width, y + height);
		p.addPoint(x, y + height);
		
		CompositeElement ce = new BaseCompositeElement();
		ce.init(this);
		ce.setUserInterface(ui);
		ce.setShape(p);
		ce.setBackgroundColor(Color.BLACK);
		ce.setOutlineColor(Color.BLACK);
		if (isCorrect) {
			ce.addCompositeAction(
				new HopperAction(
					Application.getIntProperty("reinforcement_duration")));
		} else {
			/// do something else
		}

		addCompositeElement(ce);
		
		g2.drawImage(img, x, y, this);
	
	}
	
	public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
		return infoflags != ImageObserver.ALLBITS;
	}
	
	public List<CompositeAction> getActions(int x, int y) {
		List<CompositeAction> actions = super.getActions(x, y);
		if (actions.size() > 0) {
			cc.setCompositeDuration(1);
		}
		ui.writeMessage("actions:" + actions.size());
    	return actions; 
    }

}
