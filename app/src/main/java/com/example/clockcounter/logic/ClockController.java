package com.example.clockcounter.logic;

import com.example.clockcounter.model.ArrowState;
import com.example.clockcounter.model.ArrowType;
import com.example.clockcounter.ui.ClockViewState;

public class ClockController {

    private final ArrowState firstArrow;
    private final ArrowState secondArrow;
    private final ArrowState thirdArrow;

    private ArrowType selectedArrow = ArrowType.FIRST;
    private double totalHours = 0.0;
    private RunMode runMode = RunMode.STOPPED;

    public ClockController(ArrowState firstArrow, ArrowState secondArrow, ArrowState thirdArrow) {
        this.firstArrow = firstArrow;
        this.secondArrow = secondArrow;
        this.thirdArrow = thirdArrow;
        recalculateArrowsFromTotalHours();
    }

    public ArrowType getSelectedArrow() {
        return selectedArrow;
    }

    public void selectNextArrow() {
        switch (selectedArrow) {
            case FIRST:
                selectedArrow = ArrowType.SECOND;
                break;
            case SECOND:
                selectedArrow = ArrowType.THIRD;
                break;
            case THIRD:
                selectedArrow = ArrowType.FIRST;
                break;
        }
    }

    public ArrowState getSelectedArrowState() {
        return switch (selectedArrow) {
            case FIRST -> firstArrow;
            case SECOND -> secondArrow;
            case THIRD -> thirdArrow;
        };
    }

    public ArrowState getFirstArrow() {
        return firstArrow;
    }

    public ArrowState getSecondArrow() {
        return secondArrow;
    }

    public ArrowState getThirdArrow() {
        return thirdArrow;
    }

    public double getTotalHours() {
        return totalHours;
    }

    public String formatHours(double value) {
        if (value == Math.floor(value)) {
            return String.valueOf((int) value);
        }
        return String.valueOf(value);
    }

    public String getInputHoursValue() {
        return formatHours(totalHours);
    }

    public void setTotalHours(double totalHours) {
        this.totalHours = Math.max(0.0, totalHours);
        recalculateArrowsFromTotalHours();
    }

    public boolean decrementOneTick() {
        if (totalHours < 0.1) {
            return false;
        }

        totalHours -= 0.1;
        totalHours = roundToOneDecimal(totalHours);
        recalculateArrowsFromTotalHours();
        return true;
    }

    private double roundToOneDecimal(double value) {
        return Math.round(value * 10.0) / 10.0;
    }

    public void resetAll() {
        totalHours = 0L;
        selectedArrow = ArrowType.FIRST;
        runMode = RunMode.STOPPED;
        recalculateArrowsFromTotalHours();
    }

    public void recalculateArrowsFromTotalHours() {
        int thirdTicks = (int) Math.floor(((totalHours * 10) + 1e-9) % 100);
        int secondTicks = (int) Math.floor(((totalHours / 3.0) + 1e-9) % 100);
        int firstTicks = (int) Math.floor(((totalHours / 30.0) + 1e-9) % 100);

        thirdArrow.setTicks(thirdTicks);
        secondArrow.setTicks(secondTicks);
        firstArrow.setTicks(firstTicks);
    }

    public int getSelectedInputValue() {
        return switch (selectedArrow) {
            case FIRST -> (int) (totalHours / 30);
            case SECOND -> (int) (totalHours / 3);
            case THIRD -> (int) totalHours;
        };
    }

    public void applySelectedInputValue(int value) {
        int safeValue = Math.max(0, value);

        switch (selectedArrow) {
            case FIRST:
                setTotalHours((long) safeValue * 30L);
                break;
            case SECOND:
                setTotalHours((long) safeValue * 3L);
                break;
            case THIRD:
                setTotalHours(safeValue);
                break;
        }
    }

    public double calculatePreviewTime(double inputValue) {
        return Math.max(0.0, inputValue);
    }

    public RunMode getRunMode() {
        return runMode;
    }

    public RunMode cycleRunMode() {
        switch (runMode) {
            case STOPPED:
                runMode = RunMode.X1;
                break;
            case X1:
                runMode = RunMode.X10;
                break;
            case X10:
                runMode = RunMode.X100;
                break;
            case X100:
                runMode = RunMode.STOPPED;
                break;
        }
        return runMode;
    }

    public ClockViewState buildViewState() {
        return new ClockViewState(
                selectedArrow,
                formatHours(totalHours),
                formatHours(totalHours),
                runMode,
                calculateExtraElementRotation() // Передаем расчет
        );
    }

    public double getStepHoursForSelectedArrow() {
        return switch (selectedArrow) {
            case FIRST -> 30.0;
            case SECOND -> 3.0;
            case THIRD -> 0.1;
        };
    }

    public void incrementBySelectedArrowStep() {
        totalHours += getStepHoursForSelectedArrow();
        totalHours = roundToOneDecimal(totalHours);
        recalculateArrowsFromTotalHours();
    }

    public boolean decrementBySelectedArrowStep() {
        double step = getStepHoursForSelectedArrow();

        if (totalHours < step) {
            return false;
        }

        totalHours -= step;
        totalHours = roundToOneDecimal(totalHours);
        recalculateArrowsFromTotalHours();
        return true;
    }

    public void incrementOneTick() {
        totalHours += 0.1;
        totalHours = roundToOneDecimal(totalHours);
        recalculateArrowsFromTotalHours();
    }

    public float calculateExtraElementRotation() {
        // Вводные данные:
        // < 2880 -> 0°
        if (totalHours < 2880.0) return 0f;         // >= 2880 -> -3°
        if (totalHours < 2910.0) return -3f;        // >= 2910 -> -6°
        if (totalHours < 2940.0) return -6f;        // >= 2940 -> -9°
        if (totalHours < 2970.0) return -9f;        // >= 2940 -> -12°
        if (totalHours < 3000.0) return -12f;       // >= 2970 -> -15°
        if (totalHours < 3030.0) return -15f;       // >= 3000 -> -18°
        if (totalHours < 3060.0) return -18f;       // >= 3000 -> -21°
        if (totalHours < 3090.0) return -21f;       // >= 3000 -> -24°
        if (totalHours < 3120.0) return -24f;       // >= 3000 -> -27°
        if (totalHours < 3150.0) return -27f;       // >= 3000 -> -30°
        if (totalHours < 3180.0) return -30f;       // >= 3000 -> -33°

        return -33f; // Максимальный поворот на -33 градуса для всего, что >= 3180
    }
}
