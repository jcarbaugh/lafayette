package edu.american.weiss.lafayette.chamber;

import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioInputStream;

public class AudioPlayer implements Runnable {

	private static AudioPlayer instance;
	private List<AudioInputStream> queue;
	
	static {
		instance = new AudioPlayer();
	}
	
	private AudioPlayer() {
		queue = new ArrayList<AudioInputStream>();
	}
	
	public void run() {
		
	}
	
}
