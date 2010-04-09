package edu.american.weiss.lafayette.composite;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Random;

import edu.american.weiss.lafayette.Application;
import edu.american.weiss.lafayette.chamber.Chamber;
import edu.american.weiss.lafayette.chamber.Opto22Hopper;
import edu.american.weiss.lafayette.event.ChamberEvent;
import edu.american.weiss.lafayette.event.CompositeTransitionEvent;
import edu.american.weiss.lafayette.event.ReinforcerEvent;
import edu.american.weiss.lafayette.event.ResponseEvent;
import edu.american.weiss.lafayette.experiment.Experiment;

/**
 * @author jeremy
 */
public class CompositeController implements KeyListener, MouseListener, Runnable {
        
    protected boolean isActive;
    protected long compositeDuration;
    protected long compositeStartTime;
    protected long responseCount;
    protected Composite currentComposite;
    protected Experiment exp;
    private Random rand;
    private boolean isForcedRest;
    
    public CompositeController(Experiment exp) {
        this.exp = exp;
        isActive = true;
        Chamber.getUserInterface().registerMouseListener(this);
        Chamber.getUserInterface().registerKeyListener(this);
        rand = new Random();
    }
    
    protected void displayComposite(Composite comp) {
        
        if (currentComposite != null) {
            currentComposite.destroy();
        }

        compositeStartTime = System.currentTimeMillis();
        compositeDuration = comp.getDuration();
        comp.init(Chamber.getUserInterface().getResponseGraphics(), this);
        comp.start();
    	
    	ChamberEvent ce = new CompositeTransitionEvent(currentComposite, comp);
    	Application.getEventController().notifyListeners(ce);
        
        currentComposite = comp;
        
        Chamber.getUserInterface().writeMessage("");
        
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }
    
    public Experiment getExperiment() {
    	return exp;
    }
    
    public void destroy() {
    	setActive(false);
    	if (currentComposite != null) {
    		if (isActive) {
        		ChamberEvent ce = new CompositeTransitionEvent(currentComposite, null);
        		Application.getEventController().notifyListeners(ce);
    		}
    		currentComposite.destroy();
    	}
    }
    
    public void run() {
        
        long now;
        Composite comp;
        
        Opto22Hopper hopper = null;
        
        try {
        	hopper = Chamber.getHopper();
        } catch (Exception e) { }
        
        comp = exp.getInitialComposite();
        
        compositeDuration = comp.getDuration();
        compositeStartTime = System.currentTimeMillis();
        
        displayComposite(comp);
        
        while (isActive) {
        	
        	now = System.currentTimeMillis();
        	
        	if (compositeDuration != 0 &&
        			compositeDuration < (now - compositeStartTime)) {
        		
        		if (hopper != null && hopper.isActive() &&
        				exp.isReinforcementWaiting()) {
        			
        			compositeDuration += 50;
        			
        		} else if (comp.getType() == Composite.REST_COMPOSITE &&
        				exp.isCorrecting() && responseCount > 0) {
        			
        			compositeDuration += exp.getCorrectionDuration();
        			
        			responseCount = 0;
        			
        		} else {
        			
        			if (comp.getType() == Composite.INITIAL_COMPOSITE ||
        					comp.getType() == Composite.REST_COMPOSITE) {
        				
        				comp = exp.getNextComposite();
        				
        			} else {
        				
        				if (isForcedRest) {
    						comp = exp.getRestComposite();
    						isForcedRest = false;
        				} else {
        					int prob = rand.nextInt(100);
        					if (prob < exp.getRestProbability()) {
        						comp = exp.getRestComposite();
        					} else {
        						comp = exp.getNextComposite();
        					}
        				}
        				
        			}
        			
        			if (comp == null) {
        				
        				isActive = false;
        				
        			} else {
            			
            			responseCount = 0;
        				displayComposite(comp);
        				
        			}
        			
        		}
        		
        	}
        	
        }
        
        comp = exp.getFinalComposite();
        
        displayComposite(comp);
        
        isActive = false;
        
    }
    
    public Composite getCurrentComposite() {
    	return currentComposite;
    }
    
    public void setCompositeDuration(long duration) {
    	compositeDuration = duration;
    }
    
    public void forceRest() {
    	isForcedRest = true;
    }
    
    public void mouseClicked(MouseEvent e) { }
    public void mouseEntered(MouseEvent e) { }
    public void mouseExited(MouseEvent e) { }
    public void mousePressed(MouseEvent e) {
        Chamber.getUserInterface().writeMessage("mouseEvent (" + e.getX() + ", " + e.getY() + ")");
        if (currentComposite != null) {
        	responseCount++;
        	ChamberEvent ce = new ResponseEvent(currentComposite, e.getX(), e.getY());
        	Application.getEventController().notifyListeners(ce);
        }
    }
    public void mouseReleased(MouseEvent e) { }

    public void keyPressed(KeyEvent arg0) {
        
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
			    Application.getEventController().notifyListeners(ce);
			    break;
			    
			default:
			    Chamber.getUserInterface().writeMessage("keyCode: " + arg0.getKeyCode());
			    break;
			    
        }
        
    }
    
    public void keyReleased(KeyEvent arg0) { }
    public void keyTyped(KeyEvent arg0) { }
}