package com.tesis.yudith.showmethepast.view.ar;

import android.graphics.Canvas;

public class CanvasInformation {
    public Canvas canvas;
    public int w;
    public int h;
    public int max;
    public int delta;

    public CanvasInformation(Canvas canvas, int w, int h, int delta, int max) {

        this.canvas = canvas;

        this.w = w;
        this.h = h;
        this.delta = delta;
        this.max = max;
    }
}
