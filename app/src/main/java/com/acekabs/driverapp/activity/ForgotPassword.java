package com.acekabs.driverapp.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.acekabs.driverapp.ApplicationConstants;
import com.acekabs.driverapp.JSONParser;
import com.acekabs.driverapp.R;
import com.acekabs.driverapp.SignIn;
import com.acekabs.driverapp.constants.Constants;
import com.acekabs.driverapp.utils.SharePreferanceWrapperSingleton;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ForgotPassword extends Activity {

    EditText forgot_edit;
    String EMAIL_ID = "", INPUT_EMAIL = "";
    Button forgot_btn;
    JSONParser jsonParser = new JSONParser();
    String RESULT = "", MESSAGE = "", RESPONSE_CODE = "";
    View sign_header;
    TextView title, forget_pass;
    ImageView back;
    private ProgressDialog pDialog;
    private SharePreferanceWrapperSingleton preferanceWrapperSingleton;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        context = this;
        forgot_edit = (EditText) findViewById(R.id.forgot_edit);
        forgot_btn = (Button) findViewById(R.id.forgot_submit);
        sign_header = findViewById(R.id.forgot_header);
        back = (ImageView) sign_header.findViewById(R.id.back);
        title = (TextView) sign_header.findViewById(R.id.title);
        title.setText("FORGOT PASSWORD");
        preferanceWrapperSingleton = SharePreferanceWrapperSingleton.getSingletonInstance();
        preferanceWrapperSingleton.setPref(context);
        preferanceWrapperSingleton.setEditor();
        if (!TextUtils.isEmpty(preferanceWrapperSingleton.getStringValueFromSharedPref(Constants.userId))) {
            forgot_edit.setText(preferanceWrapperSingleton.getStringValueFromSharedPref(Constants.userId));
        }
//        try {
//            AccountManager accManager = AccountManager.get(getApplicationContext());
//            Account acc[] = accManager.getAccountsByType("com.google");
//            int accCount = acc.length;
//
//            for (int i = 0; i < accCount; i++) {
//                forgot_edit.setText(acc[0].name);
//                EMAIL_ID = acc[0].name;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        forgot_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                INPUT_EMAIL = forgot_edit.getText().toString().trim();
                if (INPUT_EMAIL.length() == 0) {
                    ShowAlert("ERROR", "Please enter valid email id");
                } else {
                    if (isNetworkAvailable()) {
                        new ForgotPassSend().execute();
                    } else {
                        ShowAlert("ERROR", "Please check internet connection !!");
                    }
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i1 = new Intent(ForgotPassword.this, SignIn.class);
                i1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i1);
            }
        });
    }

    public void ShowAlert(String title, String message) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.alert_dialog);
        dialog.show();

        TextView tit = (TextView) dialog.findViewById(R.id.dialog_title);
        tit.setText(title);
        TextView mes = (TextView) dialog.findViewById(R.id.dialog_message);
        mes.setText(message);
        Button ok = (Button) dialog.findViewById(R.id.dialog_submit);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();
                dialog.cancel();

            }
        });

    }

    public void ShowAlert_Succ(String title, String message) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.alert_dialog);
        dialog.show();

        TextView tit = (TextView) dialog.findViewById(R.id.dialog_title);
        tit.setText(title);
        TextView mes = (TextView) dialog.findViewById(R.id.dialog_message);
        mes.setText(message);
        Button ok = (Button) dialog.findViewById(R.id.dialog_submit);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                dialog.cancel();
                preferanceWrapperSingleton.setValueToSharedPref(Constants.password, "");
                Intent i1 = new Intent(ForgotPassword.this, SignIn.class);
                i1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i1);

            }
        });

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    class ForgotPassSend extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ForgotPassword.this);
            pDialog.setMessage("Please wait.......");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected String doInBackground(String... args) {

            try {
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("email", INPUT_EMAIL));
                Log.e("URL", ApplicationConstants.DRIVER_FORGOTPASS_URL + INPUT_EMAIL);
                JSONObject url_res = jsonParser.makeHttpRequest(ApplicationConstants.DRIVER_FORGOTPASS_URL + INPUT_EMAIL + "/driver", "POST", params);
                Log.e("response", url_res.toString());
                RESULT = url_res.getString("result");
                Log.e("Result", RESULT);
                MESSAGE = url_res.getString("info");
                RESPONSE_CODE = url_res.getString("code");
                Log.e("RESPONSE_CODE", RESPONSE_CODE);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
            pDialog.dismiss();
            pDialog.cancel();

            if (RESPONSE_CODE.equalsIgnoreCase("101")) {
                ShowAlert("ERROR !!", MESSAGE);
                // Toast.makeText(getApplicationContext(),MESSAGE,Toast.LENGTH_SHORT).show();
            } else if (RESPONSE_CODE.equalsIgnoreCase("100")) {
                ShowAlert_Succ("MESSAGE !!", MESSAGE);
            } else if (RESPONSE_CODE.equalsIgnoreCase("400")) {
                ShowAlert("ERROR !!", MESSAGE);
            } else {

            }

        }

    }


}
