package com.arjav.platformergame.gfx;

import java.awt.image.BufferedImage;

public class Sprite {
	// class used to get images for rendering or drawing sprites
	private SpriteSheet sheet ; // image in which all the sprites are stored
	int x , y ; // x coordinate divided by the width and y coordinate divided by height of each image inside the sprite sheet 
	
	// class constructor
	public Sprite(SpriteSheet sheet , int x , int y) {
		this.sheet = sheet ;
		this.x = x ;
		this.y = y ;
	}
	
	// method to get the image for rendering or drawing the sprite
	public BufferedImage getBufferedImage() {
		return sheet.getSprite(x , y);
	}
	
}
