package edu.american.weiss.lafayette.composite;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;

public class CompositeRegistry {

	private static CompositeRegistry cr;
	private static Map registry;
	
	static {
		cr = new CompositeRegistry();
	}
	
	private CompositeRegistry() {
		registry = new Hashtable(500);
	}
	
	private static void addComponent(Composite c) {
		registry.put(c.getId(), c);		
	}
	
	private static Composite getComponent(String id) {
		
		Composite c = null;
		Object o = registry.get(id);
		
		if (o != null && o instanceof Composite) {
			c = (Composite) o;
		}
		
		return c;		
		
	}
	
	private static Collection getComponents() {
		return registry.values();
	}
	
}