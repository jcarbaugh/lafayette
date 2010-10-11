package edu.american.huntsberry.compositeelement;

import edu.american.weiss.lafayette.composite.BaseCompositeElement;

public class ObjectDiscriminationElement extends BaseCompositeElement {
	
	private boolean isCorrect;
	
	public ObjectDiscriminationElement(boolean isCorrect) {
		super();
		this.isCorrect = isCorrect;
	}
	
	public boolean isCorrect() {
		return isCorrect;
	}
	
	public void setCorrect(boolean isCorrect) {
		this.isCorrect = isCorrect;
	}
	
}
