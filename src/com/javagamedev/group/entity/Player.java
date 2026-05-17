package com.javagamedev.group.entity;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;

import com.javagamedev.graphics.Animation;
import com.javagamedev.group.GamePanel;

public class Player extends Entity {

	private static final float JUMP_SPEED = -.95f;

    private boolean onGround;
	
    public Player(GamePanel gamePanel){
    	super(gamePanel);
    	initAnimations();
    }
    
    private void initAnimations() {
    	idle_forward_animation = new Animation();
    	idle_side_animation = new Animation();
    	jump_animation = new Animation();
    	move_animation = new Animation();
    	celebrate_animation = new Animation();
    	anim = new Animation();
    	
    	// Load the sprite sheet
    	BufferedImage spriteSheet = com.javagamedev.utility.GeneralUtility.loadImage(
    			"res/images/player_spriteSheet.png");
    	
    	for(int y = 0; y <15; y++) { // for each animation catagory
    		// Get subimages
    		BufferedImage subImageA = spriteSheet.getSubimage(0, 0+(y*16), 16, 16);
    		BufferedImage subImageB = spriteSheet.getSubimage(16, 0+(y*16), 16, 16);
    		BufferedImage subImageC = spriteSheet.getSubimage(32, 0+(y*16), 16, 16);
    		BufferedImage subImageD = spriteSheet.getSubimage(48, 0+(y*16), 16, 16);
    		BufferedImage subImageE = spriteSheet.getSubimage(64, 0+(y*16), 16, 16);
    				
    		/**
    		* create animation object, and fill it
    		* Switch case lets us use custom frame timing per animation
    		*/
    		switch(y) {
    			case 0: // idle_forward_animation
    				idle_forward_animation.addFrame(scaleFrame(subImageA, 
    					GamePanel.TILE_SIZE, GamePanel.TILE_SIZE), 20);
    				idle_forward_animation.addFrame(scaleFrame(subImageB, 
        				GamePanel.TILE_SIZE, GamePanel.TILE_SIZE), 20);
    				idle_forward_animation.addFrame(scaleFrame(subImageC, 
        				GamePanel.TILE_SIZE, GamePanel.TILE_SIZE), 20);
    				idle_forward_animation.addFrame(scaleFrame(subImageD, 
        				GamePanel.TILE_SIZE, GamePanel.TILE_SIZE), 20);
    				break;
    				case 1: // idle_side_animation
    				idle_side_animation.addFrame(scaleFrame(subImageA, 
    	    			GamePanel.TILE_SIZE, GamePanel.TILE_SIZE), 20);
    				idle_side_animation.addFrame(scaleFrame(subImageB, 
        	    		GamePanel.TILE_SIZE, GamePanel.TILE_SIZE), 20);
    				idle_side_animation.addFrame(scaleFrame(subImageC, 
        	    		GamePanel.TILE_SIZE, GamePanel.TILE_SIZE), 20);
    				idle_side_animation.addFrame(scaleFrame(subImageD, 
        	    		GamePanel.TILE_SIZE, GamePanel.TILE_SIZE), 20);
    				break;
    			case 2: // jump_animation
    				jump_animation.addFrame(scaleFrame(subImageA, 
        				GamePanel.TILE_SIZE, GamePanel.TILE_SIZE), 20);
    				jump_animation.addFrame(scaleFrame(subImageB, 
            			GamePanel.TILE_SIZE, GamePanel.TILE_SIZE), 20);
    				jump_animation.addFrame(scaleFrame(subImageC, 
            			GamePanel.TILE_SIZE, GamePanel.TILE_SIZE), 20);
    				jump_animation.addFrame(scaleFrame(subImageD, 
            			GamePanel.TILE_SIZE, GamePanel.TILE_SIZE), 20);
    				break;
    			case 3: // move_animation
    				move_animation.addFrame(scaleFrame(subImageA, 
        					GamePanel.TILE_SIZE, GamePanel.TILE_SIZE), 20);
    				move_animation.addFrame(scaleFrame(subImageB, 
        				GamePanel.TILE_SIZE, GamePanel.TILE_SIZE), 20);
    				move_animation.addFrame(scaleFrame(subImageC, 
        				GamePanel.TILE_SIZE, GamePanel.TILE_SIZE), 20);
    				move_animation.addFrame(scaleFrame(subImageD, 
        				GamePanel.TILE_SIZE, GamePanel.TILE_SIZE), 20);
    				move_animation.addFrame(scaleFrame(subImageE, 
        				GamePanel.TILE_SIZE, GamePanel.TILE_SIZE), 20);
    				break;
    			case 4: // celebrate_animation
    				celebrate_animation.addFrame(scaleFrame(subImageA, 
        				GamePanel.TILE_SIZE, GamePanel.TILE_SIZE), 20);
    				celebrate_animation.addFrame(scaleFrame(subImageB, 
            			GamePanel.TILE_SIZE, GamePanel.TILE_SIZE), 20);
    				celebrate_animation.addFrame(scaleFrame(subImageC, 
            			GamePanel.TILE_SIZE, GamePanel.TILE_SIZE), 20);
    				celebrate_animation.addFrame(scaleFrame(subImageD, 
            			GamePanel.TILE_SIZE, GamePanel.TILE_SIZE), 20);
    				celebrate_animation.addFrame(scaleFrame(subImageE, 
            			GamePanel.TILE_SIZE, GamePanel.TILE_SIZE), 20);
    				break;
    			}
    		}
    			
    	anim = this.idle_forward_animation;
    }
    
    public void collideHorizontal() {
    	super.setPosition(0, position.y);
    }
    
    public void collideVertical() {
        // check if collided with ground
        if (getPosition().y > 0) {
            onGround = true;
        }
        move(this.getVelocity().x, 0);
    }

    public void setY(float y) {
        // check if falling
        if (Math.round(y) > Math.round(getPosition().y)) {
            onGround = false;
        }
        super.setPosition(position.x, y);
    }
    
    /**
	    Makes the player jump if the player is on the ground or
	    if forceJump is true.
	*/
	public void jump(boolean forceJump) {
	    if (onGround || forceJump) {
	        onGround = false;
	        move(this.getVelocity().x, JUMP_SPEED);
	    }
	}
    
	/**
	    Sets the velocity of this Entity in pixels
	    per millisecond.
	*/
	public void move(float dx, float dy) {
		super.move(dx, dy);
		if(velocity.x > 0) {
	    	//this.
	    }
	    else if(velocity.x < 0) {
	    	
	    }
	    
	    if(onGround == false) {
	    	this.situation = AnimationState.JUMP;
	    }
	}
	
	@Override
	public void draw(Graphics2D g) {
		// because sprite sheets only draw right, we must manualy reverse for left
		if (this.facing == FacingState.LEFT) {
			g.drawImage(
					this.anim.getImage(), 
					(int)position.x+anim.getImage().getWidth(gamePanel), 
					(int)position.y, 
					-this.anim.getImage().getWidth(gamePanel), 
					this.anim.getImage().getHeight(gamePanel), 
					gamePanel);
		}
		else {
			g.drawImage(
					this.anim.getImage(), 
					(int)position.x, 
					(int)position.y,
					gamePanel);
		}
	}

	/**
	    Updates this Sprite's Animation and its position based
	    on the velocity.
	*/
	public void update(long elapsedMs) {
	    position.x += velocity.x * elapsedMs;
	    position.y += velocity.y * elapsedMs;
	    anim.update(elapsedMs);
	}

}
