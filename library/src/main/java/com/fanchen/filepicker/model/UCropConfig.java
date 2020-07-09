package com.fanchen.filepicker.model;

public class UCropConfig {
    public int cropMaxWidth = 400;
    public int cropMaxHeight = 400;
    public float cropAspectRatioY = 1;
    public float cropAspectRatioX = 1;

    public UCropConfig(int cropMaxWidth, int cropMaxHeight, float cropAspectRatioY, float cropAspectRatioX) {
        this.cropMaxWidth = cropMaxWidth;
        this.cropMaxHeight = cropMaxHeight;
        this.cropAspectRatioY = cropAspectRatioY;
        this.cropAspectRatioX = cropAspectRatioX;
    }

    public UCropConfig() {
    }
}
