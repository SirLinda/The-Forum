package com.example.alter;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "MyAppPref";
    private static final String KEY_USER_ID = "user_id";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Context context;

    public SessionManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void setLoggedInUserId(String userId) {
        editor.putString(KEY_USER_ID, userId);
        editor.apply();
    }

    public String getLoggedInUserId() {
        return sharedPreferences.getString(KEY_USER_ID, "");
    }

    public void clearSession() {
        editor.clear();
        editor.apply();
    }
}
