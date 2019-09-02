package com.arjav.platformergame;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Random;

import com.arjav.platformergame.Game.AIState;
import com.arjav.platformergame.ai.AIPlayer;
import com.arjav.platformergame.utils.AIUtils;

public class AISimulationManager implements Runnable{
	
	private String title, logFile;
	private int currentGenMaxFitness, lastGenMaxFitness;
	private int currentGen, currentSpecies;
	private ArrayList<double[][]> lastGenBestInputWeights, currentGenBestInputWeights;
	
	private final static int MIN_SPECIES_PER_GEN = 20, MAX_SPECIES_PER_GEN = 200;
	private final static int ACCEPTABLE_NO_PROGRESS_TIME_MILLIS = 3000;
	public final static int LEVEL_PASS_BONUS = 1000;
	public final static int GAME_PASS_BONUS = 3000;
	
	private static long last = 0;
	
	private Font genFont, speciesFont, fitnessFont;
	
	public AISimulationManager(String title, String logFile) {
		this.title = title;
		this.logFile = logFile;
		currentGenMaxFitness = lastGenMaxFitness = 0;
		
		genFont = speciesFont = fitnessFont = new Font("Serif", Font.BOLD, 12);
		
		currentGen = currentSpecies = 0;
		lastGenBestInputWeights = new ArrayList<double[][]>();
		lastGenBestInputWeights.add(new double[1000][AIPlayer.N_INPUT_NEURONS]);
		lastGenBestInputWeights.add(new double[500][1000]);
		lastGenBestInputWeights.add(new double[100][500]);
		lastGenBestInputWeights.add(new double[50][100]);
		lastGenBestInputWeights.add(new double[10][50]);
		lastGenBestInputWeights.add(new double[AIPlayer.N_KEYS_TO_PRESS][10]);
		Random random = new Random(System.nanoTime());
		for(double[][] layer : lastGenBestInputWeights) {
			for(int i = 0; i < layer.length; i++) {
				for(int j = 0; j < layer[0].length; j++) {
					layer[i][j] = (random.nextDouble() - 0.5) * 2 * 5; // generates weights of +-5.0
				}
			}
		}
		currentGenBestInputWeights = new ArrayList<double[][]>();
		currentGenBestInputWeights.add(new double[1000][AIPlayer.N_INPUT_NEURONS]);
		currentGenBestInputWeights.add(new double[500][1000]);
		currentGenBestInputWeights.add(new double[100][500]);
		currentGenBestInputWeights.add(new double[50][100]);
		currentGenBestInputWeights.add(new double[10][50]);
		currentGenBestInputWeights.add(new double[AIPlayer.N_KEYS_TO_PRESS][10]);
		int k = 0;
		for(double[][] layer : lastGenBestInputWeights) {
			for(int i = 0; i < layer.length; i++) {
				for(int j = 0; j < layer[0].length; j++) {
					layer[i][j] = lastGenBestInputWeights.get(k)[i][j];
				}
			}
			k++;
		}
	}
	
	@Override
	public void run() {
		startSim();
	}
	
	public void startSim() {
		last = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		while(true) {
			currentGen++;
			boolean improvementMade = false;
			for(int i = 0; (i < MIN_SPECIES_PER_GEN || !improvementMade) && i < MAX_SPECIES_PER_GEN; i++) {
				currentSpecies++;
				currentGenBestInputWeights = startAndFinishGame(evolveInputWeights(lastGenBestInputWeights));
				if(currentGenMaxFitness > lastGenMaxFitness) improvementMade = true;
				System.gc();
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				printStats();
			}
			lastGenMaxFitness = currentGenMaxFitness;
			lastGenBestInputWeights = currentGenBestInputWeights;
			//logNetwork();
			System.out.println("Highest fitness in this generation: " + currentGenMaxFitness);
			currentSpecies = 0;
		}
	}
	
	private ArrayList<double[][]> startAndFinishGame(ArrayList<double[][]> inputWeights) {
		Game game = new Game(title, AIState.simulation, inputWeights);
		game.addHUDString("Generation: " + currentGen, 50, 200, Color.white, null, genFont);
		game.addHUDString("Species: " + currentSpecies, 50, 250, Color.white, null, speciesFont);
		Game.st = Game.State.start;
		game.start();
		while(!game.readyForAction());
		int lastFitness = AIUtils.getFitness(game);
		int maxFitnessAchievedAtLevel = lastFitness;
		int fitnessHUDID = game.addHUDString("Fitness: " + lastFitness, 50, 300, Color.white, null, fitnessFont);
		long lastTime = 0;
		boolean flag = false;
		
		int[] fitnessAtLevel = {0, 0, 0};
		int lastLevel = 1;
		while(game.isRunning()) {
			if(game.readyForAction() && !game.flagBeingDropped) {
				fitnessAtLevel[game.currentLevel()-1] = AIUtils.getFitnessAtCurrentLevel(game);
				if(game.currentLevel() != lastLevel) {
					lastLevel = game.currentLevel();
					maxFitnessAchievedAtLevel = fitnessAtLevel[game.currentLevel()-1];
				}
				if(fitnessAtLevel[game.currentLevel()-1] > maxFitnessAchievedAtLevel) maxFitnessAchievedAtLevel = fitnessAtLevel[game.currentLevel()-1];
				int fitness = fitnessAtLevel[0] + fitnessAtLevel[1] + fitnessAtLevel[2];
				game.setHUDString(fitnessHUDID, "Fitness: " + fitness);
				if(fitnessAtLevel[game.currentLevel()-1] < maxFitnessAchievedAtLevel || fitness <= lastFitness) {
					if(flag) {
						if((System.currentTimeMillis() - lastTime >= ACCEPTABLE_NO_PROGRESS_TIME_MILLIS / Game.AMOUNT_OF_TICKS * Game.DEFAULT_AMOUNT_OF_TICKS)) {
							game.endGame(2); // timeout
						}
					} else {
						flag = true;
						lastTime = System.currentTimeMillis();
					}
					
				} else flag = false;
				lastFitness = fitness;
			}
		}
		int thisGamesFitness = AIUtils.getFitness(game);
		System.out.println(fitnessAtLevel[0] + " " + fitnessAtLevel[1] + " " + fitnessAtLevel[2]);
		if(thisGamesFitness > currentGenMaxFitness) {
			currentGenMaxFitness = thisGamesFitness;
			return inputWeights;
		}
		return currentGenBestInputWeights;
	}
	
	private ArrayList<double[][]> evolveInputWeights(ArrayList<double[][]> inputWeights) {
		for(double[][] layerInputWeights : inputWeights) {
			Random random = new Random(System.nanoTime());
			int neuronIndex = (int)(random.nextInt(layerInputWeights.length));
			int weightIndex = (int)(random.nextInt(layerInputWeights[0].length));
			// layerInputWeights[0] because number of inputs is same for all neurons in a layer
			layerInputWeights[neuronIndex][weightIndex] = (random.nextDouble() - 0.5) * 2 * 5; // generates weights other than numbers between -5.0 and 5.0
		}
		return inputWeights;
	}
	
	private void logNetwork() {
		String fileName = logFile.substring(0, logFile.length()-3) + currentGen + logFile.substring(logFile.length()-3, logFile.length());
		System.out.println("Logging");
		new AIUtils().writeNetworkToFile(fileName, currentGenBestInputWeights);
		System.gc();
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("Logged");
		
	}
	
	private void printStats() {
		int mb = 1024 * 1024;
		long memInUse = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		if(memInUse > last) System.out.println((memInUse-last)/mb);
		last = memInUse;
	}
	
}
