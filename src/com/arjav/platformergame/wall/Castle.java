package com.arjav.platformergame.wall;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.arjav.platformergame.Game;
import com.arjav.platformergame.Id;

public class Castle extends Tile{

	private BufferedImage me; // image for drawing the sprite
	
	// class constructor
	public Castle(int x, int y, int width, int height, Id id, Game game, int level) {
		super(x, y, width, height, id, game, level);
	}

	// method to draw or render
	@Override
	public void render(Graphics g) { // to make sure that the sprite does not get rendered if it is not on the screen
		if((x + width< -game.xTranslate || x > -game.xTranslate + Game.WIDTH) || y + height < -game.yTranslate || y > -game.yTranslate + Game.HEIGHT) return;
		g.drawImage(me, x, y, width, height, null);
		
	}

	// method to initialize the sprite
	@Override
	public void init() {
		me = game.castle.getBufferedImage();
	}

}
