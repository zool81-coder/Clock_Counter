package com.example.clockcounter.logic;

import android.os.Handler;
import android.os.Looper;

public class ClockRunner {

    public interface TickListener {
        void onTick();
    }

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final TickListener tickListener;

    private boolean isRunning = false;
    private long intervalMs = 1000L;

    private final Runnable tickRunnable = new Runnable() {
        @Override
        public void run() {
            if (!isRunning) {
                return;
            }

            tickListener.onTick();
            handler.postDelayed(this, intervalMs);
        }
    };

    public ClockRunner(TickListener tickListener) {
        this.tickListener = tickListener;
    }

    public void start(RunMode runMode) {
        stop();

        switch (runMode) {
            case X1:
                intervalMs = 1000L;
                break;
            case X10:
                intervalMs = 100L;
                break;
            case X100:
                intervalMs = 10L;
                break;
            case STOPPED:
                return;
        }

        isRunning = true;
        handler.post(tickRunnable);
    }

    public void stop() {
        isRunning = false;
        handler.removeCallbacks(tickRunnable);
    }
}
