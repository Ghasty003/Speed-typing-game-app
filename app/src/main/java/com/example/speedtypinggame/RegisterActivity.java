package com.example.speedtypinggame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;
import com.google.firestore.v1.UpdateDocumentRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText, confirmPasswordEditText, userName;
    private Button register;
    private TextView login;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        confirmPasswordEditText = findViewById(R.id.confirm_password);
        userName = findViewById(R.id.user_name);
        login = findViewById(R.id.login);
        register = findViewById(R.id.btn);
        progressBar = findViewById(R.id.progress);

        login.setOnClickListener(view -> {
           startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
        });

        register.setOnClickListener(view -> createUser());

    }

    public void createUser() {
        String emailText = emailEditText.getText().toString();
        String passwordText = passwordEditText.getText().toString();
        String confirmPasswordText = confirmPasswordEditText.getText().toString();
        String userNameText = userName.getText().toString();

        Map<String, Object> users = new HashMap<>();

        users.put("email", emailText);
        users.put("password", passwordText);
        users.put("username", userNameText);

        boolean isValidated = validateUser(emailText, passwordText, confirmPasswordText, userNameText);

        if (!isValidated) {
            return;
        }

        showProgress(true);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


        firebaseAuth.createUserWithEmailAndPassword(emailText, passwordText).addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Utility.makeToast(RegisterActivity.this, task.getException().getLocalizedMessage());
                showProgress(false);
                return;
            }

            new UserProfileChangeRequest.Builder().setDisplayName(userNameText).build();
            Utility.makeToast(RegisterActivity.this, "Registration successful");
            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
            finish();
            firebaseFirestore.collection("Users").document(emailText).set(users)
                    .addOnCompleteListener(setTask -> {
                        if (!setTask.isSuccessful()) {
                            Utility.makeToast(RegisterActivity.this, setTask.getException().getLocalizedMessage());
                            showProgress(false);
                            return;
                        }

                        Utility.makeToast(RegisterActivity.this, "Registration successful");
                        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                        finish();
                    });

        });
    }

    public boolean validateUser(String email, String password, String confirmPassword, String username) {

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Email is invalid");
            return false;
        }

        if (username.length() < 0) {
            userName.setError("Username is required");
            return false;
        }

        if (!(password.length() > 6)) {
            passwordEditText.setError("Password must be at least 6 characters");
            return false;
        }

        if (!confirmPassword.equals(password)) {
            confirmPasswordEditText.setError("Password do not match");
            return false;
        }

        return true;
    }

    public void showProgress(boolean show) {
        if (!show) {
            register.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        } else {
            register.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        }
    }
}