package com.arjav.platformergame.gfx;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class SpriteSheet {
	// class to divide the sprite sheet into several sprites
	private BufferedImage sheet ; // image to be used as a sprite sheet
	
	// class constructor
	public SpriteSheet(String path) { // path at which the sprite sheet is located
		try {
			sheet = ImageIO.read(getClass().getResource(path)); // try to get the image
		} catch (IOException e) { // if IO exception is thrown then print the error in the console and close the game
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	// method to get the sprite by dividing the sheet into sprites
	public BufferedImage getSprite(int x , int y) {
		return sheet.getSubimage(x * 64, y * 64, 64 , 64);
	}

}
