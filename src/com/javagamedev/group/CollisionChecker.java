package com.javagamedev.group;

import java.awt.Point;
import java.awt.Rectangle;

import com.javagamedev.group.assets.Asset;
import com.javagamedev.group.entity.Entity;
import com.javagamedev.group.entity.Player;
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
    
    public int checkAsset(Entity entity) {
    	int index = -1;
    	
    	Asset[] assets = this.gamePanel.getAssets();
    	if (assets == null) return -1;
    	
    	// current side and its draw offset (panel-space)
    	int currentSide = this.gamePanel.getTileManager().getCurrentSideIndex();
    	int sideOffset = this.gamePanel.getTileManager().getSideDrawOffsetX(currentSide);
    	Side tileMap = this.gamePanel.getTileManager().getCurrentSide();
    	
    	for(int i=0; i<assets.length; i++) {
    		if(assets[i] != null) {
    			// skip assets that belong to other sides
    			if (assets[i].getSide() != currentSide) continue;
    			
    			// entity's predicted panel-space solid area rect (include velocity)
    			Rectangle entityBox = new Rectangle(
    					(int)Math.floor(entity.getWorldPosition().x + entity.getVelocity().x) + entity.getHitBox().x,
    					(int)Math.floor(entity.getWorldPosition().y + entity.getVelocity().y) + entity.getHitBox().y,
    					entity.getHitBox().width,
    					entity.getHitBox().height);
    			
    			// asset panel-space position (asset position is stored in side-local pixels)
    			int assetPanelX = assets[i].getPosition().x + sideOffset;
    			int assetPanelY = assets[i].getPosition().y;
    			int assetW = (assets[i].getImage() != null) ? assets[i].getImage().getWidth() : assets[i].getBounds().width;
    			int assetH = (assets[i].getImage() != null) ? assets[i].getImage().getHeight() : assets[i].getBounds().height;
    			
    			Rectangle assetBox = new Rectangle(assetPanelX, assetPanelY, assetW, assetH);
    			
    			if(entityBox.intersects(assetBox)) {
    				if(assets[i].hasCollision()) {
    					// Resolve collision against the asset itself by inspecting the intersection
    					Rectangle inter = entityBox.intersection(assetBox);
    					if (inter.width < inter.height) {
    						// Horizontal penetration is smaller -> horizontal collision
    						entity.collideHorizontal();
    					} else {
    						// Vertical penetration is smaller or equal -> vertical collision
    						entity.collideVertical();
    					}
    				}
    				if(entity instanceof Player) {
    		    		index = i;
    		    	}
    			}
    		}
    	}
    	
    	return index;
    }
    
}