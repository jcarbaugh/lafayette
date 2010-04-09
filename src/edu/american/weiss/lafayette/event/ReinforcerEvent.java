package edu.american.weiss.lafayette.event;

import edu.american.weiss.lafayette.composite.Composite;
import edu.american.weiss.lafayette.composite.CompositeElement;

public class ReinforcerEvent extends BaseChamberEventImpl {
	
	private long duration;
	private CompositeElement compositeElement;
	
	public ReinforcerEvent(Composite activeComposite, CompositeElement compositeElement, long duration) {
		super(activeComposite);
		this.duration = duration;
		this.compositeElement = compositeElement;
	}
	
	public ReinforcerEvent(Composite activeComposite, long duration) {
		super(activeComposite);
		this.duration = duration;
	}
	
	public long getDuration() {
		return duration;
	}
	
	public CompositeElement getCompositeElement() {
		return compositeElement;
	}
	
}