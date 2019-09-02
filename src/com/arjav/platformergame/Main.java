package com.arjav.platformergame;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;


public class Main implements ActionListener {
	
	//class for the main menu of the game
	
	private JButton start , settings ; // Buttons for starting the game or opening the settings window
	JFrame frame ; // Main menu window
	private Container c ; // layout container which contains both the buttons
	
	public Main() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e1) {
			e1.printStackTrace();
		}
		BufferedImage startImage = null , settingsImage = null; // these images are of buttons
		try {
			startImage = ImageIO.read(getClass().getResource("/Buttons/Enabled/start_button_enabled.png")); // putting png images 
			settingsImage = ImageIO.read(getClass().getResource("/Buttons/Enabled/settings_button_enabled.png")); // on the buttons
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		frame = new JFrame("Mario v" + Game.version); // initializes the window with the title Mario and it's version
		frame.setResizable(false); // window cannot be maximized or minimized
		frame.setSize(new Dimension(720  , 360)); // setting the size of the window to 720 pixels by 360 pixels
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //  so that all the processes inside the main menu stop when it is closed
		frame.setLocationRelativeTo(null); // puts the window in the center
		frame.setVisible(true); // makes the window visible to the user
		frame.setIconImage(new ImageIcon(getClass().getResource("/icon.png")).getImage()); //setting the icon of the window to a custom icon
		
		c = frame.getContentPane(); // initializing the container variable 
		c.setLayout(new GridLayout(2,1)); // setting layout of the container as a grid layout with 2 rows and 1 column
		
		start = new JButton(); // initializing both
		settings = new JButton(); // the buttons
		
		start.setPreferredSize(new Dimension(720 , 180)); // setting the size of both the buttons to
		settings.setPreferredSize(new Dimension(720 , 180)); // 720 pixels by 180 pixels
		
		start.addActionListener(this); // adding this class as the a class 
		settings.addActionListener(this); // that implements action listener which will make the buttons respond when clicked
		
		start.setActionCommand("start"); // command code that the buttons
		settings.setActionCommand("settings"); // will send via an action event when clicked
		
		start.setIcon(new ImageIcon(startImage)); // setting the icon of both the buttons
		settings.setIcon(new ImageIcon(settingsImage)); // to the images designed for them in paint
		
		c.add(start); // buttons added to the
		c.add(settings); // container
		
		frame.pack(); // packs everything into the smallest size possible
	}

	public static void main(String[] args) {
		new Main();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("start")) {  // if the action command is "start" , then we will want to start the game 
			frame.setVisible(false); // by making the main menu invisible and making a new instance of the AIOrPlay class
            new AIMenu("Mario v" + Game.version);
			
		}
		if(e.getActionCommand().equals("settings")){ // if the action command is "settings" 
			frame.setVisible(false); //then we will want to close main menu and 
			new Settings(this); // create a new settings window
		}
		frame.dispose();
	}
}
