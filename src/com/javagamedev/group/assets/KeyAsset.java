package com.javagamedev.group.assets;

import java.awt.Point;
import java.awt.image.BufferedImage;

public class KeyAsset extends Asset {

    public KeyAsset() {
        super("Key", null, false, new Point(), 0);
        BufferedImage img = com.javagamedev.utility.GeneralUtility.loadImage(
                "res/images/tiles/Key.png");
        if (img != null) {
            BufferedImage scaled = (BufferedImage) com.javagamedev.utility.GeneralUtility.scaleImage(img, 48, 48);
            this.setImage(scaled);
        }
    }

}