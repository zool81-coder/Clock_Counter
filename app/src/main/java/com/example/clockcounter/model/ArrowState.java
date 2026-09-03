package com.example.clockcounter.model;

import android.widget.ImageView;

public class ArrowState {

    private final ImageView imageView;
    private final float pivotX;
    private final float pivotY;
    private final float stepAngle;

    private int ticks;
    private float currentRotation;

    public ArrowState(ImageView imageView, float pivotX, float pivotY, float stepAngle) {
        this.imageView = imageView;
        this.pivotX = pivotX;
        this.pivotY = pivotY;
        this.stepAngle = stepAngle;
        this.ticks = 0;
        this.currentRotation = 0f;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public float getPivotX() {
        return pivotX;
    }

    public float getPivotY() {
        return pivotY;
    }

    public int getTicks() {
        return ticks;
    }

    public float getCurrentRotation() {
        return currentRotation;
    }

    public void setTicks(int ticks) {
        this.ticks = Math.max(0, ticks % 100);
        this.currentRotation = this.ticks * stepAngle;
    }

    public void reset() {
        ticks = 0;
        currentRotation = 0f;
    }
}
