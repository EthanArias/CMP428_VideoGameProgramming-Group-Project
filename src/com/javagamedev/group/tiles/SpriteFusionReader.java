package com.javagamedev.group.tiles;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * SpriteFusionReader
 * Reads a SpriteFusion-style map.json and builds an in-memory model.
 */
public class SpriteFusionReader {

    public static class Tile {
        public final int id;
        public final int x;
        public final int y;

        public Tile(int id, int x, int y) {
            this.id = id;
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {
            return "Tile{id=" + id + ", x=" + x + ", y=" + y + "}";
        }
    }

    public static class Layer {
        public final String name;
        public final boolean collider;
        public final List<Tile> tiles = new ArrayList<Tile>();

        public Layer(String name, boolean collider) {
            this.name = name;
            this.collider = collider;
        }

        @Override
        public String toString() {
            return "Layer{name='" + name + "', collider=" + collider + 
            		", tiles=" + tiles.size() + "}";
        }
    }

    public static class MapData {
        public final int tileSize;
        public final int mapWidth;
        public final int mapHeight;
        public final List<Layer> layers = new ArrayList<Layer>();

        public MapData(int tileSize, int mapWidth, int mapHeight) {
            this.tileSize = tileSize;
            this.mapWidth = mapWidth;
            this.mapHeight = mapHeight;
        }

        @Override
        public String toString() {
            return "MapData{tileSize=" + tileSize + ", mapWidth=" + mapWidth + 
            		", mapHeight=" + mapHeight + ", layers=" + layers.size() + "}";
        }
    }

    public static MapData readMap(Path jsonPath) throws IOException, JSONException {
        String jsonContent = new String(Files.readAllBytes(jsonPath));
        JSONObject root = new JSONObject(jsonContent);

        int tileSize = root.optInt("tileSize", 0);
        int mapWidth = root.optInt("mapWidth", 0);
        int mapHeight = root.optInt("mapHeight", 0);

        MapData mapData = new MapData(tileSize, mapWidth, mapHeight);

        JSONArray layers = root.optJSONArray("layers");
        if (layers == null) return mapData;

        for (int i = layers.length()-1; i > 0; i--) {
            JSONObject layerObj = layers.optJSONObject(i);
            if (layerObj == null) continue;

            String name = layerObj.optString("name", "unnamed");
            boolean collider = layerObj.optBoolean("collider", false);
            Layer layer = new Layer(name, collider);

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
                    if (x < 0 || y < 0) continue;

                    layer.tiles.add(new Tile(id, x, y));
                }
            }

            mapData.layers.add(layer);
        }

        return mapData;
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

    public static void main(String[] args) {
        Path path = Paths.get("res/maps/map_test.json");
        if (args != null && args.length > 0) {
            path = Paths.get(args[0]);
        }

        try {
        	int limit = 0;
            if (args.length == 0) {
                System.out.println("No arguments were provided.");
                System.out.println("You can manualy set the max tiles to print "
                		+ "per layer in system arguments");
                System.out.println("e.i., java SpriteFusionReader [int]");
                limit = 10;
            } else if (args.length >= 1){
            	limit = Integer.parseInt(args[0]);
            }
            System.out.println();
            
            MapData map = readMap(path);

            // Print basic info
            System.out.println(map);

            // Print each layer summary and first few tiles
            for (Layer layer : map.layers) {
                System.out.println("\nLayer: " + layer.name + 
                		" (collider=" + layer.collider + ")");
                System.out.println("Total tiles: " + layer.tiles.size());
                // print up to first 10 tiles for quick inspection
                limit = Math.min(limit, layer.tiles.size());
                for (int i = 0; i < limit; i++) {
                    System.out.println("  " + layer.tiles.get(i));
                }
            }

        } catch (IOException e) {
            System.err.println("Failed to read JSON file: " + e.getMessage());
            e.printStackTrace();
        } catch (JSONException e) {
            System.err.println("Invalid JSON format: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
