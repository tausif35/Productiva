package com.example.android.productiva.Timer;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.android.productiva.R;
import com.google.android.material.button.MaterialButton;

import java.util.Locale;


public class Timer extends AppCompatActivity {

    private MediaPlayer alertPlayer;
    private TextView countDownText, timerCycle, timerLabel;
    private MaterialButton btnStartPause, btnReset, btnSetup;
    private CountDownTimer countDownTimer;
    private ProgressBar progressBar;
    private TimerBottomSheet timerBottomSheet;
    private boolean timerRunning;
    private long initTimeMS = 0, workTimeMS = 0, breakTimeMS = 0;
    private int cycles = 1;
    private int cycleCounter = 0;
    private int cycleCurrent = 1;
    private boolean working = true;
    private long timeLeftMS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        setTitle("");


        //Finding Views
        countDownText = findViewById(R.id.timer_text);
        btnStartPause = findViewById(R.id.timer_start);
        btnReset = findViewById(R.id.timer_reset);
        progressBar = findViewById(R.id.progress_bar);
        btnSetup = findViewById(R.id.timer_setup);
        timerLabel = findViewById(R.id.timer_label);
        timerCycle = findViewById(R.id.timer_cycle);

        Log.d("onTick: ", String.valueOf(progressBar.getProgress()));


        Toolbar toolbar = findViewById(R.id.timer_toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(v -> finish());


        btnSetup.setOnClickListener(v -> {
            if (workTimeMS > 0 && breakTimeMS > 0)
                timerBottomSheet = new TimerBottomSheet(workTimeMS, breakTimeMS, cycles);
            else
                timerBottomSheet = new TimerBottomSheet(60000, 60000, 1);

            timerBottomSheet.setOnSetupButtonClickListener((workTimeMS, breakTimeMS, cycles) -> {
                this.workTimeMS = workTimeMS;
                this.breakTimeMS = breakTimeMS;
                this.cycles = cycles;
                initTimeMS = workTimeMS;
                timeLeftMS = initTimeMS;
                updateCountDownText(timeLeftMS);
                updateTimerProgressBar(0);
            });
            timerBottomSheet.show(getSupportFragmentManager(), "TimerBottomSheet");
        });


        btnStartPause.setOnClickListener(v -> {
            if (initTimeMS > 0) {
                btnStateChange(btnSetup, false, R.color.disabledColor);
                if (timerRunning) {
                    pauseTimer();
                } else {
                    startTimer();
                }
            } else {
                Toast.makeText(this, "Please setup your Pomodoro", Toast.LENGTH_SHORT).show();
            }
        });

        btnReset.setOnClickListener(v -> resetTimer());

        updateTimerProgressBar(0);

    }


    //This starts the timer
    private void startTimer() {
        progressBar.setMax((int) (initTimeMS / 10));
        countDownTimer = new CountDownTimer(timeLeftMS, 10) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftMS = millisUntilFinished;
                updateCountDownText(timeLeftMS);
                updateTimerProgressBar((int) (initTimeMS - timeLeftMS) / 10);
            }

            @Override
            public void onFinish() {
                updateTimerProgressBar((int) initTimeMS / 10);
                if (working) {
                    cycleCounter++;
                    if (cycleCounter < cycles) {
                        timerLabel.setText("Break");
                        initTimeMS = breakTimeMS;
                        timeLeftMS = initTimeMS;
                        working = false;
                        playAlert("workFinished");
                        updateCountDownText(timeLeftMS);
                        startTimer();
                    } else {
                        playAlert("pomodoroFinished");
                        timerCycle.setText("Completed!");
                        timerRunning = false;
                        btnStartPause.setText("Start");
                        btnStartPause.setIconResource(R.drawable.ic_play);
                        btnStateChange(btnStartPause, false, R.color.disabledColor);
                        btnStateChange(btnReset, true, R.color.colorPrimary);
                    }
                } else {
                    playAlert("breakFinished");
                    working = true;
                    cycleCurrent++;
                    timerCycle.setText(String.format("Cycle: %d", cycleCurrent));
                    timerLabel.setText("Work");
                    initTimeMS = workTimeMS;
                    timeLeftMS = initTimeMS;
                    updateCountDownText(timeLeftMS);
                    startTimer();
                }
            }
        }.start();

        timerRunning = true;
        btnStartPause.setText("pause");
        btnStartPause.setIconResource(R.drawable.ic_pause);
        btnStateChange(btnReset, false, R.color.disabledColor);

    }


    //This is for pausing the timer
    private void pauseTimer() {
        countDownTimer.cancel();
        timerRunning = false;
        btnStartPause.setText("Resume");
        btnStartPause.setIconResource(R.drawable.ic_play);
        btnStateChange(btnReset, true, R.color.colorPrimary);
    }

    //This will reset timer and everything related to it
    private void resetTimer() {
        cycleCounter = 0;
        cycleCurrent = 1;
        initTimeMS = workTimeMS;
        timeLeftMS = initTimeMS;
        btnStartPause.setText("Start");
        timerLabel.setText("Work");
        timerCycle.setText(String.format("Cycle: %d", cycleCurrent));
        btnStateChange(btnStartPause, true, R.color.colorPrimary);
        btnStateChange(btnSetup, true, R.color.colorPrimary);
        updateTimerProgressBar(0);
        updateCountDownText(initTimeMS);
        if (countDownTimer != null)
            countDownTimer.cancel();
    }

    //Timer text update
    private void updateCountDownText(long millisUntilFinished) {
        //millisUntilFinished+=1000;
        int minutes = (int) (millisUntilFinished / 1000) / 60;
        int seconds = (int) (millisUntilFinished / 1000) % 60;
        int deciSeconds = (int) (millisUntilFinished / 100) % 10;
        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d:%01d", minutes, seconds, deciSeconds);
        countDownText.setText(timeLeftFormatted);
    }

    //Progress bar increase
    private void updateTimerProgressBar(int currentProgress) {
        progressBar.setProgress(currentProgress);
    }

    // Alert Functions
    private void playAlert(String alertType) {
        if (alertPlayer == null) {

            if (alertType.equals("workFinished")) {
                alertPlayer = MediaPlayer.create(this, R.raw.work_finished);
            } else if (alertType.equals("breakFinished")) {
                alertPlayer = MediaPlayer.create(this, R.raw.break_finished);
            } else {
                alertPlayer = MediaPlayer.create(this, R.raw.pomodoro_finished);
            }

            alertPlayer.setOnCompletionListener(mp -> stopAlert());
            alertPlayer.start();
        }
    }

    //Stop alert sound
    private void stopAlert() {
        if (alertPlayer != null) {
            alertPlayer.release();
            alertPlayer = null;
            Toast.makeText(this, "MediaPlayer released", Toast.LENGTH_SHORT).show();
        }
    }


    //when activity is closed
    @Override
    protected void onStop() {
        super.onStop();
        stopAlert();
        resetTimer();
    }

    //This is for changing button clickable state
    private void btnStateChange(MaterialButton button, boolean isClickable, int color) {
        button.setClickable(isClickable);
        button.setIconTintResource(color);
        button.setTextColor(getColor(color));
        button.setStrokeColorResource(color);
    }

}