package com.example.speedtypinggame;

import androidx.annotation.Nullable;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Spinner spinner;
    private MaterialButton startGame;
    private EditText textInput;
    private TextView correctText, gameOverText, seconds, timeLeft, words, score, username, easyScore, mediumScore, hardScore;
    private ImageButton imageButton;
    private Button logout;

    private final String[] wordTexts = {"Boiler", "Javascript", "Boiler", "Milk", "Fresh", "Yoghurt",
            "Parse", "Conclude", "Kitchen", "Cook", "Home", "Random", "Language", "Find", "Seek", "Saw", "Return", "View", "Parser", "Visible"};
    private int scoreCount = 0;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

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
        username = headerView.findViewById(R.id.user_name);
        easyScore = headerView.findViewById(R.id.easy_score);
        mediumScore = headerView.findViewById(R.id.medium_score);
        hardScore = headerView.findViewById(R.id.hard_score);

        setScore();

        correctText.setVisibility(View.INVISIBLE);
        gameOverText.setVisibility(View.INVISIBLE);

        seconds.setTextColor(getResources().getColor(R.color.dodger));

        startGame.setOnClickListener(view -> {
            words.setText(wordTexts[(int) Math.round(Math.random() * 19)]);
            startGame.setVisibility(View.INVISIBLE);
            textInput.setEnabled(true);
            textInput.setBackgroundTintList(getResources().getColorStateList(R.color.white));
            textInput.setFocusable(true);
            scoreCount = 0;
            InputMethodManager imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,InputMethodManager.HIDE_IMPLICIT_ONLY);

            decrementTime();
        });
        spinner.setOnItemSelectedListener(this);

        if (user != null) {

            firebaseFirestore.collection("Users").document(user.getEmail()).get().addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    Utility.makeToast(MainActivity.this, task.getException().getLocalizedMessage());
                    return;
                }

                DocumentSnapshot documentSnapshot = task.getResult();

                username.setText(documentSnapshot.getString("username"));
            });
        }


        logout.setOnClickListener(view -> {
            logUserOut();
        });

        checkMatch();
    }


    public void setScore() {
        if (user != null) {
            firebaseFirestore.collection("UserScores").document(user.getEmail()).addSnapshotListener((value, error) -> {
                if (error != null) {
                    Utility.makeToast(MainActivity.this, error.getLocalizedMessage());
                    return;
                }

                if (value != null && value.exists()) {
                    easyScore.setText(value.getString("Easy"));
                    mediumScore.setText(value.getString("Medium"));
                    hardScore.setText(value.getString("Hard"));
                }
            });

        }
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

    public void easyGame() {

        if (user != null) {

            firebaseFirestore.collection("UserScores").document(user.getEmail()).addSnapshotListener((value, error) -> {
                if (error != null) {
                    Utility.makeToast(MainActivity.this, error.getLocalizedMessage());
                    return;
                }

                if (value != null && value.exists()) {
                    String easyScore = value.getString("Easy");

                    Map<String, Object> easy = new HashMap<>();

                    if (scoreCount > Integer.parseInt(easyScore)) {
                        easy.put("Easy", String.valueOf(scoreCount));
                        firebaseFirestore.collection("UserScores").document(user.getEmail()).update(easy).addOnCompleteListener(task -> {
                            if (!task.isSuccessful()) {
                                Utility.makeToast(MainActivity.this, task.getException().getLocalizedMessage());
                                return;
                            }

                            Utility.makeToast(MainActivity.this, "Easy score updated, new Highscore recorded.");
                        });
                    }
                }
            });
        }
    }

    public void mediumGame() {

        if (user != null) {

            firebaseFirestore.collection("UserScores").document(user.getEmail()).addSnapshotListener((value, error) -> {
                if (error != null) {
                    Utility.makeToast(MainActivity.this, error.getLocalizedMessage());
                    return;
                }

                if (value != null && value.exists()) {
                    String mediumScore = value.getString("Medium");

                    Map<String, Object> medium = new HashMap<>();

                    if (scoreCount > Integer.parseInt(mediumScore)) {
                        medium.put("Medium", String.valueOf(scoreCount));
                        firebaseFirestore.collection("UserScores").document(user.getEmail()).update(medium).addOnCompleteListener(task -> {
                            if (!task.isSuccessful()) {
                                Utility.makeToast(MainActivity.this, task.getException().getLocalizedMessage());
                                return;
                            }

                            Utility.makeToast(MainActivity.this, "Medium score updated, new Highscore recorded.");
                        });
                    }
                }
            });
        }
    }

    public void hardGame() {

        if (user != null) {

            firebaseFirestore.collection("UserScores").document(user.getEmail()).addSnapshotListener((value, error) -> {
                if (error != null) {
                    Utility.makeToast(MainActivity.this, error.getLocalizedMessage());
                    return;
                }

                if (value != null && value.exists()) {
                    String hardScore = value.getString("Hard");

                    Map<String, Object> hard = new HashMap<>();

                    if (scoreCount > Integer.parseInt(hardScore)) {
                        hard.put("Hard", String.valueOf(scoreCount));
                        firebaseFirestore.collection("UserScores").document(user.getEmail()).update(hard).addOnCompleteListener(task -> {
                            if (!task.isSuccessful()) {
                                Utility.makeToast(MainActivity.this, task.getException().getLocalizedMessage());
                                return;
                            }

                            Utility.makeToast(MainActivity.this, "Hard score updated, new Highscore recorded.");
                        });
                    }
                }
            });
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
                    resetGame();
                    switch (spinner.getSelectedItem().toString()) {
                        case "Easy":
                            easyGame();
                            break;
                        case "Medium":
                            mediumGame();
                            break;
                        case "Hard":
                            hardGame();
                            break;
                    }
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