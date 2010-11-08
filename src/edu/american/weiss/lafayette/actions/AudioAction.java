package edu.american.weiss.lafayette.actions;

import edu.american.weiss.lafayette.chamber.AudioPlayer;
import edu.american.weiss.lafayette.composite.BaseCompositeAction;

public class AudioAction extends BaseCompositeAction {
	
	private AudioPlayer audioPlayer;
	private String audioId;
	
	public AudioAction(String audioId) {
		this.audioId = audioId;
		this.audioPlayer = AudioPlayer.getInstance();
	}

	public boolean runAsThread() {
		return false;
	}

	public void run() {
		audioPlayer.playTrack(audioId);
	}

}
