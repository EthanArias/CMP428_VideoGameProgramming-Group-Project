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

    public TileManager(GamePanel gamePanel) {
    	this.gamePanel = gamePanel;
    	
        set = new TileSet("res/images/tilesets/tileGridTest.png", 64);
        Side side1 = readMapJSON("res/maps/mapTest_side1.json");
        this.map = new Map(side1, side1, side1, side1);
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
                        if (layer.isCollider() && isSolidTile(id)) {
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

    /**
     * Simple heuristic to decide whether a tile id should be considered solid.
     * Replace this with editor metadata or a configurable set of solid IDs.
     *
     * Current rule:
     *  - id < 0 : not solid
     *  - id == 0 : treat as blank / decorative (not solid)
     *  - id > 0 : solid by default
     */
    private static boolean isSolidTile(int id) {
        return id > 0;
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
    	map.draw(g, 0);
    }
    
}
