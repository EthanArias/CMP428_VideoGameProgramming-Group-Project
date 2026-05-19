package com.javagamedev.group.tiles;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

public class Side {

	private final List<Layer> layers = new ArrayList<Layer>();
	
	public Side() {
		
    }
	
	public List<Layer> getLayers(){
		return this.layers;
	}
	
	public void addLayer(Layer layer) {
		this.layers.add(layer);
	}
	
	public void update(long elapsedms) {
		for(Layer layer : layers) {
			layer.update(elapsedms);
		}
	}
	
	public void draw(Graphics2D g) {
		for(Layer layer : layers) {
			layer.draw(g);
		}
	}
	
	public String test() {
		String result = "";
		for(Layer layer : layers) {
			result += "layer:" + layer.test() + "\n";
		}
		return result;
	}
	
}
