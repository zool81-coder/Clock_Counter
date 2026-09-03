package com.example.clockcounter.ui;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.clockcounter.R;
import com.example.clockcounter.logic.RunMode;
import com.example.clockcounter.model.ArrowType;

public class ClockUiRenderer {
    private final ImageView offsetRotatingImage;
    private final EditText inputHours;
    private final TextView resultText;
    private final ImageButton selectButton;
    private final Button startButton;

    public ClockUiRenderer(EditText inputHours, TextView resultText, ImageButton selectButton, Button startButton, ImageView offsetRotatingImage) {
        this.inputHours = inputHours;
        this.resultText = resultText;
        this.selectButton = selectButton;
        this.startButton = startButton;
        this.offsetRotatingImage = offsetRotatingImage; // инициализация
    }

    public void renderAll(ClockViewState state) {
        renderSelectedHours(state.getSelectedHoursText());
        renderResult(state.getTotalTimeText());
        updateSelectedArrowIcon(state.getSelectedArrow());
        updateStartButton(state.getRunMode());
        // Обновляем поворот дополнительного элемента
        renderExtraElement(state.getExtraRotation());
    }

    private void renderExtraElement(float angle) {
        // Используем плавную анимацию или мгновенный поворот
        // В вашем случае шаги по 1 градусу редкие, можно мгновенно:
        if (offsetRotatingImage != null) {
            offsetRotatingImage.setRotation(angle);
        }
    }

    public void renderSelectedHours(String hoursText) {
        String currentValue = inputHours.getText().toString();

        if (!currentValue.equals(hoursText)) {
            inputHours.setText(hoursText);
            inputHours.setSelection(inputHours.getText().length());
        }
    }

    public void renderResult(String totalTimeText) {
        resultText.setText(totalTimeText);
    }

    public void clearResult() {
        resultText.setText("");
    }

    public void updateSelectedArrowIcon(ArrowType arrowType) {
        switch (arrowType) {
            case FIRST:
                selectButton.setImageResource(R.drawable.cifer_hours_1);
                break;
            case SECOND:
                selectButton.setImageResource(R.drawable.cifer_hour_3);
                break;
            case THIRD:
                selectButton.setImageResource(R.drawable.cifer_hour_2);
                break;
        }
    }

    public void updateStartButton(RunMode runMode) {
        switch (runMode) {
            case STOPPED:
                startButton.setText("Пуск x1");
                break;
            case X1:
                startButton.setText("Пуск x10");
                break;
            case X10:
                startButton.setText("Пуск x100");
                break;
            case X100:
                startButton.setText("Стоп");
                break;
        }
    }
}
