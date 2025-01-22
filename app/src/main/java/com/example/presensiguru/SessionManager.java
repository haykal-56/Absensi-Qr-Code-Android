package com.example.presensiguru;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Context context;

    private static final String PREF_NAME = "PresensiGuruSession";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_TELEPON = "telepon";

    public SessionManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    // Method to create a login session
    public void createLoginSession(String username) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true); // Set login status to true
        editor.putString(KEY_USERNAME, username); // Store username
        editor.commit(); // Save changes
    }

    // Method to logout user and clear session data
    public void logoutSession() {
        editor.clear(); // Clear all session data
        editor.commit(); // Save changes
    }

    // Check if user is logged in
    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    // Get stored username
    public String getUsername() {
        return sharedPreferences.getString(KEY_USERNAME, null);
    }

    public String getKeyTelepon() {return  sharedPreferences.getString(KEY_TELEPON, null);}
}
