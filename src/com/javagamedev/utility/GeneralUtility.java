package com.javagamedev.utility;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class GeneralUtility {

	/**
     * Reads an image file from the specified path into a BufferedImage.
     *
     * @param filePath The path to the image file (e.g., "images/myimage.png").
     * @return The BufferedImage created from the file, or null if reading fails.
     */
    public static BufferedImage loadImage(String filePath) {
        BufferedImage image = null;
        try {
            // Create a File object from the path
            File file = new File(filePath);

            // Verify if the file exists and is readable (optional but recommended)
            if (!file.exists()) {
                System.err.println("Image file not found at: " 
            + file.getAbsolutePath());
                return null;
            }
            if (!file.canRead()) {
                System.err.println("No read permissions for file at: " 
            + file.getAbsolutePath());
                return null;
            }

            // Read the image using ImageIO
            image = ImageIO.read(file);
            
            // ImageIO.read() returns null if no appropriate reader is found
            if (image == null) {
                System.err.println("No image reader found for the file type.");
            }

        } catch (IOException e) {
            // Handle IO exceptions (e.g., file not found, read error)
            System.err.println("Error reading image file: " + e.getMessage());
            e.printStackTrace();
        }
        return image;
    }

	public static Image scaleImage(BufferedImage originalTile, int newWidth, int newHeight) {
		BufferedImage scaledImage = 
				new BufferedImage(newWidth, newHeight, originalTile.getType());
		scaledImage.getGraphics().drawImage(
				originalTile, 0, 0, newWidth, newHeight, null);
		return scaledImage;
	}
	
	public static Image flipHorizontally(BufferedImage source) {
	    int w = source.getWidth();
	    int h = source.getHeight();
	    
	    // Create a new image of the same type and size
	    BufferedImage flipped = new BufferedImage(w, h, source.getType());
	    Graphics2D g = flipped.createGraphics();
	    
	    /**
	     * Draw the source image into the flipped destination.
	     * We map the source's (0, 0) to (w, 0) and use -w as the width
	     * to effectively "flip" the drawing direction.
	     */
	    g.drawImage(source, w, 0, -w, h, null);
	    g.dispose();
	    
	    return flipped;
	}
	
    public static ImageIcon createDarkenedIcon(ImageIcon originalIcon) {
        int width = originalIcon.getIconWidth();
        int height = originalIcon.getIconHeight();
        
        // 1. Create a transparent buffered image
        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = bi.createGraphics();

        // 2. Draw the original icon
        g2d.drawImage(originalIcon.getImage(), 0, 0, null);

        // 3. Apply the dark mask (e.g., 50% black)
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, width, height);

        g2d.dispose();
        return new ImageIcon(bi);
    }
    
    public static ImageIcon createSmallerIcon(ImageIcon originalIcon) {
        int width = originalIcon.getIconWidth();
        int height = originalIcon.getIconHeight();
        
        // 1. Create a transparent buffered image
        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = bi.createGraphics();

        // 2. Draw the original icon
        g2d.drawImage(originalIcon.getImage(), 
        		2, 2, 
        		originalIcon.getIconWidth()-4, originalIcon.getIconHeight()-4,
        		null);

        g2d.dispose();
        return new ImageIcon(bi);
    }
	
}
