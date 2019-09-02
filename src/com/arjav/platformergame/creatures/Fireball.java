package com.arjav.platformergame.creatures;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.arjav.platformergame.Game;
import com.arjav.platformergame.Id;
import com.arjav.platformergame.wall.Tile;

public class Fireball extends Creature{

	private BufferedImage meR, meL; // used to store the image of the sprite going left and sprite going right
	private double velX ; // used to store the velocity of the sprite on the x axis
	public boolean activated = false , lastTimeStored = false; // to know if fire ball has been activated and if lastTime has been stored
	private int lastTime, now ; // lastTime to know the time when the fire ball was activated and now to know the current time
	private boolean soundPlayed = false ; // boolean to check if the sound has been already played
	
	// class constructor
	public Fireball(int x, int y, int width, int height, Id id, Game game) {
		super(x, y, width, height, id, game);
	}

	@Override
	public void render(Graphics g) {
		if(!activated) return; // if not activated yet then return from here because no rendering or drawing of this sprite is required when it is not activated
		if(velX > 0) g.drawImage(meR, (int)x, (int)y, (int)width, (int)height, null); // if the sprite is going right then draw or render the image of the sprite going right
		else if(velX < 0) g.drawImage(meL, (int)x, (int)y, (int)width, (int)height, null); // if the sprite is going left then draw or render the image of the sprite going left
		
	}

	@Override
	public void tick(double dt) {
		this.dt = dt;
		if(!activated) { // if not activated then put the fire ball next to the player
			y = game.handler.getPlayer().getY() + game.handler.getPlayer().getHeight() / 2 - 16 ;
			if(game.handler.getPlayer().getHeading() == 0) velX = dt*7 ; // if player is looking towards right then the fire ball should also go right
			else if(game.handler.getPlayer().getHeading() == 1) velX = dt*-7 ; // if player is looking towards left then the fire ball should also go left
			if(velX > 0)x = game.handler.getPlayer().getX() + game.handler.getPlayer().getWidth(); // if the player is looking towards right the fire ball must also be
			// towards the right side
			else if(velX < 0) x = game.handler.getPlayer().getX(); // if the player is looking towards left the fire ball must also be
			// towards the left side
			return ;
		}
		if(!lastTimeStored) { // if the time before which the ball was activated has not been stored
			lastTime = game.seconds ; // then store it
			lastTimeStored = true; // since it is stored
		}
		if(!soundPlayed) { // if the sound has not yet been played
			game.getSounds().playSound("/fireball.wav"); // then play the sound
			soundPlayed = true ; // and set soundPlayed to true as the sound has been played
		}
		now = game.seconds ; // store the time right now
		x += dt*velX ; // make the fire ball move towards right or left depending upon the velocity along x axis
		for(int i = 0 ; i < game.handler.walls.size() ; i++) {
			Tile t = game.handler.walls.get(i); 
			if(getBounds().intersects(t.getBounds())) game.handler.die(this); // if it collides with any tile then it should die
		}
		if (now - lastTime >= 2) { // if more than or equal to two seconds have passed since it has been activated
			game.handler.die(this); // then it should die
		}
	}

	// initialization of the images for the sprite
	@Override
	public void init() {
		meR = game.fireballRight.getBufferedImage();
		meL = game.fireballLeft.getBufferedImage();
	}

}
