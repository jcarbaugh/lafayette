package edu.american.weiss.lafayette.chamber;

import java.lang.reflect.Method;

import edu.american.weiss.lafayette.Application;

/**
 * @author jeremy
 */
public class Chamber {

    private static Chamber chamber = new Chamber();
    
    private UserInterface ui;
    private Hopper hopper;
    
    private Chamber() {
        ui = UserInterfaceFactory.getUserInterfaceInstance();
        try {
            Class cls = Class.forName(
            Application.getProperty(
            	"hopper_class",
            	"edu.american.weiss.lafayette.chamber.MockHopper"));
            Method mthd = cls.getMethod("getInstance");
            this.hopper = (Hopper) mthd.invoke(null);
        } catch (Exception e) {
        	System.out.println("!!!!!" + e);
        	// couldn't create hopper. oh well?
        }
    }
    
    public static UserInterface getUserInterface() {
        return chamber.ui;
    }
    
    public static Hopper getHopper() {
        return chamber.hopper;
    }
    
}