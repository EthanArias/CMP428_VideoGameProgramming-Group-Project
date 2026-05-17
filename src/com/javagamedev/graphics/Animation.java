package com.javagamedev.graphics;

import java.awt.Image;
import java.util.ArrayList;

/**
 * The Animation class manages a series of images (frames) and 
 * the amount of time to display each frame.
 */
public class Animation {
	
	private ArrayList<AnimFrame> frames;
	private int currFrameIndex;
	private long animTime;
	private long totalDuration;
	
	/**
	 * Creates a new, empty Animation.
	 */
	public Animation() {
		frames = new ArrayList<AnimFrame>();
		totalDuration = 0;
		start();
	}
	
	/**
	 * Creates a new, empty Animation.
	 */
	public Animation(ArrayList<AnimFrame> frames, long totalDuration) {
		this.frames = frames;
		this.totalDuration = totalDuration;
		start();
	}
	
	/**
	 * Adds an image to the animation with the specified 
	 * duration (time to display the image).
	 */
	public void addFrame(Image image, long duration) {
		synchronized(this) {
			totalDuration += duration;
			frames.add(new AnimFrame(image,totalDuration));
		}
	}
	
    /**
	    Creates a duplicate of this animation. The list of frames
	    are shared between the two Animations, but each Animation
	    can be animated independently.
	*/
	public Object clone() {
	    return new Animation(frames, totalDuration);
	}
	
	/**
	 * Starts this animation over from the beginning.
	 */
	public void start() {
		synchronized(this) {
			animTime = 0;
			currFrameIndex=0;
		}
	}
	
	/**
	 * Updates this animation's current image (frame), if 
	 * necessary.
	 */
	public void update(long elapsedTime) {
		synchronized(this) {
			if (frames.size() > 1) {
				animTime += elapsedTime;
				
				if (animTime >= totalDuration) {
					animTime = animTime % totalDuration;
					currFrameIndex=0;
				}
				
				while(animTime > getFrame(currFrameIndex).endTime) {
					currFrameIndex++;
				}
			}
		}
	}
	
	/**
	 * Gets this Animation's current image. Returns null if this 
	 * animation has no images.
	 */
	public Image getImage() {
		synchronized(this) {
			if(frames.size()==0) {
				return null;
			}
			else {
				return getFrame(currFrameIndex).image;
			}
		}
	}
	
	private AnimFrame getFrame(int i) {
		return (AnimFrame)frames.get(i);
	}
	
	public int getMaxWidth() {
		int maxWidth = 0;
		for (AnimFrame frame : frames) {
			if (frame.image.getWidth(null) > maxWidth) {
				maxWidth = frame.image.getWidth(null);
			}
		}
		return maxWidth;
	}
	
	public int getMaxHeight() {
		int maxHeight = 0;
		for (AnimFrame frame : frames) {
			if (frame.image.getHeight(null) > maxHeight) {
				maxHeight = frame.image.getHeight(null);
			}
		}
		return maxHeight;
	}
	
	private class AnimFrame {
		
		Image image;
		long endTime;
		
		public AnimFrame(Image image, long endTime) {
			this.image = image;
			this.endTime = endTime;
		}
	}
}