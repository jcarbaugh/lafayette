package edu.american.weiss.lafayette.server;

import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;
import java.net.SocketException;

public class ControlServer implements Runnable {

	private boolean isPaused;
	private boolean isRunning;
	private int port;
	
	private ServerSocket sSocket;
	
	public ControlServer(int port) throws IOException {
		this.port = port;
		sSocket = new ServerSocket(port);
	}
	
	public void run() {
		
		isRunning = true;
		
		while (isRunning) {
			
			while (isPaused) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException ie) { }
			}
			
			try {
				
				Socket s = sSocket.accept();
				
				Thread t = new Thread(new SocketHandler(s));
				t.start();
				
			} catch (SocketException se) {
				isRunning = false;
			} catch (IOException io) {
				isRunning = false;
			}
			
		}
		
		try {
			sSocket.close();
		} catch (IOException ioe) { }
				
	}
	
	public int getPort() {
		return port;
	}
	
	public void stop() {
		try {
			sSocket.close();
		} catch (IOException ioe) { }
		isRunning = false;
		isPaused = false;
	}
	
	public void pause() {
		if (isRunning == true) {
			isPaused = true;
		}
	}
	
	public void start() {
		isRunning = true;
		isPaused = false;
	}
	
	public boolean isRunning() {
		return isRunning;
	}
	
	public boolean isPaused() {
		return isPaused;
	}
	
}