package com.javagamedev.group.tiles;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.javagamedev.group.GamePanel;
import com.javagamedev.group.tiles.Tile.ImageTile;

public class TileManager {
    
    private GamePanel gamePanel;
    
    private TileSet set;
    private Map map;
    
    public final int MIN_SIDE = 0;
    protected int currentSide = MIN_SIDE;
    public final int MAX_SIDE = 3;

    // horizontal offset in pixels used when the side is narrower than the screen
    private int currentSideOffsetX = 0;

    // --- shifting animation state ---
    private boolean isShifting = false;
    private int nextSideIndex = -1;
    private float shiftProgress = 0f; // pixels moved
    private float shiftDistance = 0f; // total pixels to move
    private float shiftSpeed = 1.0f; // pixels per millisecond (tweak to taste)
    private float shiftDirection = 1.0f; // sign multiplier for progress (handles left vs right)

    public TileManager(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        
        set = new TileSet("res/images/tilesets/spritesheet_test.png", 16, 48);
        this.map = new Map();
        this.map.setSide(readMapJSON("res/maps/map_side0.json"), 0);
        this.map.setSide(readMapJSON("res/maps/map_side1.json"), 1);
        this.map.setSide(readMapJSON("res/maps/map_side2.json"), 2);
        this.map.setSide(readMapJSON("res/maps/map_side3.json"), 3);
    }

    public void test() {
        System.out.println(map.test());
    }

    private Side readMapJSON(String fileLocation) {

        Path path = Paths.get(fileLocation);

        try {
            String jsonContent = new String(Files.readAllBytes(path));
            JSONObject root = new JSONObject(jsonContent);

            int width = root.optInt("mapWidth", 0);
            int height = root.optInt("mapHeight", 0);

            JSONArray layers = root.optJSONArray("layers");
            Side side = new Side(new Dimension(width, height));

            if (layers == null) {
                return side;
            }

            // iterate all layers (preserve order if needed). reading from last to first
            for (int i = layers.length() - 1; i >= 0; i--) {
                JSONObject layerObj = layers.optJSONObject(i);
                if (layerObj == null) continue;

                boolean collider = layerObj.optBoolean("collider", false);
                Layer layer = new Layer(collider);

                JSONArray tiles = layerObj.optJSONArray("tiles");
                if (tiles != null) {
                    for (int t = 0; t < tiles.length(); t++) {
                        JSONObject tileObj = tiles.optJSONObject(t);
                        if (tileObj == null) continue;

                        int id = parseId(tileObj, "id");
                        int x = tileObj.optInt("x", -1);
                        int y = tileObj.optInt("y", -1);

                        if (x < 0 || y < 0) {
                            continue;
                        }

                        Point p = new Point(x, y);
                        ImageTile imageTile = new ImageTile(set.getTileImage(id), p);
                        layer.addTile(imageTile);

                        // Only add to collision set if this layer is marked as collider
                        // and the tile id represents a solid tile.
                        if (layer.isCollider()) {
                            layer.addSolidAt(p);
                        }
                    }
                }

                side.addLayer(layer);
            }

            return side;
        } catch (IOException e) {
            System.err.println("Failed to read JSON file: " + e.getMessage());
            e.printStackTrace();
        } catch (JSONException e) {
            System.err.println("Invalid JSON format: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    private static int parseId(JSONObject obj, String key) {
        try {
            if (obj.has(key)) {
                Object val = obj.get(key);
                if (val instanceof Number) {
                    return ((Number) val).intValue();
                } else {
                    String s = String.valueOf(val);
                    try {
                        return Integer.parseInt(s);
                    } catch (NumberFormatException e) {
                        String digits = s.replaceAll("[^0-9-]", "");
                        if (!digits.isEmpty()) {
                            try {
                                return Integer.parseInt(digits);
                            } catch (NumberFormatException ex) {
                                // ignore
                            }
                        }
                    }
                }
            }
        } catch (JSONException ignored) {
        }
        return -1;
    }

    /**
     * Update must be called each frame to advance the shifting animation.
     */
    public void update(long elapsedms) {
        if (!isShifting) return;
        // advance progress in the correct direction
        shiftProgress += elapsedms * shiftSpeed * shiftDirection;
        // clamp/finish
        if (Math.abs(shiftProgress) >= Math.abs(shiftDistance)) {
            // clamp to exact distance to avoid visual overshoot
            shiftProgress = shiftDistance;
            // finalize shift: make the next side the current
            this.currentSide = this.nextSideIndex;
            this.isShifting = false;
            // reset transient shift state
            this.shiftProgress = 0f;
            this.shiftDistance = 0f;
            this.nextSideIndex = -1;
            this.shiftDirection = 1.0f;
        }
    }

    public void draw(Graphics2D g) {
        // When not shifting, draw the current side centered like before
        if (!isShifting) {
            Side side = map.getSide(currentSide);
            if (side != null) {
                int sideTileWidth = side.getDimensions().width;
                int sidePixelWidth = sideTileWidth * GamePanel.TILE_SIZE;
                if (sidePixelWidth < GamePanel.SCREEN_WIDTH) {
                    currentSideOffsetX = (GamePanel.SCREEN_WIDTH - sidePixelWidth) / 2;
                } else {
                    currentSideOffsetX = 0;
                }
            } else {
                currentSideOffsetX = 0;
            }

            if (currentSideOffsetX != 0) {
                g.translate(currentSideOffsetX, 0);
            }
            map.draw(g, currentSide);
            if (currentSideOffsetX != 0) {
                g.translate(-currentSideOffsetX, 0);
            }
            return;
        }

        // --- shifting: draw both current and next sides at animated positions ---
        Side current = map.getSide(currentSide);
        Side next = map.getSide(nextSideIndex);
        if (current == null || next == null) {
            // fallback: draw only current
            map.draw(g, currentSide);
            return;
        }

        // compute centered offsets for both sides
        int currTileW = current.getDimensions().width;
        int nextTileW = next.getDimensions().width;
        int currPixelW = currTileW * GamePanel.TILE_SIZE;
        int nextPixelW = nextTileW * GamePanel.TILE_SIZE;

        int currCenterOffset = (currPixelW < GamePanel.SCREEN_WIDTH) ? (GamePanel.SCREEN_WIDTH - currPixelW) / 2 : 0;
        int nextCenterOffset = (nextPixelW < GamePanel.SCREEN_WIDTH) ? (GamePanel.SCREEN_WIDTH - nextPixelW) / 2 : 0;

        // initial positions when shift begins (next is placed directly left of current or right for left shift)
        // we'll treat shiftDistance sign to indicate direction (positive -> move right)
        float currStartX = currCenterOffset;
        float nextStartX;
        // determine whether we're shifting to the right (bringing next from left to center) or to the left
        boolean bringingFromLeft = (shiftDistance > 0);
        if (bringingFromLeft) {
            nextStartX = currStartX - nextPixelW; // place next immediately to the left of current
        } else {
            // bringing from right
            nextStartX = currStartX + currPixelW; // place next immediately to the right of current
        }

        // current and next positions during the animation
        float currX = currStartX + shiftProgress;
        float nextX = nextStartX + shiftProgress;

        // Draw next first then current so current appears above if overlapping
        // Use translate for each draw: translate to drawX and call side.draw(g)
        // We calculate relative offsets from each side's centering offset and reuse drawSideAt

        // draw next
        int nextAdditionalOffset = Math.round(nextX - nextCenterOffset);
        drawSideAt(g, nextSideIndex, nextAdditionalOffset);

        // draw current
        int currAdditionalOffset = Math.round(currX - currCenterOffset);
        drawSideAt(g, currentSide, currAdditionalOffset);
    }

    /**
     * Returns the current horizontal draw offset (in pixels) applied when the current side
     * is centered. This can be used by collision code to translate screen/world coordinates
     * into the side's local tile coordinates. During a shift this returns the current
     * animated draw offset for the current side.
     */
    public int getCurrentSideOffsetX() {
        if (!isShifting) return currentSideOffsetX;
        // when shifting, compute based on current start + progress
        Side current = map.getSide(currentSide);
        if (current == null) return 0;
        int currPixelW = current.getDimensions().width * GamePanel.TILE_SIZE;
        int currCenterOffset = (currPixelW < GamePanel.SCREEN_WIDTH) ? (GamePanel.SCREEN_WIDTH - currPixelW) / 2 : 0;
        float currStartX = currCenterOffset;
        return Math.round(currStartX + shiftProgress);
    }

    /**
     * Returns the animated draw offset (in pixels) for a given side index.
     * This takes shifting into account; for non-shifting states it's the same as
     * the centered offset for that side.
     */
    public int getSideDrawOffsetX(int sideIndex) {
        if (!isShifting) {
            Side side = map.getSide(sideIndex);
            if (side == null) return 0;
            int pixelW = side.getDimensions().width * GamePanel.TILE_SIZE;
            return (pixelW < GamePanel.SCREEN_WIDTH) ? (GamePanel.SCREEN_WIDTH - pixelW) / 2 : 0;
        }
        // if shifting, compute for current or next side
        Side current = map.getSide(currentSide);
        Side next = map.getSide(nextSideIndex);
        if (sideIndex == currentSide && current != null) {
            int currPixelW = current.getDimensions().width * GamePanel.TILE_SIZE;
            int currCenterOffset = (currPixelW < GamePanel.SCREEN_WIDTH) ? (GamePanel.SCREEN_WIDTH - currPixelW) / 2 : 0;
            float currStartX = currCenterOffset;
            return Math.round(currStartX + shiftProgress);
        }
        if (sideIndex == nextSideIndex && next != null) {
            int currPixelW = current.getDimensions().width * GamePanel.TILE_SIZE;
            int nextPixelW = next.getDimensions().width * GamePanel.TILE_SIZE;
            int currCenterOffset = (currPixelW < GamePanel.SCREEN_WIDTH) ? (GamePanel.SCREEN_WIDTH - currPixelW) / 2 : 0;
            float nextStartX = (shiftDistance > 0) ? (currCenterOffset - nextPixelW) : (currCenterOffset + currPixelW);
            return Math.round(nextStartX + shiftProgress);
        }
        // otherwise return centered offset
        Side side = map.getSide(sideIndex);
        if (side == null) return 0;
        int pixelW = side.getDimensions().width * GamePanel.TILE_SIZE;
        return (pixelW < GamePanel.SCREEN_WIDTH) ? (GamePanel.SCREEN_WIDTH - pixelW) / 2 : 0;
    }

    public int getCurrentSideIndex() {
        return this.currentSide;
    }

    /**
     * Initiate a shift to the right: bring the next side (index+1) from the left to center.
     * If a shift is already in progress this call is ignored.
     */
    public void shiftRight() {
        if (isShifting) return;
        int candidate = (this.currentSide == this.MAX_SIDE) ? this.MIN_SIDE : this.currentSide + 1;
        startShiftTo(candidate, true);
    }

    /**
     * Initiate a shift to the left: bring the previous side (index-1) from the right to center.
     */
    public void shiftLeft() {
        if (isShifting) return;
        int candidate = (this.currentSide == this.MIN_SIDE) ? this.MAX_SIDE : this.currentSide - 1;
        startShiftTo(candidate, false);
    }

    private void startShiftTo(int candidateIndex, boolean bringFromLeft) {
        Side current = map.getSide(currentSide);
        Side next = map.getSide(candidateIndex);
        if (current == null || next == null) {
            // fallback to instant change
            this.currentSide = candidateIndex;
            return;
        }

        this.nextSideIndex = candidateIndex;
        this.isShifting = true;
        this.shiftProgress = 0f;

        int currPixelW = current.getDimensions().width * GamePanel.TILE_SIZE;
        int nextPixelW = next.getDimensions().width * GamePanel.TILE_SIZE;

        int currCenterOffset = (currPixelW < GamePanel.SCREEN_WIDTH) ? (GamePanel.SCREEN_WIDTH - currPixelW) / 2 : 0;
        int nextCenterOffset = (nextPixelW < GamePanel.SCREEN_WIDTH) ? (GamePanel.SCREEN_WIDTH - nextPixelW) / 2 : 0;

        // compute starting next X relative to centering
        float nextStartX = bringFromLeft ? (currCenterOffset - nextPixelW) : (currCenterOffset + currPixelW);
        // goal is nextCenterOffset
        this.shiftDistance = nextCenterOffset - nextStartX;
        // store direction sign so update() moves progress the right way
        this.shiftDirection = Math.signum(this.shiftDistance);
    }

    public Side getCurrentSide() {
        return map.getSide(currentSide);
    }

    /**
     * Draw a specific side with an additional horizontal pixel offset.
     * xOffset is in pixels; positive moves the side right, negative moves it left.
     */
    public void drawSideAt(Graphics2D g, int sideIndex, int xOffset) {
        Side side = map.getSide(sideIndex);
        if (side == null) return;

        int sideTileWidth = side.getDimensions().width;
        int sidePixelWidth = sideTileWidth * GamePanel.TILE_SIZE;
        int centeringOffset = 0;
        if (sidePixelWidth < GamePanel.SCREEN_WIDTH) {
            centeringOffset = (GamePanel.SCREEN_WIDTH - sidePixelWidth) / 2;
        }

        int totalOffset = centeringOffset + xOffset;
        if (totalOffset != 0) {
            g.translate(totalOffset, 0);
            side.draw(g);
            g.translate(-totalOffset, 0);
        } else {
            side.draw(g);
        }
    }

}