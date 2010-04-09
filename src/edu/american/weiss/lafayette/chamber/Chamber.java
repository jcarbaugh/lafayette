package edu.american.weiss.lafayette.chamber;

/**
 * @author jeremy
 */
public class Chamber {

    private static Chamber chamber = new Chamber();
    
    private UserInterface ui;
    private MockHopper hopper;
    
    private Chamber() {
        ui = UserInterfaceFactory.getUserInterfaceInstance();
        hopper = MockHopper.getInstance();
    }
    
    public static UserInterface getUserInterface() {
        return chamber.ui;
    }
    
    public static MockHopper getHopper() {
        return chamber.hopper;
    }
    
}