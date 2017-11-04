package com.tesis.yudith.showmethepast.domain.collections.childs;

import java.io.Serializable;

public class ImageSizeInformation implements Serializable {
    private double alterationWidth;
    private double alterationHeight;
    private double scale;
    private double screenWidth;
    private double screenHeight;
    private double screenDensityWidth;
    private double screenDensityHeight;

    public double getAlterationWidth() {
        return alterationWidth;
    }

    public void setAlterationWidth(double alterationWidth) {
        this.alterationWidth = alterationWidth;
    }

    public double getAlterationHeight() {
        return alterationHeight;
    }

    public void setAlterationHeight(double alterationHeight) {
        this.alterationHeight = alterationHeight;
    }

    public double getScale() {
        return scale;
    }

    public void setScale(double scale) {
        this.scale = scale;
    }

    public double getScreenWidth() {
        return screenWidth;
    }

    public void setScreenWidth(double screenWidth) {
        this.screenWidth = screenWidth;
    }

    public double getScreenHeight() {
        return screenHeight;
    }

    public void setScreenHeight(double screenHeight) {
        this.screenHeight = screenHeight;
    }

    public double getScreenDensityWidth() {
        return screenDensityWidth;
    }

    public void setScreenDensityWidth(double screenDensityWidth) {
        this.screenDensityWidth = screenDensityWidth;
    }

    public double getScreenDensityHeight() {
        return screenDensityHeight;
    }

    public void setScreenDensityHeight(double screenDensityHeight) {
        this.screenDensityHeight = screenDensityHeight;
    }
}
