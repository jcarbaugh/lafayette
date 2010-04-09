package edu.american.weiss.lafayette.chamber;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import edu.american.weiss.lafayette.Application;
import edu.american.weiss.lafayette.composite.CompositeController;
import edu.american.weiss.lafayette.event.ChamberEvent;
import edu.american.weiss.lafayette.event.ReinforcerEvent;
import edu.american.weiss.lafayette.event.ResponseEvent;

public class UserInterfaceListener implements KeyListener, MouseListener {
    
    public void mouseClicked(MouseEvent e) {
    	/*
        Chamber.getUserInterface().writeMessage("mouseEvent (" + e.getX() + ", " + e.getY() + ")");
        if (currentComposite != null) {
        	responseCount++;
        	ChamberEvent ce = new ResponseEvent(currentComposite, e.getX(), e.getY());
        	Application.getController().notifyListeners(ce);
        }
        */
    }

    public void mouseEntered(MouseEvent e) { }
    public void mouseExited(MouseEvent e) { }
    public void mousePressed(MouseEvent e) { }
    public void mouseReleased(MouseEvent e) { }

    public void keyPressed(KeyEvent arg0) {
        
    	/*
        switch (arg0.getKeyCode()) {
        
			case 27:	// escape
			    setActive(false);
			    break;
			    
			case 3:		// break
				Application.shutdown();
			    break;
			    
			case 32:	// spacebar
			    try {
				    Chamber.getUserInterface().writeMessage("activate hopper");
			    } catch (Exception e) { }
			    ChamberEvent ce = new ReinforcerEvent(currentComposite, exp.getReinforcementDuration());
			    Application.getController().notifyListeners(ce);
			    break;
			    
			default:
			    Chamber.getUserInterface().writeMessage("keyCode: " + arg0.getKeyCode());
			    break;
			    
        }
        */
        
    }
    
    public void keyReleased(KeyEvent arg0) { }
    public void keyTyped(KeyEvent arg0) { }

}