package com.javagamedev.group.entity;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.javagamedev.graphics.Animation;
import com.javagamedev.group.Game;
import com.javagamedev.group.GamePanel;

public class Player extends Entity {
	
    public Player(GamePanel gamePanel){
    	super(gamePanel);
    	// set a default speed so movement input actually moves the player
    	this.speed = 0.35f;
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