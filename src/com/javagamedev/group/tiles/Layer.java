package com.javagamedev.group.tiles;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;

import com.javagamedev.group.tiles.Tile;
import com.javagamedev.group.tiles.Tile.AnimationTile;
import com.javagamedev.group.tiles.Tile.BlankTile;

public class Layer {

	private boolean collider;
	private Tile[][] tiles;
	private Dimension d;
	
	public Layer(Dimension d, boolean collider) {
		this.d = d;
		tiles = new Tile[d.width][d.height];
        this.collider = collider;
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
					tiles[x][y].draw(g);
				}
			}
		}
	}
	
}
