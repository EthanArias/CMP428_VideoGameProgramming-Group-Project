package com.javagamedev.group.entity;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import com.javagamedev.graphics.Animation;
import com.javagamedev.group.GamePanel;
import com.javagamedev.group.assets.Asset;

public class Player extends Entity {
	
	private static final float JUMP_SPEED = -0.95f;
	
	private boolean onGround;
	
    public Player(GamePanel gamePanel){
    	super(gamePanel);
    	// set a default speed so movement input actually moves the player
    	this.speed = 0.35f;
    	this.maxAcceleration = 1;
    	
    	initAnimations();
    	this.hitBox = new Rectangle(
    			0, 0, 
    			this.anim.getImage().getWidth(gamePanel), 
    			this.anim.getImage().getHeight(gamePanel));
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
    			"res/images/entity/player/player_spriteSheet.png");
    	
    	for(int y = 0; y <5; y++) { // for each animation catagory
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
    					GamePanel.TILE_SIZE, GamePanel.TILE_SIZE), 200);
    				idle_forward_animation.addFrame(scaleFrame(subImageB, 
        				GamePanel.TILE_SIZE, GamePanel.TILE_SIZE), 200);
    				idle_forward_animation.addFrame(scaleFrame(subImageC, 
        				GamePanel.TILE_SIZE, GamePanel.TILE_SIZE), 200);
    				idle_forward_animation.addFrame(scaleFrame(subImageD, 
        				GamePanel.TILE_SIZE, GamePanel.TILE_SIZE), 200);
    				break;
    				case 1: // idle_side_animation
    				idle_side_animation.addFrame(scaleFrame(subImageA, 
    	    			GamePanel.TILE_SIZE, GamePanel.TILE_SIZE), 200);
    				idle_side_animation.addFrame(scaleFrame(subImageB, 
        	    		GamePanel.TILE_SIZE, GamePanel.TILE_SIZE), 200);
    				idle_side_animation.addFrame(scaleFrame(subImageC, 
        	    		GamePanel.TILE_SIZE, GamePanel.TILE_SIZE), 200);
    				idle_side_animation.addFrame(scaleFrame(subImageD, 
        	    		GamePanel.TILE_SIZE, GamePanel.TILE_SIZE), 200);
    				break;
    			case 2: // jump_animation
    				jump_animation.addFrame(scaleFrame(subImageA, 
        				GamePanel.TILE_SIZE, GamePanel.TILE_SIZE), 200);
    				jump_animation.addFrame(scaleFrame(subImageB, 
            			GamePanel.TILE_SIZE, GamePanel.TILE_SIZE), 200);
    				jump_animation.addFrame(scaleFrame(subImageC, 
            			GamePanel.TILE_SIZE, GamePanel.TILE_SIZE), 200);
    				jump_animation.addFrame(scaleFrame(subImageD, 
            			GamePanel.TILE_SIZE, GamePanel.TILE_SIZE), 200);
    				break;
    			case 3: // move_animation
    				move_animation.addFrame(scaleFrame(subImageA, 
        					GamePanel.TILE_SIZE, GamePanel.TILE_SIZE), 200);
    				move_animation.addFrame(scaleFrame(subImageB, 
        				GamePanel.TILE_SIZE, GamePanel.TILE_SIZE), 200);
    				move_animation.addFrame(scaleFrame(subImageC, 
        				GamePanel.TILE_SIZE, GamePanel.TILE_SIZE), 200);
    				move_animation.addFrame(scaleFrame(subImageD, 
        				GamePanel.TILE_SIZE, GamePanel.TILE_SIZE), 200);
    				move_animation.addFrame(scaleFrame(subImageE, 
        				GamePanel.TILE_SIZE, GamePanel.TILE_SIZE), 200);
    				break;
    			case 4: // celebrate_animation
    				celebrate_animation.addFrame(scaleFrame(subImageA, 
        				GamePanel.TILE_SIZE, GamePanel.TILE_SIZE), 200);
    				celebrate_animation.addFrame(scaleFrame(subImageB, 
            			GamePanel.TILE_SIZE, GamePanel.TILE_SIZE), 200);
    				celebrate_animation.addFrame(scaleFrame(subImageC, 
            			GamePanel.TILE_SIZE, GamePanel.TILE_SIZE), 200);
    				celebrate_animation.addFrame(scaleFrame(subImageD, 
            			GamePanel.TILE_SIZE, GamePanel.TILE_SIZE), 200);
    				celebrate_animation.addFrame(scaleFrame(subImageE, 
            			GamePanel.TILE_SIZE, GamePanel.TILE_SIZE), 200);
    				break;
    			}
    		}
    			
    	anim = this.idle_forward_animation;
    }
    
    public float getSpeed() {
		return this.speed;
	}
	
    public void collideVertical() {
        // check if collided with ground
        if (this.getVelocity().y > 0) {
            onGround = true;
        }
        super.collideVertical();
    }
    
    /**
	    Makes the player jump if the player is on the ground or
	    if forceJump is true.
	*/
	public void jump(boolean forceJump) {
	    if (onGround || forceJump) {
	        onGround = false;
	        this.move(this.getVelocity().x, JUMP_SPEED);
	    }
	}
    
	private void updateAnimaitonState(long elapsedMs) {
		float oldX = worldPosition.x;
		float oldY = worldPosition.y;
		
		float newX = worldPosition.x + velocity.x * elapsedMs;
		float newY = worldPosition.y + velocity.y * elapsedMs;
		
		if(newX != oldX) {
			this.anim = move_animation;
		}
		else if(newY < oldY) {
			this.anim = jump_animation;
		}
		else {
			this.anim = idle_forward_animation;
		}
		
	}
	
	public void interactWithAssest(int index) {
		if(index != -1) { // match foud
			Asset[] assets = this.gamePanel.getAssets();
			
			String assetName = assets[index].getName();
			switch(assetName) {
				case "": 
					break;
			}
		}
	}
	
	@Override
	public void draw(Graphics2D g) {
		// because sprite sheets only draw right, we must manualy reverse for left
		if (this.facing == FacingState.LEFT) {
			g.drawImage(
					this.anim.getImage(), 
					(int)worldPosition.x+anim.getImage().getWidth(gamePanel), 
					(int)worldPosition.y, 
					-this.anim.getImage().getWidth(gamePanel), 
					this.anim.getImage().getHeight(gamePanel), 
					gamePanel);
		}
		else {
			g.drawImage(
					this.anim.getImage(), 
					(int)worldPosition.x, 
					(int)worldPosition.y,
					gamePanel);
		}
	}

    /**
     * Draw the player at an explicit position (in pixels).
     * Used during side shifting to render the paused player at positions on both sides.
     */
    public void drawAt(Graphics2D g, float x, float y) {
        if (this.facing == FacingState.LEFT) {
            g.drawImage(
                    this.anim.getImage(),
                    Math.round(x) + anim.getImage().getWidth(gamePanel),
                    Math.round(y),
                    -this.anim.getImage().getWidth(gamePanel),
                    this.anim.getImage().getHeight(gamePanel),
                    gamePanel);
        } else {
            g.drawImage(
                    this.anim.getImage(),
                    Math.round(x),
                    Math.round(y),
                    gamePanel);
        }
    }

	/**
	    Updates this Sprite's Animation and its position based
	    on the velocity.
	*/
	public void update(long elapsedMs) {
		clampVelocity();
		updateAnimaitonState(elapsedMs);
		
		// Check tile collision
		this.gamePanel.collisionChecker.checkTile(this);
		
		// Check asset collision
		int assetIndex = this.gamePanel.collisionChecker.checkAsset(this);
		interactWithAssest(assetIndex);
		
		worldPosition.x += velocity.x * elapsedMs;
		worldPosition.y += velocity.y * elapsedMs;
	    anim.update(elapsedMs);
	}

}