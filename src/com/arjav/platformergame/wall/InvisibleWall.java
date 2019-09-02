package com.arjav.platformergame.wall;

import java.awt.Graphics;

import com.arjav.platformergame.Game;
import com.arjav.platformergame.Id;

public class InvisibleWall extends Tile{

	// class constructor
	public InvisibleWall(int x, int y, int width, int height, Id id, Game game, int level) {
		super(x, y, width, height, id, game, level);
	}

	// drawing or rendering method not required as the sprite is supposed to be invisible
	@Override
	public void render(Graphics g) {
		
	}
	
	// initialization not required as there is no image to be rendered or drawn
	@Override
	public void init() {
		
	}
	
	

}
