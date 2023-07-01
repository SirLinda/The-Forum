package com.example.alter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.ViewHolder> {
    private List<Question> questions;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_question_details, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Question question = questions.get(position);
        holder.textViewQuestionTitle.setText(question.getTitle());
        holder.textViewQuestionDescription.setText(question.getDescription());
        holder.textViewVoteCount.setText(String.valueOf(question.getVoteCount())); // Update vote count

    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewQuestionTitle;
        TextView textViewQuestionDescription;
        TextView textViewVoteCount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewQuestionTitle = itemView.findViewById(R.id.textViewQuestionTitle);
            textViewQuestionDescription = itemView.findViewById(R.id.textViewQuestionDescription);
            textViewVoteCount = itemView.findViewById(R.id.textViewVoteCount);
        }
    }
}
