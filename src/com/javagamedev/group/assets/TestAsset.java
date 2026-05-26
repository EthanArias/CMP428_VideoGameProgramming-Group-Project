package com.javagamedev.group.assets;

import java.awt.Point;
import java.awt.image.BufferedImage;

public class TestAsset extends Asset {

	public TestAsset() {
		BufferedImage img = com.javagamedev.utility.GeneralUtility.loadImage(
				"res/images/tiles/testAsset.png");
		super(
				"TestAsset", 
				(BufferedImage) 
				com.javagamedev.utility.GeneralUtility.scaleImage(img, 48, 48), 
				true, 
				new Point(),
				0);
	}
	
}
