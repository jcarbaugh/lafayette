package edu.american.weiss.lafayette.composite;

public interface CompositeAction extends Runnable {
    
    public boolean runAsThread();
    public CompositeElement getCompositeElement();
	public void setCompositeElement(CompositeElement currentComposite);

}