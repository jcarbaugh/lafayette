package edu.american.weiss.lafayette;

import java.util.Iterator;
import java.util.Properties;
import java.util.Random;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import edu.american.huntsberry.data.CompositeGroupCumulativeRecorder;
import edu.american.huntsberry.data.MasterRedBlueRecorder;
//import edu.american.huntsberry.data.TerminalBaseline2Recorder;
//import edu.american.huntsberry.data.TerminalBaseline3Recorder;
import edu.american.weiss.lafayette.chamber.Chamber;
import edu.american.weiss.lafayette.chamber.Hopper;
import edu.american.weiss.lafayette.chamber.MockHopper;
import edu.american.weiss.lafayette.chamber.Opto22Hopper;
import edu.american.weiss.lafayette.chamber.HopperListener;
import edu.american.weiss.lafayette.chamber.UserInterface;
import edu.american.weiss.lafayette.chamber.UserInterfaceFactory;
import edu.american.weiss.lafayette.composite.CompositeController;
import edu.american.weiss.lafayette.data.DataRecorderListener;
import edu.american.weiss.lafayette.data.DataRecorder;
import edu.american.weiss.lafayette.data.EventRecorderListener;
import edu.american.weiss.lafayette.data.ResponseRecorderListener;
import edu.american.weiss.lafayette.data.ResponseSummaryListener;
import edu.american.weiss.lafayette.data.ResponseSummaryListenerTB2;
import edu.american.weiss.lafayette.data.ResponseSummaryListenerTB3;
import edu.american.weiss.lafayette.event.DestroyEvent;
import edu.american.weiss.lafayette.event.listener.ChamberEventListener;
import edu.american.weiss.lafayette.experiment.Experiment;
import edu.american.weiss.lafayette.experiment.test.TestExperimentImpl;
import edu.american.weiss.lafayette.gui.ProcessingFrame;
import edu.american.weiss.lafayette.server.ControlServer;

/**
 * @author jeremy
 */
public class Application {
	
	private static CompositeController cc;
	private static Thread ccThread;
	private static UserInterface ui;
	private static EventController controller;
	
	private static Properties props;
		
	public static void main(String[] args) {

	    try {
	    	
	    	props = new Properties();
	    	loadProperties("application.properties");
	    	
	    	controller = new EventController();
	    	
//	    	try {
//				UIManager.setLookAndFeel(
//					UIManager.getCrossPlatformLookAndFeelClassName());
//			} catch (UnsupportedLookAndFeelException e) {
//				// failed to set look and feel. oh well.
//			}
		
	    	ui = UserInterfaceFactory.getUserInterfaceInstance();
	    	ui.init();
	    	controller.registerEventListener(ui);
	    
	    	if (args.length > 0) {
	    		
	    		String className = props.getProperty(args[0]);
	    		
	    		if (className == null) {
	    			className = args[0];
	    		}
	    		
	    		Class cls = Class.forName(className);
	    		loadProperties(cls.getSimpleName() + ".properties");
	    		Experiment exp = (Experiment) cls.newInstance();
	    		
	    		controller.registerEventListener(exp);
	    		
	    		Iterator it = exp.getEventListeners().iterator();
	    		while (it.hasNext()) {
	    			controller.registerEventListener(
	    					(ChamberEventListener) it.next());
	    		}
	    		
	    		cc = new CompositeController(exp);
	    		
	    	} else {
	    		cc = new CompositeController(new TestExperimentImpl());
	    	}
	    	
	    	//controller.registerEventListener(new MasterRedBlueRecorder());
	    	controller.registerEventListener(new HopperListener());
	    	controller.registerEventListener(new ResponseRecorderListener());   
	    	//controller.registerEventListener(new ResponseSummaryListener());	
	    	controller.registerEventListener(new DataRecorderListener());
	    	
	    	ccThread = new Thread(cc);
	    	ccThread.start();
	    	
	    } catch (ClassNotFoundException cnfe) {
	    	System.err.println("Unable to locate class: " + args[0]);
	    } catch (ClassCastException cce) {
	    	System.err.println("Class is not a valid experiment: " + args[0]);
	    } catch (InstantiationException ie) {
	    	System.err.println("Unable to instantiate class: " + args[0]);
	    } catch (IllegalAccessException ie) {
	    	System.err.println("Illegal access to class: " + args[0]);
	    }
		
	}
	
	public static boolean loadProperties(String propFilePath) {
		try {
			props.load(Application.class.getClassLoader()
					.getResourceAsStream(propFilePath));
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public static void shutdown() {
		
		ProcessingFrame pf = new ProcessingFrame();
		
		cc.destroy();
		ui.destroy();
		
		pf.setVisible(true);
		
		controller.notifyListeners(new DestroyEvent());
		controller.destroy();
		
		try {
			Hopper hopper = Chamber.getHopper();
			if (hopper != null) {
				hopper.destroy();
			}
		} catch (Exception e) { }
		
		DataRecorder.debug();

		pf.setVisible(false);
		
		System.exit(0);
		
	}
	
	public static CompositeController getCompositeController() {
		return cc;
	}
	
	public static String getProperty(String key, String subKey) {
		String val = props.getProperty(key + "." + subKey);
		if (val == null) {
			val = props.getProperty(key);
		}
		return val;
	}
	
	public static String getProperty(String key) {
		return props.getProperty(key);
	}
	
	public static int getIntProperty(String key) {
		return Integer.parseInt(props.getProperty(key));
	}
	
	public static boolean getBooleanProperty(String key) {
		String value = props.getProperty(key);
		boolean isTrue = false;
		if (value != null &&
				value.equalsIgnoreCase("true")) {
			isTrue = true;
		}
		return isTrue;
	}
	
	public static long getRandomLong(int min, int max) {
		if (min == max) {
			return min;
		} else {
			Random rand = new Random();
			return rand.nextInt(max - min + 1) + min;
		}
	}
	
	public static void setProperty(String key, String value) {
		props.setProperty(key, value);
	}
	
	public static EventController getEventController() {
		return controller;
	}

}