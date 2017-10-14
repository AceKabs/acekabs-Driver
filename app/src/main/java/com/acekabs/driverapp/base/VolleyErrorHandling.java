package com.acekabs.driverapp.base;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.util.Log;

import com.acekabs.driverapp.R;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by nikhil on 04-04-2017.
 */
public class VolleyErrorHandling {
    static Dialog errorDialog;

    public static void errorHandling(VolleyError volleyError, final Activity activity) {

        if (activity == null) {
            return;
        }


        try {
            if (volleyError instanceof ServerError) {
                NetworkResponse networkResponse = volleyError.networkResponse;
                if (networkResponse.data != null) {
                    String data = new String(networkResponse.data);
                    JSONObject jsonObject = new JSONObject(data);
                    if (jsonObject.has("message")) {

                        errorDialog = ErrorDialog.dialogMessage(activity.getString(R.string.error_text), activity, jsonObject.get("message").toString());
                        errorDialog.show();
                    }
                } else {
                    errorDialog = ErrorDialog.dialogMessage(activity.getString(R.string.error_text), activity, "Server Error");
                    errorDialog.show();
                }

            } else if (volleyError instanceof NoConnectionError) {
                errorDialog = ErrorDialog.dialogMessage(activity.getString(R.string.error_text), activity, "No Connection Error");
                errorDialog.show();
            } else if (volleyError instanceof TimeoutError) {
                errorDialog = ErrorDialog.dialogMessage(activity.getString(R.string.error_text), activity, "Time Out ");
                errorDialog.show();
            } else if (volleyError instanceof AuthFailureError) {

                NetworkResponse networkResponse = volleyError.networkResponse;
                if (networkResponse.data != null) {
                    String data = new String(networkResponse.data);
                    JSONObject jsonObject = new JSONObject(data);
                    if (jsonObject.has("errorMessages")) {
                        JSONArray array = jsonObject.getJSONArray("errorMessages");
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < array.length(); i++) {
                            sb.append(array.get(i));
                            sb.append("\n");
                        }
                        sb.setLength(sb.length() - 1);
                        errorDialog = ErrorDialog.dialogMessage(activity.getString(R.string.error_text), activity, sb.toString());
                        errorDialog.show();
                    }
                } else {
                    errorDialog = ErrorDialog.dialogMessage(activity.getString(R.string.error_text), activity, "Server Error");
                    errorDialog.show();
                }

                if (volleyError.networkResponse.statusCode == 401) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(activity)
                            .setTitle("Error")
                            .setMessage("Authentication failure, your session is expired.")
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setCancelable(false);

                    errorDialog = builder.create();
                    errorDialog.show();

                } else {
                    errorDialog = ErrorDialog.dialogMessage(activity.getString(R.string.error_text), activity, "Authentication Failure!");
                    errorDialog.show();
                }


            } else if (volleyError instanceof NetworkError) {
                errorDialog = ErrorDialog.dialogMessage(activity.getString(R.string.error_text), activity, "Network Error!");
                errorDialog.show();
            } else if (volleyError instanceof ParseError) {
                errorDialog = ErrorDialog.dialogMessage(activity.getString(R.string.error_text), activity, "Parse Error!");
                errorDialog.show();
            } else {
                NetworkResponse networkResponse = volleyError.networkResponse;
                if (networkResponse != null && networkResponse.data != null) {

                    String data = new String(networkResponse.data);
                    JSONObject jsonObject = new JSONObject(data);

                    if (jsonObject.has("errorMessages")) {
                        JSONArray array = jsonObject.getJSONArray("errorMessages");
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < array.length(); i++) {
                            sb.append(array.get(i));
                            sb.append("\n");
                        }
                        sb.setLength(sb.length() - 1);
                        errorDialog = ErrorDialog.dialogMessage(activity.getString(R.string.error_text), activity, sb.toString());
                        errorDialog.show();
                    }
                }
            }
        } catch (Exception e) {
            errorDialog = ErrorDialog.dialogMessage(activity.getString(R.string.error_text), activity, activity.getString(R.string.something_went_wrong_text));
            errorDialog.show();
            Log.e("VolleyErrorHandling", e.getMessage());
        }
    }

}
