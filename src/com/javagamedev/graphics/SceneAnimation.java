package com.javagamedev.graphics;

public class SceneAnimation {

	private long totalDuration = 0;
	private long elapsedDuration = 0;
	private float progress = 0f;
	private boolean active = false;
	
	public SceneAnimation(long totalDuration) {
		this.totalDuration = totalDuration;
	}
	
	public void activate() {
		if(!active) {
			this.active = true;
			this.elapsedDuration = 0;
			this.progress = 0f;
		}
	}
	
	public boolean isActive() {
		return this.active;
	}
	
	public boolean isDone() {
		return (elapsedDuration >= totalDuration);
	}
	
	public void update(long elapsedMS) {
		elapsedDuration+=elapsedMS;
		progress = (float) Math.min(1.0, elapsedDuration / totalDuration);
		
		if(elapsedDuration>=totalDuration) {
			this.active = false;
		}
	}
	
	public float getProgress() {
		return this.progress;
	}
	
	public long getTotalDuration() {
		return this.totalDuration;
	}
	
	public long getElapsedDuration() {
		return this.elapsedDuration;
	}
	
}
