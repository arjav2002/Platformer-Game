package com.arjav.platformergame.input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import com.arjav.platformergame.Game;

public class MyKeyListener implements KeyListener{
	
	private Game game;
	
	public MyKeyListener(Game game) {
		this.game = game;
	}
	
	// method called when a key is pressed
	@Override
	public void keyPressed(KeyEvent e) {
		if(game.isPlayingAI()) {
			switch(e.getKeyCode()) {
			case KeyEvent.VK_ESCAPE:
				Game.hideWindow(game);
				break;
			case KeyEvent.VK_1:
				Game.AMOUNT_OF_TICKS = 60;
				break;
			case KeyEvent.VK_2:
				Game.AMOUNT_OF_TICKS = 120;
				break;
			case KeyEvent.VK_3:
				Game.AMOUNT_OF_TICKS = 180;
				break;
			case KeyEvent.VK_4:
				Game.AMOUNT_OF_TICKS = 240;
				break;
			}
		}
		else game.getKeyManager().keyPress(e.getKeyCode());
		
		switch(e.getKeyCode()) {
			case KeyEvent.VK_0:
				game.getSounds().toggleMusic();
				break;
		}
		
	}
		
	// method called when the key is released
	@Override
	public void keyReleased(KeyEvent e) {
		if(game.isPlayingAI()) return;
		game.getKeyManager().keyRelease(e.getKeyCode());
		// then the game.getPlayer() should stop moving along the x axis. Therefore, to allow decrementation of it's accelX, we set game.getPlayer()'s keyPressed value to true
	}
	
	// method not using
	@Override
	public void keyTyped(KeyEvent e) {

	}

}
