package com.arjav.platformergame.creatures;


import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Random;

import com.arjav.platformergame.Game;
import com.arjav.platformergame.Id;
import com.arjav.platformergame.wall.Tile;

public class Goomba extends Creature{
	
	private double velX = 0 , velY = 0 , gravity = 0.2;
	private int heading; // heading -  0 = right    1 = left
	/// velocity of sprite on x axis , velocity of sprite on y axis , gravity like in the real world required to increase velocity of sprite on y axis
	private BufferedImage meR , meL ; // images used when goomba enemy is looking towards right and goomba enemy is looking towards left
	private int dir; // generating a random number between 1 and 0 which will decide the initial velX

	// class constructor
	public Goomba(int x, int y, int width, int height , Game game , Id id) {
		super(x, y, width, height, id , game);
		dir = new Random().nextInt(2);
		switch(dir) { 
		case 0 : // if randomly generated number is 0 
			velX = -4 ; // then try to move towards left
			break ;
		case 1 : // if randomly generated number is 1
			velX = 4 ; // then try to move towards right
			break ;
		}
	}

	// method to draw or render
	@Override
	public void render(Graphics g) {
		// code for rendering efficiency to make sure sprite is not being rendered when the user cannot see it
		if((x + width< -game.xTranslate || x > -game.xTranslate + Game.WIDTH) || y + height < -game.yTranslate || y > -game.yTranslate + Game.HEIGHT) return;
		if(heading==0) g.drawImage(meR , (int)x , (int)y , (int)width , (int)height , null); // if going towards right then render the image in which goomba enemy is looking towards right
		else g.drawImage(meL , (int)x , (int)y , (int)width , (int)height , null); // if going towards left then render the image in which goomba enemy is looking towards left
	}

	// method to update
	@Override
	public void tick(double dt) { // to make sure the goomba is not updated if it is not on the screen
		this.dt = dt;
		
		x += dt*velX ; // x to be increased according to velX
		y += dt*velY ; // y to be increased according to velY
		velY += dt*gravity ; // velY to be increased according to gravity
		if(velY > 10) velY = 10;
		
		for(Tile t : game.handler.walls) { // loop through every tile in the linked list wall in the Handler class
			if((getBoundsRight().intersects(t.getBoundsLeft()) || getBoundsRight().intersects(t.getBoundsInterior())) && velX > 0) { // if the right hand side part touches the tile's left hand side partW
				x = t.getX() - width;
				heading = 1; // then the goomba enemy should start moving towards left
				velX = -4;
			}
			else if((getBoundsLeft().intersects(t.getBoundsRight()) || getBoundsRight().intersects(t.getBoundsInterior())) && velX < 0) { // if the left hand side part touches the tile's right hand side part
				x = t.getX() + t.getWidth() + 5;
				heading  = 0 ; // then the goomba enemy should start moving towards right
				velX = 4;
			}
			else if(getBoundsBottom().intersects(t.getBoundsTop())) { // if bottom part touches the tile's upper part
				y = t.getY() - height;
				velY = 0 ; // then velY should be made 0 so goomba enemy does not go down anymore
			}
		}
		
		
		for(Creature c : game.handler.creatures) { // loop through every creature in the linked list creature in the Handler class
			if(getBounds().intersects(c.getBounds()) && c.getId() == Id.fireball) { // if the ID of the creature is fireball
				if(game.handler.getPlayer().f.activated) { // to make sure the fire ball does not kill the goomba if it is not activated
					game.handler.die(c); // then first kill the fireball
					game.score += 100;
					game.handler.die(this); // then kill itself
				}
			}
		}
	}

	// method to initialize the images used for rendering or drawing the sprite
	@Override
	public void init() {
		meR = game.goombaRight.getBufferedImage(); // initializing the image in which the goomba enemy is looking towards right
		meL = game.goombaLeft.getBufferedImage(); // initializing the image in which the goomba enemy is looking towards left
	}
	
	

}
