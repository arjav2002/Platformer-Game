package com.arjav.platformergame;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

import com.arjav.platformergame.ai.AIPlayer;
import com.arjav.platformergame.creatures.Player;
import com.arjav.platformergame.gfx.Sprite;
import com.arjav.platformergame.gfx.SpriteSheet;
import com.arjav.platformergame.input.KeyManager;
import com.arjav.platformergame.input.MyKeyListener;
import com.arjav.platformergame.utils.FrameTimer;
import com.arjav.platformergame.utils.Sounds;

public class Game implements Runnable {
	
	public static State st;
	private AIState aiState;
	private static boolean hideWindow = false;
	
	public static void hideWindow(Game game) {
		hideWindow = !hideWindow;
		if(SystemTray.isSupported()) {
			if(hideWindow) game.frame.setExtendedState(game.frame.getExtendedState() | JFrame.ICONIFIED);
			else game.frame.setExtendedState(game.frame.getExtendedState() & (~JFrame.ICONIFIED));
		}
	}
	
	public enum State {
		start, level, end;
	}
	
	public enum AIState {
		off, simulation, load
	}
	
	private class HUDString {
		private String text;
		private int x, y;
		private Color foregroundColor, backgroundColor;
		private Font font;
		
		private HUDString(String text, int x, int y, Color foregroundColor, Color backgroundColor, Font font) {
			this.text = text;
			this.x = x;
			this.y = y;
			this.foregroundColor = foregroundColor;
			this.backgroundColor = backgroundColor;
			this.font = font;
		}
	}
	
	public int addHUDString(String text, int x, int y, Color foregroundColor, Color backgroundColor, Font font) {
		huds.add(new HUDString(text, x, y, foregroundColor, backgroundColor, font));
		return huds.size()-1;
	}
	
	public void setHUDString(int ID, String text) {
		huds.get(ID).text = text;
	}

	private int scoreHUDID, fpsHUDID, timeHUDID;
    private Thread updateThread, renderThread; // the thread which will start or stop the game
    // updateThread handles level switches and ending the game
    private int fps ; // variable for frames per second
	public JFrame frame; // window
	public BufferedImage level1 , level2 , level3; // images of levels which contains the colour coding for player , wall , grass , etc.
	public Handler handler; // an instance of the handler class which handles all the rendering and updating
	public Sprite fireballRight, fireballLeft, coin, fireFlower, block, pipeDown, pipeUp, brick, playerRightNormal , playerLeftNormal , playerRightFire , playerLeftFire , grass , mud , goombaRight , goombaLeft , mushroomBlockNotPoppedUp , mushroomBlockPoppedUp , mushroom, castle;
	// various sprites in the game are instances of the Sprite class
	SpriteSheet sheet ; // image which contains all the sprites . This helps in efficient management of sprites by the developer
	int pipes = 0; // number of pipes a certain level has
	int frames = 0; // number of frames rendered
	private Canvas canvas ; // canvas on which graphics are rendered
	private MyKeyListener mkl; // instance of MyKeyListener class used for listening to the keyboard input
	private KeyManager km; // instance of key manager class used for processing keyboard input
	public Graphics g ; // graphics object to be used for rendering
	public static final String version = "3.0.0" ; // the version of the game
	public Sprite[] flag, playerNormal, playerFire, inAirPlayer; // sprite arrays of flag and the player. Used for animation in the player
	// and easier handling of Sprite flag
	private BufferStrategy bs ; // buffer strategy object used for implementing buffers for enhancing the gaming experience
	private ArrayList<HUDString> huds; // will hold information of Heads Up Display Text
	volatile boolean running = false ; // boolean variable to know if game has stopped running or not
	public int seconds = 0 , completionTime = 100 , score = 0; // number of seconds for which the game has been played. the completion time of level in seconds and the score
	boolean resultDisplayed = false; // to tell if the result has been displayed or not
	private int startX, endX;
	public int xTranslate, yTranslate;
	public static final int GRID_SIZE = 64;
	public static double AMOUNT_OF_TICKS = 60.0; // number of target frames per second
	public static final double DEFAULT_AMOUNT_OF_TICKS = 60.0;
	private boolean gameCompleted = false;
	private int currentLevel = 0;
	private boolean initialisedLevel = false;
	public static final int N_ENTITIES = Id.values().length;
	public static final int N_CREATURES = 5;
	public static final int N_WALLS = N_ENTITIES - N_CREATURES;
	private Sounds sounds;
	public boolean flagBeingDropped = false;
	
	private Runnable renderRunnable;
	
	public Sounds getSounds() {
		return sounds;
	}
	
	public boolean hasCompleted() {
		return gameCompleted;
	}
	
	public boolean readyForAction() {
		return initialisedLevel;
	}
	
	public int currentLevel() {
		return currentLevel;
	}
	
	public boolean isPlayingAI() { return aiState==AIState.load || aiState==AIState.simulation; }
	
	public int getScore() { return score; }
	
	public boolean isRunning() {  return running; }
	
	private void stopAndStartBackgroundSounds() {
		if(sounds.isBackgroundInitialised()) {
			sounds.stopBackground();
		}
		sounds.init();
		sounds.playBackground();
	}
	
	private void initAssets() {
		sheet = new SpriteSheet("/spritesheet.png"); // the sprite sheet is first initialized 
		grass = new Sprite(sheet , 3 , 0); // grass sprite initialized
		mud = new Sprite(sheet , 2 , 0); // mud sprite initialized
		mushroomBlockPoppedUp = new Sprite(sheet , 4 , 2); // mushroom block when not activated sprite initialized
		mushroomBlockNotPoppedUp = new Sprite(sheet , 3 , 2); // mushroom block when activated sprite initialized
		playerRightNormal = new Sprite(sheet , 0 , 0); // when player is looking towards right sprite initialized
		playerLeftNormal = new Sprite(sheet , 1 , 0); // when player is looking towards left sprite initialized
		playerRightFire = new Sprite(sheet , 6 , 0); // when player in fire mode is looking towards right sprite initialized
		playerLeftFire = new Sprite(sheet , 7 , 0); // when player in fire mode is looking towards left sprite initialized 
		goombaRight = new Sprite(sheet , 0 , 1); // when goomba enemy is looking towards right sprite initialized
		goombaLeft = new Sprite(sheet , 1 , 1); // when goomba enemy is looking towards left sprite initialized
		mushroom = new Sprite(sheet , 4 , 0); // mushroom sprite initialized
		castle = new Sprite(sheet, 5, 0); // castle sprite initialized
		brick = new Sprite(sheet, 2, 1); // brick sprite initialized
		pipeUp = new Sprite(sheet, 3, 1); // pipe's upper end sprite initialized
		pipeDown = new Sprite(sheet, 4, 1); // pipe's lower end sprite initialized
		block = new Sprite(sheet, 5 , 1); // pipe's connecting block sprite initialized
		fireFlower= new Sprite(sheet , 4 , 3); // fireFlower's sprite initialized
		coin = new Sprite(sheet, 5, 2); // coin's sprite initialized
		fireballRight = new Sprite(sheet, 8, 0); // fire ball going right sprite initialized
		fireballLeft = new Sprite(sheet, 9, 0); // fire ball going left sprite initialized
		try {
			level1 = ImageIO.read(getClass().getResource("/level1.png")); // trying to initialize all the levels
			level2 = ImageIO.read(getClass().getResource("/level2.png"));
			level3 = ImageIO.read(getClass().getResource("/level3.png"));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0); // if we cannot initialize the levels, the game will exit as there is no point in continuing further as it will only cause a bunch or errors
		}
		flag = new Sprite[3];
		for(int i = 0 ; i < flag.length ; i++) flag[i] = new Sprite(sheet , i , 2); // initializing flag sprite array
		
		playerNormal = new Sprite[4];
		for(int i = 0 ; i < playerNormal.length; i++) playerNormal[i] = new Sprite(sheet, i, 3); // initializing playerNormal sprite array
		
		playerFire = new Sprite[4];
		for(int i = 5 ; i < playerFire.length + 5 ; i++) playerFire[i - 5] = new Sprite(sheet , i , 3); // initializing playerFire sprite array
		
		// initializing inAirPlayer array
		inAirPlayer = new Sprite[4];
		for(int i = 0 ; i < inAirPlayer.length; i++) {
			if(i < 2) inAirPlayer[i] = new Sprite(sheet, 6, i+1);
			else inAirPlayer[i] = new Sprite(sheet, 7, i-1);
		}
	}
	
	/* method to switch the level when a level has been won*/	
	public void switchLevel(int number) { // number is the number of the level to which we have to switch
		handler.clearWorld(); // clearing the previous world
		handler.nPipes = 0 ; // making sure the number of pipes is 0 before creating a new level
		ArrayList<Integer> arr;
		switch(number) {
		case 1 :
			currentLevel = 1;
			arr = handler.createLevel(level1); // if we have to switch to one that is first level then we create the first level using the handler
			startX = arr.get(0);
			endX = arr.get(1);
			break;
		case 2 :
			currentLevel = 2;
			arr = handler.createLevel(level2); // if we have to switch to two that is second level then we create the second level using the handler
			seconds = 0 ; // seconds that have passed have to be made 0
			startX = arr.get(0);
			endX = arr.get(1);
			break;
		case 3 :
			currentLevel = 3;
			arr = handler.createLevel(level3); // if we have to switch to three that is third level then we create the third level using the handler
			seconds = 0; // seconds that have passed have to be made 0
			startX = arr.get(0);
			endX = arr.get(1);
			break ;
		}
	}
	
	public int getGridSize() {  return GRID_SIZE; }
	
	public int getGridWidth() { return frame.getWidth() / GRID_SIZE ; }
	
	public int getGridHeight() {  return frame.getHeight() / GRID_SIZE ; }
	
	/* method to initialize all the sprites and create the first level*/ 
	public void init(int level){
		initialisedLevel = false;
		flagBeingDropped = false;
		while(!initialisedLevel) {
			try {
				if(renderThread.isAlive()) {
					renderThread.join();
				}
				switch(level) {
				case 1 :
					switchLevel(1); // to switch levels
					st = State.level;
					break;
				case 2:
					switchLevel(2);
					st = State.level;
					break;
				case 3 :
					switchLevel(3);
					st = State.level;
				}
				stopAndStartBackgroundSounds();
				handler.initCreature(); // initializing all the classes extending the Creature class
				handler.initTile(); // initializing all the classes extending the Tile class
				
				if(!renderThread.isAlive()) {
					renderThread = new Thread(renderRunnable);
					renderThread.start();
				}
				initialisedLevel = true;
			} catch(Exception e) {
				e.printStackTrace();
				continue;
			}
		}
	}	
	
	/* public method so that all classes can access the key manager used for keyboard input*/
	public KeyManager getKeyManager() {
		return km; 
	}
	
	public static int WIDTH = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth(), HEIGHT = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
	
	public Game(String title) {
		this(title, AIState.off, null);
	}
	
	// class constructor
	public Game(String title, AIState aiState, ArrayList<double[][]> allInputWeights) {
		handler = new Handler(this, allInputWeights);
		km = new KeyManager(this);
		mkl = new MyKeyListener(this);
		sounds = new Sounds();
		this.aiState = aiState;
		
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		WIDTH = (int) dim.getWidth();
		HEIGHT = (int) dim.getHeight();
		frame = new JFrame(title); // creating a new window for the game with the title specified while calling the constructor
		frame.setSize(WIDTH , HEIGHT); // of size WIDTH pixels by HEIGHT pixels specified while calling the constructor
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // the processes of which stop the moment it is closed 
		frame.setResizable(false); // cannot be maximized or minimized
		frame.setLocationRelativeTo(null); // and is in the centre of the screen
		frame.setIconImage(new ImageIcon(getClass().getResource("/icon.png")).getImage()); //setting the icon of the window to a custom icon
		frame.setVisible(true); // which is visible to the use0r
		frame.setExtendedState(hideWindow? JFrame.ICONIFIED : ~JFrame.ICONIFIED);
		
		canvas = new Canvas(); // initializing canvas object
		canvas.setMaximumSize(new Dimension(WIDTH , HEIGHT)); // setting maximum
		canvas.setMinimumSize(new Dimension(WIDTH , HEIGHT)); // minimum
		canvas.setPreferredSize(new Dimension(WIDTH , HEIGHT)); // and preferred size of the canvas
		canvas.setFocusable(false); // it should not be focusable as it will not let the game function
		
		frame.addKeyListener(mkl); // an instance of the key manager class is added to the window for catching key inputs
		frame.add(canvas); // canvas object being added to the window
	
		frame.pack(); // making the JFrame of the smallest size possible
		
		renderRunnable = new Runnable() {

			@Override
			public void run() {
				long lastTime = System.nanoTime(); // getting time correct to nano seconds
		        double ns = 1000000000 / AMOUNT_OF_TICKS; // variable which contains 1 / target frames per second value
		        double delta = 0; // difference between two times

		        while(running && !hideWindow && readyForAction()){ // while the game is running, we want to -
		        	long now = System.nanoTime(); // get the time correct to nanoseconds 
		        	delta += (now - lastTime) / ns; // fancy calculation to get the time between last update and now in seconds
		        	lastTime = now; // lastTime will be equal to now for future 
		        	if(delta >= 1 && readyForAction()){ // if more than or equal to one / (target frames per second)  seconds of time has passed , we have to
		                render();
		                frames++;
		        		delta--; // since we have updated already we can decrease delta by one / (target frames per second) that is 1
		        	}
		        }
			}
		};
		renderThread = new Thread(renderRunnable);
		updateThread = new Thread(this);
		
		// memory leak
		huds = new ArrayList<HUDString>();
		fpsHUDID = addHUDString("FRAMES PER SECOND: " + fps, 50, 50, Color.WHITE, null, new Font("Serif", Font.BOLD, 12));
		timeHUDID = addHUDString("TIME: " + (completionTime - seconds), 210, 50, Color.WHITE, null, new Font("Serif", Font.BOLD, 12));
		scoreHUDID = addHUDString("SCORE: " + score, 275, 50, Color.WHITE, null, new Font("Serif", Font.BOLD, 12));
	}
	
	// method to start the game
	public synchronized void start() {
		if(running) return ; // if already running, then return because it will prevent errors
		running = true ; // setting running to true as game is about to start running
		initAssets();
		init(1); // initializing the first level
		
		updateThread.start(); // thread has been started which calls the run method of the class
	}
	
	public void run() {
        long lastTime = System.nanoTime(); // getting time correct to nano seconds
        double ns = 1000000000 / AMOUNT_OF_TICKS; // variable which contains 1 / target frames per second value (one cycle)
        double delta = 0; // difference between two times
        int updates = 0; // number of updates
        long timer = System.currentTimeMillis(); // helps in printing fps and ticks every second
        
        FrameTimer ft = new FrameTimer(AMOUNT_OF_TICKS);
        while(running){ // while the game is running, we want to -
        	long now = System.nanoTime(); // get the time correct to nanoseconds 
        	delta += (now - lastTime) / ns; // fancy calculation to get the time between last update and now 
        	// in 1 / target frames per second
        	lastTime = now; // lastTime will be equal to now for future 
        	if(delta >= 1 && readyForAction()){ // if more than or equal to one / (target frames per second)  seconds of time has passed , we have to
        		tick(ft.mark()); // update
        		updates++; // record it
               	delta--; // since we have updated already we can decrease delta by one / (target frames per second) that is 1
        	}
        		
        	if(System.currentTimeMillis() - timer > 1000){ // if one second has passed
       			timer += 1000;
       			fps = (frames < updates)? frames : updates;
       			System.out.println(updates + " Ticks, Fps " + frames); // print the updates and frames per seconds on the console
       			updates = 0; // updates 
       			frames = 0; // and frames to be made 0 for printing the correct set of frames and updates next time
       			if(!flagBeingDropped)seconds++; // since one second has passed and making sure that if the user has completed the level the 'clock' stops
       		}
        }
	}
	
	// method to stop the game
	private synchronized void stop() {
		if(!running) return ; // if the game is already not running we do not want to stop it again, hence return
		running = false ; // running to be made false as game will stop running
		try {
			updateThread.join();
			renderThread.join();
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	// update method called many times per second
	public void tick(double dt) {
		handler.tickCreatures(dt); // updating all the classes extending the Creature class
		km.tick(); // updating variables of the key manager for the player to use
		handler.f.tick();
		
		if(completionTime - seconds <= 0 && !resultDisplayed) {
			Thread endGame = new Thread(new Runnable() {
				@Override
				public void run() {
					endGame(2);
				}
			});
			endGame.start();
			return;
		}
		sounds.playBackground();
	}
	
	// getter for the player created in the handler
	public Player getPlayer() {
		return handler.getPlayer();
	}
	
	public int getLastXTranslate() { return lastXTranslate ; }
	public int getLastYTranslate() { return lastYTranslate ; }
	
	private int lastXTranslate = 0, lastYTranslate = 0;
	// method to draw or render onto the window
	public void render() {
		if(bs == null) canvas.createBufferStrategy(3); // if buffer strategy object does not contain anything, then we want the canvas to create a new bufferstrategy
		bs = canvas.getBufferStrategy(); // then we initialize the buffer strategy object with the buffer strategy created by the canvas
		g = bs.getDrawGraphics(); // graphics object being initialized
		
		g.clearRect(0, 0, 3000, 3000); // clearing the window of all the previous drawings
		
		g.setColor(Color.BLUE); // setting the colour of the graphics object to blue
		g.fillRect(0, 0, Game.WIDTH,  Game.HEIGHT); // background which follows the player
		
		if(!getPlayer().inPipe()) {			
			int someConstant = (int)(Game.HEIGHT/2*0.8);
			if(getPlayer().getY() >= Game.HEIGHT + someConstant) yTranslate = -someConstant - Game.HEIGHT;
			else yTranslate = -someConstant;
		} else yTranslate = lastYTranslate; // freeze the translation if in pipe
		if(getPlayer().getX() - startX <= 200) { // again 200 is a random number that looks alright
			xTranslate = 0;
		}
		else if(endX - getPlayer().getX() <= frame.getWidth() - 200) {
			xTranslate = lastXTranslate;
		}
		else xTranslate = (int)-handler.getPlayer().getX() + 200;
		g.translate(xTranslate, yTranslate); // it will always position the camera onto player such that it is 200 pixels away from it's left side
		lastXTranslate = xTranslate;
		lastYTranslate = yTranslate;
		
		// all classes extending the Creature class are drawn or rendered
		handler.renderCreatures(g); 
		// all classes extending the Tile class are drawn or rendered
		handler.renderTiles(g);

		g.translate(-xTranslate, -yTranslate);
		
		setHUDString(scoreHUDID, "SCORE: " + score);
		setHUDString(timeHUDID, "TIME: " + (completionTime - seconds));
		setHUDString(fpsHUDID, "FRAMES PER SECOND: " + fps);

		for(HUDString hudString : huds) {
			g.setFont(hudString.font);
			if(hudString.backgroundColor != null) {
				FontMetrics fm = g.getFontMetrics();
				Rectangle2D rect = fm.getStringBounds(hudString.text, g);
				g.setColor(hudString.backgroundColor);
				g.fillRect(hudString.x, hudString.y-fm.getAscent(), (int)rect.getWidth(), (int)rect.getHeight());
			}
			g.setColor(hudString.foregroundColor);
			g.drawString(hudString.text, hudString.x, hudString.y);
		}
		if(isPlayingAI() && handler.getPlayer() instanceof AIPlayer) ((AIPlayer)(handler.getPlayer())).displayNeuralNetwork(g);
		
		bs.show(); 
		g.dispose();
	}
	
	public void endGame(int params) { // 0 -> player wins game    1 -> death by goomba     2 -> time over
		if(params == 0) gameCompleted = true;
		stop(); // stop the game
		sounds.stopBackground();
		frame.setVisible(false); // make the game window disappear
		frame.dispose(); // destroy the frame
		st = State.end;
		if(aiState != AIState.simulation) {
			new DialogueBox(params, g , this);
			resultDisplayed = true ; // since the result has been printed
		}
	}
	
}
