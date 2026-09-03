package com.example.clockcounter.ui;

import android.view.animation.RotateAnimation;
import android.widget.ImageView;

public class ArrowAnimator {

    private final long durationMs;

    public ArrowAnimator(long durationMs) {
        this.durationMs = durationMs;
    }

    public void animateForward(ImageView imageView, float pivotX, float pivotY, float from, float to) {
        float adjustedTo = to;

        if (to < from) {
            adjustedTo = to + 360f;
        }

        RotateAnimation rotate = new RotateAnimation(
                from,
                adjustedTo,
                RotateAnimation.RELATIVE_TO_SELF, pivotX,
                RotateAnimation.RELATIVE_TO_SELF, pivotY
        );
        rotate.setDuration(durationMs);
        rotate.setFillAfter(true);
        imageView.startAnimation(rotate);
    }

    public void animateBackward(ImageView imageView, float pivotX, float pivotY, float from, float to) {
        float adjustedTo = to;

        if (to > from) {
            adjustedTo = to - 360f;
        }

        RotateAnimation rotate = new RotateAnimation(
                from,
                adjustedTo,
                RotateAnimation.RELATIVE_TO_SELF, pivotX,
                RotateAnimation.RELATIVE_TO_SELF, pivotY
        );
        rotate.setDuration(durationMs);
        rotate.setFillAfter(true);
        imageView.startAnimation(rotate);
    }

    public void animateDirect(ImageView imageView, float pivotX, float pivotY, float from, float to) {
        RotateAnimation rotate = new RotateAnimation(
                from,
                to,
                RotateAnimation.RELATIVE_TO_SELF, pivotX,
                RotateAnimation.RELATIVE_TO_SELF, pivotY
        );
        rotate.setDuration(durationMs);
        rotate.setFillAfter(true);
        imageView.startAnimation(rotate);
    }

    public void clear(ImageView imageView) {
        imageView.clearAnimation();
    }
}
