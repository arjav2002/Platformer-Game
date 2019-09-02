package com.arjav.platformergame;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

public class DialogueBox extends JFrame{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	boolean resultDisplayed = false ; // win to see if the player won or lost and resultDisplayed is used if the code has already displayed the result
	Font font ; // font object 
	Game game;
	int win ;
	
	public DialogueBox(int win , Graphics g ,Game game) {
		this.win = win ; 
		setSize(300 , 300); // set the size of the window to 200 pixels by 200 pixels
		setTitle("Mario v" + Game.version); // set the title of the window
		setLocationRelativeTo(null); // put it into the center of the screen
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // to make sure all processes stop when the window is closed
		setResizable(false); // window cannot be maximized or minimized
		font = new Font("Serif" , Font.PLAIN , 50); // setting the font size to 50
		setIconImage(new ImageIcon(getClass().getResource("/icon.png")).getImage()); //setting the icon of the window to a custom icon
		this.game = game;
		setVisible(true); // make the window visible
	}
	
	public void paint(Graphics g) {
		if(resultDisplayed) return; // if the result was displayed then return from here
		else { // else the result was not displayed so
			switch(win) { // if the player won
			case 0 :
				g.setColor(Color.RED); // set the colour of the graphics object to red
				g.fillRect(0, 0, getWidth(), getHeight()); // create a filled rectangle of the size of the window
				g.setColor(Color.BLACK); // set the colour of the graphics object to black
				g.setFont(new Font("Serif" , Font.PLAIN , 50)); // set the font of the graphics object to the font object
				g.drawString("You win" , 15, 100); // draw the message
				g.drawString("Your score :" , 15 , 150);
				g.drawString("" + game.score , 15 , 200);
				setLocationRelativeTo(null); // put the window in the center of the screen
				game.getSounds().playSound("/stage_complete.wav");	// play the game completed sound
				resultDisplayed = true ; // set result displayed to true as result has been displayed
				break;
			case 1 :
				g.setColor(Color.RED); // set the colour of the graphics object to red
				g.fillRect(0, 0, getWidth(), getHeight()); // create a filled rectangle of the size of the window
				g.setFont(new Font("Serif" , Font.PLAIN , 50)); // set the font of the graphics object to the font object
				g.setColor(Color.BLACK); // set the colour of graphics object to black
				g.drawString("You lose" , 15, 100); // draw the message
				g.drawString("Your score :" , 15 , 150);
				g.drawString("" + game.score , 15 , 200);
				setLocationRelativeTo(null); // put the window in the centre of the screen
				game.getSounds().playSound("/game_over.wav"); // play the game over sound
				resultDisplayed = true ; // set the result displayed to true as result has been displayed
				break;
			case 2 :
				g.clearRect(0, 0, getWidth(), getHeight()); // to clear the window of everything 
				g.setColor(Color.RED); // set the colour of the graphics object to red
				g.fillRect(0, 0, getWidth(), getHeight()); // create a filled rectangle of the size of the window
				g.setFont(new Font("Serif" , Font.PLAIN , 50)); // set the font of the graphics object to the font object
				g.setColor(Color.BLACK); // set the colour of graphics object to black
				g.drawString("You ran " , 15, 100); // draw the message
				g.drawString("out of time!", 15, 150);
				g.drawString("Your score :" , 15 , 200);
				g.drawString("" + game.score , 15 , 250);
				setLocationRelativeTo(null); // put the window in the centre of the screen
				game.getSounds().playSound("/game_over.wav"); // play the game over sound
				resultDisplayed = true ; // set the result displayed to true as result has been displayed
				break;
			}
		}
	}	
}
