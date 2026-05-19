package com.javagamedev.group.tiles;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;

import com.javagamedev.graphics.Animation;

public abstract class Tile {
	public abstract void update(long elapsedms);
	public abstract void draw(Graphics2D g, int x, int y);
	
	public String test() {
		String result = "TILE: TEST";
		return result;
	}
	
	public static class BlankTile extends Tile{
		
		public BlankTile() {}
		
		@Override
		public void update(long elapsedms) {
			// does nothing
		}
		
		@Override
		public void draw(Graphics2D g, int x, int y) {
			// does nothing
		}
		
	}
	
	public static class ImageTile extends Tile{
		private Image img;
		
		public ImageTile(Image img) {
			this.img = img;
		}
		
		public Image getImage() {
			return img;
		}
		
		public void setImage(Image img) {
			this.img = img;
		}
		
		@Override
		public void update(long elapsedms) {
			// does nothing
		}
		
		@Override
		public void draw(Graphics2D g, int x, int y) {
			g.drawImage(img, 
					x*img.getWidth(null), 
					y*img.getHeight(null), 
					img.getWidth(null), 
					img.getHeight(null), 
					null);
		}
		
	}
	
	public static class AnimationTile extends Tile{
		private Animation anim;
		
		public AnimationTile(Animation anim) {
			this.anim = anim;
		}
		
		public Animation getImage() {
			return anim;
		}
		
		public void setImage(Animation anim) {
			this.anim = anim;
		}
		
		@Override
		public void update(long elapsedms) {
			this.anim.update(elapsedms);
		}
		
		@Override
		public void draw(Graphics2D g, int x, int y) {
			Image img = anim.getImage();
			g.drawImage(
					img, 
					x*img.getWidth(null), 
					y*img.getHeight(null), 
					img.getWidth(null), 
					img.getHeight(null), 
					null);
		}
		
	}
}
