package com.example.alter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class post_question extends AppCompatActivity {

    private EditText editTextQuestionTitle;
    private EditText editTextQuestionDescription;
    private Button buttonPostQuestion;

    private OkHttpClient client;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_question);

        // Track: Initialize session manager
        sessionManager = new SessionManager(this);

        // Initialize views and button
        editTextQuestionTitle = findViewById(R.id.editTextQuestionTitle);
        editTextQuestionDescription = findViewById(R.id.editTextQuestionDescription);
        buttonPostQuestion = findViewById(R.id.buttonPostQuestion);

        client = new OkHttpClient();

        buttonPostQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = editTextQuestionTitle.getText().toString().trim();
                String description = editTextQuestionDescription.getText().toString().trim();

                // Validate input
                if (title.isEmpty() || description.isEmpty()) {
                    Toast.makeText(post_question.this, "Please enter both title and description", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Create the request body
                RequestBody requestBody = new FormBody.Builder()
                        .add("title", title)
                        .add("description", description)
                        .add("username", sessionManager.getLoggedInUserId())
                        .build();

                String p = sessionManager.getLoggedInUserId();
                // Create the request
                Request request = new Request.Builder()
                        .url("https://lamp.ms.wits.ac.za/home/s2572083/postQ.php")
                        .post(requestBody)
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // Error case: connectivity
                                Toast.makeText(post_question.this, "Network Error. Please try again later.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            // Posted successfully
                            String responseBody = response.body().string();

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // Vacate activity
                                    Toast.makeText(post_question.this, "Question posted successfully", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(post_question.this, question_list.class);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                        } else {
                            // Posted unsuccessfully
                            final String errorBody = response.body().string(); // Get the error message from the response
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // Error case
                                    Toast.makeText(post_question.this, "Error posting the question: " + errorBody, Toast.LENGTH_SHORT).show();
                                }
                            });

                            // Debugging log
                            Log.e("post_question", "Error posting the question: " + errorBody);
                        }
                    }
                });
            }
        });
    }
}
