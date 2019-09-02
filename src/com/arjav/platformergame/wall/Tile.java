package com.arjav.platformergame.wall;

import java.awt.Graphics;
import java.awt.Rectangle;

import com.arjav.platformergame.Game;
import com.arjav.platformergame.Id;

public abstract class Tile {
	
	// abstract method to render or draw tile using a graphics object
	public abstract void render(Graphics g);
	
	// abstract method to initialize the sprite
	public abstract void init();
	
	// getter for width of the Tile
	public int getWidth() {
		return width ;
	}
	
	// x = x coordinate , y = y coordinate , width = width of the Tile , height = height of the tile , id = id of the tile , game = game object of the tile for initializing sprite
	
	protected int x;
	public int y;
	protected int width, height;
	public int level;
	protected Id id;
	protected Game game ;
	
	// class constructor
	public Tile(int x , int y , int width , int height , Id id , Game game, int level) {
		this.x = x ;
		this.y = y ;
		this.width = width ;
		this.height = height ;
		this.level = level ;
		this.game = game ;
		this.id = id ;
	}
	
	// getter for id
	public Id getId() {
		return id;
	}
	
	// getter for x
	public int getX() {
		return x ;
	}

	// getters for Rectangles for collision detection
	public Rectangle getBounds() {
		return new Rectangle(x , y , width , height);
	}
	
	public Rectangle getBoundsTop() {
		return new Rectangle(x + 7, y - 3 , width - 14, 10);
	}
	
	public Rectangle getBoundsBottom() {
		return new Rectangle(x + 7 , y + height - 3 , width - 14 , 5);
	}
	
	public Rectangle getBoundsRight() {
		return new Rectangle(x + width - 5 , y + 3 , 5 , height - 10);
	}
	
	public Rectangle getBoundsLeft() {
		return new Rectangle(x, y + 3 , 5 , height - 10);
	}
	
	public Rectangle getBoundsInterior() {
		return new Rectangle(x + 10 , y + 10 , width - 20 , height - 20);
	}
	
	// getter for y
	public int getY() {
		return y;
	}
	
	// getter for height
	public int getHeight() {
		return height;
	}
	
	
}
