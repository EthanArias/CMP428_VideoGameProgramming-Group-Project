package com.javagamedev.group;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;

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
	public java.awt.Point.Float checkTile(Entity entity, long elapsedMs) {
	    if (entity == null) return new java.awt.Point.Float(1f, 1f);

	    // required data
	    java.awt.Point.Float worldPos = entity.getWorldPosition();
	    Rectangle hitBox = entity.getHitBox();
	    java.awt.Point.Float velocity = entity.getVelocity();

	    if (worldPos == null || hitBox == null) return new java.awt.Point.Float(1f, 1f);
	    if (velocity == null) velocity = new java.awt.Point.Float(0f, 0f);

	    // compute actual displacement this tick
	    float dx = velocity.x * elapsedMs;
	    float dy = velocity.y * elapsedMs;

	    int tileSize = GamePanel.TILE_SIZE;
	    Side tileMap = this.gamePanel.getTileManager().getTempSide();
	    if (tileMap == null) return new java.awt.Point.Float(1f, 1f);

	    int maxCol = Math.max(0, tileMap.getDimensions().width - 1);
	    int maxRow = Math.max(0, tileMap.getDimensions().height - 1);

	    // Helper lambda to test an AABB for collision
	    java.util.function.BiFunction<Float, Float, Boolean> aabbCollides = (offX, offY) -> {
	        int futureLeft = (int) Math.floor(worldPos.x + offX + hitBox.x);
	        int futureTop = (int) Math.floor(worldPos.y + offY + hitBox.y);
	        int futureRight = futureLeft + hitBox.width - 1;
	        int futureBottom = futureTop + hitBox.height - 1;

	        int leftCol = (int) Math.floor((double) futureLeft / tileSize);
	        int rightCol = (int) Math.floor((double) futureRight / tileSize);
	        int topRow = (int) Math.floor((double) futureTop / tileSize);
	        int bottomRow = (int) Math.floor((double) futureBottom / tileSize);

	        leftCol = Math.max(0, Math.min(leftCol, maxCol));
	        rightCol = Math.max(0, Math.min(rightCol, maxCol));
	        topRow = Math.max(0, Math.min(topRow, maxRow));
	        bottomRow = Math.max(0, Math.min(bottomRow, maxRow));

	        for (int col = leftCol; col <= rightCol; col++) {
	            for (int row = topRow; row <= bottomRow; row++) {
	                if (tileMap.isAnyCollideableAt(col, row)) {
	                    return true;
	                }
	            }
	        }
	        return false;
	    };

	    // Test X-only (apply dx, but no dy)
	    boolean collideX = aabbCollides.apply(dx, 0f);

	    // Test Y-only (apply dy, but no dx)
	    boolean collideY = aabbCollides.apply(0f, dy);

	    // Set entity collision flag if either axis is blocked
	    boolean anyCollision = collideX || collideY;
	    entity.setCollision(anyCollision);

	    float allowedX = collideX ? 0f : 1f;
	    float allowedY = collideY ? 0f : 1f;

	    return new java.awt.Point.Float(allowedX, allowedY);
	}
    
}
