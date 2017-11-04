package com.tesis.yudith.showmethepast.view.ar;

import android.graphics.PointF;

import com.tesis.yudith.showmethepast.rotation.UVWVector;

public class ArCalibratorInformation {
    private UVWVector rotation;
    private PointF screenPoint;

    public ArCalibratorInformation() {
        this.rotation = new UVWVector();
        this.screenPoint = new PointF();
    }

    public ArCalibratorInformation(UVWVector rotation, int xScreen, int yScreen) {
        this.rotation = rotation;
        this.screenPoint = new PointF(xScreen, yScreen);
    }

    public UVWVector getRotation() {
        return rotation;
    }

    public void setRotation(UVWVector rotation) {
        this.rotation = rotation;
    }

    public PointF getScreenPoint() {
        return screenPoint;
    }

    public void setScreenPoint(PointF screenPoint) {
        this.screenPoint = screenPoint;
    }
}
