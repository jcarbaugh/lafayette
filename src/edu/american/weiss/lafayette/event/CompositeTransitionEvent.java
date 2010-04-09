package edu.american.weiss.lafayette.event;

import edu.american.weiss.lafayette.composite.Composite;

public class CompositeTransitionEvent extends BaseChamberEventImpl {

	private Composite previousComposite;
	
	public CompositeTransitionEvent(Composite previousComposite, Composite activeComposite) {
		
		super(activeComposite);
		this.previousComposite = previousComposite;
		
	}
	
	public Composite getPreviousComposite() {
		return previousComposite;
	}

}