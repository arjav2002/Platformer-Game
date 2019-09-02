package com.arjav.platformergame.wall;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import com.arjav.platformergame.Game;
import com.arjav.platformergame.Id;

public class Flag extends Tile{

	public int level , flagY; // variable for storing the level and the y coordinate of the level
	private BufferedImage[] me = new BufferedImage[3]; // array to draw or render sprite 
	public boolean animate = false; // to see if the flag should animate
	
	// class constructor
	public Flag(int x, int y, int width, int height, Id id, Game game , int level) {
		super(x, y, width, height, id, game, level);
		flagY = y ;
	}

	// render or draw method
	@Override
	public void render(Graphics g) {
		// rendering efficiency to make sure that flag is not being rendered when it is not displayed on the screen
		if((x + width< -game.xTranslate || x > -game.xTranslate + Game.WIDTH) || y + height < -game.yTranslate || y > -game.yTranslate + Game.HEIGHT) return;
		// the sprite at index 1 in array me is of a rod, at index 2 is of a platform and at 0 is of a flag therefore making a height * 5 flag
		g.drawImage(me[1], x , y+height , width , height , null);
		g.drawImage(me[1], x , y+height*2 , width , height , null);
		g.drawImage(me[1], x , y+height*3 , width , height , null);
		g.drawImage(me[2], x , y+height*4 , width , height , null);
		g.drawImage(me[0], x , flagY , width , height , null);
		
	}
	
	public void tick() {
		if(animate) animate(); // if animate is true, then the flag should animate
	}
	
	// method to animate the flag in which the flag goes down by 3 pixels each time it called
	public void animate() {
		flagY += 3 ;
	}
	
	// collision detection rectangle
	@Override
	public Rectangle getBounds() {
		return new Rectangle(x , y , width , height * 5);
	}

	// initialization method to initialize the sprite
	@Override
	public void init() {
		for(int i = 0 ; i < 3; i++) {
			me[i] = game.flag[i].getBufferedImage();
		}
		
	}

}
