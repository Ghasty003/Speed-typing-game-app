package com.example.speedtypinggame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Spinner spinner;
    private MaterialButton startGame;
    private EditText textInput;
    private TextView correctText, gameOverText, seconds, timeLeft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinner = findViewById(R.id.spinner);
        startGame = findViewById(R.id.start_btn);
        textInput = findViewById(R.id.text_input);
        correctText = findViewById(R.id.correct_text);
        gameOverText = findViewById(R.id.game_over);
        seconds = findViewById(R.id.seconds);
        timeLeft = findViewById(R.id.time);

        correctText.setVisibility(View.INVISIBLE);
        gameOverText.setVisibility(View.INVISIBLE);

        startGame.setOnClickListener(view -> {
            startGame.setVisibility(View.INVISIBLE);
            textInput.setEnabled(true);
            textInput.setBackgroundTintList(getResources().getColorStateList(R.color.white));
            textInput.setFocusable(true);
            InputMethodManager imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,InputMethodManager.HIDE_IMPLICIT_ONLY);

            decrementTime();
            resetGame();
        });
        spinner.setOnItemSelectedListener(this);

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        ((TextView) adapterView.getChildAt(0)).setTextColor(Color.WHITE);

        switch (adapterView.getSelectedItem().toString()) {
            case "Easy":
                seconds.setText("5");
                timeLeft.setText("5");
                break;
            case "Medium":
                seconds.setText("3");
                timeLeft.setText("3");
                break;
            case "Hard":
                seconds.setText("2");
                timeLeft.setText("2");
                break;
        }
    }

    public void decrementTime() {
        Handler handler = new Handler(Looper.getMainLooper());
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                int time = Integer.parseInt(timeLeft.getText().toString());
                time--;
                timeLeft.setText(String.valueOf(time));
                handler.postDelayed(this, 1000);

                if (gameOver()) {
                    handler.removeCallbacks(this);
                    startGame.setVisibility(View.VISIBLE);
                    gameOverText.setVisibility(View.VISIBLE);
                    textInput.setEnabled(false);
                    textInput.setBackgroundTintList(getResources().getColorStateList(R.color.input_disable));
                    Toast.makeText(MainActivity.this, "Game over", Toast.LENGTH_SHORT).show();
                }
            }
        };
        handler.postDelayed(runnable, 1000);
    }

    public boolean gameOver() {
        boolean isGameOver = false;

        if(timeLeft.getText().toString().equals("0")) {
            isGameOver = true;
        }

        return isGameOver;
    }

    public void resetGame() {
        if (seconds.getText().toString().equals("5")) {
            timeLeft.setText("5");
            gameOverText.setVisibility(View.INVISIBLE);
        } else if (seconds.getText().toString().equals("3")) {
            timeLeft.setText("3");
            gameOverText.setVisibility(View.INVISIBLE);
        } else {
            timeLeft.setText("2");
            gameOverText.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }
}