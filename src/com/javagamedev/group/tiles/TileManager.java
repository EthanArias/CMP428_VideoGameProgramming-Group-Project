package com.javagamedev.group.tiles;

import java.awt.Dimension;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.javagamedev.group.tiles.Tile.ImageTile;

public class TileManager {
	private TileSet set;
	private Map map;
	
	public TileManager() {
		set = new TileSet("res/images/tilesets/tileGrid.png", 64);
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
	        Side side = new Side();
	        
	        for (int i = layers.length()-1; i > 0; i--) {
	            JSONObject layerObj = layers.optJSONObject(i);
	            if (layerObj == null) continue;

	            // String name = layerObj.optString("name", "unnamed");
	            boolean collider = layerObj.optBoolean("collider", false);
	            Layer layer = new Layer(new Dimension(width, height), collider);

	            JSONArray tiles = layerObj.optJSONArray("tiles");
	            if (tiles != null) {
	                for (int t = 0; t < tiles.length(); t++) {
	                    JSONObject tileObj = tiles.optJSONObject(t);
	                    if (tileObj == null) continue;

	                    // id in the file may be a string; handle both string and numeric
	                    int id = parseId(tileObj, "id");
	                    int x = tileObj.optInt("x", -1);
	                    int y = tileObj.optInt("y", -1);

	                    // skip invalid coordinates
	                    if (x < 0 || y < 0) {
	                    	continue;
	                    }
	                    
	                    layer.setTile((new ImageTile(set.getTileImage(id))), x, y);
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
        // Accept numeric or string id values
        try {
            if (obj.has(key)) {
                Object val = obj.get(key);
                if (val instanceof Number) {
                    return ((Number) val).intValue();
                } else {
                    // try parse string
                    String s = String.valueOf(val);
                    try {
                        return Integer.parseInt(s);
                    } catch (NumberFormatException e) {
                        // fallback: try to strip non-digits
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
        } catch (JSONException ignored) {}
        return -1; // unknown id
    }
	
}
