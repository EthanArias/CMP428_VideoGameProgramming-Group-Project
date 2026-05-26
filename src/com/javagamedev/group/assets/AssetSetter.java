package com.javagamedev.group.assets;

import com.javagamedev.group.GamePanel;

public class AssetSetter {
	
	private GamePanel gamePanel;
	
	public AssetSetter(GamePanel gamePanel) {
		this.gamePanel = gamePanel;
	}
	
	/**
	 * Array must be size <=10
	 */
	public void setObject(Asset[] assets) {
		
		assets[0] = new TestAsset();
		assets[0].setPosition(GamePanel.TILE_SIZE*3, GamePanel.TILE_SIZE*5);
		assets[0].setSide(0);
		
	}
}
