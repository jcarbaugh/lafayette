package edu.american.huntsberry.composite;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.image.ImageObserver;
import java.util.Random;

import edu.american.weiss.lafayette.chamber.UserInterface;
import edu.american.weiss.lafayette.composite.BaseComposite;
import edu.american.weiss.lafayette.composite.CompositeController;

public class MTSComposite extends BaseComposite implements ImageObserver {
	
	private Image img;
	private Random rand = new Random();
	
	public MTSComposite(UserInterface ui, Image img) {
		super(ui);
		this.d = ui.getResponseSize();
		this.img = img;
	}

	@Override
	public void init(Graphics2D g2, CompositeController cc) {
		
        g2.clearRect(0, 0, (int) d.getWidth(), (int) d.getHeight());
        
		Polygon p = new Polygon();
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
		
		drawImage(g2, 0, 0, true);
		
	}
	
	private void drawImage(Graphics2D g2, int xOffset, int yOffset, boolean isCorrect) {
		
		int height = img.getHeight(this);
		int width = img.getWidth(this);
		int x = xOffset + ((d.width - width) / 2);
		int y = yOffset + ((d.height - height) / 2);
		
//		Polygon p = new Polygon();
//		p.addPoint(x, y);
//		p.addPoint(x + width, y);
//		p.addPoint(x + width, y + height);
//		p.addPoint(x, y + height);
		
//		CompositeElement ce = new ObjectDiscriminationElement(isCorrect);
//		ce.init(this);
//		ce.setUserInterface(ui);
//		ce.setShape(p);
//		ce.setBackgroundColor(Color.BLACK);
//		ce.setOutlineColor(Color.BLACK);
		
//		if (isCorrect) {
//			ce.setGroupName("correct");
//			ce.addCompositeAction(new ODAction(true));
//			ce.addCompositeAction(
//				new AudioAction("od.correct"));
//			ce.addCompositeAction(
//				new HopperAction(
//					Application.getIntProperty("reinforcement_duration")));
//		} else {
//			ce.setGroupName("incorrect");
//			ce.addCompositeAction(new ODAction(false));
//			ce.addCompositeAction(
//				new AudioAction("od.incorrect"));
//		}
		
//		ce.addCompositeAction(new RestAction());
//
//		addCompositeElement(ce);
		
		g2.drawImage(img, 0, 0, this);
	
	}

	@Override
	public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
		return infoflags != ImageObserver.ALLBITS;
	}

}
