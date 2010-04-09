package edu.american.weiss.lafayette.server;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

import javax.imageio.ImageIO;

import edu.american.weiss.lafayette.Application;
import edu.american.weiss.lafayette.composite.CompositeController;
import edu.american.weiss.lafayette.data.DataRecorder;

public class SocketHandler implements Runnable {
	
	private Socket s;
	
	public SocketHandler(Socket s) {
		this.s = s;
	}

	public void run() {
	
		if (s != null) {
			
			try {
			
				InputStream is = s.getInputStream();
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				
				String requestString = br.readLine();
				
				if (requestString.equals("getData")) {
					
					OutputStreamWriter osw = new OutputStreamWriter(s.getOutputStream());
					CompositeController cc = Application.getCompositeController();
					
					StringBuffer output = new StringBuffer();
					output.append("[RESP=");
					output.append(DataRecorder.getResponses(cc.getCurrentComposite()));
					output.append(":");
					output.append(DataRecorder.getResponseTotal());
					output.append("] [REINF=");
					output.append(DataRecorder.getReinforcers(cc.getCurrentComposite()));
					output.append(":");
					output.append(DataRecorder.getReinforcerTotal());
					output.append("] [DUR=");
					output.append(DataRecorder.getDuration(cc.getCurrentComposite()) + System.currentTimeMillis());
					output.append(":");
					output.append(DataRecorder.getDurationTotal());
					output.append("]");
					
					osw.write(output.toString());
					
				} else if (requestString.equals("getCumulativeRecorder")) {

					//BufferedImage output = OldCumulativeRecorder.getInstance().getBufferedImage();
					
					//if (output != null) {
					//	ImageIO.write(output, "PNG", s.getOutputStream());
					//}
					
				} else if (requestString.equals("setVariable")) {
					
					String variableString = br.readLine();
					
					while (variableString != null) {

						String key = variableString.substring(0, variableString.indexOf("="));
						String value = variableString.substring(variableString.indexOf("=") + 1);
						
						Application.setProperty(key, value);
						
						variableString = br.readLine();
						
					}					
					
				}
				
				s.close();
								
			} catch (IOException ioe) {
				try {
					s.close();
				} catch (Exception e) { }
			}
			
		}
		
	}
	
}