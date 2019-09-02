package com.arjav.platformergame;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.arjav.platformergame.Game.AIState;
import com.arjav.platformergame.utils.AIUtils;

public class AIMenu extends JFrame implements ActionListener {
	
	private static final long serialVersionUID = 1L;
	
	private static final String DEFAULT_FILE = "C:/Users/NISHI/Desktop/ai_data/sim_log.ai";
	
	private Game game ; // an instance of game class to start the game
	private AISimulationManager simManager;
	
	private JPanel fileLoadPanel, startAISimPanel;
	private JTextField loadFilePathField;
	private JTextField writeNetworkPathField;
	private JButton loadFile, normalGame, newAI;
	
	private Thread simThread;
	
	public AIMenu(String titleAndVersion) {
		setSize(720, 360);
		setTitle(titleAndVersion);
		setResizable(false);
		setLayout(new GridLayout(3, 1));
		setLocationRelativeTo(null);
		
		loadFile = new JButton("Load");
		normalGame = new JButton("Play a Normal Game");
		newAI = new JButton("Start a new AI simulation");
		loadFilePathField = new JTextField("Enter path of AI file to load");
		writeNetworkPathField = new JTextField(DEFAULT_FILE);
		fileLoadPanel = new JPanel();
		startAISimPanel = new JPanel();
		
		loadFilePathField.setFont(new Font("Serif", Font.PLAIN, 20));
		writeNetworkPathField.setFont(new Font("Serif", Font.PLAIN, 20));
		
		fileLoadPanel.setLayout(new GridBagLayout());
		startAISimPanel.setLayout(new GridBagLayout());
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.weightx = 0.9;
		gbc.weighty = 1.0;
		gbc.fill = GridBagConstraints.BOTH;
		fileLoadPanel.add(loadFilePathField, gbc);
		startAISimPanel.add(writeNetworkPathField, gbc);
		
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.weightx = 0.1;
		gbc.weighty = 1.0;
		gbc.fill = GridBagConstraints.BOTH;
		fileLoadPanel.add(loadFile, gbc);
		startAISimPanel.add(newAI, gbc);
		
		add(fileLoadPanel);
		add(startAISimPanel);
		add(normalGame);
		
		setVisible(true);
		
		loadFile.setActionCommand("load file");
		normalGame.setActionCommand("normal game");
		newAI.setActionCommand("new AI");
		loadFile.addActionListener(this);
		normalGame.addActionListener(this);
		newAI.addActionListener(this);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		switch(e.getActionCommand()) {
		case "load file":
			setVisible(false);
			game = new Game(getTitle(), AIState.load, new AIUtils().readNetworkFromFile(loadFilePathField.getText()));
			Game.st = Game.State.start;
			game.start();
			break;
		case "new AI":
			setVisible(false);
			simManager = new AISimulationManager(getTitle(), writeNetworkPathField.getText());
			simThread = new Thread(simManager);
			simThread.start();
			break;
		case "normal game":
			setVisible(false);
			game = new Game(getTitle());
			Game.st = Game.State.start;
			game.start();
			break;
		}
	}
}
