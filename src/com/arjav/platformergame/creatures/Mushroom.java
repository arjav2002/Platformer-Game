package com.arjav.platformergame.creatures;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Random;

import com.arjav.platformergame.Game;
import com.arjav.platformergame.Id;
import com.arjav.platformergame.wall.MushroomBlock;
import com.arjav.platformergame.wall.Tile;

public class Mushroom extends Creature{

	private int velX = 0 , velY = 0 ; // velocity of the mushroom on the x axis and the y axis
	private int gravity = 1 ; // gravity used to increase velY
	private boolean ranAway = false; // if the mushroom has run away from it's block
	private int dir; // random integer which determines the initial velX
	private BufferedImage me1 , me2; // two images - one for the mushroom other for the flower
	private boolean scoreRespawned ; // to check if the score has been increased
	private MushroomBlock block; // to store the block from which the mushroom erupts
	private int scoreRespawnedTime, now; // to store the time at which the score was increased and the current time
	
	// class constructor
	public Mushroom(int x, int y, int width, int height, Id id , Game game, MushroomBlock block) {
		super(x, y, width, height, id , game);
		this.block = block;
		dir = new Random().nextInt(2);
		
	}

	@Override
	public void render(Graphics g) {
		// rendering efficiency which makes sure that the mushroom is not rendered if it is not being displayed
		if((x + width< -game.xTranslate || x > -game.xTranslate + Game.WIDTH) || y + height < -game.yTranslate || y > -game.yTranslate + Game.HEIGHT) return;

		if(block.poppedUp) { // if the mushroom block has been activated or popped up
			if(!Player.increasedHeight) g.drawImage(me1 , (int)x , (int)y , (int)width , (int)height , null); // if the player has not increased in height then render or draw the mushroom
			else if(!Player.fireState && Player.increasedHeight){
				g.drawImage(me2 , (int)x , (int)y , (int)width , (int)height , null); // else draw  or render the flower
			}
			else if(Player.fireState && Player.increasedHeight) { // if the player is in fire state and the player has already increased
				if(!scoreRespawned) { // if score increase has not been performed
					game.score += 500 ; // then increase the score by 500
					scoreRespawned = true ; // since the score has been increased scoreRespawned is set to true
					scoreRespawnedTime = game.seconds ; // and get the time at which the score was increased
					game.getSounds().playSound("/coin.wav"); // play the coin sound
				}
				now = game.seconds; // get the current time
				if(now - scoreRespawnedTime >= 1) game.handler.die(this); // if more than or equal to 2 seconds have passed since the score was increased then remove the mushroom 
				// from the linked list creature in the Handler class
				g.setFont(new Font("Serif", Font.BOLD , 30)); // setting the font of the graphics object
				g.setColor(Color.WHITE); // setting the colour of the graphics object to white
				g.drawString("" + 500, (int)x, (int)y); // drawing 500 in the game in place of the mushroom image
				velY = -3; // making the mushroom go up
			}
		}
	}
	
	// method to set set initial velX after mushroom has popped up
	private void runAway() {
		if(!Player.increasedHeight) {
			switch(dir) { 
			case 0 : // if dir is 0
				velX = -2 ; // then start moving towards left
			case 1 : // if dir is 1
				velX = 2 ; // then start moving towards right
			}
		}
		ranAway = true ; // since the mushroom has ran away
	}

	@Override
	public void tick(double dt) { // to  make sure that the mushroom is not updated if it is not on the screen
		this.dt = dt;
		if((x + width< game.getPlayer().getX() - 200 - width || x > game.getPlayer().getX() + game.getPlayer().getWidth() + Game.WIDTH) || y < game.getPlayer().getY() - 500 - height && !game.getPlayer().inPipe())
			return;
		x += dt * velX ; // x to be increased according to velX
		y += dt * velY ; // y to be increased according to velY
		
		if(block.poppedUp && !ranAway)  // if the mushroom block says that the mushroom has popped up and the mushroom has not run away
			velY = -2; // then make the mushroom move up
		if(y <= block.getY() - height && !ranAway){  // if the mushroom is totally out of its block and not run away
			velY = 0 ; // then velY should be made 0
			runAway(); // the mushroom should run away 
		}
		if(ranAway){ // once the mushroom has run away, the game will check for collisions
			for(int i = 0 ; i < game.handler.walls.size() ; i ++) { // loop through every tile in the linked list wall in the Handler class
				Tile t = game.handler.walls.get(i); // get the tile in the linked list wall in the Handler class at the index i
				if(t.getId()==Id.mushroomBlock){ // if the ID of the tile is mushroomBlock
					if(getBoundsBottom().intersects(t.getBoundsTop())) { // if the lower part of the mushroom touches the top part of the tile
						velY = 0 ; // set the velY of mushroom to 0
						y = t.getY() - getHeight(); // and put the mushroom right above the tile
					}
					else velY += gravity ; // else increase velY according to gravity
				}
				if(t.getId()==Id.grass || t.getId() == Id.pipeUp || t.getId() == Id.mud || t.getId() == Id.brick){ // if the ID of the tile is grass or pipeUp or mud or brick
					if(getBoundsBottom().intersects(t.getBoundsTop())){ // if the lower part touches the upper part of the tile
						velY = 0 ; // set the VelY to 0
						y = t.getY()-height ; // put the mushroom just above the tile
					}
					if(getBoundsRight().intersects(t.getBoundsLeft())) velX = -2 ; // if right hand side part touches the left hand side part of the tile it should start going towards left
					if(getBoundsLeft().intersects(t.getBoundsRight())) velX = 2 ; // if left hand side part touches the right hand side part of the tile it should start going towards right
				}
			}
			for(int a = 0 ; a < game.handler.creatures.size() ; a ++) { // loop through every creature in the linked list creature in the Handler class
				Creature c = game.handler.creatures.get(a); // get the Creature in the linked list creature in the linked list creature at index i in the Handler class
				if(c.getId()==Id.player){ // if the ID of the creature is player
					if(getBounds().intersects(c.getBounds())) { // and if the creature touches the mushroom
						game.getSounds().playSound("/power_up.wav"); // play the sound power up using a new instance of Assets class if the player is not in fire state
						if(!Player.increasedHeight) {
							game.handler.getPlayer().y -= 64 ; // decrease the y coordinate of the player by 64
							game.handler.getPlayer().height = 128 ; // and increase the height of the player to 128
							Player.increasedHeight = true ; // set the player's increaseHeight variable to true
						} else if(!Player.fireState) { // or else if the player is not in fire state
							Player.fireState = true ; // then set the Player's fireState variable to true
						}
						game.handler.die(this); // and then remove the mushroom from the linked list creature in the handler class
					}
				}
	
			}
		}
	}

	// method to initialize the images to be used for rendering or drawing the sprites
	@Override
	public void init() {
		me1 = game.mushroom.getBufferedImage(); // gets the image of the mushroom
		me2 = game.fireFlower.getBufferedImage(); // gets the image of the flower
		
	}
}
