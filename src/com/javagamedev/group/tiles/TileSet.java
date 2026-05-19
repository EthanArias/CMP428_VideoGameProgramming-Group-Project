package com.javagamedev.group.tiles;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class TileSet {
	private BufferedImage set;
	private int scale;
	private int max;
	
	public TileSet(String name, int scale) {
		this.set = gridToLine(name, scale);
		this.scale = scale;
	}
	
	private BufferedImage gridToLine(String name, int scale) {
		BufferedImage original = com.javagamedev.utility.GeneralUtility.loadImage(name);
		int totalLines = original.getHeight()/scale;
		
		BufferedImage result = new BufferedImage(
			original.getWidth() * totalLines, scale, BufferedImage.TYPE_INT_ARGB );
        
        Graphics2D g = result.createGraphics();
        
        int cols = original.getWidth() / scale;
        int rows = original.getHeight() / scale;
        int currentTileIndex = 0;
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                BufferedImage tile = original.getSubimage(col * scale, row * scale, scale, scale);
                
                g.drawImage(tile, currentTileIndex * scale, 0, null);
                
                currentTileIndex++;
            }
        }
        
        g.dispose();
        
        this.max = result.getWidth()/scale;
        return result;
	}
	
	public BufferedImage getEntireSet() {
		return this.set;
	}
	
	public int getScale() {
		return this.scale;
	}
	
	public int getMax() {
		return this.max;
	}
	
	public BufferedImage getTileImage(int id) {
		if(id >= 0 && id <= max) {
			return set.getSubimage(id*scale, 0, scale, scale);
		}
		else {
			return new BufferedImage(scale, scale, BufferedImage.TYPE_INT_ARGB);
		}
	}
	
}
