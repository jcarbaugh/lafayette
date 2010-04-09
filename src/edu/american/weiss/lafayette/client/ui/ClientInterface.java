package edu.american.weiss.lafayette.client.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

public class ClientInterface extends JFrame {

	private JPanel pnlMain;
	private JPanel pnlControl;
	private JPanel pnlControlConnection;
	private JPanel pnlCumulativeRecorder;
	
	public static void main(String[] args) {
		ClientInterface ci = new ClientInterface();
		ci.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		try {
			ci.setCumulativeRecorderData(ImageIO.read(new File("c:\\1125788029765.png")));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public ClientInterface() {
		
		pnlMain = new JPanel(new BorderLayout());
		
		pnlCumulativeRecorder = new JPanel();
		
		pnlControl = new JPanel(new BorderLayout());
		
		pnlControlConnection = new JPanel(new GridLayout(3, 2));
		pnlControlConnection.add(new JLabel("IP Address  ", JLabel.RIGHT));
		pnlControlConnection.add(new JTextField(8));
		pnlControlConnection.add(new JLabel("Port  ", JLabel.RIGHT));
		pnlControlConnection.add(new JTextField(8));
		pnlControlConnection.add(new JPanel());
		pnlControlConnection.add(new JButton("Connect"));
		
		pnlControl.add(pnlControlConnection, BorderLayout.WEST);
		
		pnlMain.add(new JScrollPane(pnlCumulativeRecorder), BorderLayout.CENTER);
		pnlMain.add(pnlControl, BorderLayout.SOUTH);
		
		this.setPreferredSize(new Dimension(300, 630));
		this.setContentPane(pnlMain);
		
		this.pack();
		this.setVisible(true);
		
	}
	
	public void setCumulativeRecorderData(BufferedImage bi) {
		
		pnlCumulativeRecorder.setSize(bi.getWidth(), bi.getHeight());
		
		Graphics2D g2d = (Graphics2D) pnlCumulativeRecorder.getGraphics();
		g2d.drawRenderedImage(bi, new AffineTransform());
		
		pnlCumulativeRecorder.revalidate();
		
	}
	
}