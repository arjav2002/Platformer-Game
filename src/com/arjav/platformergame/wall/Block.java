package com.arjav.platformergame.wall;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.arjav.platformergame.Game;
import com.arjav.platformergame.Id;

public class Block extends Tile{
	
	private BufferedImage me; // image to be rendered for the sprite
	
	// class constructor
	public Block(int x, int y, int width, int height, Id id, Game game, int level) {
		super(x, y, width, height, id, game, level);
	}

	// render or drawing method
	public void render(Graphics g) { // to make sure the block does not get rendered if it is not on the screen
		if((x + width< -game.xTranslate || x > -game.xTranslate + Game.WIDTH) || y + height < -game.yTranslate || y > -game.yTranslate + Game.HEIGHT) return;
		g.drawImage(me, x, y, width, height, null);
	}
	
	// initializing method
	public void init() {
		me = game.block.getBufferedImage();
	}

}
