package com.javagamedev.group.tiles;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class Side {

	private final List<Layer> layers = new ArrayList<Layer>();
	private Dimension d;
	
	public Side(Dimension d) {
		this.d = d;
    }
	
	public Dimension getDimensions() {
		return d;
	}
	
	public List<Layer> getLayers(){
		return this.layers;
	}
	
	public void addLayer(Layer layer) {
		this.layers.add(layer);
	}
	
	public boolean isAnyCollideableAt(Point p) {
		return isAnyCollideableAt(p.x, p.y);
	}
	
	public boolean isAnyCollideableAt(int col, int row) {
		for(Layer layer: layers) {
			// use (col,row) order — layers store Points as (x=col, y=row)
			if(layer.isCollider() && layer.isSolidAt(new Point(col, row))) {
				return true;
			}
		}
		return false;
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