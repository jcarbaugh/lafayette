package edu.american.huntsberry.composite;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;

import edu.american.weiss.lafayette.ImageManager;
import edu.american.weiss.lafayette.actions.NextCompositeAction;
import edu.american.weiss.lafayette.actions.RestAction;
import edu.american.weiss.lafayette.chamber.UserInterface;
import edu.american.weiss.lafayette.composite.BaseComposite;
import edu.american.weiss.lafayette.composite.CompositeAction;
import edu.american.weiss.lafayette.composite.CompositeController;
import edu.american.weiss.lafayette.composite.CompositeElement;

public class StartComposite extends BlackComposite {
	
	private final String MESSAGE = "CLICK TO START";
	
	public StartComposite(UserInterface ui, Dimension d) {
		super(ui, d);
	}

	public void init(Graphics2D g2, CompositeController cc) {
		
		super.init(g2, cc);
		
		for (CompositeElement ce : getCompositeElements()) {
			ce.addCompositeAction(new RestAction());
		}
		
		int fontSize = (int) Math.round(32.0 * ui.getScreenResolution() / 72.0);
		Font f = new Font("Arial", Font.PLAIN, fontSize);
		FontMetrics fm = g2.getFontMetrics(f);
		
		g2.setFont(f);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		g2.setColor(Color.WHITE);
		g2.drawString(MESSAGE,
			(d.width - fm.stringWidth(MESSAGE)) / 2,
			(d.height - 50) / 2);
		
		for (Image img : ImageManager.getInstance().getImages()) {
			g2.drawImage(img, -10000, -10000, null);
		}

	}

}
