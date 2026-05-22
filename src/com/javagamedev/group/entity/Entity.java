package com.javagamedev.group.entity;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import com.javagamedev.graphics.Animation;
import com.javagamedev.group.GamePanel;

public abstract class Entity {

	protected GamePanel gamePanel;
	
	public Point.Float worldPosition = new Point.Float(0, 0);
	protected Point.Float velocity = new Point.Float(0, 0);
	protected float speed;
	
	public enum AnimationState {IDLE, MOVE, JUMP, CELEBRATE};
	protected AnimationState situation = AnimationState.IDLE;
	
	public enum FacingState {FORWARD, LEFT, RIGHT};
	protected FacingState facing = FacingState.FORWARD;
	
	protected Animation idle_forward_animation, idle_side_animation; 
	protected Animation jump_animation, move_animation, celebrate_animation;
	protected Animation anim;
	
	// Collision detection
	protected Rectangle hitBox;
	protected boolean collision = false;
	protected int solidAreaDefaultX = 0;
	protected int solidAreaDefaultY = 0;

	public Entity(GamePanel gamePanel) {
		this.gamePanel = gamePanel;
	}
	
	public void setAnimationState(AnimationState situation) {
		this.situation = situation;
	}
	
	public void setFacingState(FacingState facing) {
		this.facing = facing;
	}
	
	public AnimationState getAnimationState() {
		return situation;
	}
	
	public FacingState getFacingState() {
		return facing;
	}
    
	public Rectangle getHitBox() {
		return hitBox;
	}
	
	public float getSpeed() {
		return this.speed;
	}
	
    /**
        Gets this Entity's current worldPosition.
    */
    public Point.Float getWorldPosition() {
        return worldPosition;
    }

    /**
        Sets this Entity's current worldPosition.
    */
    public void setWorldPosition(float x, float y) {
        this.worldPosition.x = x;
        this.worldPosition.y = y;
    }
    
    public boolean getCollision() {
    	return this.collision;
    }
    
    public void setCollision(boolean collision) {
    	this.collision = collision;
    }
    
    /**
        Gets this Entity's dimensions, based on the size of the
        current image.
    */
    public Dimension getHeight() {
        return new Dimension(anim.getImage().getWidth(null), anim.getImage().getHeight(null));
    }

    /**
        Gets the velocity of this Entity in pixels
        per millisecond.
    */
    public Point.Float getVelocity() {
        return this.velocity;
    }

    /**
	    Sets the velocity of this Entity in pixels
	    per millisecond.
	*/
    public void move(float dx, float dy) {
		if(dx>0) {
			this.facing = FacingState.RIGHT;
		}
		else if(dx<0) {
			this.facing = FacingState.LEFT;
		}
		
		this.velocity = new Point.Float(dx, dy);
	}

    /**
        Gets this Entity's Entity image.
    */
    public Image getImage() {
        return anim.getImage();
    }
    
    public abstract void draw(Graphics2D g);
    public abstract void update(long elapsedTime);
    
    protected Image scaleFrame(BufferedImage frame, int width, int height) {
		return com.javagamedev.utility.GeneralUtility.scaleImage(frame, width, height);
	}
}
