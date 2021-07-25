package com.example.android.productiva;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Locale;


public class Timer extends AppCompatActivity {

    private Toolbar mToolbar;
    private TextView mTextViewCountDown;
    private MaterialButton mButtonStartPause, mButtonReset;
    private FloatingActionButton fabSetupTimer;
    private CountDownTimer mCountDownTimer;
    private ProgressBar mProgressBar;
    private  TextView timerCycle;
    private TextView timerLabel;
    private int currentProgress = 0;
    private boolean mTimerRunning;
    private long initTimeMS = 0, workTimeMS = 10000, breakTimeMS = 10000;
    private int cycles = 2;
    private int cycleCounter = 0;
    private int cycleCurrent =1;
    private boolean working = true;

    private long timeLeftMS;

    MediaPlayer alertPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        setTitle("");

        //Finding Views
        mTextViewCountDown = findViewById(R.id.timer_text);
        mButtonStartPause = findViewById(R.id.timer_start);
        mButtonReset = findViewById(R.id.timer_reset);
        mProgressBar = findViewById(R.id.progress_bar);
        fabSetupTimer = findViewById(R.id.fab_setupTimer);
        timerLabel = findViewById(R.id.timer_label);
        timerCycle =findViewById(R.id.timer_cycle);

        Toolbar toolbar = findViewById(R.id.timer_toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(v -> finish());


        fabSetupTimer.setOnClickListener(v -> {
            TimerBottomSheet timerBottomSheet = new TimerBottomSheet(25, 5, 4);
            timerBottomSheet.setOnSetupButtonClickListener((workTime, breakTime, cycles) -> {
                workTimeMS = workTime * 60000;
                breakTimeMS = breakTime * 60000;
                this.cycles = cycles;
                initTimeMS = workTimeMS;
                timeLeftMS = initTimeMS;
                updateCountDownText(timeLeftMS);
            });
            timerBottomSheet.show(getSupportFragmentManager(), "TimerBottomSheet");
        });

        mProgressBar.setMax((int) (initTimeMS / 10));
        mButtonStartPause.setOnClickListener(v -> {
            if (initTimeMS > 0) {
                if (mTimerRunning) {
                    pauseTimer();
                } else {
                    startTimer();
                }
            } else {
                Toast.makeText(this, "Please setup your Pomodoro", Toast.LENGTH_SHORT).show();
            }

        });
        mButtonReset.setOnClickListener(v -> resetTimer());
        updateCountDownText(initTimeMS);
    }

    private void startTimer() {
        mCountDownTimer = new CountDownTimer(timeLeftMS, 10) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftMS = millisUntilFinished;
                updateCountDownText(millisUntilFinished);
                updateTimerProgressBar((int) (initTimeMS - millisUntilFinished) / 10);
            }

            @Override
            public void onFinish() {
                updateTimerProgressBar((int) initTimeMS / 10);
                if (working) {
                    cycleCounter++;
                    if (cycleCounter < cycles) {
                        playAlert("workFinished");
                        timerLabel.setText("Break");
                        initTimeMS = breakTimeMS;
                        timeLeftMS = initTimeMS;
                        updateCountDownText(timeLeftMS);
                        working = false;
                        startTimer();
                    } else {
                        timerCycle.setText("Completed!");
                        playAlert("pomodoroFinished");
                        mTimerRunning = false;
                        mButtonStartPause.setText("Start");
                        mButtonStartPause.setIconResource(R.drawable.ic_play);
                        mButtonStartPause.setClickable(false);
                        mButtonStartPause.setIconTintResource(R.color.disabledColor);
                        mButtonStartPause.setTextColor(getColor(R.color.disabledColor));
                        mButtonStartPause.setStrokeColorResource(R.color.disabledColor);
                        mButtonReset.setClickable(true);
                        mButtonReset.setIconTintResource(R.color.colorPrimary);
                        mButtonReset.setTextColor(getColor(R.color.colorPrimary));
                        mButtonReset.setStrokeColorResource(R.color.colorPrimary);
                    }
                } else {
                    cycleCurrent++;
                    timerCycle.setText("Cycle: "+cycleCurrent);
                    playAlert("breakFinished");
                    timerLabel.setText("Work");
                    initTimeMS = workTimeMS;
                    timeLeftMS = initTimeMS;
                    updateCountDownText(timeLeftMS);
                    working = true;
                    startTimer();
                }
            }
        }.start();

        mTimerRunning = true;
        mButtonStartPause.setText("pause");
        mButtonStartPause.setIconResource(R.drawable.ic_pause);
        mButtonReset.setClickable(false);
        mButtonReset.setIconTintResource(R.color.disabledColor);
        mButtonReset.setTextColor(getColor(R.color.disabledColor));
        mButtonReset.setStrokeColorResource(R.color.disabledColor);

    }

    private void pauseTimer() {
        
        mCountDownTimer.cancel();
        mTimerRunning = false;
        mButtonStartPause.setText("Resume");
        mButtonStartPause.setIconResource(R.drawable.ic_play);
        mButtonReset.setClickable(true);
        mButtonReset.setIconTintResource(R.color.colorPrimary);
        mButtonReset.setTextColor(getColor(R.color.colorPrimary));
        mButtonReset.setStrokeColorResource(R.color.colorPrimary);
    }


    private void resetTimer() {
        updateCountDownText(initTimeMS);
        timeLeftMS = initTimeMS;
        mButtonReset.setClickable(false);
        mButtonReset.setIconTintResource(R.color.disabledColor);
        mButtonReset.setTextColor(getColor(R.color.disabledColor));
        mButtonReset.setStrokeColorResource(R.color.disabledColor);
        mButtonStartPause.setClickable(true);
        mButtonStartPause.setText("Start");
        mButtonStartPause.setIconTintResource(R.color.colorPrimary);
        mButtonStartPause.setTextColor(getColor(R.color.colorPrimary));
        mButtonStartPause.setStrokeColorResource(R.color.colorPrimary);
        updateTimerProgressBar(0);
    }

    private void updateCountDownText(long millisUntilFinished) {
        //millisUntilFinished+=1000;
        int minutes = (int) (millisUntilFinished / 1000) / 60;
        int seconds = (int) (millisUntilFinished / 1000) % 60;
        int deciSeconds = (int) (millisUntilFinished / 100) % 10;
        String timeLeftFormatted = String.format(Locale.getDefault(), "%01d:%02d:%01d", minutes, seconds, deciSeconds);
        mTextViewCountDown.setText(timeLeftFormatted);
    }

    private void updateTimerProgressBar(int currentProgress) {
        mProgressBar.setProgress(currentProgress);
    }

    // Alert Functions

    private void playAlert(String alertType) {
        if (alertPlayer == null) {

            if (alertType == "workFinished") {
                alertPlayer = MediaPlayer.create(this, R.raw.work_finished);
            } else if (alertType == "breakFinished") {
                alertPlayer = MediaPlayer.create(this, R.raw.break_finished);
            } else {
                alertPlayer = MediaPlayer.create(this, R.raw.pomodoro_finished);
            }

            alertPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    stopAlert();
                }
            });
            alertPlayer.start();
        }
    }

    private void stopAlert() {
        if (alertPlayer != null) {
            alertPlayer.release();
            alertPlayer = null;
            Toast.makeText(this, "MediaPlayer released", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopAlert();
    }


}