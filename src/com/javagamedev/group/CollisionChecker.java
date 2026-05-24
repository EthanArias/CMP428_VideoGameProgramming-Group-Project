package com.javagamedev.group;

import java.awt.Point;
import java.awt.Rectangle;

import com.javagamedev.group.entity.Entity;
import com.javagamedev.group.tiles.Side;

public class CollisionChecker {
	private GamePanel gamePanel;
	
	public CollisionChecker(GamePanel gamePanel) {
		this.gamePanel = gamePanel;
	}
	
	/**
     * Check tile collisions for the entity based on its next movement step (velocity).
     * Sets entity.setCollision(true) if any overlapped tile is collideable.
     */
    public void checkTile(Entity entity) {
    	if (entity == null) return;
	    
    	Side tileMap = this.gamePanel.getTileManager().getCurrentSide();
        if (tileMap == null) {
        	return;
        }
        
        // account for horizontal centering offset applied during drawing
        int offsetX = this.gamePanel.getTileManager().getCurrentSideOffsetX();

        // Convert entity world pixel position into tile coordinates by subtracting
        // the draw offset and dividing by TILE_SIZE.
        float worldX = entity.getWorldPosition().x - offsetX;
        float worldY = entity.getWorldPosition().y; // vertical offset not changed

        Point entityPos = this.gamePanel.pixelsToTiles(new Point.Float(worldX, worldY));
        
        // moving right
        if(entity.getVelocity().x > 0) {
        	if(tileMap.isAnyCollideableAt(entityPos.x+1, entityPos.y)) {
				entity.collideHorizontal();
			}
        }
        // moving left
        else if(entity.getVelocity().x < 0) {
        	if(tileMap.isAnyCollideableAt(entityPos.x, entityPos.y)) {
				entity.collideHorizontal();
			}
        }
        
        // jumping
        if(entity.getVelocity().y < 0) {
        	if(tileMap.isAnyCollideableAt(entityPos.x, entityPos.y-1)) {
				entity.collideVertical();
			}
        }
        // falling
        else if(entity.getVelocity().y > 0) {
        	if(tileMap.isAnyCollideableAt(entityPos.x, entityPos.y+1)) {
				entity.collideVertical();
			}
        }
    }
    
}