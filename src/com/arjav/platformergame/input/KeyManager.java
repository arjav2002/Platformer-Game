package com.arjav.platformergame.input;

import java.awt.event.KeyEvent;

import com.arjav.platformergame.Game;
import com.arjav.platformergame.creatures.Player;

public class KeyManager {

    private boolean[] keys  ; // array of keys on the keyboard
    private Game game; // game object used to access many things
	
    // class constructor
	public KeyManager(Game game) { // game object used to access many things
        keys = new boolean[256]; // such a high length because of the number of keys on the keyboard
        this.game = game; 
	}

	// update method 
	public void tick() {
		if(game.getPlayer().inPipe()) { // if the game.getPlayer() is inside a pipe, it should not go right or left
			game.getPlayer().setVelX(0); // so set it's velX to 0
		}
	}
	
	private void jumpPlayer() {
		game.getPlayer().setVelY(-30); // then shoot it up 
		if(Player.increasedHeight) game.getSounds().playSound("/jump_super.wav"); // if a tall game.getPlayer() jumps then play the super jump sound
		else game.getSounds().playSound("/jump_small.wav"); // else if a short game.getPlayer() jumps then play the small jump sound
		game.getPlayer().jumping = true ; // and since it is in the air, set jumping to true
	}
	
	private void pressUp() {
		int pipeIndex;
		if(!game.getPlayer().inPipe()) {
			if(!game.getPlayer().jumping) {
				jumpPlayer();
			}
			if((pipeIndex = game.getPlayer().canGoUpAPipe()) >= 0) {
				jumpPlayer();
				game.getPlayer().requestToGoUpPipe(pipeIndex);
			}
		}
	}
	
	private void pressLeft() {
		if(!game.getPlayer().inPipe()) {
			game.getPlayer().goLeft = true; // then make the game.getPlayer() go left
			game.getPlayer().goRight = false;
			game.getPlayer().heading = 1 ; // and set the heading to 1 as the game.getPlayer() is going left
			game.getPlayer().leftOrRightKeyReleased = false;
		}
	}

	private void pressRight() {
		if(!game.getPlayer().inPipe()) {
			game.getPlayer().goRight = true; // then make the game.getPlayer() go right 
			game.getPlayer().goLeft = false;
			game.getPlayer().heading = 0 ; // and set the heading to 0 as the game.getPlayer() going right
			game.getPlayer().leftOrRightKeyReleased = false;
		}
	}
	
	private void pressDown() {
		int pipeIndex;
		if(!game.getPlayer().inPipe()) {
			if((pipeIndex = game.getPlayer().canGoDownAPipe()) >= 0) {
				game.getPlayer().requestToGoDownPipe(pipeIndex);
			}
		}
	}
	
	private void pressZ() {
		if(!game.getPlayer().inPipe()) {
			if(Player.fireState) {
				game.handler.getPlayer().f.activated = true; // then set the variable acitvated of instance of the Fireball class in the game.getPlayer() to true
			}
		}
	}
	
	private void releaseLeft() {
		if(!keys[KeyEvent.VK_RIGHT]) game.getPlayer().leftOrRightKeyReleased = true; // if the left arrow key and the right arrow key have been released, 
		game.getPlayer().goLeft = false;
	}
	
	private void releaseRight() {
		if(!keys[KeyEvent.VK_LEFT]) game.getPlayer().leftOrRightKeyReleased = true; // // if the left arrow key and the right arrow key have been released, 
		game.getPlayer().goRight = false;
	}
	
	public void keyPress(int keycode) {
		keys[keycode] = true;
		switch(keycode) {
		case KeyEvent.VK_UP:
			pressUp();
			break;
		case KeyEvent.VK_DOWN:
			pressDown();
			break;
		case KeyEvent.VK_RIGHT:
			pressRight();
			break;
		case KeyEvent.VK_LEFT:
			pressLeft();
			break;
		case KeyEvent.VK_Z:
			pressZ();
			break;
		}
	}
	
	public void keyRelease(int keycode) {
		keys[keycode] = false;
		switch(keycode) {
		case KeyEvent.VK_LEFT:
			releaseLeft();
			break;
		case KeyEvent.VK_RIGHT:
			releaseRight();
			break;
		}
	}
}
