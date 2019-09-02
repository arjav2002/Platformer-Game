
package com.arjav.platformergame.creatures;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import com.arjav.platformergame.Game;
import com.arjav.platformergame.Id;
import com.arjav.platformergame.wall.Flag;
import com.arjav.platformergame.wall.MushroomBlock;
import com.arjav.platformergame.wall.Tile;
public class Player extends Creature{

	public int heading = 0 ; // 0 = right  1 = left
	private double velY = 0; //  velocity of the sprite on the y axis
	private double velX = 0; // velocity of the sprite on the x axis
	public int fireBalls = 0; // number of fire balls
	private float gravity = 2.0f; // gravity which will increase the velY
	public boolean leftOrRightKeyReleased = false;
	private BufferedImage meRnormal , meLnormal , meRfire , meLfire ; // images for rendering or drawing the player when it is not moving
	private BufferedImage meNormal[] = new BufferedImage[4] , meFire[] = new BufferedImage[4] , inAir[] = new BufferedImage[4]; // images for rendering or drawing the player when it is moving
	public boolean canGoDownPipes[]; // boolean variables to check if the player can go down or up pipes
	public boolean canGoUpPipes[];
	public boolean jumping = false;
	public static boolean increasedHeight = false , fireState = false; // booleans to check if the player is jumping and has already increased in height and to check if the player is in fire state or not
	private	boolean increasedAlready = false ; // to check if the player was in the increased state or fire state before the current level 
    public boolean inPipes[]; // to check if the player was in a pipe
    private	int frame = 0, totalFrames = 2;
	private int frameDelay = 0; // frame number of animation and delay between each frame
	public Fireball f ; // fire ball shot by the player
	private Flag fl ; // flag with which the player will collide at the end of the level
	private boolean soundPlayed = false ; // to check if the sound of the flag coming down has been already played
	private boolean blink = false; // to check if the player is blinking
	public boolean goRight = false, goLeft = false; // variables to see whether the player has to go right or left
	private int renderFrames = 0, blinks = 0; // to keep track of the number of renders happened out of 60 and the number of times the player has blinked
	
	private int requestedPipeIndex = -1; // holds the index of the pipe requested to travel in
	private boolean requestedPipeDirection = false; // holds the direction of travel in the requested pipe travel 	up = true	down = false
	
	public int getHeading() {
		return heading;
	}
	
	//setter for x
	public void setX(int x) {
		this.x = x;
	}
	
	// class constructor
	public Player(int x , int y , int width , int height , Game game , Id  id) {
        super((int)x , (int)y , (int)width , (int)height , id , game);
		this.game = game ;
	}
	
	// getter for height
	public int getHeight() {
		return height ;
	}
	
	//setter for height
	public void setHeight(int h) {
		height = h ;
	}

	// method for drawing or rendering the player
	@Override
	public void render(Graphics g) {
		if((x + width< -game.xTranslate || x > -game.xTranslate + Game.WIDTH) || y + height < -game.yTranslate || y > -game.yTranslate + Game.HEIGHT) return;
		if(renderFrames!=60) renderFrames++;
		else if(renderFrames==60) renderFrames = 0;
		if(blink && renderFrames >= 50)	{
			if(renderFrames==50) blinks++;
			return;
		}
		if(blinks>=3) {
			blinks = 0 ;
			blink = false;
		}
		if(jumping) { // if the player is in air
			if(fireState) { // if the player is in fire state
				if(heading == 0) g.drawImage(inAir[0], (int)x, (int)y, (int)width, (int)height, null); // if the player is going right then draw the image in which the player is in the fire state, in  air and looking right
				else g.drawImage(inAir[2], (int)(int)x, (int)y, (int)width, (int)height, null); // if the player is going left then draw the image in which the player is in the fire state, in  air and looking left
			}
			else { // else the player in is normal state, then
				if(heading == 0) g.drawImage(inAir[1], (int)x, (int)y, (int)width, (int)height, null); // if the player is going right then draw the image in which the player is in the normal state, in  air and looking right
				else g.drawImage(inAir[3], (int)x, (int)y, (int)width, (int)height, null); // if the player is going left then draw the image in which the player is in the normal state, in  air and looking left
			}
		}
		else if(!fireState) {  // if the player is not in fireState
			if(velX == 0 && !jumping) { // if the player is not moving
				if(heading==0) { // and it is looking right
					g.drawImage(meRnormal , (int)x , (int)y , (int)width , (int)height , null); 
					// then draw the sprite which is of normal state and looking towards right
				}
				else {
					g.drawImage(meLnormal , (int)x , (int)y , (int)width , (int)height , null); 
					// else draw the sprite which is of normal state and looking towards left
				}
			} else if(velX > 0 ) g.drawImage(meNormal[frame], (int)x, (int)y, (int)width, (int)height, null); 
			// if the player is moving towards right then draw the image in the array meNormal at the index of frame
			
			else if(velX < 0) g.drawImage(meNormal[frame+totalFrames], (int)x, (int)y, (int)width, (int)height, null);
			// else if the player is moving towards left then draw the image in the array meNormal at the index of frame + totalFrames as it has to skip one animation
		} else {
			if(velX == 0 && !jumping) { // if the player is not moving
				if(heading==0)  // and it is looking right
					g.drawImage(meRfire , (int)x , (int)y , (int)width , (int)height , null); 
				// then draw the sprite which is of fire state and looking towards right
				
				else g.drawImage(meLfire , (int)x , (int)y , (int)width , (int)height , null);  
				// else draw the sprite which is of fire state and looking towards left
			} else if(velX > 0 ) g.drawImage(meFire[frame], (int)x, (int)y, (int)width, (int)height, null);
			// if the player is moving towards right then draw the image in the array meFire at the index of frame
			
			else if(velX < 0) g.drawImage(meFire[frame+totalFrames], (int)x, (int)y, (int)width, (int)height, null);
			// else if the player is moving towards left then draw the image in the array meNormal at the index of frame + totalFrames as it has to skip one animation
		}
	}
	
	public void goRight() {
		if(velX < 10) velX += dt*0.5;
	}
	
	public void goLeft() {
		if(velX > -10) velX -= dt*0.5;
	}
	
	// method for stopping player
	public  void stopPlayer() {
		if(velX > 0)  {
			velX-= dt*0.5;
			if(velX < 0) velX = 0;
		}

		else if (velX < 0) {
			velX+= dt*0.5;
			if(velX > 0) velX = 0;
		}
		
	}
	
	// setter for velY
	public void setVelY(double v) {
		velY = v ;
	}
	
	// setter for velX
	public void setVelX(double v) {
		velX = v ;
	}
	
	// getter for x
	public double getX(){
		return x ;
	}
	
	// getter for y
	public double getY() {
		return y ;
	}

	// method to update the player class
	@Override
	public void tick(double dt) {
		this.dt = dt;
		if(fl != null) { // if the flag object has been initialized
			if(fl.animate && fl.flagY >= fl.y + fl.getHeight() * 3) { // if the flag was animating and has come sufficiently down
				fl.animate = false ; // because it has not been animated yet in the other level
				Tile t = (Tile) fl; // getting the tile for the level
				if(t.level==3) { // if the player clears the third level
					Thread endGameThread = new Thread(new Runnable() {
						@Override
						public void run() {
							game.endGame(0);
						}
					});
					endGameThread.start();
				}
				game.init(t.level + 1); // because the game will go the next level
			}
		}
		
		if(fireBalls == 0) {
			Fireball f = new Fireball((int)(getX() + getWidth()) , (int)(getY() + getHeight()/ 2 - 16) , 32 , 32, Id.fireball , game);
			game.handler.addCreature(f);
			f.init();
			
		}
		// if no fire balls exist, then create one
		
		if(velY != 0 && !inPipe()) jumping = true;
		// if the player is not stationary along the y axis and is not in a pipe then it is in air or jumping
		
		if(increasedHeight && !increasedAlready){
			height = 128 ;
			y -= 64 ;
			increasedAlready = true ;
		}
		// if increaseHeight is true but the player's height has not been increased, then increase it and set increasedAlready to true
		
		if(leftOrRightKeyReleased) stopPlayer();
		
		y += dt*velY ; // y to be increased according to velY
		x += dt*velX ; // x to be increased according to velX
		
		if(goRight && !goLeft) goRight();
		else if(goLeft && !goRight) goLeft();
		
		if(!inPipe()){ // if the player is not in a pipe
			velY += dt*gravity ; 
			if (velY > 20) velY = 20 ; // the max velY is 20 . Therefore if velY is more than 20, it should be set to 20
		}
		frameDelay++; // frameDelay to be increased by 1 as the frame has been delayed by 1/targetfps seconds 
		if(frameDelay >= 7) { // if frameDelay is more than or equal to 
			frame++; // then the frame of animation should be increased
			if(frame >= game.playerNormal.length - totalFrames) frame = 0 ; //  if the frame is more than or equal to the length of the 
			frameDelay = 0 ; // normal sprite array - totalFrames (both normal and fire state sprite arrays' lengths are going to be the same) 
							// then frame = 0 as no frame which is more than the length - totalFrames exists
			// also frameDelay is set to 0 as a frame has been rendered
		}
		
		for(int i = 0 ; i < game.handler.nPipes ; i++) { // loop through all the upper ends of the pipes			
			if(!isInPipeParts(i)) inPipes[i] = false; // if the player is going down
			// and it's bottom part touches the top part of the lower end of the pipe in the piD array in the handler at the index of i
			// then the player is not in a pipe anymore
			
			if(x - 24> game.handler.piU[i].getX() && x + width + 24< game.handler.piU[i].getX() + game.handler.piU[i].getWidth() && y < game.handler.piU[i].getY()) canGoDownPipes[i] = true;
			// if the player is in between the two ends of the upper end of a pipe in the array piU at the index of i in the Handler class
			// then the player can go down the pipe
			else canGoDownPipes[i] = false ; // else the player cannot go down the pipe
			
			if(x - 24 > game.handler.piD[i].getX() && x + width + 24< game.handler.piD[i].getX() + game.handler.piD[i].getWidth() && y >= game.handler.piD[i].getY() + game.handler.piD[i].getHeight()) {
				canGoUpPipes[i] = true;
			}
			// if the player is in between the two ends of the lower end of a pipe in the array piD at the index of i in the Handler class
			// and the player is below the pipe then the player can go up the pipe
			else canGoUpPipes[i] = false; // else the player cannot go up the pipe
			
			if(requestedPipeIndex >= 0) {
				boolean wentDownPipe = false;
				if(requestedPipeDirection && (getBoundsTop().intersects(game.handler.piD[i].getBoundsBottom()) || getBoundsTop().intersects(game.handler.piD[i].getBoundsInterior())) && velY < 0) {
					wentDownPipe = true;
				}
				else if(getBoundsBottom().intersects(game.handler.piU[i].getBoundsTop()) || getBoundsBottom().intersects(game.handler.piU[i].getBoundsInterior())) {
					velY = 20;
					wentDownPipe = true;
				}
				if(wentDownPipe) {
					inPipes[i] = true;
					jumping = false;
					canGoDownPipes[i] = false;
					canGoUpPipes[i] = false;
					requestedPipeIndex = -1;
					game.getSounds().playSound("/pipe_travel_power_down.wav");// play the sound of pipe travel
				}
			}
		}
		
		if(!inPipe()) {
			for(int i = 0 ; i < game.handler.walls.size() ; i ++) { // loop through every Tile in the linked list wall in the Handler class
				Tile t = game.handler.walls.get(i); // get the Tile in the linked list wall at the index of i in the Handler class
				if(t.getId()==Id.flag && getBounds().intersects(t.getBounds())) { // if the player collides with a tile and the tile has an ID of flag
					fl = (Flag) t ; // initialize the flag object
					fl.animate = true; // as we want to animate the flag
					game.flagBeingDropped = true;
					if(!soundPlayed) { // if the sound of flag going down has not been played
						game.getSounds().playSound("/flagpole.wav"); // then the sound of the flag going down 
						soundPlayed = true ; // and set soundPlayed to true as the sound of flag going down has already been played
					}
				}
				if(t.getId()!= Id.flag && (getBoundsTop().intersects(t.getBoundsBottom()) || (getBoundsTop().intersects(t.getBoundsInterior()))) && velY < 0) {
					// if the top part of the player touches the tile's bottom part or it touches it's interior part and if the tile does not have an ID of pipeDown(as such a tile has to react differently)
					y = t.getY() + t.getHeight(); // then put the player right below the tile
					velY = -velY ; // make player's velY negative (perfectly elastic collision)
					if(t.getId()==Id.mushroomBlock) {
						if(t instanceof MushroomBlock) {
							MushroomBlock mb = (MushroomBlock)(t);
							mb.poppedUp = true; // and if the tile has an ID of mushroomBlock then set the poppedUp variable in MushrooomBlock to true
						}
					}
				}
				if(t.getId()!= Id.flag &&(getBoundsBottom().intersects(t.getBoundsTop()) || getBoundsBottom().intersects(t.getBoundsInterior())) && velY > 0 && t.getId()!=Id.pipeDown) {
					// if the lower part of the player  touches the top part of a tile, the top part of the player does not touch the interior of the tile and tile does not have an ID of pipeDown(as such a tile has to react differently)
					y = t.getY() - getHeight(); // then put the player right above the tile
					velY = 0 ; // make player's velY 0
					jumping = false ; // as the player is not jumping anymore
				} 
				if(t.getId()!= Id.flag &&(getBoundsRight().intersects(t.getBoundsLeft()) || getBoundsRight().intersects(t.getBoundsInterior())) && velX > 0) {
					// if the right hand side part of the player touches the left hand side part of the tile or it touches the interior part of the tile and the player is going right then put the player to the left of the tile
					velX = 0; // then set the velX to 0
					x = t.getX() - getWidth(); // and put the player to the left of the tile
				}
				
				if(t.getId()!= Id.flag &&(getBoundsLeft().intersects(t.getBoundsRight()) || getBoundsLeft().intersects(t.getBoundsInterior())) && velX < 0) {
				// if the left hand side part of the player touches the right hand side part of the tile or it touches the interior part of the tile and the player is going left
					velX = 0; // then set the velX to 0
					x = t.getX() + t.getWidth() ; // and put the player to the right of the tile
				}
			}
			for(int k = 0 ; k < game.handler.creatures.size() ; k++) { // loop through every Creature in the linked list creature in the Handler class
				Creature c = game.handler.creatures.get(k); // get the Creature in the linked list creature at the index of k in the Handler class
				if(c.getId()==Id.goomba) { // if the Creature has an ID of goomba
					if(getBoundsBottom().intersects(c.getBoundsTop())) {
						game.handler.die(c); // if the bottom part of the player touches the top part of the creature then remove the creature from the linked list creature in the Handler class
						velY = -30 ; // then shoot it up into the sky
						game.getSounds().playSound("/stomp.wav"); // as the player has stamped the goomba enemy
						game.score += 100 ; // and increase the score too
					}
					else if(getBounds().intersects(c.getBounds()) && !blink) {// if the left hand side part of the player touches the right hand side of the creature or the right hand side of player touches the left hand side of the creature and the player is not blinking
						if(getHeight() == 128 && fireState){ // if the height of the player is 128 or has increased and is in the fire state
							fireState = false; // then the player can no more be in fire state
							game.getSounds().playSound("/pipe_travel_power_down.wav"); // play the power down sound
							blink = true; // and start blinking the player
						}
						else if(getHeight()==128 && !fireState) { // or if height of the player is 128 or has increased and is not in the fire state
							setHeight(64); // then the set the height of the player to 64 (back to original size)
							increasedHeight = false ; // set increasedHeight to false as the height is back to the original one again
							game.getSounds().playSound("/pipe_travel_power_down.wav");// play the power down sound
							blink = true; // and start blinking the player
						}
						else { // else
							Thread endGame = new Thread(new Runnable() {
								@Override
								public void run() {
									game.endGame(1);
								}
							});
							endGame.start();
						}
					}
				}
				if(c.getId()==Id.fireball) { // if the ID of the creature is fireball
					f = (Fireball) game.handler.creatures.get(k); // then initialize the Fireball object by getting the creature 
					fireBalls++; // and add one to the fire balls as the number of fire balls has been increased 
				} else fireBalls = 0; // since there are no fire balls in the linked list 
		    }
		}
	}


	//method to initialize the images for the player
	@Override
	public void init() {
		meRnormal = game.playerRightNormal.getBufferedImage(); // image contains player looking towards right and is in normal state
		meLnormal = game.playerLeftNormal.getBufferedImage(); // image contains player looking towards left and is in normal state
		for(int i = 0 ; i < game.playerNormal.length ; i++) meNormal[i] = game.playerNormal[i].getBufferedImage(); // array contains animation for normal state
		meRfire = game.playerRightFire.getBufferedImage(); // image contains player looking towards right and is in the fire state
		meLfire = game.playerLeftFire.getBufferedImage(); // image contains player looking towards left and is in the fire state
		for(int i = 0 ; i < game.playerFire.length ; i++) meFire[i] = game.playerFire[i].getBufferedImage(); // array contains animation for fire state
		for(int i = 0 ; i < game.inAirPlayer.length ; i++) inAir[i] = game.inAirPlayer[i].getBufferedImage();// array contains animation when the player is in the air
		canGoDownPipes = new boolean[game.handler.nPipes];
		canGoUpPipes = new boolean[game.handler.nPipes];
		inPipes = new boolean[game.handler.nPipes];
	}

	// getter for velY
	public double getVelY() {
		return velY;
	}
	
	public double getVelX() {
		return velX;
	}

	// setter for velY
	public void setY(int i) {
		this.y = i ;
	}

	// getter for width
	public int getWidth() {
		return width;
	}
	
	public boolean inPipe() {
		if(inPipes == null) return false;
		for(boolean a : inPipes) {
			if(a) return true;
		}
		return false;
	}
	
	public int canGoDownAPipe() {
		for(int i = 0; i < canGoDownPipes.length; i++) {
			if(canGoDownPipes[i]) return i;
		}
		return -1;
	}
	
	public int canGoUpAPipe() {
		for(int i = 0; i < canGoUpPipes.length; i++) {
			if(canGoUpPipes[i]) return i;
		}
		return -1;
	}
	
	public void requestToGoDownPipe(int i) {
		requestedPipeDirection = false;
		requestedPipeIndex = i;
	}
	
	public void requestToGoUpPipe(int i) {
		requestedPipeDirection = true;
		requestedPipeIndex = i;
	}
	
	private boolean isInPipeParts(int i) {
		ArrayList<Tile> pipePartsAtI = game.handler.pipeParts.get(i);
		for(Tile t : pipePartsAtI) {
			if(getBounds().intersects(t.getBounds())) return true;
		}
		return false;
	}
}
	
	
