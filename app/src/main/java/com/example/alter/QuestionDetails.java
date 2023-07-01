package com.example.alter;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class QuestionDetails extends AppCompatActivity {
    private TextView textViewQuestionTitle;
    private TextView textViewQuestionDescription;

    private TextView textViewVoteCount;
    private ListView listViewReplies;
    private EditText editTextReply;
    private Button buttonPostReply;
    private Button buttonUpvote;
    private ArrayAdapter<String> repliesAdapter;
    private ArrayList<String> repliesList;
    private int questionId;
    private int voteCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_details);

        // Initialize the views
        textViewQuestionTitle = findViewById(R.id.textViewQuestionTitle);
        textViewQuestionDescription = findViewById(R.id.textViewQuestionDescription);
        textViewVoteCount = findViewById(R.id.textViewVoteCount);
        buttonUpvote = findViewById(R.id.buttonUpvote);
        listViewReplies = findViewById(R.id.repliesView);
        editTextReply = findViewById(R.id.editTextReply);
        buttonPostReply = findViewById(R.id.buttonReply);

        // Retrieve from the intent
        String questionTitle = getIntent().getStringExtra("questionTitle");
        String questionDescription = getIntent().getStringExtra("questionDescription");

        textViewQuestionTitle.setText(questionTitle);
        textViewQuestionDescription.setText(questionDescription);

        questionId = getIntent().getIntExtra("questionId", -1);
        voteCount = getIntent().getIntExtra("voteCount", 0);
        textViewVoteCount.setText(String.valueOf(voteCount));

        // Retrieve the replies from the intent
        repliesList = getIntent().getStringArrayListExtra("replies");

        // Initialize replies ListView
        repliesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, repliesList);
        listViewReplies.setAdapter(repliesAdapter);

        // Post Reply button
        buttonPostReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postReply();
            }
        });
        // Vote button
        buttonUpvote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                voteCount++;
                // Update the vote in the database
                updateVoteCountInDatabase(v,voteCount);
            }
        });
    }



    private void postReply() {
        String reply = editTextReply.getText().toString().trim();
        if (reply.isEmpty()) {
            Toast.makeText(this, "Please enter a reply", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Clear the input field
        editTextReply.setText("");

        // Post reply to the database and view
        postReplyToDatabase(reply);
    }

    private void postReplyToDatabase(String reply) {
        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = new FormBody.Builder()
                .add("question_id", String.valueOf(questionId))
                .add("reply", reply)
                .build();

        // Build the request
        Request request = new Request.Builder()
                .url("https://lamp.ms.wits.ac.za/home/s2572083/postR.php")
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    // Posted successfully
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Add to reply list
                            repliesList.add(reply);
                            repliesAdapter.notifyDataSetChanged();

                            // Clear the input field
                            editTextReply.setText("");
                            Toast.makeText(QuestionDetails.this, "Reply posted successfully", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    // Error case
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(QuestionDetails.this, "Error posting reply", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                // Connectivity
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(QuestionDetails.this, "Failed to post reply", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
    private void updateVoteCountInDatabase(View v,int voteCount) {
        SessionManager sessionManager = new SessionManager(this);
        String userId = sessionManager.getLoggedInUserId();

        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = new FormBody.Builder()
                .add("username", userId)
                .add("question_id", String.valueOf(questionId))
                .build();

        Request request = new Request.Builder()
                .url("https://lamp.ms.wits.ac.za/home/s2572083/vote.php") // Replace with your PHP endpoint URL
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();

                    if (responseData.equals("Vote recorded successfully!")) {

                        textViewVoteCount.setText(String.valueOf(voteCount));
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(QuestionDetails.this, "Vote count updated", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        // One man one vote the user has already voted for this question
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(QuestionDetails.this, responseData, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    // updating the vote count in the database error
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(QuestionDetails.this, "Error updating vote count", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                // Connectivity issues

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(QuestionDetails.this, "Failed to update vote count", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}