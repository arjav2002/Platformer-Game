package com.arjav.platformergame.creatures;

import java.awt.Graphics;
import java.awt.Rectangle;

import com.arjav.platformergame.Game;
import com.arjav.platformergame.Id;

public abstract class Creature {

	// abstract method to draw or render using a graphics object
	public abstract void render(Graphics g);
	
	// abstract method to update 
	/*
	 * When implementing this method, the first 
	 * line must be :-
	 * this.dt = dt;
	 */
	public abstract void tick(double dt);
	
	// abstract method to initialize
	public abstract void init();
	
    protected double x, y;
	protected int width, height;
	protected Id id ;
	protected Game game;
	protected double dt;
		
	// x = x coordinate , y = y coordinate , width = width of creature , height = height of creature , id = id of creature , game = instance of game class for certain purposes
	
	// getter for ID
	public Id getId() {
		return this.id ;
	}
	
	// class constructor
	public Creature(int x , int y , int width , int height , Id id , Game game) {
		this.x = x ;
		this.y = y ;
		this.id = id ;
		this.width = width ;
		this.height = height ;
		this.game = game ;
	}
	
	// rectangles for collision detection
	
	// rectangle which covers the whole creature sprite itself
	public Rectangle getBounds() {

		return new Rectangle((int)(x) , (int)(y) , (int)(width) , (int)(height));
	}
	
	// rectangle which covers only the top part of the creature sprite
	public Rectangle getBoundsTop() {
		return new Rectangle((int)((x + 7)), (int)((y - 11)), (int)((width - 14))  ,(int)(11));
	}
	
	// rectangle which covers only the bottom part of the creature sprite
	public Rectangle getBoundsBottom() {
		return new Rectangle((int)((x + 3)), (int)((y + height - 4) ), (int)((width - 8)) , (int)(13));
	}
	
	// rectangle which covers only the right hand side part of the creature sprite
	public Rectangle getBoundsRight() {
		return new Rectangle((int)(x + width) , (int)((y + 5)) , (int)(5) , (int)((height - 10)));
	}
	
	// rectangle which covers only the left hand side part 
	public Rectangle getBoundsLeft() {
		return new Rectangle((int)(x - 5) , (int)((y + 3)) , (int)(5) , (int)((height - 9)));
	}

	// setter for height variable
	public void setHeight(int i) {
		height = i ;
		
	}

	// getter for height variable
	public int getHeight() {
		
		return height;
	}
	
	public int getWidth() {
		return width;
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
}
