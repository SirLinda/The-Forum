package com.example.alter;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class post_reply extends AppCompatActivity {

    private EditText editTextReply;
    private ImageView buttonReply;
    private SessionManager sessionManager;
    private String questionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_reply);

        // Find views by IDs
        editTextReply = findViewById(R.id.editTextReply);
        buttonReply = findViewById(R.id.buttonReply);

        // Get the question ID from the intent
        questionId = getIntent().getStringExtra("questionId");

        buttonReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the reply text entered by the user
                String replyText = editTextReply.getText().toString().trim();
                postReply(replyText);
            }
        });
    }

    private void postReply(String replyText) {
        OkHttpClient client = new OkHttpClient();

        // Create a request body with the question ID, reply text, and username
        RequestBody requestBody = new FormBody.Builder()
                .add("questionId", questionId)
                .add("replyText", replyText)
                .add("username", sessionManager.getLoggedInUserId())
                .build();

        // Create a POST request to the PHP script
        Request request = new Request.Builder()
                .url("https://lamp.ms.wits.ac.za/home/s2572083/postR.php")
                .post(requestBody)
                .build();

        // Execute the request asynchronously
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Handle the failure case
                Log.e("PostReplyActivity", "Error: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // Handle the response from the PHP script
                final String responseData = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Display a success message or perform any additional actions as needed
                        Log.d("PostReplyActivity", "Response: " + responseData);
                    }
                });
            }
        });
    }
}
