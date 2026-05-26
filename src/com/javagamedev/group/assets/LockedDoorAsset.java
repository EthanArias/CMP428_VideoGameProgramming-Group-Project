package com.javagamedev.group.assets;

import java.awt.Point;
import java.awt.image.BufferedImage;

public class LockedDoorAsset extends Asset {

    public LockedDoorAsset() {
        // call super first with a placeholder image (null) then set image
        super("LockedDoor", null, false, new Point(), 0);
        BufferedImage img = com.javagamedev.utility.GeneralUtility.loadImage(
                "res/images/tiles/lockedDoor.png");
        if (img != null) {
            BufferedImage scaled = (BufferedImage) com.javagamedev.utility.GeneralUtility.scaleImage(img, 48, 96);
            this.setImage(scaled);
        }
    }

}