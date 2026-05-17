package com.javagamedev.group.tiles;

import java.awt.Graphics2D;

public class Map {

	private Side[] sides = new Side[4];
	
	public Map() {
    }
	
	public Side[] getSides(){
		return this.sides;
	}
	
	public Side getSide(int i) {
		return this.sides[i];
	}
	
	public void update(long elapsedms) {
		for(int i=0; i<4;i++) {
			this.sides[i].update(elapsedms);
		}
	}
	
	public void draw(Graphics2D g) {
		for(int i=0; i<4;i++) {
			this.sides[i].draw(g);
		}
	}
	
}
