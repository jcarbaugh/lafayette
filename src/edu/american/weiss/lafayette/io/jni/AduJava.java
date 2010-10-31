package edu.american.weiss.lafayette.io.jni;

import java.net.URL;

import edu.american.weiss.lafayette.Application;

public class AduJava {
	
	static {
    	URL dllPath = Application.class.getClassLoader().getResource("AduJava.dll");
		System.load(dllPath.getFile().substring(1));
	}

	public native int displayVersion();
	public native int OpenAduDevice(int iTimeout);
	public native int WriteAduDevice(int hDevice, String jBuffer, int nNumberOfBytesToWrite, int iTimeout);
	public native String ReadAduDevice(int hDevice, int nNumberOfBytesToRead, int iTimeout);
	public native void CloseAduDevice(int hDevice);
	
	private int relay;
	private int handle;
	private static AduJava adu = null;
	
	private AduJava() throws Exception {
		
		Application.loadProperties("adu.properties");
		
		relay = Application.getIntProperty(
					Application.getProperty("adu_relay"));
		
		handle = OpenAduDevice(500);
		if (handle == -1) {
			throw new Exception("Unable to locate ADU controller");
		} else {
			WriteAduDevice(handle, "mk0", 3, 500);
		}
	}
	
	public static synchronized AduJava getInstance() throws Exception {
		if (adu == null) {
			adu = new AduJava(); 
		}
		return adu;
	}
	
	public void activateHopper() {
		String cmd = "mk" + relay;
		WriteAduDevice(handle, cmd, cmd.length(), 500);
	}
	
	public void deactivateHopper() {
		WriteAduDevice(handle, "mk0", 3, 500);
	}
	
	public void closeAllHandles() {
		CloseAduDevice(handle);
		handle = -1;
	}
	
}
