package com.example.alter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class question_list extends AppCompatActivity {
    private static final String TAG = question_list.class.getSimpleName();
    private static String SERVER_URL = "https://lamp.ms.wits.ac.za/home/s2572083/Qlist.php";
    private static final String SERVER_URL_ORDER = "https://lamp.ms.wits.ac.za/home/s2572083/order.php";

    private TextView orderButton;
    private ListView listViewQuestions;
    private ArrayAdapter<String> adapter;
    private List<Question> questionList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_list);

        // Buttons in your activity
        Button logoutButton = findViewById(R.id.buttonLogout);
        Button addQuestion = findViewById(R.id.buttonAddQuestion);
        orderButton = findViewById(R.id.textViewOrderByVote);

        addQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(question_list.this, post_question.class);
                startActivity(intent);
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Clear the session
                SessionManager sessionManager = new SessionManager(question_list.this);
                String p = sessionManager.getLoggedInUserId();
                sessionManager.clearSession();
                Toast.makeText(question_list.this, "Miss you already...", Toast.LENGTH_SHORT).show();
                // Roll out
                Intent intent = new Intent(question_list.this, Login.class);
                startActivity(intent);
                finish();
            }
        });

        listViewQuestions = findViewById(R.id.listViewQuestions);

        // Create an empty adapter for the ListView
        List<String> questionTitles = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, questionTitles);
        listViewQuestions.setAdapter(adapter);

        // Set click listener for the ListView items
        listViewQuestions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Question selectedQuestion = questionList.get(position);
                Intent intent = new Intent(question_list.this, QuestionDetails.class);
                intent.putExtra("questionId", selectedQuestion.getId());
                intent.putExtra("questionTitle", selectedQuestion.getTitle());
                intent.putExtra("questionDescription", selectedQuestion.getDescription());
                intent.putStringArrayListExtra("replies", new ArrayList<>(selectedQuestion.getReplies()));
                startActivity(intent);
            }
        });

        // Retrieve questions from the server
        retrieveQuestions();

        // Smooth operator @SirLinda
        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SERVER_URL = SERVER_URL_ORDER;
                retrieveQuestions();
            }
        });
    }

    private void retrieveQuestions() {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(SERVER_URL)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String responseData = response.body().string();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            processQuestionsResponse(responseData);
                        }
                    });
                } else {
                    Log.e(TAG, "HTTP error code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Error retrieving questions: " + e.getMessage());
            }
        });
    }

    private void processQuestionsResponse(String response) {
        try {
            JSONArray jsonArray = new JSONArray(response);
            questionList = new ArrayList<>();
            List<String> questionTitles = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                int id = jsonObject.getInt("id");
                String title = jsonObject.getString("title");
                String description = jsonObject.getString("description");
                int voteCount = jsonObject.getInt("vote_count"); // Retrieve vote count from the response
                JSONArray repliesArray = jsonObject.getJSONArray("replies");
                List<String> replies = new ArrayList<>();
                for (int j = 0; j < repliesArray.length(); j++) {
                    replies.add(repliesArray.getString(j));
                }
                questionList.add(new Question(id, title, description, replies, voteCount)); // Pass vote count to the Question object
                questionTitles.add(title);
            }

            adapter.clear();
            adapter.addAll(questionTitles);
            adapter.notifyDataSetChanged();
        } catch (JSONException e) {
            Log.e(TAG, "Error processing questions response: " + e.getMessage());
        }
    }
}
