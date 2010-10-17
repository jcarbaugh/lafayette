package edu.american.huntsberry.composite;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.image.ImageObserver;
import java.util.List;
import java.util.Random;

import edu.american.huntsberry.compositeelement.ObjectDiscriminationElement;
import edu.american.weiss.lafayette.Application;
import edu.american.weiss.lafayette.actions.AudioAction;
import edu.american.weiss.lafayette.actions.HopperAction;
import edu.american.weiss.lafayette.chamber.UserInterface;
import edu.american.weiss.lafayette.composite.BaseComposite;
import edu.american.weiss.lafayette.composite.CompositeAction;
import edu.american.weiss.lafayette.composite.CompositeController;
import edu.american.weiss.lafayette.composite.CompositeElement;

public class ObjectDiscriminationComposite extends BaseComposite implements ImageObserver {
    
	private CompositeController cc;
    private Dimension d;
    private Image correctImage;
    private Image incorrectImage;
    private Random rand = new Random();
    private UserInterface ui;
    
    private String status = "refused";
    
    public ObjectDiscriminationComposite(UserInterface ui, Dimension d, Image cImg, Image iImg) {
		super(ui);
		this.d = d;
		this.ui = ui;
		this.correctImage = cImg;
		this.incorrectImage = iImg;
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
		
		if (rand.nextInt(2) == 0) {
			qWidth = qWidth * -1;
		}
		
		drawImage(g2, correctImage, -qWidth, 0, true);
		drawImage(g2, incorrectImage, qWidth, 0, false);

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
		
		CompositeElement ce = new ObjectDiscriminationElement(isCorrect);
		ce.init(this);
		ce.setUserInterface(ui);
		ce.setShape(p);
		ce.setBackgroundColor(Color.BLACK);
		ce.setOutlineColor(Color.BLACK);
		if (isCorrect) {
			ce.setGroupName("correct");
			ce.addCompositeAction(
				new AudioAction(
					Application.getProperty("correct_response_wav")));
			ce.addCompositeAction(
				new HopperAction(
					Application.getIntProperty("reinforcement_duration")));
		} else {
			ce.setGroupName("incorrect");
			ce.addCompositeAction(
				new AudioAction(
					Application.getProperty("incorrect_response_wav")));
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
	
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String s) {
		this.status = s;
	}

}
