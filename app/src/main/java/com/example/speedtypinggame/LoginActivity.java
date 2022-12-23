package com.example.speedtypinggame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import io.grpc.okhttp.internal.Util;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private Button login;
    private TextView register;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        login = findViewById(R.id.btn);
        progressBar = findViewById(R.id.progress);
        register = findViewById(R.id.register);


        register.setOnClickListener(view -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });

        login.setOnClickListener(view -> loginUser() );

    }

    public void loginUser() {
        String emailText = emailEditText.getText().toString();
        String passwordText = passwordEditText.getText().toString();

        showProgress(true);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        firebaseAuth.signInWithEmailAndPassword(emailText, passwordText).addOnCompleteListener(task -> {
            if(!task.isSuccessful()) {
                Utility.makeToast(LoginActivity.this, task.getException().getLocalizedMessage());
                showProgress(false);
                return;
            }

            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        });
    }


    public void showProgress(boolean show) {
        if (!show) {
            login.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        } else {
            login.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        }
    }
}