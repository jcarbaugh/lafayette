package edu.american.weiss.lafayette.chamber;

import java.util.HashMap;
import java.util.Map;

public class AudioPlayer {

	private static AudioPlayer instance;
	
	private Map<String, AudioFile> cache;
	private boolean isRunning = true;
	
	static {
		instance = new AudioPlayer();
	}
	
	private AudioPlayer() {
		cache = new HashMap<String, AudioFile>();
	}
	
	public static AudioPlayer getInstance() {
		return instance;
	}
	
	public void addTrack(String id, String path) {
		AudioFile af = new AudioFile(path);
		cache.put(id, af);
	}
	
	public void addTrack(String id, AudioFile af) {
		cache.put(id, af);
	}
	
	public AudioFile getTrack(String id) {
		return cache.get(id);
	}
	
	public void playTrack(String id) {
		getTrack(id).play();
	}
	
	public void close() {
		for (AudioFile af : cache.values()) {
			af.destroy();
		}
	}
	
}
