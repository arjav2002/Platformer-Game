package com.arjav.platformergame;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedList ;

import com.arjav.platformergame.ai.AIPlayer;
import com.arjav.platformergame.creatures.Coin;
import com.arjav.platformergame.creatures.Creature;
import com.arjav.platformergame.creatures.Goomba;
import com.arjav.platformergame.creatures.Player;
import com.arjav.platformergame.wall.Block;
import com.arjav.platformergame.wall.Brick;
import com.arjav.platformergame.wall.Castle;
import com.arjav.platformergame.wall.Flag;
import com.arjav.platformergame.wall.Grass;
import com.arjav.platformergame.wall.InvisibleWall;
import com.arjav.platformergame.wall.Mud;
import com.arjav.platformergame.wall.MushroomBlock;
import com.arjav.platformergame.wall.PipeDown;
import com.arjav.platformergame.wall.PipeUp;
import com.arjav.platformergame.wall.Tile;

public class Handler {
	
	private Game game ; // instance of the Game class for use
	int level = 1; // the current level number
	public Flag f; // flag object for use by Game class
	public int nPipes; // number of pipes
	public PipeUp[] piU ; // array of upper ends of pipes
	public PipeDown[] piD; // array of lower ends of pipes
	private ArrayList<double[][]> allInputWeights;

	// getter for the player created
	public Player getPlayer() {
		return p;
	}
	
	// class constructor
	public Handler(Game game, ArrayList<double[][]> allInputWeights) {
		this.game = game ; // the Game object that it gets is used by all other classes
		this.allInputWeights = allInputWeights;
		
		creatures = new LinkedList<Creature>();
		walls = new LinkedList<Tile>();
		pipeParts = new ArrayList<ArrayList<Tile>>();
	}
	
	public LinkedList<Creature> creatures; // contains instances of all classes that extend the Creature class
	public LinkedList<Tile> walls; // contains instances of all classes that extend the Tile class
	public ArrayList<ArrayList<Tile>> pipeParts;
	
	private Player p ; // instance of player class used by other classes
	
	// method to add a class extending the Creature class to the linked list creature
	public void addCreature(Creature c) { // get the creature to be added
		creatures.add(c); // add it to the linked list creature
	}
	
	// method to clear a world of all of it's tiles and creatures
	public void clearWorld(){
		creatures.clear(); // clear the linked list creature
		walls.clear(); // clear the linked list wall
		pipeParts.clear();
	}
	
	// method to create a new level
	public ArrayList<Integer> createLevel(BufferedImage level) { // BufferedImage of the level the handler is going to add
		int width = level.getWidth(); // width of the image
		int height = level.getHeight(); // height of the image
		
		ArrayList<Integer> startAndEnd = new ArrayList<Integer>(); 
		
		for(int x = 0 ; x < width; x++ ){ // for loop to loop through all the pixels on the y axis 
			y_loop:
			for(int y = 0 ; y < height ; y++) { // for loop to loop through all the pixels on the x axis
				
				Color c = new Color(level.getRGB(x, y)); // to get the RGB (red , green , blue) value of the pixel with x,y coordinate
				
				int red = c.getRed(); // to get the red value of the pixel
				int green = c.getGreen(); // to get the green value of the pixel
				int blue = c.getBlue(); // to get the blue value of the pixel
				
				if(red==76 && green == 255 && blue == 0) { // if the red value of the pixel is 76 , green value of pixel is 255 and blue value of pixel is 0
					// then we add a new instance of the Grass class which extends the Tile class to the level
					Grass g = new Grass(x*64 , y*64 , 64 , 64 , Id.grass , game , this.level); 
					addTile(g);
				}
				
				if(red==127 && green == 0 && blue == 0 ) { // if the red value of the pixel is 127, green value of pixel is 0 and blue value of pixel is 0
					// then we add a new instance of the Mud class which extends the Tile class to the level
					addTile(new Mud(x*64 , y*64 , 64 , 64 , Id.mud , game , this.level));
					
				}
				if(red==0 && green == 148 && blue == 255) { // if the red value of the pixel is 0, green value of pixel is 148 and blue value of pixel is 255
					// then we add a new instance of the Player class which extends the Creature class to the level
					if(!game.isPlayingAI()) {
						p = new Player(x*64 , y*64 , 64 , 64 , game , Id.player);
						addCreature(p);
					}
					else {
						p = new AIPlayer(x*64, y*64, 64, 64, game, Id.player, allInputWeights);
						addCreature(p);
					}
					
				}
				if(red==255 && green==0 && blue==220) { // if the red value of the pixel is 255, green value of pixel is 0 and blue value of pixel is 220
					// then we add a new instance of MushroomBlock class which extends the Tile class to the level
					addTile(new MushroomBlock(x*64 , y*64 , 64 , 64 , Id.mushroomBlock , game , this.level));
					
				}
				if(red==128 && green == 128 && blue ==128) { // if the red value of the pixel is 128, green value of pixel is 128 and blue value of pixel is 128
					// then we add a new instance of the Goomba class which extends the Creature class to the level
					addCreature(new Goomba(x*64 , y*64 , 64 , 64 , game , Id.goomba));
				}
				if(red==255 && green == 0 && blue == 110) { // if the red value of the pixel is 255, green value of pixel is 0 and blue value of pixel is 110
					// then we add a new instance of the InvisibleWall class which extends the Tile class to the level
					addTile(new InvisibleWall(x*64 , y * 64 , 64 , 64 , Id.invisibleWall , game , this.level));
					for(Integer a : startAndEnd) {
						if(a == x*64) continue y_loop;
					}
					startAndEnd.add(x*64);
				}
				if(red==234 && green == 21 && blue == 123) { // if the red value of the pixel is 234 , green value of pixel is 21 and blue value of pixel is 123
					// then we create a new instance of the Flag class which extends the Tile class and add it to the level
					f = new Flag(x*64 , y*64 , 64 , 64 , Id.flag , game , this.level);
					addTile(f);
					this.level++;
				}
				if(red==0 && green==255 && blue==255) { // if the red value of the pixel is 0, green value of pixel is 255 and blue value of pixel is 255
					// then we add a new instance of the PipeUp class which extends the Tile class to the level
					addTile(new PipeUp(x*64 , y*64 , 128 , 128 , Id.pipeUp , game , this.level));
					pipeParts.add(new ArrayList<Tile>());
					pipeParts.get(nPipes).add(walls.get(walls.size() - 1));
					nPipes++;
				}	
				if(red==42 && green==213 && blue==42) { // if the red value of the pixel is 42, green value of pixel is 213 and blue value of pixel is 42
					// then we add a new instance of the PipeDown class which extends the Tile class to the level
					addTile(new PipeDown(x*64, y*64, 128, 128, Id.pipeDown, game, this.level));
					pipeParts.get(nPipes - 1).add(walls.get(walls.size() - 1));
				}
				if(red==50 && green==250 && blue==150) addTile(new Castle(x*64, y*64, 256, 256, Id.castle, game, this.level)); // if the red value of the pixel is 50, green value of pixel is 250 and blue value of pixel is 150
				// then we add a new instance of the Castle class which extends the Tile class to the level
				
				if(red==31 && green==34 && blue==42) addTile(new Brick(x*64, y*64, 64, 64, Id.brick, game, this.level)); // if the red value of the pixel is 31, green value of pixel is 34 and blue value of pixel is 42
				// then we add a new instance of the Brick class which extends the Tile class to the level
				
				if(red==32 && green==134 && blue==95) {
					addTile(new Block(x*64, y*64, 128, 128, Id.block, game, this.level)); // if the red value of the pixel is 32, green value of pixel is 134 and blue value of pixel is 95
					pipeParts.get(nPipes - 1).add(walls.get(walls.size() - 1));
				}
				// then we add a new instance of the Block class which extends the Tile class to the level
				if(red==21 && green==10 && blue==53) addCreature(new Coin(x * 64 , y * 64 , 64 , 64 , Id.coin , game)); // if the red value of the pixel is 21, green value of pixel is 10 and blue value of pixel is 53
				// then we add a new new instance of the Coin class which extends the Creature class to the level
			}
		}
		piU = new PipeUp[nPipes]; // initializing the piU array
		piD = new PipeDown[nPipes]; // initializing the piD array
		int i = 0 , m = 0; // i to keep track of number of instances of PipeDown class and m to keep track of number of instances of PipeUp class inside the index initialization loops
		for(int a = 0 ; a < walls.size() ; a++) {
			Tile t = walls.get(a); // getting the Tile object at the index of a in the linked list wall
			if(t.getId()==Id.pipeUp) { // if the object has the Id of pipeUp
				piU[m] = (PipeUp) t; // then initialize the space at index m in the piU array with the Tile object
				m++;
			}
			else if(t.getId()==Id.pipeDown) { // or if the object has the Id of pipeDown
				piD[i] = (PipeDown) t; // then initialize the space at index i in the piD array with the Tile object
				i++;
			}
		}
		return startAndEnd;
	}
	
	// method to remove an instance of a class that extends the Creature class from the linked list creature
	public void die(Creature c) { // instance to be removed
		creatures.remove(c); //remove the instance from the linked list creature
	}
	
	// method to initialize all the contents of the linked list creature
	public void initCreature(){
		for(int i = 0 ; i <  creatures.size() ; i ++) {
			Creature c = creatures.get(i); // get the contents of the linked list creature at the index i
			c.init(); // call the init method of the instance
		}
	}
	
	// method to initialize all the contents of the linked list wall
	public void initTile() {
		for(int i = 0 ; i < walls.size() ; i ++) {
			Tile t = walls.get(i); // get the contents of the linked list wall at index i
			t.init(); // call the init method of the instance
		}
	}
		
	// method to update all the contents of the linked list creature
	public void tickCreatures(double dt) {
		for(int i = 0 ; i <  creatures.size() ; i ++) {
			Creature c = creatures.get(i); // get the contents of the linked list creature at index i
			c.tick(dt); // call the tick method of the instance 
		}
	}
	
	// method to draw or render all the contents of the linked list creature
	public void renderCreatures(Graphics g) {
		for(int i = 0 ; i <  creatures.size() ; i ++) {
			Creature c = creatures.get(i); // get the contents of the linked list creature at index i
			c.render(g); // call the render method of the instance
		}
	}
	
	// method to add an instance of a class extending the Tile class to the linked list wall
	public void addTile(Tile t) { // get the instance
		walls.add(t); // add the instance to the linked list wall
	}
	
	// method to remove an instance of class extending the Tile class from the linked list wall
	public void removeTile(Tile t) { // get the instance
		walls.remove(t); // remove the instance from the linked list wall
	}
	
	// method to draw or render all the contents of the linked list wall
	public void renderTiles(Graphics g) {
		for(int i = 0 ; i <  walls.size() ; i ++) {
			Tile t = walls.get(i); // store the contents of linked list wall at the index i in a variable t
			t.render(g); // call the render method of the contents
		}
	}
}

