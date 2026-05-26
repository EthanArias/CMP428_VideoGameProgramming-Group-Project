package com.javagamedev.group.assets;

import java.awt.Point;
import java.awt.image.BufferedImage;

public class TestAsset extends Asset {

	public TestAsset() {
		BufferedImage spriteSheet = com.javagamedev.utility.GeneralUtility.loadImage(
				"res/images/tiles/testAsset.png");
		BufferedImage img = spriteSheet.getSubimage(0, 112, 16, 16);
		super(
				"TestAsset", 
				(BufferedImage) 
				com.javagamedev.utility.GeneralUtility.scaleImage(img, 48, 48), 
				true, 
				new Point(),
				0);
	}
	
}
