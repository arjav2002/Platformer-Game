package com.arjav.platformergame.utils;

import java.awt.Rectangle;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import com.arjav.platformergame.AISimulationManager;
import com.arjav.platformergame.Game;

public class AIUtils {
	
	public static double sigmoid(double x) {
		return 1.0/(1 + Math.exp(-x));
	}
	
	public static boolean isRectInsideRect(Rectangle r1, Rectangle r2) {
		return (r1.x>=r2.x) && ((r1.x + r1.width)<=(r2.x + r2.width)) && (r1.y>=r2.y) && ((r1.y + r1.height)<=(r2.y + r2.height));
	}
	
	//each row has a single neuron's input weights
	public static void computeNeurons(double[][] inputWeights, double[] inputNeurons, double[] computedNeurons) {
		for(int i = 0; i < inputWeights.length; i++) {
			double sum = 0;
			for(int j = 0; j < inputNeurons.length; j++) {
				sum += inputWeights[i][j] * inputNeurons[j];
			}
			computedNeurons[i] = sigmoid(sum);
		}
	}

	public ArrayList<double[][]> readNetworkFromFile(String fileName) {
		ArrayList<double[][]> allInputWeights = new ArrayList<double[][]>();
		
		try {
			DataInputStream dis = new DataInputStream(new FileInputStream(fileName));
			
			int nLayers = dis.readInt();
			int[] neuronsInLayer = new int[nLayers];
			for(int i = 0; i < nLayers; i++) neuronsInLayer[i] = dis.readInt();
			for(int i = 0; i < nLayers - 1; i++) allInputWeights.add(new double[neuronsInLayer[i+1]][neuronsInLayer[i]]);
			
			for(double[][] weightsOfLayer : allInputWeights) {
				for(double[] weightsOfNeuron : weightsOfLayer) {
					for(int i = 0; i < weightsOfNeuron.length; i++) {
						weightsOfNeuron[i] = dis.readDouble();
					}
				}
			}
			
			dis.close();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		
		return allInputWeights;
	}
	
	public void writeNetworkToFile(String filePath, ArrayList<double[][]> allInputWeights) {
		try {
			DataOutputStream dos = new DataOutputStream(new FileOutputStream(filePath));
			
			dos.writeInt(allInputWeights.size()); // wriitng number of layers
			for(int i = 0; i < allInputWeights.size(); i++) dos.writeInt(allInputWeights.get(i)[0].length); // writing number of neurons in each layer
			
			for(double[][] layerWeightList : allInputWeights) {
				for(double[] neuronWeightList : layerWeightList) {
					for(double weight : neuronWeightList) {
						dos.writeDouble(weight);
					}
				}
			}
			
			dos.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public ArrayList<double[][]> cloneInputWeightList(ArrayList<double[][]> inputWeightList) {
		ArrayList<double[][]> newInputWeightList = new ArrayList<double[][]>();
		for(double[][] layerInputWeightList : inputWeightList) {
			double[][] arr = new double[layerInputWeightList.length][layerInputWeightList[0].length];
			for(int i = 0; i < layerInputWeightList.length; i++) {
				for(int j = 0; j < layerInputWeightList[0].length; j++) { 
					// layerInputWeightList[0] because every neuron in one layer has the same number inputs
					arr[i][j] = layerInputWeightList[i][j];
				}
			}
			newInputWeightList.add(arr);
		}
		return newInputWeightList;
	}
	
	public static int getFitness(Game game) {
		return (int)getFitnessAtCurrentLevel(game) +
					(game.hasCompleted()? AISimulationManager.GAME_PASS_BONUS:0) +
					(game.currentLevel()-1)*AISimulationManager.LEVEL_PASS_BONUS;
	}
	
	public static int getFitnessAtCurrentLevel(Game game) {
		return (int) game.getPlayer().getX()*10 + game.getScore();
	}
	
}
