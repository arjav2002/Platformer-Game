package com.arjav.platformergame.wall;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.arjav.platformergame.Game;
import com.arjav.platformergame.Id;
import com.arjav.platformergame.creatures.Mushroom;
import com.arjav.platformergame.creatures.Player;

public class MushroomBlock extends Tile{
	
	public boolean poppedUp = false , madeMushroom = false; // variables to know if the mushroom has popped up or has been made
	private BufferedImage mePu, meNpU; // to store image of the sprite when the mushroom has popped up and not popped up

	// class constructor
	public MushroomBlock(int x, int y, int width, int height , Id id , Game game, int level) {
		super(x, y, width, height , id , game, level);
		
	}

	// method to draw or render the sprite
	@Override
	public void render(Graphics g) {  // to make sure that the sprite does not get rendered if it is not on the screen
		if((x + width< -game.xTranslate || x > -game.xTranslate + Game.WIDTH) || y + height < -game.yTranslate || y > -game.yTranslate + Game.HEIGHT) return;
		if(!poppedUp) g.drawImage(meNpU , x , y , width , height , null);
		// if the mushroom has not popped up so draw the image in which the mushroom has not popped up
		else { // else the mushroom has popped up so draw the image in which the mushroom has popped up
			g.drawImage(mePu , x , y , width , height , null);
			if(!madeMushroom) { // if the mushroom has not been made
				Mushroom m = new Mushroom(x, y, width, height, Id.mushroom, game, this); // then create a new mushroom in place of the sprite
				game.handler.addCreature(m); // add it to the linked list creature in the Handler class
				m.init(); // initialize the mushroom
				madeMushroom = true; // and since the mushroom has been made, set madeMushroom to true
				if(!Player.fireState) game.getSounds().playSound("/power_up_appears.wav"); // if the player is not in fire state play the sound of the power up appearing
				// only then play the sound because if the player is in fire state, no sound should be played
			}
		}
	}
	
	// initialization method for the sprite
	@Override
	public void init() {
		meNpU = game.mushroomBlockNotPoppedUp.getBufferedImage();
		mePu = game.mushroomBlockPoppedUp.getBufferedImage();
		
	}

}
