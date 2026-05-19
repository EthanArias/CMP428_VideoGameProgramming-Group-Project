package com.javagamedev.group.tiles;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.Arrays;

import com.javagamedev.group.tiles.Tile;
import com.javagamedev.group.tiles.Tile.AnimationTile;
import com.javagamedev.group.tiles.Tile.BlankTile;
import com.javagamedev.group.tiles.Tile.ImageTile;

public class Layer {

	private boolean collider;
	private Tile[][] tiles;
	private Dimension d;
	
	public Layer(Dimension d, boolean collider) {
		this.d = d;
		tiles = new Tile[d.width][d.height];
        this.collider = collider;
        
        for (Tile[] row : tiles) {
            Arrays.fill(row, new BlankTile());
        }
    }
	
	public void setTile(Tile tile, int x, int y) {
		tiles[x][y] = tile; 
	}
	
	public void setTile(Tile tile, Point p) {
		tiles[p.x][p.y] = tile; 
	}
	
	public boolean isCollider() {
		return collider;
	}
	
	public Tile[][] getTiles(){
		return tiles;
	}
	
	public Dimension getDimensions(){
		return d;
	}
	
	public Tile getTile(int x, int y) {
		return tiles[x][y];
	}
	
	public Tile getTile(Point p) {
		return tiles[p.x][p.y];
	}
	
	public void update(long elapsedms) {
		for(int x=0; x<d.width;x++) {
			for(int y=0; y<d.height;y++) {
				if(tiles[x][y] instanceof AnimationTile) {
					tiles[x][y].update(elapsedms);
				}
			}
		}
	}
	
	public void draw(Graphics2D g) {
		for(int x=0; x<d.width;x++) {
			for(int y=0; y<d.height;y++) {
				if(!(tiles[x][y] instanceof BlankTile)) {
					tiles[x][y].draw(g, x, y);
				}
			}
		}
	}
	
	public String test() {
		String result = "";
		for(Tile[] tiles : tiles) {
			for(Tile tile : tiles) {
				result += tile.test() + "\n";
			}
		}
		return result;
	}
	
}
