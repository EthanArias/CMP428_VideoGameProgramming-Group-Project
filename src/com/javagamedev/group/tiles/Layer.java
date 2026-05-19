package com.javagamedev.group.tiles;

import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.javagamedev.group.tiles.Tile.AnimationTile;
import com.javagamedev.group.tiles.Tile.BlankTile;

/**
 * Layer holds drawable tiles and, optionally, a sparse set of solid tile coordinates
 * used for collision checks. Rendering and collision storage are separated.
 */
public class Layer {

    private final boolean collider;
    private final ArrayList<Tile> tiles = new ArrayList<Tile>();
    private final Set<Point> solidTiles = new HashSet<Point>();

    public Layer(boolean collider) {
        this.collider = collider;
    }

    /**
     * Add a tile for rendering. If this layer is a collider and the tile position
     * should be considered solid, also call addSolidAt.
     */
    public void addTile(Tile tile) {
        this.tiles.add(tile);
    }

    /**
     * Mark a tile coordinate as solid for collision checks.
     * Use only when layer.isCollider() is true.
     */
    public void addSolidAt(Point p) {
        if (p != null) {
            // store a defensive copy to avoid external mutation issues
            this.solidTiles.add(new Point(p));
        }
    }

    public boolean isCollider() {
        return collider;
    }

    public List<Tile> getTiles() {
        return tiles;
    }

    /**
     * Check whether a tile coordinate is solid (collision).
     */
    public boolean isSolidAt(Point p) {
        return solidTiles.contains(p);
    }

    public void update(long elapsedms) {
        for (Tile tile : tiles) {
            if (tile instanceof AnimationTile) {
                tile.update(elapsedms);
            }
        }
    }

    public void draw(Graphics2D g) {
        for (Tile tile : tiles) {
            if (!(tile instanceof BlankTile)) {
                tile.draw(g);
            }
        }
    }

    public String test() {
        StringBuilder result = new StringBuilder();
        for (Tile tile : tiles) {
            result.append(tile.test()).append("\n");
        }
        return result.toString();
    }
}
