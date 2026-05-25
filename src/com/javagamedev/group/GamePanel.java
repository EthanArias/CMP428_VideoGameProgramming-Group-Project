package com.javagamedev.group;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;

import javax.sound.sampled.AudioFormat;
import javax.swing.JPanel;

import com.javagamedev.graphics.SceneAnimation;
import com.javagamedev.group.entity.Player;
import com.javagamedev.group.tiles.TileManager;
import com.javagamedev.input.GameAction;
import com.javagamedev.input.InputManager;
import com.javagamedev.sound.Sound;
import com.javagamedev.sound.SoundManager;

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
	public final static int MAX_WORLD_COL = 20;
	public final static int MAX_WORLD_ROW = 20;
	public final static int WORLD_WIDTH = TILE_SIZE*MAX_SCREEN_COL;
	public final static int WORLD_HEIGHT = TILE_SIZE*MAX_SCREEN_ROW;
	
	// SOUND SETTINGS
	// uncompressed, 44100Hz, 16-bit, mono, signed, little-endian
	private static final AudioFormat PLAYBACK_FORMAT =
		new AudioFormat(44100, 16, 1, true, false);
	private SoundManager soundManager;
	private Sound[] sounds = new Sound[10];
	
	// GAME SETTINGS
	public static final float GRAVITY = 0.005f;
	
	// SCENE ANIMATION
	private SceneAnimation pause = new SceneAnimation(250);
	private SceneAnimation shifting = new SceneAnimation(1000);
	
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
		
		soundManager = new SoundManager(PLAYBACK_FORMAT);
		//sounds[0] = soundManager.getSound("res/sounds/bgm.wav");
		//sounds[1] = soundManager.getSound("res/sounds/sfx1.wav");
		//soundManager.play(sounds[0], null, true);
		
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
		
		if(!shifting.isActive()) {
			if(shift.isPressed()) {
				shifting.activate();
				shiftPlayer();
			}
			updatePlayer((long)elapsedMS);
		}
		else if(shifting.isActive()) {
			updateShiftAnimation((long)elapsedMS);
		}
	}
	
	private void updateShiftAnimation(long elapsedMS) {
		if(pause.isActive()) {
			pause.update(elapsedMS);
		}
		shifting.update(elapsedMS);
	    if (pause.isActive()) {
	        // waiting during pause
	    } else if (!shifting.isDone()) {
	        // animation in progress — rendering handles the visual
	    } else {
	        // animation finished: commit the side change and start pause
	        tileManager.shiftLeft();
	        pause.activate();
	    }
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
	
	private void drawShiftingAnimation(Graphics2D g2) {
	    float p = shifting.getProgress(); // 0..1
	    int shiftPx = (int) (p * GamePanel.SCREEN_WIDTH);

	    int currentIndex = tileManager.getCurrentSideIndex();
	    int nextIndex = (currentIndex == tileManager.MAX_SIDE) ? 
	    		tileManager.MIN_SIDE : currentIndex + 1;

	    // current side slides right
	    tileManager.drawSideAt(g2, currentIndex, +shiftPx);

	    // next side starts off-screen left at -SCREEN_WIDTH and moves right to 0
	    tileManager.drawSideAt(g2, nextIndex, -GamePanel.SCREEN_WIDTH + shiftPx);
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g); // fixed to singular method
		Graphics2D g2 = (Graphics2D)g;
		g2.setColor(Color.black);
		g2.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
		
		// TILES
		if (shifting.isActive() && !shifting.isDone()) {
			drawShiftingAnimation(g2);
		}
		else {
			tileManager.draw(g2);
		}
		
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
	
	private void shiftPlayer() {
		int currentSide = tileManager.getCurrentSideIndex();
		switch(currentSide) {
		case 0:
			break;
		case 1:
			break;
		case 2:
			break;
		case 3:
			break;
		}
	}
	
	public void playSFX(int index) {
		if(index < 1 || index >= sounds.length) {
			return;
		}
		else {
			soundManager.play(sounds[index], null, false);
		}
	}
	
}