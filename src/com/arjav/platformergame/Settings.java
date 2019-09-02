package com.arjav.platformergame;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;

import com.arjav.platformergame.utils.Sounds;

public class Settings implements ActionListener{
	
	private Container c; // container which contains the layout
	private JButton musicToggle, goBack; // buttons for toggling music and going back to main menu
	private JFrame frame ; // window
	private Main main;

	// class constructor
	public Settings(Main main) {
		this.main = main;
		frame = new JFrame("Mario v" + Game.version + " - Settings"); // creating a new window 
		c = frame.getContentPane(); // initialize the container object
		c.setLayout(new GridLayout(2 , 1)); // setting the layout of the container object to a new grid layout
		frame.setSize(new Dimension(720 , 360)); // setting the size of the window to 720 pixels by 360 pixels
		frame.setResizable(false); // window cannot be maximized or minimized
		frame.setVisible(true); // make the window visible
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // making sure that all the processes stop when the window is closed
		frame.setLocationRelativeTo(null);  // puts the window in the centre of the screen
		
		musicToggle = new JButton("Music Enabled");
		musicToggle.setPreferredSize(new Dimension(720 , 180)); // setting the preferred size
		goBack = new JButton("Back");
		goBack.setPreferredSize(new Dimension(720 , 180)); // of the buttons
		goBack.setActionCommand("goBack"); // setting the action commands
		musicToggle.setActionCommand("toggleMusic"); // of both the buttons
		c.add(musicToggle); // adding the buttons
		c.add(goBack); // to the container
		goBack.addActionListener(this); // adding the action listener
		musicToggle.addActionListener(this); // to the buttons
		
		frame.pack(); // consolidating the contents of the windows
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand()=="goBack") { // if the action command received is "goBack"
			frame.setVisible(false); // make the window invisible
			frame.dispose();
			main.frame.setVisible(true); // make the main menu window visible
		}
		if(e.getActionCommand()=="toggleMusic") { // if the action command received is "toggleMusic"
			if(!Sounds.canPlay())	{
				Sounds.canPlay = true;
				musicToggle.setText("Music not enabled"); // if canPlay variable in Assets is false , then set the text of musicToggle button to "Music not enabled"
			}
			else {
				Sounds.canPlay = false;
				musicToggle.setText("Music enabled"); // else set the text of musicToggle button to "Music enabled"
			}
		}
		
	}

	
	
}
