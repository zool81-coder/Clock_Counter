package com.example.clockcounter.ui;

import com.example.clockcounter.logic.RunMode;
import com.example.clockcounter.model.ArrowType;

public class ClockViewState {
    private final float extraRotation;

    private final ArrowType selectedArrow;
    private final String selectedHoursText;
    private final String totalTimeText;
    private final RunMode runMode;

    public ClockViewState(ArrowType selectedArrow, String selectedHoursText, String totalTimeText, RunMode runMode, float extraRotation) {
        this.selectedArrow = selectedArrow;
        this.selectedHoursText = selectedHoursText;
        this.totalTimeText = totalTimeText;
        this.runMode = runMode;
        this.extraRotation = extraRotation;
    }

    public float getExtraRotation() { return extraRotation; }

    public ArrowType getSelectedArrow() {
        return selectedArrow;
    }

    public String getSelectedHoursText() {
        return selectedHoursText;
    }

    public String getTotalTimeText() {
        return totalTimeText;
    }

    public RunMode getRunMode() {
        return runMode;
    }
}
