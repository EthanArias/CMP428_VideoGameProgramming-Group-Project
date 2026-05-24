package com.javagamedev.group;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;

import javax.swing.JPanel;

import com.javagamedev.group.entity.Player;
import com.javagamedev.group.tiles.TileManager;
import com.javagamedev.input.GameAction;
import com.javagamedev.input.InputManager;

public class GamePanel extends JPanel {
	
	private static final long serialVersionUID = 1L;
	private boolean debug = false;
	
	// SCREEN SETTINGS
	private final static int originalTileSize = 16; // 16x16 tile
	private final static int scale = 3;

	public final static int TILE_SIZE = originalTileSize * scale; // 48x48 tile
	public final static int MAX_SCREEN_COL = 10;
	public final static int MAX_SCREEN_ROW = 8;
	public final static int SCREEN_WIDTH = TILE_SIZE * MAX_SCREEN_COL; // 768 pixels
	public final static int SCREEN_HEIGHT = TILE_SIZE * MAX_SCREEN_ROW; // 576 pixels
	public final static Dimension SIZE = new Dimension (SCREEN_WIDTH, SCREEN_HEIGHT);
	
	// WORLD SETTINGS
	public final static int MAX_WORLD_COL = 50;
	public final static int MAX_WORLD_ROW = 50;
	public final static int WORLD_WIDTH = TILE_SIZE*MAX_SCREEN_COL;
	public final static int WORLD_HEIGHT = TILE_SIZE*MAX_SCREEN_ROW;
	
	public static final float GRAVITY = 0.005f;
	
	// BACKGROUND / TILES
	public final TileManager tileManager = new TileManager(this);
	public final  CollisionChecker collisionChecker = new CollisionChecker(this);
	
	// MIDDLE GROUND / PLAYER
	private Player player = new Player(this);
	
	// FOREGROUND / UI
	
	// INPUT
	private GameAction jump;
	private GameAction left;
	private GameAction right;
	private GameAction shift;
	private GameAction debugAction;
	private GameAction escapeAction;
	public final  InputManager inputManager = new InputManager(this);
	
	public GamePanel() {
		initJSettings();
		createInput();
		
		this.player.setWorldPosition(
				player.getWorldPosition().x+TILE_SIZE*1, 
				SCREEN_HEIGHT-player.getImage().getHeight(null)-TILE_SIZE*1);
	}
	
	private void initJSettings() {
		this.setMinimumSize(SIZE);
		this.setPreferredSize(SIZE);
		this.setMaximumSize(SIZE);
		
		this.setDoubleBuffered(true);
	}
	
	public void update(double elapsedMS) {
		// elapsedUnits is the number of update 'ticks' elapsed (fractional values allowed)
		if(debugAction.isPressed()) {
			debug = !debug;
		}
		if(escapeAction.isPressed()) {
			System.exit(0);
		}
		if(shift.isPressed()) {
			tileManager.shiftRight();
		}
		
		updatePlayer((long)elapsedMS);
	}
	
	private void updatePlayer(long elapsedMS) {
		float speed = player.getSpeed();
		float dx = 0f;
		float dy = 0f;
		
		if (left.isPressed()) {
			dx -= speed;  
		}
		if (right.isPressed()) {
			dx += speed;
		}
		
		if(jump.isPressed()) {
			player.jump(false);
		}
		
		dy = player.getVelocity().y+GRAVITY*elapsedMS;
		
		// apply gravity into vertical velocity and move
		player.move(dx, dy);
		player.update(elapsedMS);
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g); // fixed to singular method
		Graphics2D g2 = (Graphics2D)g;
		g2.setColor(Color.black);
		g2.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
		
		// TILES
		tileManager.draw(g2);
		
		// Entities
		player.draw(g2);
		
		// UI
		
		g2.dispose();
	}
	
	public Point pixelsToTiles(Point.Float pixels) {
		Point tiles = new Point();
		tiles.x = (int) Math.floor((pixels.x + 0) / TILE_SIZE);
		tiles.y = (int) Math.floor((pixels.y + 0) / TILE_SIZE);
		return tiles;
	}
	
	private void createInput() {
		this.jump = new GameAction("jump", GameAction.DETECT_INITAL_PRESS_ONLY);
		this.left = new GameAction("left");
		this.right = new GameAction("right");
		this.shift = new GameAction("shift", GameAction.DETECT_INITAL_PRESS_ONLY);
		this.debugAction = new GameAction("debug", GameAction.DETECT_INITAL_PRESS_ONLY);
		this.escapeAction = new GameAction("exit", GameAction.DETECT_INITAL_PRESS_ONLY);
		
		inputManager.mapToKey(jump, KeyEvent.VK_SPACE);
		inputManager.mapToKey(left, KeyEvent.VK_A);
		inputManager.mapToKey(right, KeyEvent.VK_D);
		
		inputManager.mapToKey(jump, KeyEvent.VK_UP);
		inputManager.mapToKey(left, KeyEvent.VK_LEFT);
		inputManager.mapToKey(right, KeyEvent.VK_RIGHT);
		
		inputManager.mapToKey(shift, KeyEvent.VK_SHIFT);
		
		inputManager.mapToKey(debugAction, KeyEvent.VK_Q);
		inputManager.mapToKey(escapeAction, KeyEvent.VK_ESCAPE);
	}
	
	public TileManager getTileManager() {
		return this.tileManager;
	}
	
	public CollisionChecker getCollisionChecker() {
		return this.collisionChecker;
	}
	
	public boolean inDebugMode() {
		return debug;
	}
	
}