package edu.american.weiss.lafayette;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.ByteArrayInputStream;
import java.io.RandomAccessFile;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

public class MediaManager {

	private static MediaManager instance;
	private Map<String, Object> cache;
	private Toolkit tk;
	
	static {
		instance = new MediaManager();
	}
	
	private MediaManager() {
		cache = new HashMap<String, Object>();
		tk = Toolkit.getDefaultToolkit();
	}
	
	public static MediaManager getInstance() {
		return instance;
	}
	
	public Object get(String id) {
		if (cache.containsKey(id)) {
			return cache.get(id);
		}
		return null;
	}
	
	public Image getImage(String id) {
		return (Image) get(id);
	}
	
	public AudioInputStream getAudio(String id) {
		AudioInputStream ais = null;
		byte[] buffer = (byte[]) get(id);
		try {
			ais = AudioSystem.getAudioInputStream(
				new ByteArrayInputStream(buffer.clone()));
		} catch (IOException ioe) {
			
		} catch (UnsupportedAudioFileException uafe) {
			
		}
		return ais;
	}
	
	public void loadImage(String id, String path) {
		Image img = tk.getImage(path);
		if (img != null) {
			cache.put(id, img);
		}
	}
	
	public void loadAudio(String id, String path) {
		try {
			RandomAccessFile raf = new RandomAccessFile(path, "r");
			byte[] buffer = new byte[(int)raf.length()];
			raf.readFully(buffer);
			raf.close();
			cache.put(id, buffer);
		} catch (IOException ioe) {
			
		}
	}
	
}
