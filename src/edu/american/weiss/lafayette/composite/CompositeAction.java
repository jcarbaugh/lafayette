package edu.american.weiss.lafayette.composite;

public interface CompositeAction extends Runnable {
    
    public boolean runAsThread();
    public Composite getComposite();
	public void setComposite(Composite composite);
    public CompositeElement getCompositeElement();
	public void setCompositeElement(CompositeElement currentComposite);

}