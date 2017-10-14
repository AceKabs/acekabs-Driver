package com.acekabs.driverapp.db;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.acekabs.driverapp.SignIn;
import com.acekabs.driverapp.activity.OTPActivity;

import java.util.HashMap;

/**
 * Created by srinivas on 19/1/17.
 */
public class OTPManager {

    public static final String MOBILE_NO = "mobile_no";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_FNAME = "fname";
    private static final String PREF_NAME = "OtpCheck";
    private static final String IS_LOGIN = "IsLoggedIn";
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;
    Activity newcontext;
    int PRIVATE_MODE = 0;


    public OTPManager(Context context) {
        this._context = context.getApplicationContext();
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();

    }

    //  Creating Login Session
    public void createLoginSession(String name, String email, String fname) {
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(MOBILE_NO, name);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_FNAME, fname);
        editor.commit();
    }

    // Checking Login Condition, Driver Login or Not
    public void checkLogin() {
        // Check login status
        if (!this.isLoggedIn()) {
            Intent i = new Intent(_context, OTPActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            _context.startActivity(i);
        }

    }

    // Storing Login Data
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        user.put(MOBILE_NO, pref.getString(MOBILE_NO, null));
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));
        user.put(KEY_FNAME, pref.getString(KEY_FNAME, null));

        return user;
    }

    // Clearing Session when User Logout
    public void logoutUser() {
        editor.clear();
        editor.commit();
        Intent i = new Intent(_context, SignIn.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.putExtra("finish", true);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        _context.startActivity(i);
    }


    // Get Login State
    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }
}
