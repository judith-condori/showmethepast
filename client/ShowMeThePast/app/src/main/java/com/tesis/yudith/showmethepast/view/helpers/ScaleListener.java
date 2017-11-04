package com.tesis.yudith.showmethepast.view.helpers;

import android.view.ScaleGestureDetector;

public class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
    public interface ScaleExtraListener {
        void onScaleChanged(float newScale);
    }

    private float mScaleFactor;
    private float minScale;
    private float maxScale;
    private ScaleExtraListener scaleExtraListener;

    public ScaleListener(float minScale, float maxScale, float startScaleFactor, ScaleExtraListener scaleExtraListener) {
        this.minScale = minScale;
        this.maxScale = maxScale;
        this.mScaleFactor = startScaleFactor;
        this.scaleExtraListener = scaleExtraListener;
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        this.mScaleFactor *= detector.getScaleFactor();

        // Don't let the object get too small or too large.
        this.mScaleFactor = Math.max(this.minScale, Math.min(this.mScaleFactor, this.maxScale));

        if (this.scaleExtraListener != null) {
            this.scaleExtraListener.onScaleChanged(this.mScaleFactor);
        }

        return true;
    }

    public void setScale(float scale) {
        if (scale >= this.minScale && scale <= this.maxScale) {
            this.mScaleFactor = scale;
        }
    }

    public float getScaleFactor() {
        return mScaleFactor;
    }

    public float getMinScale() {
        return minScale;
    }

    public void setMinScale(float minScale) {
        this.minScale = minScale;
    }

    public float getMaxScale() {
        return maxScale;
    }

    public void setMaxScale(float maxScale) {
        this.maxScale = maxScale;
    }
}
