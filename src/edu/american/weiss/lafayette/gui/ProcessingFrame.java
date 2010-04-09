package edu.american.weiss.lafayette.gui;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;

public class ProcessingFrame extends JFrame {

	public ProcessingFrame() {
		
		JLabel lbl = new JLabel("Processing, please wait...");
		
		JPanel pnlMain = new JPanel(new BorderLayout());
		pnlMain.add(lbl, BorderLayout.CENTER);
		pnlMain.setSize(500, 300);
		
		setSize(500, 300);
		setLocationRelativeTo(null);
		
		setContentPane(pnlMain);
		pack();

	}
	
}