package edu.american.weiss.lafayette.composite;

public abstract class BaseCompositeAction implements CompositeAction {

	protected Composite composite;
	protected CompositeElement compositeElement;

	public CompositeElement getCompositeElement() {
		return compositeElement;
	}

	public void setCompositeElement(CompositeElement compositeElement) {
		this.compositeElement = compositeElement;
	}

	public Composite getComposite() {
		return composite;
	}

	public void setComposite(Composite composite) {
		this.composite = composite;
	}

}