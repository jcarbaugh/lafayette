package edu.american.weiss.lafayette.experiment;

import java.util.Collection;

import edu.american.weiss.lafayette.composite.Composite;
import edu.american.weiss.lafayette.event.ChamberEvent;
import edu.american.weiss.lafayette.event.listener.ChamberEventListener;

/**
 * @author jeremy
 */
public interface Experiment extends ChamberEventListener {
    
    public Composite getInitialComposite();
    public Composite getFinalComposite();
    public Composite getNextComposite();
    public Composite getRestComposite();
    public long getCorrectionDuration();
    public long getReinforcementDuration();
    public long getRestDuration();
    public boolean isCorrecting();
    public boolean isPseudoRandom();
    public boolean isReinforcementWaiting();
    public int getRestProbability();
    
    public Collection getEventListeners();
    
    public void handleChamberEvent(ChamberEvent ce);

}