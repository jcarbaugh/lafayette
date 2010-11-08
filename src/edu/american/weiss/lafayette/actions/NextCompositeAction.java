package edu.american.weiss.lafayette.actions;

import edu.american.weiss.lafayette.Application;
import edu.american.weiss.lafayette.composite.BaseCompositeAction;
import edu.american.weiss.lafayette.composite.CompositeController;

public class NextCompositeAction extends BaseCompositeAction {

private CompositeController cc;
	
	public NextCompositeAction() {
		super();
		cc = Application.getCompositeController();
	}
	
	public boolean runAsThread() {
		return false;
	}

	public void run() {
		cc.forceChange();
	}
	
}
