package com.arjav.platformergame.ai;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import com.arjav.platformergame.Game;
import com.arjav.platformergame.Id;
import com.arjav.platformergame.creatures.Player;

public class AIPlayer extends Player {
	
	/*
	 * Game scene will be read grid by grid, and will be stored in a 2D array which will be updated every cycle
	 * Every tile has two mappings to it, one for creature, other for tile
	 *
	 * */
	
	private NeuralNetwork neuralNet;
	
	//processing variables and constants
	public static final int N_VISIBLE_GRIDS_HORIZONTAL = Game.WIDTH/Game.GRID_SIZE, N_VISIBLE_GRIDS_VERTICAL = Game.HEIGHT/Game.GRID_SIZE;
	public static final int N_KEYS_TO_PRESS = 5;
	public final static int N_INPUT_NEURONS = N_VISIBLE_GRIDS_HORIZONTAL*N_VISIBLE_GRIDS_VERTICAL*Game.N_ENTITIES;
	private static final int[] keyCodes = {KeyEvent.VK_LEFT, KeyEvent.VK_DOWN, KeyEvent.VK_UP, KeyEvent.VK_RIGHT, KeyEvent.VK_Z};
	private static final String[] keyNames = {"LEFT", "DOWN", "UP", "RIGHT", "Z"};
	
	public AIPlayer(int x, int y, int width, int height, Game game, Id id, ArrayList<double[][]> allInputWeights) {
		super(x, y, width, height, game, id);
		neuralNet = new NeuralNetwork(game, N_VISIBLE_GRIDS_HORIZONTAL, N_VISIBLE_GRIDS_VERTICAL, allInputWeights, N_KEYS_TO_PRESS, keyCodes, keyNames);
		
	}
	
	@Override
	public void tick(double dt) {
		if(game.readyForAction()) playGame();
		super.tick(dt);
	}
	
	private void playGame() { // method in which AI looks at tiles and presses keys through its brain
		neuralNet.initMappedTilesAndFireNeuralNetwork();
		neuralNet.triggerHiddenLayers();
		neuralNet.fireKeyboard();
	}
	
	public void displayNeuralNetwork(Graphics g) {
		neuralNet.draw(g);
	}
	
	public ArrayList<double[][]> getAllInputWeights() {
		return neuralNet.getAllInputWeights();
	}

}
