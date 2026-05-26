package com.javagamedev.group.assets;

import java.awt.Point;
import java.awt.image.BufferedImage;

public class KeyAsset extends Asset {

	public KeyAsset() {
		BufferedImage img = com.javagamedev.utility.GeneralUtility.loadImage(
				"res/images/tiles/Key.png");
		super(
				"LockedDoor", 
				(BufferedImage) 
				com.javagamedev.utility.GeneralUtility.scaleImage(img, 48, 48), 
				false, 
				new Point(),
				0);
	}
	
}