package com.javagamedev.group;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import com.javagamedev.group.GamePanel.GameState;

public class GraphicalUserInterface {

	private GamePanel gamePanel;
	
	Font arial_30;
	Font arial_60B;
	
	public GraphicalUserInterface(GamePanel gamePanel) {
		this.gamePanel = gamePanel;
		
		arial_30 = new Font("Arial", Font.PLAIN, 30);
		arial_60B = new Font("Arial", Font.BOLD, 60);
	}
	
	public void draw(Graphics2D g) {
		GameState gameState = gamePanel.getCurrentGameState();
		
		switch(gameState) {
			case START_SCREEN:
				drawStart(g);
				break;
			case PLAYING:
				drawPlay(g);
				break;
			case END_SCREEN:
				drawEnd(g);
				break;
		}
		
	}
	
	private void drawStart(Graphics2D g) {
		// fill in
		g.fillRect(0, 0, GamePanel.SCREEN_WIDTH, GamePanel.SCREEN_HEIGHT);
	}
	
	private void drawPlay(Graphics2D g) {
		Font oldFont = g.getFont();
		Color oldColor = g.getColor();
		
		g.setFont(arial_30);
		g.setColor(Color.white);
		
		g.setRenderingHint(
			    RenderingHints.KEY_TEXT_ANTIALIASING, 
			    RenderingHints.VALUE_TEXT_ANTIALIAS_ON
			);
		
		g.drawString("ESCAPE", GamePanel.TILE_SIZE/2, 35);
		
		g.setFont(oldFont);
		g.setColor(oldColor);
	}
	
	private void drawEnd(Graphics2D g) {
		
	}
	
}
