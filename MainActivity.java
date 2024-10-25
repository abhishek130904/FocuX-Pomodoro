package com.example.focux_studytimer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private Button edit;
    private Button startstop;
    private MediaPlayer mediaPlayer;
    private TextView timer;
    private TextView breakMessage;
    private CountDownTimer countDownTimer;
    private boolean isTimerRunning = false;
    private long timeLeftInMillis;
    private final long DEFAULT_TIME_IN_MILLIS = 25 * 60 * 1000; // to convert into milliseconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startstop = findViewById(R.id.startstop);
        edit = findViewById(R.id.edit);
        timer = findViewById(R.id.timer);
        breakMessage = findViewById(R.id.breakMessage);
        timeLeftInMillis = DEFAULT_TIME_IN_MILLIS;

        updateTimerText();
        showWelcomeDialog();

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditTimeDialog();
            }
        });

        startstop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isTimerRunning) {
                    startTimer();
                } else {
                    stopTimer();
                }
                mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.noti2);
                mediaPlayer.start(); //for notification sound
            }
        });
    }

    private void showWelcomeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("DEVELOPED BY ABHISHEK RAJ");
        builder.setMessage("Welcome to Pomodoro Study Timer!");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showEditTimeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Set Timer");

        final EditText input = new EditText(this);
        input.setHint("Enter minutes");
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String value = input.getText().toString();
                if (!value.isEmpty()) {
                    int minutes = Integer.parseInt(value);
                    timeLeftInMillis = minutes * 60 * 1000;
                    updateTimerText();
                }
            }
        });

        builder.show();
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateTimerText();
            }

            @Override
            public void onFinish() {
                timer.setText("00:00");
                mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.noti);
                mediaPlayer.start();
                startBreakTimer();
            }
        }.start();

        isTimerRunning = true;
        startstop.setText("Stop");
    }

    private void startBreakTimer() {
        long breakTimeInMillis = 5 * 60 * 1000;

        // Show break start message
        breakMessage.setText("BREAK TIME!");
        breakMessage.setVisibility(View.VISIBLE);

        countDownTimer = new CountDownTimer(breakTimeInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                updateTimerText(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                isTimerRunning = false;
                mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.noti);
                mediaPlayer.start();


                breakMessage.setText("BREAK OVER!");


                startstop.setText("Start");
            }
        }.start();
    }

    private void updateTimerText(long millis) {
        int minutes = (int) (millis / 1000) / 60; //to convert into minutes
        int seconds = (int) (millis / 1000) % 60;
        String timeLeftFormatted = String.format("%02d:%02d", minutes, seconds);
        timer.setText(timeLeftFormatted);
    }

    private void updateTimerText() {
        updateTimerText(timeLeftInMillis);
    }

    private void stopTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        isTimerRunning = false;
        startstop.setText("Start");
    }

}
