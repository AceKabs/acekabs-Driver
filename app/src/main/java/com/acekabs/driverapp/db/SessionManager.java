package com.acekabs.driverapp.db;

/* Purpose    : Managing Session For Driver App
   Updated On : 08/01/2017
   Auther     : Srinivas
*/

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.widget.Toast;

import com.acekabs.driverapp.LandingPage;
import com.acekabs.driverapp.SignIn;
import com.acekabs.driverapp.activity.MainHomeScreen;
import com.acekabs.driverapp.activity.Profile;
import com.acekabs.driverapp.constants.Constants;
import com.acekabs.driverapp.utils.SharePreferanceWrapperSingleton;

import java.util.HashMap;

public class SessionManager {

    public static final String KEY_NAME = "email";
    public static final String KEY_PASS = "pass";
    public static final String KEY_TOKEN = "token";
    private static final String PREF_NAME = "LoginCheck";
    private static final String IS_LOGIN = "IsLoggedIn";
    SharedPreferences pref;
    Editor editor;
    Context _context;
    Activity newcontext;
    int PRIVATE_MODE = 0;
    private SharePreferanceWrapperSingleton preferanceWrapperSingleton;
    public static String imei_check="";
    // Checking Token Status  : (1 = success,2 = IMEI Wrong, 3 = Pending Images, 4 = First Time Login)


    public SessionManager(Context context) {
        this._context = context.getApplicationContext();
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
        preferanceWrapperSingleton = SharePreferanceWrapperSingleton.getSingletonInstance();
        preferanceWrapperSingleton.setPref(context);
        preferanceWrapperSingleton.setEditor();

    }

    //  Creating Login Session
    public void createLoginSession(String name, String pass, String token) {
        if(token.equals("2")){
        imei_check=token;
            Log.e("mylog","token set==========================="+imei_check);
        }
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_PASS, pass);
        editor.putString(KEY_TOKEN, token);
        editor.commit();
    }

    // Checking Login Condition, Driver Login or Not
    public void checkLogin() {
        // Check login status
        if (!this.isLoggedIn()) {
            Intent i = new Intent(_context, MainHomeScreen.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            _context.startActivity(i);
        }

    }

    // Storing Login Data
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        user.put(KEY_NAME, pref.getString(KEY_NAME, null));
        user.put(KEY_PASS, pref.getString(KEY_PASS, null));
        user.put(KEY_TOKEN, pref.getString(KEY_TOKEN, null));
        return user;
    }

    // Clearing Session when User Logout
    public void logoutUser() {
//        editor.clear();
//        editor.commit();
        Intent i = new Intent(_context, LandingPage.class);
        preferanceWrapperSingleton.setValueToSharedPref(Constants.userId, "");
        preferanceWrapperSingleton.setValueToSharedPref(Constants.password, "");
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
