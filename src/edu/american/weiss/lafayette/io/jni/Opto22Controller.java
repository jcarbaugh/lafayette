package edu.american.weiss.lafayette.io.jni;

import java.net.URL;
import edu.american.weiss.lafayette.Application;

public class Opto22Controller {
    
    static {
    	URL dllPath = Application.class.getClassLoader().getResource("pci-ac5.dll");
		System.load(dllPath.getFile().substring(1));
	}
	
	private native long nativeAc5Open(long baseIo);
	private native long nativePciac5Open(long boardId);
	private native void nativeClose(long handle);
	private native void nativeCloseAllHandles();
	private native void nativeDirectPointConfig(long handle, long point, long state);
	private native void nativeDirectPackedConfig(long handle, long bus, long mask);
	private native void nativeDirectUnpackedConfig(long handle, long bus, long[] list);
	private native void nativeDirectPackedMOMO(long handle, long bus, long on_mask, long off_mask);
	private native void nativeDirectUnpackedMOMO(long handle, long bus, long[] on_list, long[] off_list);
	private native void nativeDirectPointUpdate(long handle, long point, long state);
	private native void nativeDirectPackedUpdate(long handle, long bus, long mask);
	private native void nativeDirectUnpackedUpdate(long handle, long bus, long[] list);
	private native long nativeDirectPointRead(long handle, long bus);
	private native long nativeDirectPackedRead(long handle, long bus);
	private native long[] nativeDirectUnpackedRead(long handle, long bus);
	private native void nativedirectLightshow(long handle, long bus, long mask);
	private native void nativeenableDebug();
	private native void nativedisableDebug();
	private native long nativepciac5Qty();
	private native long nativetestDriver();
	private native long nativegetLastError();
	
	private long handle;
	
	private static Opto22Controller ocInstance = null;
	
	private Opto22Controller() throws Exception {
		handle = nativePciac5Open(0);
		if (handle == -1) {
			throw new Exception("Unable to locate PCI-AC5 controller card");
		} else {
			nativeDirectPointConfig(handle, 0, 1);
		}
	}
	
	public static synchronized Opto22Controller getInstance() throws Exception {
	    if (ocInstance == null) {
	        ocInstance = new Opto22Controller();
	    }
	    return ocInstance;
	}
	
	public void activateHopper() {
		nativeDirectPointUpdate(handle, 0, 1);
	}
	
	public void deactivateHopper() {
		nativeDirectPointUpdate(handle, 0, 0);
	}
	
	public void closeAllHandles() { 
	    nativeCloseAllHandles();
	}
	
}