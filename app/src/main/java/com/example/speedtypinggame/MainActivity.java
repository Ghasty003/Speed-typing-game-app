package com.example.speedtypinggame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Spinner spinner;
    private MaterialButton startGame;
    private EditText textInput;
    private TextView correctText, gameOverText, seconds, timeLeft, words, score;
    private ImageButton imageButton;
    private Button logout;

    private final String[] wordTexts = {"Boiler", "Javascript", "Boiler", "Milk", "Fresh", "Yoghurt",
            "Parse", "Conclude", "Kitchen", "Cook", "Home", "Random", "Language", "Find", "Seek", "Saw", "Return", "View", "Parser", "Visible"};
    int scoreCount = 0;

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
        words = findViewById(R.id.words);
        score = findViewById(R.id.score_count);
        imageButton = findViewById(R.id.image_btn);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);

        View headerView = getLayoutInflater().inflate(R.layout.header, navigationView, false);
        navigationView.addHeaderView(headerView);

        imageButton.setOnClickListener(view -> {
            drawerLayout.openDrawer(GravityCompat.START);
        });

        logout = headerView.findViewById(R.id.logout);

        correctText.setVisibility(View.INVISIBLE);
        gameOverText.setVisibility(View.INVISIBLE);

        seconds.setTextColor(getResources().getColor(R.color.dodger));

        startGame.setOnClickListener(view -> {
            words.setText(wordTexts[(int) Math.round(Math.random() * 19)]);
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

        logout.setOnClickListener(view -> {
            logUserOut();
        });

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        String email = Objects.requireNonNull(user).getEmail();
        String displayName = Objects.requireNonNull(user).getDisplayName();
        Log.d("MY_DOC", email);
        Log.d("MY_DOC", displayName);

        checkMatch();
    }

    public void checkMatch() {
        textInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().equals(words.getText().toString())) {
                    resetGame();
                    scoreCount++;
                    score.setText(String.valueOf(scoreCount));
                    words.setText(wordTexts[(int) Math.round(Math.random() * 19)]);
                    correctText.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
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
                    correctText.setVisibility(View.INVISIBLE);
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
        textInput.getText().clear();
        score.setText("0");
    }

    public void logUserOut() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signOut();
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        finish();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }
}