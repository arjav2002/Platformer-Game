package com.arjav.platformergame.creatures;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.arjav.platformergame.Game;
import com.arjav.platformergame.Id;

public class Coin extends Creature{

	private BufferedImage me; // image of the sprite
	
	// class constructor
	public Coin(int x, int y, int width, int height, Id id, Game game) {
		super(x, y, width, height, id, game);
		
	}

	// method to draw coin
	@Override
	public void render(Graphics g) { // to make sure the coin does not get rendered if it is not on the screen
		if((x + width< -game.xTranslate || x > -game.xTranslate + Game.WIDTH) || y + height < -game.yTranslate || y > -game.yTranslate + Game.HEIGHT) return;
		g.drawImage(me, (int)x, (int)y, width, height, null); // drawing the sprite
		
	}

	// update method
	@Override
	public void tick(double dt) { // to make sure the coin is not updated if it is not on the screen
		if((x + width< game.getPlayer().getX() - 200 - width || x > game.getPlayer().getX() + game.getPlayer().getWidth() + Game.WIDTH) || y < game.getPlayer().getY() - 500 - height && !game.getPlayer().inPipe()) return;
		for(int i = 0; i < game.handler.creatures.size(); i++) { // loop through all the Creatures in the linked list creature in the Handler class
			if(game.handler.creatures.get(i).getId()==Id.player) { // if the Creature in the linked list creature at the index of i in the Handler class has an ID of player
				Player p = (Player) game.handler.creatures.get(i); // then store the creature as an instance of the Player class
				if(p.getBounds().intersects(getBounds())) { // if the instance touches the coin
					game.score += 100 ; // then increase score by 100
					game.getSounds().playSound("/coin.wav"); // play the coin sound
					game.handler.die(this); // and remove this coin from the linked list creature in the Handler class
				} 
			}
		}
	}

	// initialization of the image of the sprite
	@Override
	public void init() {
		me = game.coin.getBufferedImage();
		
	}

}
