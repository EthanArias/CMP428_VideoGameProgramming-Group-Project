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

    public void draw(Graphics2D g) {
    	// Compute horizontal offset so the side is centered when narrower than the screen
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

    	// Apply translation so drawing is centered. We translate the Graphics2D context
    	if (currentSideOffsetX != 0) {
    		g.translate(currentSideOffsetX, 0);
    	}
    	map.draw(g, currentSide);
    	if (currentSideOffsetX != 0) {
    		// revert translation
    		g.translate(-currentSideOffsetX, 0);
    	}
    }
    
    /**
     * Returns the current horizontal draw offset (in pixels) applied when the current side
     * is centered. This can be used by collision code to translate screen/world coordinates
     * into the side's local tile coordinates.
     */
    public int getCurrentSideOffsetX() {
    	return currentSideOffsetX;
    }
    
    public int getCurrentSideIndex() {
    	return this.currentSide;
    }
    
    public void shiftRight() {
    	if(this.currentSide == this.MAX_SIDE) {
    		this.currentSide = this.MIN_SIDE;
    	}
    	else {
    		this.currentSide +=1;
    	}
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

    /**
     * Shift the current side index one step to the left (wraps around).
     */
    public void shiftLeft() {
        if (this.currentSide == this.MIN_SIDE) {
            this.currentSide = this.MAX_SIDE;
        } else {
            this.currentSide -= 1;
        }
    }
    
}