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
		
		assets[0] = new LockedDoorAsset();
		assets[0].setPosition(GamePanel.TILE_SIZE*4, GamePanel.TILE_SIZE*5);
		assets[0].setSide(0);
		
		assets[1] = new KeyAsset();
		assets[1].setPosition(GamePanel.TILE_SIZE*4, GamePanel.TILE_SIZE*6);
		assets[1].setSide(1);
		
	}
}
