package com.arjav.platformergame.utils;

import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.BooleanControl;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Sounds {
	
	private AudioInputStream ais ;
	private AudioFormat af;
	private Clip backgroundClip = null;
	private DataLine.Info info;
	public static boolean canPlay = true; // to know whether the sound can be played
	private boolean initialisedBackground = false;
	
	// to toggle the sound between being enabled or not
	public void toggleMusic() { 
		if(canPlay) {
			canPlay = false ; // if it is enabled, then make it disabled
		}
		else canPlay = true; // else make it enabled
		updateBackgroundStatus(!canPlay);
	}
	
	private void updateBackgroundStatus(boolean mute) {
		BooleanControl muteControl = (BooleanControl) backgroundClip.getControl(BooleanControl.Type.MUTE);
		if(muteControl != null) muteControl.setValue(mute);
	}
	
	public static boolean canPlay() {
		return canPlay;
	}
	
	public boolean isBackgroundInitialised() {
		return initialisedBackground;
	}
	
	public void init() {
		if(initialisedBackground) return;
		try {
			ais = AudioSystem.getAudioInputStream(new Sounds().getClass().getResource("/background.wav"));
			af = ais.getFormat();
			info = new DataLine.Info(Clip.class, af);
			backgroundClip = (Clip) AudioSystem.getLine(info);
			backgroundClip.open(ais);
		} catch (UnsupportedAudioFileException | IOException e) {
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
		initialisedBackground = true;
	}
	
	public void stopBackground() {
		if(!initialisedBackground) return;
		backgroundClip.stop();
		backgroundClip.close();
		try {
			ais.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		initialisedBackground = false;
	}
	
	public void playBackground() {
		if(!initialisedBackground) return;
		updateBackgroundStatus(!canPlay);
		if(backgroundClip.getMicrosecondPosition()==backgroundClip.getMicrosecondLength() || !backgroundClip.isRunning()) {
			backgroundClip.setMicrosecondPosition(0);
			backgroundClip.start();
		}
	}
	
	// to play the sound located at a path specified in the parameter of the method
	public void playSound(String path) {
		if(!canPlay) return;
		AudioInputStream audioStream;
		try {
			audioStream = AudioSystem.getAudioInputStream(getClass().getResource(path));
			AudioFormat format = audioStream.getFormat();
			DataLine.Info info = new DataLine.Info(Clip.class, format);
			Clip audioClip = (Clip) AudioSystem.getLine(info);
			audioClip.open(audioStream);
			audioClip.start();
		} catch (UnsupportedAudioFileException | IOException e) {
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
	}
	
}