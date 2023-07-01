package com.example.alter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Registration extends AppCompatActivity {

    private EditText editTextUsername;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        // Initialize views
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonRegister = findViewById(R.id.buttonRegister);

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = editTextUsername.getText().toString().trim();
                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                // Validate user input
                if (isValidInput(username, email, password)) {
                     performRegistration(username, email, password);
                } else {
                    Toast.makeText(Registration.this, "Please enter valid input", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean isValidInput(String username, String email, String password) {
        // Check if username is empty
        if (TextUtils.isEmpty(username)) {
            editTextUsername.setError("Please enter a username");
            return false;
        }

        // Check if email is empty
        if (TextUtils.isEmpty(email)) {
            editTextEmail.setError("Please enter an email");
            return false;
        }

        // Check if password is empty
        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError("Please enter a password");
            return false;
        }

        // Additional validation logic for email format
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Please enter a valid email address");
            return false;
        }

        // Return true if the input is valid
        return true;
    }

    private void performRegistration(String username, String email, String password) {
        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = new FormBody.Builder()
                .add("username", username)
                .add("password", password)
                .add("email", email)
                .build();

        Request request = new Request.Builder()
                .url("https://lamp.ms.wits.ac.za/home/s2572083/register.php")
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                // Request was not successful case
                e.printStackTrace();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(Registration.this, "Registration failed. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                // Handle the response from the server
                if (response.isSuccessful()) {
                    // Registration successful
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(Registration.this, "Registration successful", Toast.LENGTH_SHORT).show();

                            // Now you can login
                            Intent intent = new Intent(Registration.this, Login.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                } else {
                    // Registration unsuccessful
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(Registration.this, "Registration failed. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

}