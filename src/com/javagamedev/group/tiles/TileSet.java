package com.javagamedev.group.tiles;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

public class TileSet {
    private final LinkedList<BufferedImage> set = new LinkedList<BufferedImage>();
    private int size;

    public TileSet(String name, int size) {
        this.size = size;
        LinkedList<BufferedImage> tiles = gridToLine(name, size, size);
        this.set.addAll(tiles);
    }
    
    public TileSet(String name, int size, int scale) {
        this.size = size;
        LinkedList<BufferedImage> tiles = gridToLine(name, size, scale);
        this.set.addAll(tiles);
    }

    private LinkedList<BufferedImage> gridToLine(String name, int size, int scale) {
        BufferedImage original = com.javagamedev.utility.GeneralUtility.loadImage(name);
        if (original == null) {
            throw new RuntimeException("Tilesheet not found: " + name);
        }

        int cols = original.getWidth() / size;
        int rows = original.getHeight() / size;

        LinkedList<BufferedImage> result = new LinkedList<BufferedImage>();

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                BufferedImage sub = original.getSubimage(col * size, row * size, size, size);
                // copy subimage to a new BufferedImage to avoid referencing parent raster
                BufferedImage copy = new BufferedImage(scale, scale, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = copy.createGraphics();
                g.drawImage(sub, 0, 0, scale, scale, null);
                g.dispose();
                result.add(copy);
            }
        }

        // add a transparent backup tile as fallback
        BufferedImage backUp = new BufferedImage(scale, scale, BufferedImage.TYPE_INT_ARGB);
        result.add(backUp);

        return result;
    }

    public LinkedList<BufferedImage> getEntireSet() {
        return this.set;
    }

    public int getSize() {
        return this.size;
    }

    public BufferedImage getTileImage(int id) {
        if (id >= 0 && id < set.size()) {
            return set.get(id);
        } else {
            return set.getLast();
        }
    }
}
