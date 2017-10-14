package com.acekabs.driverapp;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.acekabs.driverapp.activity.Company;
import com.acekabs.driverapp.entities.AceKabsDriverPreRegistration;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Srinivas Devarapalli on 25/11/16.
 */
public class OTPActivity extends Activity implements View.OnClickListener {
    Button otp_btn_confirm;
    EditText otp_edit;
    String OTP = "";
    View otp_header;
    ImageView back;
    TextView title;
    JSONParser jsonParser = new JSONParser();
    String PNUM = "", sle_item = "", RESULT = "", MESSAGE = "";
    AceKabsDriverPreRegistration driver_reg;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.otp_verification);
        otp_header = findViewById(R.id.otp_header);
        back = (ImageView) otp_header.findViewById(R.id.back);
        title = (TextView) otp_header.findViewById(R.id.title);
        title.setText("MOBILE VERIFICATION");

        otp_btn_confirm = (Button) findViewById(R.id.otp_btn_confirm);
        otp_edit = (EditText) findViewById(R.id.otp_edit);
        otp_btn_confirm.setOnClickListener(this);
        back.setOnClickListener(this);

        driver_reg = new AceKabsDriverPreRegistration();
    }

    public void onClick(View v) {

        if (v.getId() == R.id.otp_btn_confirm) {
            OTP = otp_edit.getText().toString().trim();
            if (OTP.length() == 0) {
                ShowAlert("Alert !!", "Please Enter OTP Number");
            } else {

                if (isNetworkAvailable()) {
                    new OTPVerification().execute();
                } else {
                    ShowAlert("Warning", "Please check Network connection !!");
                }

                /* Intent li = new Intent(OTPActivity.this, Company.class);
                li.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(li);*/
            }

        } else if (v.getId() == R.id.back) {
            Intent li = new Intent(OTPActivity.this, SignUp.class);
            li.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(li);

        }

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

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    class OTPVerification extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(OTPActivity.this);
            pDialog.setMessage("Please wait Verifying OTP");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected String doInBackground(String... args) {

            try {

                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("Otp", OTP));


                Log.e("URL", ApplicationConstants.DRIVER_OTP_CHECK_URL + SignUp.PNUM + "/" + OTP);
                JSONObject response = jsonParser.makeHttpRequest(ApplicationConstants.DRIVER_OTP_CHECK_URL + SignUp.PNUM + "/" + OTP, "POST", params);
                Log.e("response", response.toString());
                RESULT = response.getString("result");
                Log.e("Result", RESULT);
                MESSAGE = response.getString("info");
                Log.e("MESSAGE", MESSAGE);

            } catch (Exception e) {

            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
            pDialog.dismiss();
            pDialog.cancel();

            if (RESULT.equalsIgnoreCase("Failed")) {
                ShowAlert("Failed", MESSAGE);
            } else if (RESULT.equalsIgnoreCase("Sucess")) {
                Intent li = new Intent(OTPActivity.this, Company.class);
                li.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(li);
                Toast.makeText(getApplicationContext(), MESSAGE, Toast.LENGTH_SHORT).show();
            }

        }

    }

}
