package com.javagamedev.group.tiles;

import java.awt.Graphics2D;

public class Map {

	private Side[] sides = new Side[4];
	
	public Map(Side side1, Side side2, Side side3, Side side4) {
		sides[0] = side1;
		sides[1] = side2;
		sides[2] = side3;
		sides[3] = side4;
    }
	
	public Map() {
    }
	
	public void setSide(Side side, int i) {
		if(i>=0 && i<sides.length) {
			this.sides[i] = side;
		}
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
	
	public void draw(Graphics2D g, int i) {
		this.sides[i].draw(g);
	}
	
	public String test() {
		String result = "";
		result += "sides[0]: " + sides[0].test();
		result += "sides[1]: " + sides[1].test();
		result += "sides[2]: " + sides[2].test();
		result += "sides[3]: " + sides[3].test();
		return result;
	}
	
}