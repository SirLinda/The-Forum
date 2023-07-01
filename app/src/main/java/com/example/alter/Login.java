package com.example.alter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Login extends AppCompatActivity {

    private EditText editTextUsername;
    private EditText editTextPassword;
    private Button buttonLogin;
    private TextView textViewRegister;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize pref
        sharedPreferences = getSharedPreferences("MyAppPref", MODE_PRIVATE);
        //views
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        //buttons
        buttonLogin = findViewById(R.id.buttonLogin);
        textViewRegister = findViewById(R.id.textViewRegister);

        // doLogin
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = editTextUsername.getText().toString();
                String password = editTextPassword.getText().toString();

                // Validate user input
                if (isValidInput(username, password)) {
                    // Perform the login process
                    performLogin(username, password);
                } else {
                    Toast.makeText(Login.this, "Please enter valid input", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // goToRegister
        textViewRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, Registration.class);
                startActivity(intent);
            }
        });
    }

    private boolean isValidInput(String username, String password) {
        // Validate
        if (TextUtils.isEmpty(username)) {
            editTextUsername.setError("Please enter a username");
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError("Please enter a password");
            return false;
        }

        return true;
    }

    private void performLogin(String username, String password) {
        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = new FormBody.Builder()
                .add("username", username)
                .add("password", password)
                .build();

        Request request = new Request.Builder()
                .url("https://lamp.ms.wits.ac.za/home/s2572083/login.php")
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(Login.this, "An error occurred", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String responseData = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject responseJson = new JSONObject(responseData);
                            boolean success = responseJson.getBoolean("success");
                            String message = responseJson.getString("message");

                            if (success) {
                                // Save the user ID in SharedPreferences
                                String userId = responseJson.getString("user_id");
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("user_id", userId);
                                editor.apply();
                                Toast.makeText(Login.this, "Welcome "+userId, Toast.LENGTH_SHORT).show();
                                // Now you in
                                Intent postQuestionIntent = new Intent(Login.this, question_list.class);
                                startActivity(postQuestionIntent);
                                finish();
                            } else {
                                // Login failed, display an error message to the user
                                Toast.makeText(Login.this, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }
}
