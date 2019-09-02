package com.arjav.platformergame.ai;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;

import com.arjav.platformergame.Game;
import com.arjav.platformergame.Handler;
import com.arjav.platformergame.Id;
import com.arjav.platformergame.creatures.Creature;
import com.arjav.platformergame.utils.AIUtils;
import com.arjav.platformergame.wall.Tile;

public class NeuralNetwork {

	// By Neuron, we mean a double variable
	// By Layer, we mean a single dimensional array of neurons
	
	private Handler handler;
	private double[] mappedTileInputLayer; // each Neuron in this is connected to a single mapped tile
	private ArrayList<double[]> hiddenLayers; // layers other than the ones mapped to the keyboard and the tiles
	private double[] keyboardOutputLayer;
	private double[] lastKeyboardOutputLayer;
	private ArrayList<double[][]> allInputWeights;
	
	//processing variables and constants
	private Rectangle entityRect, screenRect;
	private final int N_KEYS_TO_PRESS;
	private final int N_VISIBLE_GRIDS_HORIZONTAL, N_VISIBLE_GRIDS_VERTICAL;
	private int[] keyCodes;
	private String[] keyNames;
	private Game game;
	
	private static final double STIMULATION_CONSTANT = Math.pow(10, 2);
	
	private HashMap<Id, Integer> offsets;

	/* THE BASIC WORKING
	 * Mapped Tile -> Creature Neuron and Tile Neuron -> Hidden Layers -> KeyboardOutputLayer -> fireKeyboard() called by AIPlayer
	 * */
	
	// Constructor to load from raw input weight data
	public NeuralNetwork(Game game, int N_VISIBLE_GRIDS_HORIZONTAL, int N_VISIBLE_GRIDS_VERTICAL, ArrayList<double[][]> allInputWeights, int N_KEYS_TO_PRESS, int[] keyCodes, String[] keyNames) {
		this.N_VISIBLE_GRIDS_HORIZONTAL = N_VISIBLE_GRIDS_HORIZONTAL;
		this.N_VISIBLE_GRIDS_VERTICAL = N_VISIBLE_GRIDS_VERTICAL;
		this.N_KEYS_TO_PRESS = N_KEYS_TO_PRESS;
		this.keyCodes = keyCodes;
		this.keyNames = keyNames;
		this.game = game;
		this.handler = game.handler;
		
		// memory leak here
		mappedTileInputLayer = new double[AIPlayer.N_INPUT_NEURONS];
		keyboardOutputLayer = new double[N_KEYS_TO_PRESS];
		lastKeyboardOutputLayer = new double[N_KEYS_TO_PRESS];
		hiddenLayers = new ArrayList<double[]>();
		
		this.allInputWeights = allInputWeights;
		
		for(int i = 0; i < lastKeyboardOutputLayer.length; i++) lastKeyboardOutputLayer[i] = 0.0;
		
		// initialising hidden layers
		for(int i = 0; i < allInputWeights.size() - 1; i++) {
			hiddenLayers.add(new double[allInputWeights.get(i).length]);
		}
		
		offsets = new HashMap<Id, Integer>();
		int i = 0;
		for(Id id : Id.values()) offsets.put(id, i++);
		
		screenRect = new Rectangle();
		entityRect = new Rectangle();
	}
	
	// sets each mapped tile to "creature" state or "tile" state
	public void initMappedTilesAndFireNeuralNetwork() {
		int lastTranslateX = game.getLastXTranslate();
		int lastTranslateY = game.getLastYTranslate();
		
		// coordinate bounds for the area of input from the screen
		// The AI will see anything from this rectangle
		screenRect.x = -lastTranslateX;
		screenRect.y = -lastTranslateY;
		screenRect.width = game.getGridSize() * N_VISIBLE_GRIDS_HORIZONTAL;
		screenRect.height = game.getGridSize() * N_VISIBLE_GRIDS_VERTICAL;
		
		//referesh mappedTileInputLayer
		for(int i = 0; i < mappedTileInputLayer.length; i++) {
			mappedTileInputLayer[i] = 0.0;
		}
		
		// goes through all the walls in the game
		for(int i = 0; i < handler.walls.size(); i++) {
			Tile t = handler.walls.get(i);
			entityRect.x = t.getX();
			entityRect.y = t.getY();
			entityRect.width = t.getWidth();
			entityRect.height = t.getHeight();
			
			// only processing the walls in the display area
			if(AIUtils.isRectInsideRect(entityRect, screenRect)) {
				int x = (entityRect.x - screenRect.x) /game.getGridSize();
				int y = (entityRect.y - screenRect.y) /game.getGridSize();
				mappedTileInputLayer[(y*N_VISIBLE_GRIDS_HORIZONTAL + x) * Game.N_ENTITIES + offsets.get(t.getId())] = STIMULATION_CONSTANT;
			}
		}
		
		for(int i = 0; i < handler.creatures.size(); i++) {
			Creature c = handler.creatures.get(i);
			entityRect.x = (int) c.getX();
			entityRect.y = (int) c.getY();
			entityRect.width = c.getWidth();
			entityRect.height = c.getHeight();
			
			if(AIUtils.isRectInsideRect(entityRect, screenRect)) {
				int x = (entityRect.x - screenRect.x) /game.getGridSize();
				int y = (entityRect.y - screenRect.y) /game.getGridSize();
				mappedTileInputLayer[(y*N_VISIBLE_GRIDS_HORIZONTAL + x)*Game.N_ENTITIES + offsets.get(c.getId())] = STIMULATION_CONSTANT;
			}
			
		}
	}

	public void triggerHiddenLayers() {
		if(hiddenLayers.size() == 0) {
			AIUtils.computeNeurons(allInputWeights.get(0), mappedTileInputLayer, keyboardOutputLayer);
		}
		else {
			for(int i = 0; i < hiddenLayers.size(); i++) {
				AIUtils.computeNeurons(allInputWeights.get(i), (i==0? mappedTileInputLayer:hiddenLayers.get(i-1)), hiddenLayers.get(i));
			}
			AIUtils.computeNeurons(allInputWeights.get(hiddenLayers.size()), hiddenLayers.get(hiddenLayers.size()-1), keyboardOutputLayer);
		}
	}

	// called by AIPlayer
	public void fireKeyboard() {
		fireKey(0, keyCodes[0]);
		fireKey(3, keyCodes[3]);
		fireKey(1, keyCodes[1]);
		fireKey(2, keyCodes[2]);
		fireKey(4, keyCodes[4]);

	}
	
	// fires keys based on the activation of the keyboardOutputLayerNeurons
	private void fireKey(int layerIndex, int keycode) {
		if(keyboardOutputLayer[layerIndex] >= 0.5) game.getKeyManager().keyPress(keycode);
		else if(lastKeyboardOutputLayer[layerIndex] >= 0.5) game.getKeyManager().keyRelease(keycode);
		
		for(int i = 0; i < N_KEYS_TO_PRESS; i++) lastKeyboardOutputLayer[i] = keyboardOutputLayer[i];
	}
	
	private void renderKeys(Graphics g) {
		int startGridX = Game.WIDTH/game.getGridSize() - 2, startGridY = 1;
		for(int i = 0; i < N_KEYS_TO_PRESS; i++) {
			g.setColor(Color.BLACK);
			g.drawString("" + keyboardOutputLayer[i], (startGridX-2) * game.getGridSize(), startGridY * game.getGridSize());
			g.setColor((keyboardOutputLayer[i]>=0.5? Color.BLACK : Color.WHITE));
			g.fillRect(startGridX * game.getGridSize(), startGridY * game.getGridSize(), game.getGridSize(), game.getGridSize());
			g.setColor(Color.BLACK);
			g.drawString(keyNames[i], startGridX * game.getGridSize(), startGridY * game.getGridSize());
			startGridY += 2;
		}
	}
	
/*	private void renderTiles(Graphics g) {
		int startX = Game.WIDTH - game.getGridSize()*5, startY = game.getGridSize()*2;
		int entityGridSize = game.getGridSize()/5;
		for(int y = 0; y < N_VISIBLE_GRIDS_VERTICAL; y++) {
			for(int x = 0; x < N_VISIBLE_GRIDS_HORIZONTAL; x++) {
				int oneDCoord = (y*N_VISIBLE_GRIDS_HORIZONTAL + x) * 2;
				if((int)mappedTileInputLayer[oneDCoord] != 0) g.setColor(Color.WHITE);
				else if((int)mappedTileInputLayer[oneDCoord + 1] != 0) g.setColor(Color.BLACK);
				else g.setColor(Color.RED);
				g.fillRect(startX + x*entityGridSize, startY + y*entityGridSize, entityGridSize, entityGridSize);
			}
		}
		
	}
	*/

	public void draw(Graphics g) {
		renderKeys(g);
	//	renderTiles(g);
	}
	
	public void writeNetworkToFile(String logFile) {
		new AIUtils().writeNetworkToFile(logFile, allInputWeights);
	}
	
	public ArrayList<double[][]> getAllInputWeights() {
		return allInputWeights;
	}
}
