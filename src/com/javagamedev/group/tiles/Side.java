package com.javagamedev.group.tiles;

import java.awt.Graphics2D;

public class Side {

	private Layer[] layers;
	
	public Side(int layers) {
		this.layers = new Layer[layers];
    }
	
	public Layer[] getLayers(){
		return this.layers;
	}
	
	public Layer getLayer(int i) {
		return this.layers[i];
	}
	
	public void update(long elapsedms) {
		for(int i=0; i<layers.length;i++) {
			this.layers[i].update(elapsedms);
		}
	}
	
	public void draw(Graphics2D g) {
		for(int i=0; i<layers.length;i++) {
			this.layers[i].draw(g);
		}
	}
	
}
