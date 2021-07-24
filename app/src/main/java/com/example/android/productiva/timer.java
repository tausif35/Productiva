package com.example.android.productiva;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import java.util.Locale;


public class timer extends AppCompatActivity {


    private TextView mTextViewCountDown;
    private MaterialButton mButtonStartPause;
    private MaterialButton mButtonReset;
    private CountDownTimer mCountDownTimer;
    private ProgressBar mProgressBar;
    private int currentProgress = 0;
    private boolean mTimerRunning;

    private long mInitialTimeInMiliSec = 10000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        //Finding Views
        mTextViewCountDown = findViewById(R.id.timer_text);
        mButtonStartPause = findViewById(R.id.timer_start);
        mButtonReset = findViewById(R.id.timer_reset);
        mProgressBar = findViewById(R.id.progress_bar);


        mProgressBar.setMax((int) (mInitialTimeInMiliSec / 1000));
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
        mCountDownTimer = new CountDownTimer(mInitialTimeInMiliSec, 10) {
            @Override
            public void onTick(long millisUntilFinished) {
                updateCountDownText(millisUntilFinished);
                updateTimerProgressBar((int) (mInitialTimeInMiliSec - millisUntilFinished) / 1000);
            }

            @Override
            public void onFinish() {
                updateTimerProgressBar((int)mInitialTimeInMiliSec/1000);
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
        mButtonStartPause.setText("Start");
        mButtonStartPause.setIconResource(R.drawable.ic_play);
        mButtonReset.setEnabled(true);
    }


    private void resetTimer() {
        mInitialTimeInMiliSec = 10000;
        updateCountDownText(mInitialTimeInMiliSec);
        mButtonReset.setEnabled(false);
        mButtonStartPause.setEnabled(true);
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