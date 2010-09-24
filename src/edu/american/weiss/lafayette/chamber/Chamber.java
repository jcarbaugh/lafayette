package edu.american.weiss.lafayette.chamber;

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
            hopper = (Hopper) cls.newInstance();
        } catch (Exception e) {
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