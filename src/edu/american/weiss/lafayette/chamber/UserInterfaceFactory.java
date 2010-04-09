package edu.american.weiss.lafayette.chamber;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

/**
 * @author jeremy
 */
public class UserInterfaceFactory {
    
    private static UserInterface uiInstance = null;
    
    public static UserInterface getUserInterfaceInstance() {
        
        if (uiInstance == null) {

            GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
            GraphicsDevice[] devices = env.getScreenDevices();
            
            for (int i = 0; i < 1; i++) {
                uiInstance = new UserInterface(devices[i]);
                uiInstance.initComponents();
            }
            
        }
        
        return uiInstance;
        
    }

}