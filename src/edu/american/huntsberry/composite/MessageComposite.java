package edu.american.huntsberry.composite;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import edu.american.weiss.lafayette.chamber.UserInterface;
import edu.american.weiss.lafayette.composite.CompositeController;

public class MessageComposite extends ColorComposite {

	private Color fontColor;
	private String message;

	public MessageComposite(UserInterface ui, Dimension d, Color bgColor, Color fontColor, String message) {
		super(ui, d, bgColor);
		this.fontColor = fontColor;
		this.message = message;
	}
	
	public void init(Graphics2D g2, CompositeController cc) {
		
		super.init(g2, cc);
		
		int fontSize = (int) Math.round(32.0 * ui.getScreenResolution() / 72.0);
		Font f = new Font("Arial", Font.PLAIN, fontSize);
		FontMetrics fm = g2.getFontMetrics(f);
		
		g2.setFont(f);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		g2.setColor(fontColor);
		g2.drawString(message,
			(d.width - fm.stringWidth(message)) / 2,
			(d.height - 50) / 2);
		
	}
	
}
