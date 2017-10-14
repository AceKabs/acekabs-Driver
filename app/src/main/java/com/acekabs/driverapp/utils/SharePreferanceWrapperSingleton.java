package com.acekabs.driverapp.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SharePreferanceWrapperSingleton {

    private static SharePreferanceWrapperSingleton singleton;
    private SharedPreferences pref;
    private Editor editor;

    /*
     * A private Constructor prevents any other class from instantiating.
     */
    private SharePreferanceWrapperSingleton() {

    }

    /* Static 'instance' method */
    public static SharePreferanceWrapperSingleton getSingletonInstance() {
        if (null == singleton) {
            singleton = new SharePreferanceWrapperSingleton();
        }
        return singleton;
    }

    // getter and setter
    public int getValueFromSharedPref(String key) {
        if (key.equalsIgnoreCase("alaramManagerIsChecked"))
            return pref.getInt(key, 1);
        else if (key.equalsIgnoreCase("delaytime"))
            return pref.getInt(key, 1000 * 15);
        else
            return pref.getInt(key, 0);
    }

    public String getStringValueFromSharedPref(String key) {

        return pref.getString(key, "");
    }

    public String getStringValueFromSharedPrefrences(String key) {
        String value = "";
        try {
            value = pref.getString(key, "");
        } catch (Exception ex) {
            Editor edit = pref.edit();
            edit.putString(key, "");
            edit.commit();
        }
        return pref.getString(key, "");
    }

    public Boolean getBooleanValueFromSharedPref(String key) {

        return pref.getBoolean(key, false);
    }

    public void setValueToSharedPref(String key, String value) {
        editor.putString(key, value);
        editor.commit();
    }

    public void setBooleanValueToSharedPref(String key, Boolean value) {
        editor.putBoolean(key, value);
        editor.commit();
    }

    @SuppressWarnings("static-access")
    public void setPref(Context context) {
        this.pref = context.getSharedPreferences("KabsDriverAppPref", context.MODE_PRIVATE);
    }

    @SuppressLint("CommitPrefEdits")
    public void setEditor() {
        this.editor = pref.edit();
    }

    public void setValuesParameters(SharePreferanceWrapperSingleton objSPS, int heightScreen, int widthScreen) {

    }

}
