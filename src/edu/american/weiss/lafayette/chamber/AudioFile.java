package edu.american.weiss.lafayette.chamber;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

public class AudioFile implements Runnable {
	
	private AudioInputStream audioInputStream;
	private Clip c;

	public AudioFile(String path) {
		
		File soundFile = new File(path);
		
		if (!soundFile.exists()) { 
            System.err.println("WAV file not found: " + path);
            return;
        }
		
        try {

            audioInputStream = AudioSystem.getAudioInputStream(soundFile);
     
            AudioFormat format = audioInputStream.getFormat();
            DataLine.Info info = new DataLine.Info(Clip.class, format);
        
        	c = (Clip) AudioSystem.getLine(info);
			c.open(audioInputStream);
        	
        } catch (LineUnavailableException lue) {
        	lue.printStackTrace();
        } catch (UnsupportedAudioFileException uafe) {
        	uafe.printStackTrace();
        } catch (IOException ioe) {
        	ioe.printStackTrace();
        }
		
	}
	
	public void play() {
		Thread t = new Thread(this);
		t.start();
	}
	
	public void run() {
		c.setFramePosition(0);
		c.start();
		c.drain();
	}
	
	public void destroy() {
		c.close();
	}
	
	
	
}
