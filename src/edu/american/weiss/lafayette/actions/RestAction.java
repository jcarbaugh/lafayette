package edu.american.weiss.lafayette.actions;

import edu.american.weiss.lafayette.Application;
import edu.american.weiss.lafayette.composite.BaseCompositeAction;
import edu.american.weiss.lafayette.composite.CompositeController;

public class RestAction extends BaseCompositeAction {

	private CompositeController cc;
	
	public boolean runAsThread() {
		return false;
	}

	public void run() {
		if (cc == null) {
			cc = Application.getCompositeController();
		}
		cc.forceRest();
		cc.setCompositeDuration(-1);
	}

}