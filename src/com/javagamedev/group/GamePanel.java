package com.javagamedev.group;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

import javax.swing.JPanel;

import com.javagamedev.group.tiles.TileManager;
import com.javagamedev.input.GameAction;
import com.javagamedev.input.InputManager;

public class GamePanel extends JPanel {
	
	private static final long serialVersionUID = 1L;
	private boolean debug = false;
	public static final Dimension SIZE = new Dimension (400,400);
	public static final int TILE_SIZE = 0;
	
	TileManager tileManager;
	
	// INPUT
	private GameAction jump;
	private GameAction left;
	private GameAction right;
	private GameAction shift;
	private GameAction debugAction;
	private GameAction escapeAction;
	private InputManager inputManager;
	
	public GamePanel() {
		inputManager = new InputManager(this);
		tileManager = new TileManager(this);
		initJSettings();
		createInput();
	}
	
	private void initJSettings() {
		this.setMinimumSize(SIZE);
		this.setPreferredSize(SIZE);
		this.setMaximumSize(SIZE);
		
		this.setDoubleBuffered(true);
	}
	
	public void update(double elapsedUnits) {
		// elapsedUnits is the number of update 'ticks' elapsed (fractional values allowed)
		if(debugAction.isPressed()) {
			debug = !debug;
		}
		if(escapeAction.isPressed()) {
			System.exit(0);
		}
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponents(g);
		Graphics2D g2 = (Graphics2D)g;
		
		tileManager.draw(g2);
	}
	
	private void createInput() {
		this.jump = new GameAction("jump", GameAction.DETECT_INITAL_PRESS_ONLY);
		this.left = new GameAction("left");
		this.right = new GameAction("right");
		this.shift = new GameAction("shift", GameAction.DETECT_INITAL_PRESS_ONLY);
		this.debugAction = new GameAction("debug", GameAction.DETECT_INITAL_PRESS_ONLY);
		this.escapeAction = new GameAction("exit", GameAction.DETECT_INITAL_PRESS_ONLY);
		
		inputManager.mapToKey(jump, KeyEvent.VK_W);
		inputManager.mapToKey(left, KeyEvent.VK_A);
		inputManager.mapToKey(right, KeyEvent.VK_D);
		
		inputManager.mapToKey(jump, KeyEvent.VK_UP);
		inputManager.mapToKey(left, KeyEvent.VK_LEFT);
		inputManager.mapToKey(right, KeyEvent.VK_RIGHT);
		
		inputManager.mapToKey(shift, KeyEvent.VK_SPACE);
		
		inputManager.mapToKey(debugAction, KeyEvent.VK_D);
		inputManager.mapToKey(escapeAction, KeyEvent.VK_ESCAPE);
	}
	
}