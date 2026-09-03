package com.example.clockcounter;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.clockcounter.audio.SoundManager;
import com.example.clockcounter.logic.ClockController;
import com.example.clockcounter.logic.ClockRunner;
import com.example.clockcounter.logic.RunMode;
import com.example.clockcounter.model.ArrowState;
import com.example.clockcounter.ui.ArrowAnimator;
import com.example.clockcounter.ui.ClockUiRenderer;

public class MainActivity extends AppCompatActivity {
    private ImageView offsetRotatingImage;
    private ImageView rotatingImage;                // наш PNG‑элемент
    private ObjectAnimator rotatingAnimator;        // аниматор вращения

    private Button startButton;
    private ClockRunner clockRunner;

    private static final float PIVOT_X = 0.5f;
    private static final float FIRST_ARROW_PIVOT_Y = 0.6f;
    private static final float OTHER_ARROWS_PIVOT_Y = 0.74f;
    private static final float STEP_ANGLE = 3.6f;

    private static final long FIRST_REPEAT_DELAY_MS = 500L;
    private static final long REPEAT_DELAY_MS = 50L;
    private static final long ANIMATION_DURATION_MS = 200L;

    private ImageView imageView2;
    private ImageView imageView3;
    private ImageView imageView4;
    private EditText inputHours;
    private TextView resultText;
    private Button resetButton;
    private ImageButton forwardButton;
    private ImageButton backButton;
    private ImageButton selectButton;
    private ImageButton applyButton;

    private ClockController controller;
    private ClockUiRenderer uiRenderer;
    private SoundManager soundManager;

    private final Handler handler = new Handler(Looper.getMainLooper());

    private boolean isRunning = false;
    private Direction currentDirection = Direction.FORWARD;
    private ArrowAnimator arrowAnimator;

    private enum Direction {
        FORWARD,
        BACKWARD
    }

    private final Runnable repeatRunnable = new Runnable() {
        @Override
        public void run() {
            if (!isRunning) return;

            if (currentDirection == Direction.FORWARD) {
                rotateSelectedArrowForward();
            } else {
                rotateSelectedArrowBackward();
            }

            handler.postDelayed(this, REPEAT_DELAY_MS);
        }
    };

    @SuppressLint({"ClickableViewAccessibility", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        initLogic();
        initListeners();

        uiRenderer.renderAll(controller.buildViewState());
    }

    private void initViews() {
        offsetRotatingImage = findViewById(R.id.offsetRotatingImage);
        rotatingImage = findViewById(R.id.rotatingImage);
        startButton = findViewById(R.id.buttonStart);

        imageView2 = findViewById(R.id.imageView2);
        imageView3 = findViewById(R.id.imageView3);
        imageView4 = findViewById(R.id.imageView4);

        inputHours = findViewById(R.id.editTabloHours);
        applyButton = findViewById(R.id.buttonInter);
        resetButton = findViewById(R.id.buttonReset);
        forwardButton = findViewById(R.id.ButtonForwardArrow);
        backButton = findViewById(R.id.ButtonBackArrow);
        selectButton = findViewById(R.id.imageButtonSelect);
        resultText = findViewById(R.id.textView);
    }

    private void initLogic() {
        ArrowState firstArrow = new ArrowState(imageView2, PIVOT_X, FIRST_ARROW_PIVOT_Y, STEP_ANGLE);
        ArrowState secondArrow = new ArrowState(imageView3, PIVOT_X, OTHER_ARROWS_PIVOT_Y, STEP_ANGLE);
        ArrowState thirdArrow = new ArrowState(imageView4, PIVOT_X, OTHER_ARROWS_PIVOT_Y, STEP_ANGLE);

        controller = new ClockController(firstArrow, secondArrow, thirdArrow);
        uiRenderer = new ClockUiRenderer(inputHours, resultText, selectButton, startButton, offsetRotatingImage);
        soundManager = new SoundManager(this);
        arrowAnimator = new ArrowAnimator(ANIMATION_DURATION_MS);

        // НАСТРОЙКА ЦЕНТРА ВРАЩЕНИЯ
        // Подберите значения X и Y (в пикселях или вычислите относительно размера картинки)
        // Например, если картинка 100x100 и вы хотите вращать вокруг нижнего правого угла:
        offsetRotatingImage.post(() -> {
            // Здесь вы можете задать конкретные пиксели
            offsetRotatingImage.setPivotX(40.0f); // Пример смещения по X
            offsetRotatingImage.setPivotY(40.0f); // Пример смещения по Y
            // Если хотите вращать относительно центра, оставьте по умолчанию или используйте:
            // offsetRotatingImage.setPivotX(offsetRotatingImage.getWidth() * 0.5f);
        });

        // ---- аниматор вращения ----
        rotatingAnimator = ObjectAnimator.ofFloat(
                rotatingImage,
                View.ROTATION,        // свойство rotation
                0f, 360f);            // от 0° до 360°
        rotatingAnimator.setDuration(1000L);            // 1 секунда = 1 оборот
        rotatingAnimator.setInterpolator(new LinearInterpolator());
        rotatingAnimator.setRepeatCount(ValueAnimator.INFINITE);
        rotatingAnimator.setRepeatMode(ValueAnimator.RESTART);

        clockRunner = new ClockRunner(() -> {
            float firstFrom = controller.getFirstArrow().getCurrentRotation();
            float secondFrom = controller.getSecondArrow().getCurrentRotation();
            float thirdFrom = controller.getThirdArrow().getCurrentRotation();

            controller.incrementOneTick();

            float firstTo = controller.getFirstArrow().getCurrentRotation();
            float secondTo = controller.getSecondArrow().getCurrentRotation();
            float thirdTo = controller.getThirdArrow().getCurrentRotation();

            arrowAnimator.animateForward(
                    controller.getFirstArrow().getImageView(),
                    controller.getFirstArrow().getPivotX(),
                    controller.getFirstArrow().getPivotY(),
                    firstFrom,
                    firstTo
            );

            arrowAnimator.animateForward(
                    controller.getSecondArrow().getImageView(),
                    controller.getSecondArrow().getPivotX(),
                    controller.getSecondArrow().getPivotY(),
                    secondFrom,
                    secondTo
            );

            arrowAnimator.animateForward(
                    controller.getThirdArrow().getImageView(),
                    controller.getThirdArrow().getPivotX(),
                    controller.getThirdArrow().getPivotY(),
                    thirdFrom,
                    thirdTo
            );

            uiRenderer.renderAll(controller.buildViewState());
        });

    }

    @SuppressLint("ClickableViewAccessibility")
    private void initListeners() {
        selectButton.setOnClickListener(this::onClickSelect);
        applyButton.setOnClickListener(this::onClickApply);
        resetButton.setOnClickListener(this::onClickReset);
        startButton.setOnClickListener(this::onClickStart);

        forwardButton.setOnTouchListener((v, event) -> handleRotateTouch(event, true));
        backButton.setOnTouchListener((v, event) -> handleRotateTouch(event, false));

        inputHours.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String value = s.toString().trim();

                if (value.isEmpty()) {
                    uiRenderer.clearResult();
                    return;
                }

                try {
                    double inputValue = Double.parseDouble(value);
                    resultText.setText(controller.formatHours(controller.calculatePreviewTime(inputValue)));
                } catch (NumberFormatException e) {
                    uiRenderer.clearResult();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void onClickStart(View view) {
        RunMode newMode = controller.cycleRunMode();

        if (newMode == RunMode.STOPPED) {
            if (rotatingAnimator.isRunning()) {
                rotatingAnimator.cancel();      // остановить
            }
            clockRunner.stop();
        } else {
            if (!rotatingAnimator.isRunning()) {
                rotatingAnimator.start();       // запустить
            }
            clockRunner.start(newMode);
        }

        uiRenderer.renderAll(controller.buildViewState());
    }

    private boolean handleRotateTouch(MotionEvent event, boolean forward) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (forward) {
                    forwardButton.setBackgroundResource(R.drawable.round_button_2);
                    currentDirection = Direction.FORWARD;
                } else {
                    backButton.setBackgroundResource(R.drawable.round_button_2);
                    currentDirection = Direction.BACKWARD;
                }
                startRepeating();
                return true;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                forwardButton.setBackgroundResource(R.drawable.round_button);
                backButton.setBackgroundResource(R.drawable.round_button);
                stopRepeating();
                return true;
        }
        return false;
    }

    private void onClickSelect(View view) {
        soundManager.playButtonSound();
        controller.selectNextArrow();
        uiRenderer.renderAll(controller.buildViewState());
    }

    private void onClickApply(View view) {
        soundManager.playButtonSound();

        String value = inputHours.getText().toString().trim();
        if (value.isEmpty()) {
            showInfo("Введите число");
            return;
        }

        try {
            double inputValue = Double.parseDouble(value);

            if (inputValue < 0) {
                inputValue = 0;
                showInfo("Отрицательные значения недопустимы. Установлен ноль.");
            }

            float firstFrom = controller.getFirstArrow().getCurrentRotation();
            float secondFrom = controller.getSecondArrow().getCurrentRotation();
            float thirdFrom = controller.getThirdArrow().getCurrentRotation();

            controller.setTotalHours(inputValue);

            float firstTo = controller.getFirstArrow().getCurrentRotation();
            float secondTo = controller.getSecondArrow().getCurrentRotation();
            float thirdTo = controller.getThirdArrow().getCurrentRotation();

            arrowAnimator.animateDirect(controller.getFirstArrow().getImageView(),
                    controller.getFirstArrow().getPivotX(),
                    controller.getFirstArrow().getPivotY(),
                    firstFrom, firstTo);

            arrowAnimator.animateDirect(controller.getSecondArrow().getImageView(),
                    controller.getSecondArrow().getPivotX(),
                    controller.getSecondArrow().getPivotY(),
                    secondFrom, secondTo);

            arrowAnimator.animateDirect(controller.getThirdArrow().getImageView(),
                    controller.getThirdArrow().getPivotX(),
                    controller.getThirdArrow().getPivotY(),
                    thirdFrom, thirdTo);

            uiRenderer.renderAll(controller.buildViewState());

        } catch (NumberFormatException e) {
            showInfo("Введите корректное число");
        }
    }

    private void onClickReset(View view) {
        soundManager.playButtonSound();
        stopRepeating();
        clockRunner.stop();

        if (rotatingAnimator != null && rotatingAnimator.isRunning()) {
            rotatingAnimator.cancel();
        }

        controller.resetAll();

        clearArrowAnimation(controller.getFirstArrow().getImageView());
        clearArrowAnimation(controller.getSecondArrow().getImageView());
        clearArrowAnimation(controller.getThirdArrow().getImageView());

        uiRenderer.renderAll(controller.buildViewState());
    }

    private void startRepeating() {
        if (isRunning) return;

        isRunning = true;

        if (currentDirection == Direction.FORWARD) {
            rotateSelectedArrowForward();
        } else {
            rotateSelectedArrowBackward();
        }

        handler.postDelayed(repeatRunnable, FIRST_REPEAT_DELAY_MS);
    }

    private void stopRepeating() {
        isRunning = false;
        handler.removeCallbacks(repeatRunnable);
    }

    private void rotateSelectedArrowForward() {
        float firstFrom = controller.getFirstArrow().getCurrentRotation();
        float secondFrom = controller.getSecondArrow().getCurrentRotation();
        float thirdFrom = controller.getThirdArrow().getCurrentRotation();

        controller.incrementBySelectedArrowStep();

        float firstTo = controller.getFirstArrow().getCurrentRotation();
        float secondTo = controller.getSecondArrow().getCurrentRotation();
        float thirdTo = controller.getThirdArrow().getCurrentRotation();

        arrowAnimator.animateForward(
                controller.getFirstArrow().getImageView(),
                controller.getFirstArrow().getPivotX(),
                controller.getFirstArrow().getPivotY(),
                firstFrom,
                firstTo
        );

        arrowAnimator.animateForward(
                controller.getSecondArrow().getImageView(),
                controller.getSecondArrow().getPivotX(),
                controller.getSecondArrow().getPivotY(),
                secondFrom,
                secondTo
        );

        arrowAnimator.animateForward(
                controller.getThirdArrow().getImageView(),
                controller.getThirdArrow().getPivotX(),
                controller.getThirdArrow().getPivotY(),
                thirdFrom,
                thirdTo
        );

        soundManager.playArrowSound();
        uiRenderer.renderAll(controller.buildViewState());
    }

    private void rotateSelectedArrowBackward() {
        float firstFrom = controller.getFirstArrow().getCurrentRotation();
        float secondFrom = controller.getSecondArrow().getCurrentRotation();
        float thirdFrom = controller.getThirdArrow().getCurrentRotation();

        boolean changed = controller.decrementBySelectedArrowStep();
        if (!changed) {
            return;
        }

        float firstTo = controller.getFirstArrow().getCurrentRotation();
        float secondTo = controller.getSecondArrow().getCurrentRotation();
        float thirdTo = controller.getThirdArrow().getCurrentRotation();

        arrowAnimator.animateBackward(
                controller.getFirstArrow().getImageView(),
                controller.getFirstArrow().getPivotX(),
                controller.getFirstArrow().getPivotY(),
                firstFrom,
                firstTo
        );

        arrowAnimator.animateBackward(
                controller.getSecondArrow().getImageView(),
                controller.getSecondArrow().getPivotX(),
                controller.getSecondArrow().getPivotY(),
                secondFrom,
                secondTo
        );

        arrowAnimator.animateBackward(
                controller.getThirdArrow().getImageView(),
                controller.getThirdArrow().getPivotX(),
                controller.getThirdArrow().getPivotY(),
                thirdFrom,
                thirdTo
        );

        soundManager.playArrowSound();
        uiRenderer.renderAll(controller.buildViewState());
    }

    private void animateArrowToState(ArrowState arrowState) {
        animateRotation(
                arrowState.getImageView(),
                arrowState.getPivotX(),
                arrowState.getPivotY(),
                0f,
                arrowState.getCurrentRotation()
        );
    }

    private void animateRotation(ImageView imageView, float pivotX, float pivotY, float from, float to) {
        RotateAnimation rotate = new RotateAnimation(
                from,
                to,
                RotateAnimation.RELATIVE_TO_SELF, pivotX,
                RotateAnimation.RELATIVE_TO_SELF, pivotY
        );
        rotate.setDuration(ANIMATION_DURATION_MS);
        rotate.setFillAfter(true);
        imageView.startAnimation(rotate);
    }

    private void clearArrowAnimation(ImageView imageView) {
        imageView.clearAnimation();
    }

    private void showInfo(String text) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopRepeating();
        clockRunner.stop();
        soundManager.release();
        if (rotatingAnimator != null && rotatingAnimator.isRunning()) {
            rotatingAnimator.cancel();
        }
    }
}
