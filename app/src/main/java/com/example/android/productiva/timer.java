package com.example.android.productiva;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import java.util.Locale;


public class timer extends AppCompatActivity {

private Toolbar mToolbar;
    private TextView mTextViewCountDown;
    private MaterialButton mButtonStartPause;
    private MaterialButton mButtonReset;
    private CountDownTimer mCountDownTimer;
    private ProgressBar mProgressBar;
    private int currentProgress = 0;
    private boolean mTimerRunning;


    private long mInitialTimeInMiliSec = 120000;
    private int cycles =2;
    private long mInitialBreakTimeInMillisec =40000;

    private long mTimeLeftInMillis = mInitialTimeInMiliSec;

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

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.timer_toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(v -> finish());

        mProgressBar.setMax((int) (mInitialTimeInMiliSec / 10));
        mButtonStartPause.setOnClickListener(v -> {
            if (mTimerRunning) {
                pauseTimer();
            } else {
                startTimer();
            }
        });
        mButtonReset.setOnClickListener(v -> resetTimer());
        updateCountDownText(mInitialTimeInMiliSec);
    }

    private void startTimer() {
        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 10) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText(millisUntilFinished);
                updateTimerProgressBar((int) (mInitialTimeInMiliSec - millisUntilFinished) / 10);
            }

            @Override
            public void onFinish() {
                updateTimerProgressBar((int)mInitialTimeInMiliSec/10);
                mTimerRunning = false;
                mButtonStartPause.setText("Start");
                mButtonStartPause.setIconResource(R.drawable.ic_play);
                mButtonStartPause.setEnabled(false);
                mButtonReset.setEnabled(true);

            }
        }.start();

        mTimerRunning = true;
        mButtonStartPause.setText("pause");
        mButtonStartPause.setIconResource(R.drawable.ic_pause);
        mButtonReset.setEnabled(false);
    }

    private void pauseTimer() {
        mCountDownTimer.cancel();
        mTimerRunning = false;
        mButtonStartPause.setText("Resume");
        mButtonStartPause.setIconResource(R.drawable.ic_play);
        mButtonReset.setEnabled(true);
    }



    private void resetTimer() {
        updateCountDownText(mInitialTimeInMiliSec);
        mTimeLeftInMillis =mInitialTimeInMiliSec;
        mButtonReset.setEnabled(false);
        mButtonStartPause.setEnabled(true);
        updateTimerProgressBar(0);
    }

    private void updateCountDownText(long millisUntilFinished) {
        //millisUntilFinished+=1000;
        int minutes = (int) (millisUntilFinished / 1000) / 60;
        int seconds = (int) (millisUntilFinished / 1000) % 60;
        int deciSeconds = (int)(millisUntilFinished/100)%10;
        String timeLeftFormatted = String.format(Locale.getDefault(), "%01d:%02d:%01d", minutes, seconds, deciSeconds);
        mTextViewCountDown.setText(timeLeftFormatted);
    }

    private void updateTimerProgressBar(int currentProgress) {
        mProgressBar.setProgress(currentProgress);
    }


}