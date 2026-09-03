package com.example.clockcounter.util;

import android.os.Handler;
import android.os.Looper;

public class RepeatActionHandler {

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final long firstDelayMs;
    private final long repeatDelayMs;
    private boolean isRunning = false;

    private final Runnable internalRunnable = new Runnable() {
        @Override
        public void run() {
            if (!isRunning || action == null) return;
            action.run();
            handler.postDelayed(this, repeatDelayMs);
        }
    };

    private Runnable action;

    public RepeatActionHandler(long firstDelayMs, long repeatDelayMs) {
        this.firstDelayMs = firstDelayMs;
        this.repeatDelayMs = repeatDelayMs;
    }

    public void start(Runnable action) {
        if (isRunning) return;
        this.action = action;
        isRunning = true;

        if (this.action != null) {
            this.action.run();
        }

        handler.postDelayed(internalRunnable, firstDelayMs);
    }

    public void stop() {
        isRunning = false;
        handler.removeCallbacks(internalRunnable);
    }
}
