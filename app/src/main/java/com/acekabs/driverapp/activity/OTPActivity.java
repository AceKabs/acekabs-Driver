package com.acekabs.driverapp.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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

import com.acekabs.driverapp.ApplicationConstants;
import com.acekabs.driverapp.JSONParser;
import com.acekabs.driverapp.R;
import com.acekabs.driverapp.SignIn;
import com.acekabs.driverapp.SignUp;
import com.acekabs.driverapp.db.DBAdapter;
import com.acekabs.driverapp.db.OTPManager;
import com.acekabs.driverapp.entities.AceKabsDriverPreRegistration;
import com.acekabs.driverapp.interfaces.SmsListener;
import com.acekabs.driverapp.receivers.SmsReceiver;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
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
    TextView title, otp_resend;
    JSONParser jsonParser = new JSONParser();
    String PNUM = "", sle_item = "", RESULT = "", MESSAGE = "", FNAME = "", MOBILE = "", EMAIL = "", SESSION_MOBILE = "", SESSION_EMAIL = "", SESSION_FNAME = "";
    DBAdapter mydb;
    Cursor cursor;
    OTPManager otp_manager;
    private ProgressDialog pDialog;
    private String RESPONSE_CODE = "", EMAIL_RES_CODE = "", OTP_RESEND_CODE = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.otp_verification);
        otp_header = findViewById(R.id.otp_header);
        back = (ImageView) otp_header.findViewById(R.id.back);
        title = (TextView) otp_header.findViewById(R.id.title);
        title.setText("MOBILE VERIFICATION");
        otp_resend = (TextView) findViewById(R.id.otp_resend);
        otp_btn_confirm = (Button) findViewById(R.id.otp_btn_confirm);
        otp_edit = (EditText) findViewById(R.id.otp_edit);
        otp_btn_confirm.setOnClickListener(this);
        back.setOnClickListener(this);
        otp_resend.setOnClickListener(this);
        mydb = DBAdapter.getInstance(getApplicationContext());

        otp_manager = new OTPManager(getApplicationContext());

        HashMap<String, String> user = otp_manager.getUserDetails();
        SESSION_MOBILE = user.get(OTPManager.MOBILE_NO);
        SESSION_EMAIL = user.get(OTPManager.KEY_EMAIL);
        SESSION_FNAME = user.get(OTPManager.KEY_FNAME);


        try {
            for (AceKabsDriverPreRegistration driver_details : SignUp.Signup_details) {
                FNAME = driver_details.getFirstName();
                EMAIL = driver_details.getEmailID();
                MOBILE = driver_details.getPhoneNumber();
                Log.e("FName", driver_details.getFirstName());
                Log.e("LName", driver_details.getLastName());
                Log.e("EMail", driver_details.getEmailID());
                Log.e("MobileNo", driver_details.getPhoneNumber());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Cursor c = mydb.getDriver_reg(MOBILE);


        try {
            if (c != null) {
                if (c.moveToFirst()) {
                    String code;
                    code = c.getString(c.getColumnIndex("driver_mobileno"));

                    Log.e("code", code);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        /********************added code for get OTP**********/

        SmsReceiver.bindListener(new SmsListener() {
            @Override
            public void messageReceived(String messageText) {
                Log.d("Text", messageText);
//                Toast.makeText(OTPActivity.this, "Message: " + messageText, Toast.LENGTH_LONG).show();
                otp_edit.setText(messageText);
            }
        });
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

        } else if (v.getId() == R.id.otp_resend) {
            if (isNetworkAvailable()) {
                new OtpResend().execute();
            } else {
                ShowAlert("Warning", "Please check Network connection !!");
            }
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

    public void recivedSms(String message) {
        try {
            otp_edit.setText(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void ShowAlert1(String title, String message) {
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
                otp_manager.logoutUser();
                Intent li = new Intent(OTPActivity.this, SignIn.class);
                li.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(li);

            }
        });

    }

    class OTPVerification extends AsyncTask<String, String, String>
    {

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            pDialog = new ProgressDialog(OTPActivity.this);
            pDialog.setMessage("Please wait Verifying OTP");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected String doInBackground(String... args)
        {

            try {

                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("Otp", OTP));


                Log.e("URL", ApplicationConstants.DRIVER_OTP_CHECK_URL + SESSION_MOBILE + "/" + OTP);
                JSONObject response = jsonParser.makeHttpRequest(ApplicationConstants.DRIVER_OTP_CHECK_URL + SESSION_MOBILE + "/" + OTP, "POST", params);
                Log.e("response", response.toString());
                RESULT = response.getString("result");
                Log.e("Result", RESULT);
                MESSAGE = response.getString("info");
                RESPONSE_CODE = response.getString("code");
                Log.e("RESPONSE_CODE", RESPONSE_CODE);

            } catch (Exception e) {

            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
            pDialog.dismiss();
            pDialog.cancel();

            try {
                if (RESPONSE_CODE.equalsIgnoreCase("101")) {
                    ShowAlert("Failed", MESSAGE);
                } else if (RESPONSE_CODE.equalsIgnoreCase("100")) {
                    new EmailVerification().execute();
                } else if (RESPONSE_CODE.equalsIgnoreCase("400")) {
                    ShowAlert("ERRROR !!!", MESSAGE);
                } else {

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    class EmailVerification extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(OTPActivity.this);
            pDialog.setMessage("Please wait ......");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected String doInBackground(String... args) {
            try {
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("email", SESSION_EMAIL));
                params.add(new BasicNameValuePair("emailfor", "Driver"));
                params.add(new BasicNameValuePair("userName", SESSION_FNAME));


                Log.e("Email verification", ApplicationConstants.DRIVER_EMAIL_URL + "/" + SESSION_EMAIL + "/" + "Driver" + "/" + SESSION_FNAME);
                JSONObject response = jsonParser.makeHttpRequest(ApplicationConstants.DRIVER_EMAIL_URL + "/" + SESSION_EMAIL + "/" + "Driver" + "/" + SESSION_FNAME, "POST", params);
                Log.e("response", response.toString());
                RESULT = response.getString("result");
                Log.e("Result", RESULT);
                MESSAGE = response.getString("info");
                EMAIL_RES_CODE = response.getString("code");
                Log.e("MESSAGE", MESSAGE);

            } catch (Exception e) {

            }
            return null;
        }

        protected void onPostExecute(String file_url)
        {
            // dismiss the dialog once done
            pDialog.dismiss();
            pDialog.cancel();

            try
            {
                if (EMAIL_RES_CODE.equalsIgnoreCase("101"))
                {
                    ShowAlert("Failed", MESSAGE);
                }
                else if (EMAIL_RES_CODE.equalsIgnoreCase("100"))
                {
                    // Toast.makeText(getApplicationContext(),"Email Sent",Toast.LENGTH_SHORT).show();
                    ShowAlert1("There is just one more step", "We sent you a confirmation email with a link to activation Your subscription. \n " +
                            "Please check your mail and click the link to continue to signin.");
                } else if (EMAIL_RES_CODE.equalsIgnoreCase("400")) {
                    ShowAlert("ERROR", MESSAGE);
                } else {

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    class OtpResend extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(OTPActivity.this);
            pDialog.setMessage("Please wait Sending OTP");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected String doInBackground(String... args) {
            try {
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("MobileNumber", SESSION_MOBILE));
                Log.e("URL", ApplicationConstants.DRIVER_OTP_RESEND_URL + SESSION_MOBILE);
                JSONObject response = jsonParser.makeHttpRequest(ApplicationConstants.DRIVER_OTP_RESEND_URL + SESSION_MOBILE, "POST", params);
                Log.e("response", response.toString());
                RESULT = response.getString("result");
                Log.e("Result", RESULT);
                MESSAGE = response.getString("info");
                Log.e("MESSAGE", MESSAGE);
                OTP_RESEND_CODE = response.getString("code");
                Log.e("EMAIL_RES_CODE", OTP_RESEND_CODE);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
            pDialog.dismiss();
            pDialog.cancel();

            try {
                if (OTP_RESEND_CODE.equalsIgnoreCase("101")) {
                    ShowAlert("Failed", MESSAGE);
                } else if (OTP_RESEND_CODE.equalsIgnoreCase("100")) {
                    Toast.makeText(getApplicationContext(), MESSAGE, Toast.LENGTH_SHORT).show();
                } else if (OTP_RESEND_CODE.equalsIgnoreCase("400")) {
                    ShowAlert("ERROR !!", MESSAGE);
                } else {

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
