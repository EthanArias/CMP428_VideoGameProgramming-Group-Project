package com.javagamedev.group.assets;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;

public abstract class Asset {

	public BufferedImage image;
	private String name;
	private boolean collision = false;
	private Point position;
	private int side;
	
	protected Asset() {
	}
	
	protected Asset(String name, BufferedImage image, boolean collision, 
			Point position, int side) {
		this.name = name;
		this.image = image;
		this.collision = collision;
		this.position = position;
		this.side = side;
	}
	
	public BufferedImage getImage() {
		return this.image;
	}
	
	public void setImage(BufferedImage image) {
		this.image = image;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public boolean hasCollision() {
		return this.collision;
	}
	
	public void setCollision(boolean collision) {
		this.collision = collision;
	}
	
	public Point getPosition() {
		return this.position;
	}
	
	public void setPosition(Point position) {
		this.position = position;
	}
	
	public int getSide() {
		return this.side;
	}
	
	public void setSide(int side) {
		this.side = side;
	}
	
	public void setPosition(int x, int y) {
		this.position = new Point(x,y);
	}
	
	public void draw(Graphics2D g, int mapSide) {
		if (mapSide == this.side) {
			g.drawImage(this.image, this.position.x, this.position.y, null);
		}
		
	}
	
}
