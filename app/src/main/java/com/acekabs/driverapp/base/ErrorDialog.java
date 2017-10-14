package com.acekabs.driverapp.base;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by nikhil on 04-04-2017.
 */
public class ErrorDialog {


    public static Dialog GetDialog(String title, Context context, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete

                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert);
        return builder.create();

    }

    public static Dialog dialogMessage(String title, Context context, String msg) {
        AlertDialog.Builder builder;
        if (title.equalsIgnoreCase("") || title.equalsIgnoreCase(" ")) {
            builder = new AlertDialog.Builder(context)
                    .setMessage(msg)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .setCancelable(false);
        } else {
            builder = new AlertDialog.Builder(context)
                    .setTitle(title)
                    .setMessage(msg)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .setCancelable(false);
        }
        return builder.create();
    }

    public static Dialog dialogMessage(String msg, String title, final Activity context) {
        AlertDialog.Builder builder;

        builder = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
//                        ((ActionBarActivity)context).onBackPressed();
                    }
                })
                .setCancelable(false);

        return builder.create();
    }

    public static Dialog dialogMessage(Context context, String title, String msg) {
        AlertDialog.Builder builder;
        if (title.equalsIgnoreCase("") || title.equalsIgnoreCase(" ")) {
            builder = new AlertDialog.Builder(context, AlertDialog.THEME_DEVICE_DEFAULT_DARK)
                    .setMessage(msg)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .setCancelable(false);
        } else {
            builder = new AlertDialog.Builder(context, AlertDialog.THEME_DEVICE_DEFAULT_DARK)
                    .setTitle(title)
                    .setMessage(msg)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .setCancelable(false);
        }
        return builder.create();
    }

    /**
     * Display any error message or if anything went wrong on server
     *
     * @param mContext
     * @param error
     * @param responseBody
     * @param TAG
     */
    public void displayErrorMessage(Context mContext, Throwable error, byte[] responseBody, String TAG) {
        String message = null;
        try {
            if (error.getMessage().contains("timed out")) {
                message = "server down";
                GetDialog("Error", mContext, message).show();
            } else {
                String response = new String(responseBody);
                JSONObject jsonObject = new JSONObject(response);
                if (!jsonObject.has("errorMessages")) {

                } else {
                    JSONArray array = jsonObject.getJSONArray("errorMessages");
                    String errors = array.getString(0);
                }
                message = new String(responseBody);
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }
}
