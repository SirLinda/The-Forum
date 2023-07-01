package com.example.alter;

import java.util.List;

public class Question {
    private int id;
    private String title;
    private String description;
    private List<String> replies;
    private int voteCount;

    public Question(int id, String title, String description, List<String> replies, int voteCount) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.replies = replies;
        this.voteCount = voteCount;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getReplies() {
        return replies;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }
}
