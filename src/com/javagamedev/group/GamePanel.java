package com.javagamedev.group;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;

import javax.sound.sampled.AudioFormat;
import javax.swing.JPanel;

import com.javagamedev.group.assets.Asset;
import com.javagamedev.group.assets.AssetSetter;
import com.javagamedev.group.entity.Player;
import com.javagamedev.group.tiles.TileManager;
import com.javagamedev.input.GameAction;
import com.javagamedev.input.InputManager;
import com.javagamedev.sound.Sound;
import com.javagamedev.sound.SoundManager;

public class GamePanel extends JPanel {
	
	private static final long serialVersionUID = 1L;
	private boolean debug = false;
	
	// GAME STATE
	public enum GameState { START_SCREEN, PLAYING, END_SCREEN};
	private GameState gameState;
	
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
	
	// BACKGROUND / TILES
	public final TileManager tileManager = new TileManager(this);
	public final  CollisionChecker collisionChecker = new CollisionChecker(this);
	
	// MIDDLE GROUND / PLAYER
	private Player player = new Player(this);
	private AssetSetter assetSetter = new AssetSetter(this);
	private Asset[] assets = new Asset[10];

    // Shift capture state: when a side shift starts we capture the player's local
    // pixel position on the current side and the corresponding local pixel on the next side.
    private boolean shiftCaptureDone = false;
    private float frozenLocalCurrentX = 0f;
    private float frozenLocalNextX = 0f;
    private float frozenY = 0f;
    private int frozenCurrentSide = -1;
    private int frozenNextSide = -1;
    private boolean wasShifting = false;
	
	// FOREGROUND / UI
	private GraphicalUserInterface gui = new GraphicalUserInterface(this);
    
	// INPUT
	private GameAction jump;
	private GameAction left;
	private GameAction right;
	private GameAction shift;
	private GameAction debugAction;
	private GameAction escapeAction;
	private GameAction enterAction;
	public final  InputManager inputManager = new InputManager(this);
	
	public GamePanel() {
		initJSettings();
		createInput();
		gameState = GameState.START_SCREEN;
		
		soundManager = new SoundManager(PLAYBACK_FORMAT);
		//sounds[0] = soundManager.getSound("res/sounds/bgm.wav");
		//sounds[1] = soundManager.getSound("res/sounds/sfx1.wav");
		//soundManager.play(sounds[0], null, true);
		
		this.player.setWorldPosition(
				player.getWorldPosition().x+TILE_SIZE*1, 
				SCREEN_HEIGHT-player.getImage().getHeight(null)-TILE_SIZE*1);
		
		assetSetter.setObject(assets);
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
		
		switch(gameState) {
			case START_SCREEN:
				updateOnStart(elapsedMS);
				break;
			case PLAYING:
				updateOnPlaying(elapsedMS);
				break;
			case END_SCREEN:
				updateOnEnd(elapsedMS);
				break;
		}
		
	}

	private void updateOnStart(double elapsedMS) {
		if(enterAction.isPressed()) {
			gameState = GameState.PLAYING;
		}
	}
	
	private void updateOnPlaying(double elapsedMS) {
		if(shift.isPressed()) {
			tileManager.shiftRight();
		}
		// advance tile manager animations (e.g., side shifting)
		tileManager.update((long)elapsedMS);

		// handle shift start/end capture
		boolean nowShifting = tileManager.isShifting();
		if (nowShifting && !wasShifting) {
			// shift just started: capture player positions
			frozenCurrentSide = tileManager.getCurrentSideIndex();
			frozenNextSide = tileManager.getNextSideIndex();
			// current side draw offset at capture
			int currOffset = tileManager.getSideDrawOffsetX(frozenCurrentSide);
			// player's absolute panel position
			float playerPanelX = player.getWorldPosition().x;
			// local pixel within current side
			frozenLocalCurrentX = playerPanelX - currOffset;
			// compute fractional tile and map to next side tile index
			float tileIndexF = (float)Math.floor(frozenLocalCurrentX / TILE_SIZE);
			float frac = frozenLocalCurrentX - tileIndexF * TILE_SIZE;
			// clamp tileIndex to next side width
			int nextTileWidth = tileManager.getSideTileWidth(frozenNextSide);
			int tileIndex = (int)tileIndexF;
			if (tileIndex < 0) tileIndex = 0;
			if (tileIndex >= nextTileWidth) tileIndex = Math.max(0, nextTileWidth - 1);
			frozenLocalNextX = tileIndex * TILE_SIZE + frac;
			// store vertical
			frozenY = player.getWorldPosition().y;
			shiftCaptureDone = true;
		}

		// If shifting, do not update player physics; otherwise update normally
		if (!tileManager.isShifting()) {
			updatePlayer((long)elapsedMS);
		} else {
			// skip updating player while shifting
		}

		// handle shift end: when shifting finishes this frame
		if (!nowShifting && wasShifting && shiftCaptureDone) {
			// shift finished: set player to the captured position on the new current side
			int newCurrent = tileManager.getCurrentSideIndex();
			// Allow customizable resolution of player position after a shift
			java.awt.Point.Float newPos = resolvePlayerPositionAfterShift(
					frozenCurrentSide, // from side
					newCurrent,         // to side
					frozenLocalCurrentX,
					frozenLocalNextX,
					frozenY);
			player.setWorldPosition(newPos.x, newPos.y);
			// clear capture
			shiftCaptureDone = false;
			frozenCurrentSide = -1;
			frozenNextSide = -1;
		}

		wasShifting = tileManager.isShifting();
	}
	
	private void updateOnEnd(double elapsedMS) {
		
	}
	
	/**
	 * Hook called when a side shift finishes to determine the player's new world position
	 * on the destination side. Default implementation preserves the prior behavior
	 * (maps the captured local X on the next side into panel coordinates using the
	 * destination side's draw offset and preserves Y). Override or edit this method
	 * to implement custom mapping logic (percentage-based, ground snap, etc.).
	 *
	 * @param fromSide index of the side we shifted from
	 * @param toSide index of the side we shifted to (now current)
	 * @param frozenLocalCurrentX the captured local X (pixels) within the original side
	 * @param frozenLocalNextX the computed local X (pixels) on the destination side
	 * @param frozenY the captured Y (pixels)
	 * @return new world position (panel pixel coordinates) for the player
	 */
	public Point.Float resolvePlayerPositionAfterShift(int fromSide, int toSide,
							float frozenLocalCurrentX, float frozenLocalNextX, float frozenY) {
		// Default: use frozenLocalNextX plus the destination side's draw offset
		int destOffset = tileManager.getSideDrawOffsetX(toSide);
		float newWorldX = GamePanel.TILE_SIZE + destOffset;
		
		if(fromSide==0) {
			// TODO: fill in
		}
		else if(fromSide==1) {
			// TODO: fill in
		}
		else if(fromSide==2) {
			// TODO: fill in
		}
		else if(fromSide==3) {
			// TODO: fill in
		}
		
		return new Point.Float(newWorldX, frozenY);
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
		
		if(tileManager.getCurrentSideIndex()==0 || tileManager.getCurrentSideIndex()==3) {
			player.position3D.x = player.getWorldPosition().x;
		}
		else if(tileManager.getCurrentSideIndex()==1 || tileManager.getCurrentSideIndex()==2) {
			player.position3D.y = player.getWorldPosition().y;
		}
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g); // fixed to singular method
		Graphics2D g2 = (Graphics2D)g;
		g2.setColor(Color.black);
		g2.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
		
		// TILES
		tileManager.draw(g2);

		// Entities
		drawEntities(g2);
		drawAssets(g2);
		
		// UI
		gui.draw(g2);
		
		g2.dispose();
	}
	
	private void drawEntities(Graphics2D g2) {
		if (tileManager.isShifting() && shiftCaptureDone) {
			// draw player at both current and next side positions using captured local pixels
			int currOffsetNow = tileManager.getSideDrawOffsetX(frozenCurrentSide);
			int nextOffsetNow = tileManager.getSideDrawOffsetX(frozenNextSide);
			float drawXCurrent = frozenLocalCurrentX + currOffsetNow;
			float drawXNext = frozenLocalNextX + nextOffsetNow;
			// draw next first, then current on top
			player.drawAt(g2, drawXNext, frozenY);
			player.drawAt(g2, drawXCurrent, frozenY);
		} else {
			player.draw(g2);
		}
	}
	
	private void drawAssets(Graphics2D g2) {
		for(Asset asset : assets) {
			if(asset != null) {
				asset.draw(g2, tileManager.getCurrentSideIndex());
			}
		}
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
		this.enterAction = new GameAction("enter", GameAction.DETECT_INITAL_PRESS_ONLY);
		
		inputManager.mapToKey(jump, KeyEvent.VK_SPACE);
		inputManager.mapToKey(left, KeyEvent.VK_A);
		inputManager.mapToKey(right, KeyEvent.VK_D);
		
		inputManager.mapToKey(jump, KeyEvent.VK_UP);
		inputManager.mapToKey(left, KeyEvent.VK_LEFT);
		inputManager.mapToKey(right, KeyEvent.VK_RIGHT);
		
		inputManager.mapToKey(shift, KeyEvent.VK_SHIFT);
		
		inputManager.mapToKey(debugAction, KeyEvent.VK_Q);
		inputManager.mapToKey(escapeAction, KeyEvent.VK_ESCAPE);
		inputManager.mapToKey(enterAction, KeyEvent.VK_ENTER);
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
	
	public Asset[] getAssets() {
		return this.assets;
	}
	
	public void playSFX(int index) {
		if(index < 1 || index >= sounds.length) {
			return;
		}
		else {
			soundManager.play(sounds[index], null, false);
		}
	}
	
	public GameState getCurrentGameState() {
		return gameState;
	}
	
}