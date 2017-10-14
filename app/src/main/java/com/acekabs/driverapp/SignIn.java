package com.acekabs.driverapp;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TextInputEditText;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.acekabs.driverapp.activity.ForgotPassword;
import com.acekabs.driverapp.activity.MainHomeScreen;
import com.acekabs.driverapp.activity.Vehicle;
import com.acekabs.driverapp.constants.Constants;
import com.acekabs.driverapp.db.DBAdapter;
import com.acekabs.driverapp.db.SessionManager;
import com.acekabs.driverapp.utils.SharePreferanceWrapperSingleton;
import com.acekabs.driverapp.utils.UtilityMethods;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by srinivas Devarapalli on 24/11/16.
 */
public class SignIn extends Activity implements View.OnClickListener {

   public static String imei_check="1";
    EditText signin_email;
    TextInputEditText signin_pass;
    Button signin_submit;
    String EMAIL = "", PASS = "";
    ImageView back;
    View sign_header;
    TextView title, forget_pass;
    JSONParser jsonParser = new JSONParser();
    String RESULT = "", MESSAGE = "", RESPONSE_CODE = "", RESULT1 = "", MESSAGE1 = "", RESPONSE_CODE1 = "";
    DBAdapter mydb;
    Cursor mcursor;
    SessionManager msession;
    String Login_Email = "", Login_Pass = "", Driver_Status = "";
    Cursor all_cursor;
    String firstName = "", lastName = "", phoneNumber = "", emailID = "", compName = "", compCity = "", deviceInfo = "",
            country = "", images = "", activated = "", vehiclePhotoDoc = "", plateNumberDoc = "", driverPhotoDoc = "",
            driverLicenseDoc = "", driverIDCardDocFront = "", driverIDCardDocBack = "", driverPassportDocFront = "",
            driverPassportDocBack = "", carRegistrationDocFront = "", carRegistrationDocBack = "", vehicleInsuranceDoc = "",
            Pincode = "", CarName = "", CarModel = "", CarColor = "", CarManu_year = "", NoPass = "", Driver_DOB = "",
            Vehicle_URL = "", LicensePlate_URL = "", DriverPhoto_URL = "", DriverLicenseNo_URL = "", IDFront_URL = "", IDBack_URL = "",
            PassportFront_URL = "", PassportBack_URL = "", VehicleRegFront_URL = "", VehicleRegBack_URL = "", Insurence_URL = "",
            regDate = "", Res_PassWord = "", compAddr = "", iMEI = "", latLong = "";
    String IMEI = "";
    SharePreferanceWrapperSingleton preferanceWrapperSingleton;
    Context context;
    private ProgressDialog pDialog;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedinstance) {
        super.onCreate(savedinstance);
        setContentView(R.layout.activity_sign);
        context = this;
        msession = new SessionManager(getApplicationContext());
        preferanceWrapperSingleton = SharePreferanceWrapperSingleton.getSingletonInstance();
        preferanceWrapperSingleton.setPref(context);
        preferanceWrapperSingleton.setEditor();
        signin_email = (EditText) findViewById(R.id.signin_email);
        signin_pass = (TextInputEditText) findViewById(R.id.signin_pass);
        if (!TextUtils.isEmpty(preferanceWrapperSingleton.getStringValueFromSharedPref(Constants.userId))) {
            signin_email.setText(preferanceWrapperSingleton.getStringValueFromSharedPref(Constants.userId));
        }
        if (!TextUtils.isEmpty(preferanceWrapperSingleton.getStringValueFromSharedPref(Constants.password))) {
            signin_pass.setText(preferanceWrapperSingleton.getStringValueFromSharedPref(Constants.password));
        }
        signin_submit = (Button) findViewById(R.id.signin_submit);
        signin_submit.setOnClickListener(this);
        sign_header = findViewById(R.id.signin_header);
        back = (ImageView) sign_header.findViewById(R.id.back);
        title = (TextView) sign_header.findViewById(R.id.title);
        title.setText("SIGN IN");
        forget_pass = (TextView) findViewById(R.id.sign_forget);
        forget_pass.setOnClickListener(this);
        back.setOnClickListener(this);


//        if (msession.isLoggedIn() && msession.getUserDetails().get(SessionManager.KEY_TOKEN).equals("1")) {
//            Intent i1 = new Intent(SignIn.this, MainHomeScreen.class);
//            startActivity(i1);
//        }

//        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
//        IMEI = telephonyManager.getDeviceId();
        IMEI = UtilityMethods.getIMEI(SignIn.this);
        Log.e("IMEI", IMEI);

        // Checking Driver Status
//        try {
//            AccountManager accManager = AccountManager.get(getApplicationContext());
//            Account acc[] = accManager.getAccountsByType("com.google");
//            int accCount = acc.length;
//
//            for (int i = 0; i < accCount; i++) {
//                signin_email.setText(acc[0].name);
//                //  EMAIL_ID = acc[0].name;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        mydb = DBAdapter.getInstance(getApplicationContext());
        mydb.addNewColumn("driverLicensePlate_URL");
        if (!TextUtils.isEmpty(preferanceWrapperSingleton.getStringValueFromSharedPref(Constants.userId)) &&
                !TextUtils.isEmpty(preferanceWrapperSingleton.getStringValueFromSharedPref(Constants.password))) {
            CallAutoLogin();
        }
//        signin_email.setText("golumca010@gmail.com");
//        signin_pass.setText("Test12@");
    }

    private void CallAutoLogin() {
        EMAIL = signin_email.getText().toString().trim();
        PASS = signin_pass.getText().toString().trim();
        if (isNetworkAvailable()) {
            try {
                all_cursor = mydb.getDriver_reg_Email(EMAIL);
                Log.e("Cursor0", "" + all_cursor.getCount());
                if (all_cursor != null) {
                    if (all_cursor.moveToFirst()) {
                        Log.e("Cursor1", "" + all_cursor.getCount());
                        Driver_Status = all_cursor.getString(all_cursor.getColumnIndex("driver_status"));
                        Login_Email = all_cursor.getString(all_cursor.getColumnIndex("driver_emailid"));
                        Login_Pass = all_cursor.getString(all_cursor.getColumnIndex("password"));
                        Log.e("Driver_Status", Driver_Status);
                        Log.e("Login_Email", Login_Email);
                        Log.e("Login_Pass", Login_Pass);
                        if (Driver_Status.equalsIgnoreCase("0")) { // Driver Not Upload Documents,First time login
                           Log.e("SignIn","Driver_Status=>0");
                            if (EMAIL.equalsIgnoreCase(Login_Email) && PASS.equalsIgnoreCase(Login_Pass)) {
                                // Call Email Registerd WebService
                                if (isNetworkAvailable()) {
                                    new EmailCheck().execute();
                                } else {
                                    ShowAlert("WARNING !!", "Please Check Internet Connection!!");
                                }
                            } else {
                                ShowAlert("ERROR !!", "Please Check Email/Password Not Matched");
                            }
                        } else if (Driver_Status.equalsIgnoreCase("1")) {// Driver  Upload Documents,Second time login
                            Log.e("SignIn","Driver_Status=>1");
                            if (EMAIL.equalsIgnoreCase(Login_Email) && PASS.equalsIgnoreCase(Login_Pass)) {
                                // Call Email Registerd WebService
                                if (isNetworkAvailable()) {
                                    new EmailCheckOne().execute();
                                } else {
                                    ShowAlert("WARNING !!", "Please Check Internet Connection!!");
                                }
                            } else {
                                ShowAlert("ERROR !!", "Please Check Email/Password Not Matched");
                            }
                        } else if (Driver_Status.equalsIgnoreCase("2")) { // Driver Documents Approved ,Token Generated
                            Log.e("SignIn","Driver_Status=>2");
                            if (isNetworkAvailable()) {
                                new EmailCheckTwo().execute();
                            } else {
                                ShowAlert("WARNING !!", "Please Check Internet Connection!!");
                            }
                        } else if (Driver_Status.equalsIgnoreCase("false")) {
                            Log.e("SignIn","Driver_Status=>false");
                            Log.e("FLOW" , "CreateSession With 5");
                            msession.createLoginSession(signin_email.getText().toString().trim(), signin_pass.getText().toString().trim(), "5");
                            preferanceWrapperSingleton.setValueToSharedPref(Constants.userId, signin_email.getText().toString().trim());
                            preferanceWrapperSingleton.setValueToSharedPref(Constants.password, signin_pass.getText().toString().trim());
                            Intent i1 = new Intent(SignIn.this, MainHomeScreen.class);
                            i1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i1);
                            finish();
                        } else if (Driver_Status.equalsIgnoreCase("true")) {
                            Log.e("SignIn","Driver_Status=>true");
                            if (isNetworkAvailable()) {
                                new SignInVerificationCheck().execute();
                            }
                        }
                    }
                } else {
                    //  null getting from Cursor Means User app Uninstalled and Installed
                    Log.e("Else", "Else");
                    if (isNetworkAvailable()) {
                        new EmailCheckTwo().execute();
                    } else {
                        ShowAlert("WARNING !!", "Please Check Internet Connection!!");
                    }
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void onClick(View v) {
        if (v.getId() == R.id.signin_submit)
        {
            EMAIL = signin_email.getText().toString().trim();
            PASS = signin_pass.getText().toString().trim();
            if (EMAIL.length() == 0 && PASS.length() == 0) {
                ShowAlert("Error", "Please enter all fields");
            } else if (EMAIL.length() == 0) {
                ShowAlert("Error", "Please enter valid email id");
            } else if (PASS.length() == 0) {
                ShowAlert("Error", "Please enter password");
            } else {
                try {
                    all_cursor = mydb.getDriver_reg_Email(EMAIL);
                    Log.e("Cursor0", "" + all_cursor.getCount());
                    if (all_cursor != null && all_cursor.getCount() > 0) {
                        if (all_cursor.moveToFirst()) {
                            Log.e("Cursor1", "" + all_cursor.getCount());
                            Driver_Status = all_cursor.getString(all_cursor.getColumnIndex("driver_status"));
                            Login_Email = all_cursor.getString(all_cursor.getColumnIndex("driver_emailid"));
                            Login_Pass = all_cursor.getString(all_cursor.getColumnIndex("password"));
                            Log.e("Driver_Status", Driver_Status);
                            Log.e("Login_Email", Login_Email);
                            Log.e("Login_Pass", Login_Pass);
                            if (Driver_Status.equalsIgnoreCase("0")) { // Driver Not Upload Documents,First time login
                                if (EMAIL.equalsIgnoreCase(Login_Email) && PASS.equalsIgnoreCase(Login_Pass)) {
                                    // Call Email Registerd WebService
                                    if (isNetworkAvailable()) {
                                        new EmailCheck().execute();
                                    } else {
                                        ShowAlert("WARNING !!", "Please Check Internet Connection!!");
                                    }
                                } else {
                                    ShowAlert("ERROR !!", "Please Check Email/Password Not Matched");
                                }
                            } else if (Driver_Status.equalsIgnoreCase("1")) {// Driver  Upload Documents,Second time login
                                if (EMAIL.equalsIgnoreCase(Login_Email) && PASS.equalsIgnoreCase(Login_Pass)) {
                                    // Call Email Registerd WebService
                                    if (isNetworkAvailable()) {
                                        new EmailCheckOne().execute();
                                    } else {
                                        ShowAlert("WARNING !!", "Please Check Internet Connection!!");
                                    }
                                } else {
                                    ShowAlert("ERROR !!", "Please Check Email/Password Not Matched");
                                }
                            } else if (Driver_Status.equalsIgnoreCase("2")) { // Driver Documents Approved ,Token Generated
                                if (isNetworkAvailable()) {
                                    new EmailCheckTwo().execute();
                                } else {
                                    ShowAlert("WARNING !!", "Please Check Internet Connection!!");
                                }
                            } else if (Driver_Status.equalsIgnoreCase("false")) {
                                Log.e("FLOW" , "CreateSession With 5");
                                msession.createLoginSession(signin_email.getText().toString().trim(), signin_pass.getText().toString().trim(), "5");
                                Intent i1 = new Intent(SignIn.this, MainHomeScreen.class);
                                i1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(i1);
                                finish();
                            } else if (Driver_Status.equalsIgnoreCase("true")) {
                                if (isNetworkAvailable()) {
                                    new SignInVerificationCheck().execute();
                                }
                            }
                        }
                    } else {
                        //  null getting from Cursor Means User app Uninstalled and Installed
                        Log.e("Else", "Else");
                        if (isNetworkAvailable()) {
                            new EmailCheckTwo().execute();
                        } else {
                            ShowAlert("WARNING !!", "Please Check Internet Connection!!");
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
        else if (v.getId() == R.id.sign_forget)
        {
            Intent i1 = new Intent(SignIn.this, ForgotPassword.class);
            startActivity(i1);
        }
        else if (v.getId() == R.id.back)
        {
            Intent signin = new Intent(SignIn.this, LandingPage.class);
            signin.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(signin);
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

    @Override
    public void onPause() {
        super.onPause();

        if ((pDialog != null) && pDialog.isShowing())
            pDialog.dismiss();
        pDialog = null;
    }

    class EmailCheck extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(SignIn.this);
            pDialog.setMessage("Please wait Checking  EMail..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected String doInBackground(String... args) {

            try {

                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("email", EMAIL));

                Log.e("URL", ApplicationConstants.DRIVER_EMAIL_VALIDATION_URL + EMAIL);
                JSONObject url_res = jsonParser.makeHttpRequest(ApplicationConstants.DRIVER_EMAIL_VALIDATION_URL + EMAIL, "POST", params);
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
            if (pDialog != null) {
                pDialog.dismiss();
                pDialog.cancel();
            }

            if (RESPONSE_CODE.equalsIgnoreCase("101")) {
                // Toast.makeText(getApplicationContext(),MESSAGE,Toast.LENGTH_SHORT).show();
                ShowAlert("Failed !!", MESSAGE);
            } else if (RESPONSE_CODE.equalsIgnoreCase("100")) {

                Log.e("FLOW" , "CreateSession With 4");
                msession.createLoginSession(signin_email.getText().toString().trim(), signin_pass.getText().toString().trim(), "4");
                try {
                    mcursor = mydb.getDriver_reg_Email(signin_email.getText().toString().trim());
                    if (mcursor != null)
                    {
                        if (mcursor.moveToFirst())
                        {
                            String fname = mcursor.getString(mcursor.getColumnIndex("driver_fname"));
                            String lname = mcursor.getString(mcursor.getColumnIndex("driver_lname"));
                            String dri_mobile = mcursor.getString(mcursor.getColumnIndex("driver_mobileno"));
                            String driver_emailid = mcursor.getString(mcursor.getColumnIndex("driver_emailid"));
                            String driver_pass = mcursor.getString(mcursor.getColumnIndex("password"));
                            String company_name = mcursor.getString(mcursor.getColumnIndex("company_name"));
                            String company_address = mcursor.getString(mcursor.getColumnIndex("company_address"));
                            String city = mcursor.getString(mcursor.getColumnIndex("city"));
                            String pincode = mcursor.getString(mcursor.getColumnIndex("pincode"));
                            ApplicationConstants.MOBILE_NO = dri_mobile;
                        }
                    }
                    Intent li = new Intent(SignIn.this, Vehicle.class);
                    li.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(li);
                    Toast.makeText(getApplicationContext(), MESSAGE, Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // new SignInVerification().execute();
            } else if (RESPONSE_CODE.equalsIgnoreCase("400")) {
                ShowAlert("ERROR !!", MESSAGE);
            } else {

            }

        }

    }

    class EmailCheckOne extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(SignIn.this);
            pDialog.setMessage("Please wait Checking  EMail..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected String doInBackground(String... args) {

            try {

                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("email", EMAIL));

                Log.e("URL", ApplicationConstants.DRIVER_EMAIL_VALIDATION_URL + EMAIL);
                JSONObject url_res = jsonParser.makeHttpRequest(ApplicationConstants.DRIVER_EMAIL_VALIDATION_URL + EMAIL, "POST", params);
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
            if (pDialog != null) {
                pDialog.dismiss();
                pDialog.cancel();
            }

            if (RESPONSE_CODE.equalsIgnoreCase("101")) {
                // Toast.makeText(getApplicationContext(),MESSAGE,Toast.LENGTH_SHORT).show();
                ShowAlert("Failed !!", MESSAGE);
            } else if (RESPONSE_CODE.equalsIgnoreCase("100")) { // If Email Verified Then We are calling this one

                new SignInVerification().execute();

                Log.e("MESSAGE", MESSAGE);

                //  msession.createLoginSession(signin_email.getText().toString().trim(),signin_pass.getText().toString().trim());
                try {


                    // For Now Not Required
                    /*mcursor  = mydb.getDriver_reg_Email(signin_email.getText().toString().trim());
                    if(mcursor!=null){
                        if(mcursor.moveToFirst()){
                            String fname = mcursor.getString(mcursor.getColumnIndex("driver_fname"));
                            String lname = mcursor.getString(mcursor.getColumnIndex("driver_lname"));
                            String dri_mobile = mcursor.getString(mcursor.getColumnIndex("driver_mobileno"));
                            String driver_emailid = mcursor.getString(mcursor.getColumnIndex("driver_emailid"));
                            String driver_pass = mcursor.getString(mcursor.getColumnIndex("password"));
                            String company_name = mcursor.getString(mcursor.getColumnIndex("company_name"));
                            String company_address = mcursor.getString(mcursor.getColumnIndex("company_address"));
                            String city = mcursor.getString(mcursor.getColumnIndex("city"));
                            String pincode = mcursor.getString(mcursor.getColumnIndex("pincode"));
                            ApplicationConstants.MOBILE_NO = dri_mobile;
                        }
                    }*/
                   /* Intent li = new Intent(SignIn.this, Vehicle.class);
                    li.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(li);
                    Toast.makeText(getApplicationContext(),MESSAGE,Toast.LENGTH_SHORT).show();*/

                } catch (Exception e) {
                    e.printStackTrace();
                }

                // new SignInVerification().execute();
            } else if (RESPONSE_CODE.equalsIgnoreCase("400")) {
                ShowAlert("ERROR !!", MESSAGE);
            } else {

            }

        }

    }

    class EmailCheckTwo extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(SignIn.this);
            pDialog.setMessage("Please wait Checking  Email..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected String doInBackground(String... args) {

            try {

                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("email", EMAIL));

                Log.e("URL", ApplicationConstants.DRIVER_EMAIL_VALIDATION_URL + EMAIL);
                JSONObject url_res = jsonParser.makeHttpRequest(ApplicationConstants.DRIVER_EMAIL_VALIDATION_URL + EMAIL, "POST", params);
                Log.e("response", url_res.toString());
                RESULT = url_res.getString("result");
                Log.e("Result", RESULT);
                MESSAGE = url_res.getString("info");
                RESPONSE_CODE = url_res.getString("code");
                Log.e("RESPONSE_CODE", RESPONSE_CODE);

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
            if (pDialog != null) {
                pDialog.dismiss();
                pDialog.cancel();
            }

            if (RESPONSE_CODE.equalsIgnoreCase("101")) {
                // Toast.makeText(getApplicationContext(),MESSAGE,Toast.LENGTH_SHORT).show();
                ShowAlert("Failed !!", MESSAGE);
            } else if (RESPONSE_CODE.equalsIgnoreCase("100")) {

                //  msession.createLoginSession(signin_email.getText().toString().trim(),signin_pass.getText().toString().trim());
                try {
                    Toast.makeText(getApplicationContext(), MESSAGE, Toast.LENGTH_SHORT).show();
                    new SignInVerificationCheck().execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // new SignInVerification().execute();
            } else if (RESPONSE_CODE.equalsIgnoreCase("400")) {
                ShowAlert("ERROR !!", MESSAGE);
            } else {

            }

        }

    }

    class SignInVerification extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(SignIn.this);
            pDialog.setMessage("Please wait Validating...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected String doInBackground(String... args) {

            try {

                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("email", EMAIL));
                params.add(new BasicNameValuePair("password", PASS));
                params.add(new BasicNameValuePair("imeiNumber", IMEI));

                Log.e("URL", ApplicationConstants.DRIVER_SIGNIN_URL + EMAIL + "/" + PASS);
                JSONObject url_res = jsonParser.makeHttpRequest(ApplicationConstants.DRIVER_SIGNIN_FIREBASE_URL, "POST", params);
                Log.e("response", url_res.toString());
                RESULT1 = url_res.getString("result");
                Log.e("Result", RESULT1);
                MESSAGE1 = url_res.getString("info");
                RESPONSE_CODE1 = url_res.getString("code");
                Log.e("RESPONSE_CODE", RESPONSE_CODE1);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
            if (pDialog.isShowing()) {
                pDialog.dismiss();
                pDialog.cancel();
            }

            if (RESPONSE_CODE1.equalsIgnoreCase("110")) { // Success Status
                Intent i1 = new Intent(SignIn.this, MainHomeScreen.class);
                i1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i1);
                finish();
                preferanceWrapperSingleton.setValueToSharedPref(Constants.userId, EMAIL);
                preferanceWrapperSingleton.setValueToSharedPref(Constants.password, PASS);
                msession.createLoginSession(EMAIL, PASS, "1");
                Log.e("FLOW" , "CreateSession With 1");
            } else if (RESPONSE_CODE1.equalsIgnoreCase("111")) { // Input Issue
                mydb.DeleteAll(EMAIL); //  Deleting all records and Inserting new response
                new PendingImages().execute();

            } else if (RESPONSE_CODE1.equalsIgnoreCase("114")) { // Server issue
                ShowAlert("ERROR !!", MESSAGE1);

            } else if (RESPONSE_CODE1.equalsIgnoreCase("112")) { // IMEI Number Worong
                // ShowAlert("ERROR !!",MESSAGE1);
                imei_check="2";
                Toast.makeText(getApplicationContext(),MESSAGE1, Toast.LENGTH_SHORT).show();

                Log.e("mylog","Email-->"+EMAIL+"Pass-->"+PASS);
                msession.createLoginSession(EMAIL, PASS, "2");
                Log.e("FLOW" , "CreateSession With 2");

                mydb.InsertIMEI_Status(EMAIL);
                preferanceWrapperSingleton.setValueToSharedPref(Constants.userId, EMAIL);
                preferanceWrapperSingleton.setValueToSharedPref(Constants.password, PASS);
                Intent i1 = new Intent(SignIn.this, MainHomeScreen.class);
                i1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i1);
                finish();
            }

        }
    }

    class SignInVerificationCheck extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(SignIn.this);
            pDialog.setMessage("Please wait Verifying Account Details...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected String doInBackground(String... args) {
            try {
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("email", EMAIL));
                params.add(new BasicNameValuePair("password", PASS));
                params.add(new BasicNameValuePair("imeiNumber", IMEI));
                Log.e("URL", ApplicationConstants.DRIVER_SIGNIN_URL + EMAIL + "/" + PASS);
                JSONObject url_res = jsonParser.makeHttpRequest(ApplicationConstants.DRIVER_SIGNIN_FIREBASE_URL, "POST", params);
                Log.e("response", url_res.toString());
                RESULT1 = url_res.getString("result");
                Log.e("Result", RESULT1);
                MESSAGE1 = url_res.getString("info");
                RESPONSE_CODE1 = url_res.getString("code");
                Log.e("RESPONSE_CODE", RESPONSE_CODE1);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
            if (pDialog != null && pDialog.isShowing()) {
                pDialog.dismiss();
                pDialog.cancel();
            }
            try {
                if (RESPONSE_CODE1.equalsIgnoreCase("110")) { // Success Status
                    Intent i1 = new Intent(SignIn.this, MainHomeScreen.class);
                    i1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i1);
                    finish();
                    preferanceWrapperSingleton.setValueToSharedPref(Constants.userId, EMAIL);
                    preferanceWrapperSingleton.setValueToSharedPref(Constants.password, PASS);
                    msession.createLoginSession(EMAIL, PASS, "1"); //  Token Generated,Storing Session 1
                    Log.e("FLOW" , "CreateSession With 1");
                } else if (RESPONSE_CODE1.equalsIgnoreCase("111")) { // Input Issue
                    ShowAlert("ERROR !!", MESSAGE1);
                    preferanceWrapperSingleton.setValueToSharedPref(Constants.userId, "");
                    preferanceWrapperSingleton.setValueToSharedPref(Constants.password, "");
                    mydb.DeleteAll(EMAIL);
                    signin_email.setText("");
                    signin_pass.setText("");
                } else if (RESPONSE_CODE1.equalsIgnoreCase("114")) { // Server issue
                    ShowAlert("ERROR !!", MESSAGE1);
                } else if (RESPONSE_CODE1.equalsIgnoreCase("112")) { // IMEI Number wrong
                    Toast.makeText(getApplicationContext(), MESSAGE1, Toast.LENGTH_SHORT).show();
                    msession.createLoginSession(EMAIL, PASS, "2"); // IMEI Number wrong
                    Log.e("FLOW" , "CreateSession With 2");
                    preferanceWrapperSingleton.setValueToSharedPref(Constants.userId, EMAIL);
                    preferanceWrapperSingleton.setValueToSharedPref(Constants.password, PASS);
                    mydb.InsertIMEI_Status(EMAIL);
                    mydb.UpdatedDriverStatus_in(EMAIL);
                    Intent i1 = new Intent(SignIn.this, MainHomeScreen.class);
                    i1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i1);
                    finish();
                } else if (RESPONSE_CODE1.equalsIgnoreCase("113")) { // IMEI Number wrong
                    Toast.makeText(getApplicationContext(), MESSAGE1, Toast.LENGTH_SHORT).show();
                    msession.createLoginSession(EMAIL, PASS, "3"); // IMEI Number wrong
                    Log.e("FLOW" , "CreateSession With 3");
                    preferanceWrapperSingleton.setValueToSharedPref(Constants.userId, EMAIL);
                    preferanceWrapperSingleton.setValueToSharedPref(Constants.password, PASS);
                    mydb.InsertIMEI_Status(EMAIL);
                    mydb.UpdatedDriverStatus_in(EMAIL);
                    Intent i1 = new Intent(SignIn.this, MainHomeScreen.class);
                    i1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i1);
                    finish();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class PendingImages extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(SignIn.this);
            pDialog.setMessage("Please wait.......");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected Void doInBackground(Void... arg0) {
        Log.e("SignIn","PendingImages called");
            try {
                String strjsn = jsonParser.makeServiceCall(ApplicationConstants.DRIVER_PENDINGIMAGES2_URL + "/" + EMAIL);
                JSONObject json_res = new JSONObject(strjsn);

                Log.e("JSON Response", json_res.toString());

                firstName = json_res.getString("firstName");
                lastName = json_res.getString("lastName");
                phoneNumber = json_res.getString("phoneNumber");
                emailID = json_res.getString("emailID");
                compName = json_res.getString("compName");
                compCity = json_res.getString("compCity");
                deviceInfo = json_res.getString("deviceInfo");
                country = json_res.getString("country");
                images = json_res.getString("images");
                activated = json_res.getString("activated");
                Pincode = json_res.getString("pinCode");
                regDate = json_res.getString("regDate");
                Res_PassWord = json_res.getString("password");
                iMEI = json_res.getString("iMEI");

                CarName = json_res.getString("carManufacturer");
                CarModel = json_res.getString("carModel");
                CarColor = json_res.getString("carColor");
                CarManu_year = json_res.getString("yearOfManufacture");
                NoPass = json_res.getString("noOfPass");
                Driver_DOB = json_res.getString("driverDOB");
                compAddr = json_res.getString("compAddr");
                latLong = json_res.getString("latLong");

                Vehicle_URL = json_res.getString("vehiclePhotoDocURL");
                LicensePlate_URL = json_res.getString("plateNumberDocURL");

                DriverPhoto_URL = json_res.getString("driverPhotoDocURL");
                DriverLicenseNo_URL = json_res.getString("driverLicenseDocURL");
                IDFront_URL = json_res.getString("driverIDCardDocFrontURL");
                IDBack_URL = json_res.getString("driverIDCardDocBackURL");
                PassportFront_URL = json_res.getString("driverPassportDocFrontURL");
                PassportBack_URL = json_res.getString("driverPassportDocBackURL");

                VehicleRegFront_URL = json_res.getString("carRegistrationDocFrontURL");
                VehicleRegBack_URL = json_res.getString("carRegistrationDocBackURL");
                Insurence_URL = json_res.getString("vehicleInsuranceDocURL");


                Log.e("Vehicle_URL", Vehicle_URL);
                Log.e("LicensePlate_URL", LicensePlate_URL);

                Log.e("DriverPhoto_URL", DriverPhoto_URL);
                Log.e("DriverLicenseNo_URL", DriverLicenseNo_URL);
                Log.e("IDFront_URL", IDFront_URL);
                Log.e("IDBack_URL", IDBack_URL);
                Log.e("PassportFront_URL", PassportFront_URL);
                Log.e("PassportBack_URL", PassportBack_URL);

                Log.e("VehicleRegFront_URL", VehicleRegFront_URL);
                Log.e("VehicleRegBack_URL", VehicleRegBack_URL);
                Log.e("Insurence_URL", Insurence_URL);

                Log.e("firstName", firstName);
                Log.e("lastName", lastName);
                Log.e("phoneNumber", phoneNumber);
                Log.e("emailID", emailID);
                Log.e("compName", compName);
                Log.e("compCity", compCity);
                Log.e("deviceInfo", deviceInfo);
                Log.e("country", country);
                Log.e("activated", activated);
                Log.e("images", images);
                Log.e("CarName", CarName);
                Log.e("CarModel", CarModel);
                Log.e("CarColor", CarColor);
                Log.e("CarManu_year", CarManu_year);
                Log.e("NoPass", NoPass);
                Log.e("Driver_DOB", Driver_DOB);


                JSONObject im = new JSONObject(images);
                vehiclePhotoDoc = im.getString("vehiclePhotoDoc");
                plateNumberDoc = im.getString("plateNumberDoc");

                driverPhotoDoc = im.getString("driverPhotoDoc");
                driverLicenseDoc = im.getString("driverLicenseDoc");
                driverIDCardDocFront = im.getString("driverIDCardDocFront");
                driverIDCardDocBack = im.getString("driverIDCardDocBack");
                driverPassportDocFront = im.getString("driverPassportDocFront");
                driverPassportDocBack = im.getString("driverPassportDocBack");

                carRegistrationDocFront = im.getString("carRegistrationDocFront");
                carRegistrationDocBack = im.getString("carRegistrationDocBack");
                vehicleInsuranceDoc = im.getString("vehicleInsuranceDoc");

                Log.e("vehiclePhotoDoc", vehiclePhotoDoc);
                Log.e("plateNumberDoc", plateNumberDoc);

                Log.e("driverPhotoDoc", driverPhotoDoc);
                Log.e("driverLicenseDoc", driverLicenseDoc);
                Log.e("driverIDCardDocFront", driverIDCardDocFront);
                Log.e("driverIDCardDocBack", driverIDCardDocBack);
                Log.e("driverPassportDocFront", driverPassportDocFront);
                Log.e("driverPassportDocBack", driverPassportDocBack);

                Log.e("carRegistrationDocFront", carRegistrationDocFront);
                Log.e("carRegistrationDocBack", carRegistrationDocBack);
                Log.e("vehicleInsuranceDoc", vehicleInsuranceDoc);

                // Checking Image Status for Vehicle Photo
                JSONObject json_car_pic = new JSONObject(vehiclePhotoDoc);
                Log.e("json_car_pic", json_car_pic.toString());
                String json_car_pic_status = json_car_pic.getString("valid");
                Log.e("CAR PIC", json_car_pic_status);

                // Checking Image Status PlateNumber
                JSONObject json_pic_plate = new JSONObject(plateNumberDoc);
                Log.e("json_car_pic", json_pic_plate.toString());
                String json_pic_plate_status = json_pic_plate.getString("valid");
                Log.e("PLATE NUMBER", json_pic_plate_status);


                // Checking Image Status Driver Photo
                JSONObject json_driver_pic = new JSONObject(driverPhotoDoc);
                Log.e("json_car_pic", json_driver_pic.toString());
                String json_driver_pic_status = json_driver_pic.getString("valid");
                Log.e("DRIVER PIC", json_driver_pic_status);


                // Checking Image Status License Plate Number
                JSONObject json_lic_pic = new JSONObject(driverLicenseDoc);
                Log.e("json_lic_pic", json_lic_pic.toString());
                String json_lic_pic_status = json_lic_pic.getString("valid");
                Log.e("LICENSE NO", json_lic_pic_status);


                // Checking Image Status ID Front
                JSONObject json_id_front_pic = new JSONObject(driverIDCardDocFront);
                Log.e("json_idfront_pic", json_id_front_pic.toString());
                String json_frontid_pic_status = json_id_front_pic.getString("valid");
                Log.e("ID FRONT", json_frontid_pic_status);


                // Checking Image Status ID Back
                JSONObject json_idback_pic = new JSONObject(driverIDCardDocBack);
                Log.e("json_idback_pic", json_idback_pic.toString());
                String json_id_back_pic_status = json_car_pic.getString("valid");
                Log.e("ID BACK", json_id_back_pic_status);


                // Checking Image Status Passport Front
                JSONObject json_passfront_pic = new JSONObject(driverPassportDocFront);
                Log.e("json_passfront_pic", json_passfront_pic.toString());
                String carfront_pic_status = json_passfront_pic.getString("valid");
                Log.e("PASSPORT FRONT", carfront_pic_status);


                // Checking Image Status Passport Back
                JSONObject json_passBack_pic = new JSONObject(driverPassportDocBack);
                Log.e("json_passBack_pic", json_passBack_pic.toString());
                String carpassback_pic_status = json_passBack_pic.getString("valid");
                Log.e("PASSPORT BACK", carpassback_pic_status);


                // Checking Image Status Carreg Front
                JSONObject json_carreg_front_pic = new JSONObject(carRegistrationDocFront);
                Log.e("json_car_pic", json_carreg_front_pic.toString());
                String carreg_front_pic_status = json_carreg_front_pic.getString("valid");
                Log.e("CAR REG FRONT", carreg_front_pic_status);


                // Checking Image Status CarRegback
                JSONObject json_carreg_back_pic = new JSONObject(carRegistrationDocBack);
                Log.e("json_car_pic", json_carreg_back_pic.toString());
                String json_carreg_back_pic_status = json_carreg_back_pic.getString("valid");
                Log.e("CAR REG BACK", json_carreg_back_pic_status);


                // Checking Image Status Insurance
                JSONObject json_insu_pic = new JSONObject(vehicleInsuranceDoc);
                Log.e("json_insuu_pic", json_insu_pic.toString());
                String json_insu_pic_status = json_insu_pic.getString("valid");
                Log.e("INSURANCE", json_car_pic_status);

                Cursor checkCoursor = mydb.CheckIsDataAlreadyInDBorNot(emailID);
                boolean isInsert = true;
                if (checkCoursor.getCount() <= 0) {
                    isInsert = false;
                }

                mydb.DriverRegRes_Details(firstName, lastName, phoneNumber, emailID, Res_PassWord, compName, compAddr, compCity, deviceInfo,
                        iMEI, latLong, country, activated, Pincode, CarName, CarModel, CarColor, CarManu_year, NoPass, Driver_DOB,
                        Vehicle_URL, LicensePlate_URL, DriverPhoto_URL, DriverLicenseNo_URL, IDFront_URL, IDBack_URL,
                        PassportFront_URL, PassportBack_URL, VehicleRegFront_URL, VehicleRegBack_URL, Insurence_URL,
                        regDate, json_car_pic_status, json_pic_plate_status, json_driver_pic_status, json_lic_pic_status,
                        json_frontid_pic_status, json_id_back_pic_status, carfront_pic_status, carpassback_pic_status,
                        carreg_front_pic_status, json_carreg_back_pic_status, json_insu_pic_status, true);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;

        }

        protected void onPostExecute(Void file_url) {
            // dismiss the dialog once done
            if (pDialog != null) {
                pDialog.dismiss();
                pDialog.cancel();

                Toast.makeText(getApplicationContext(), MESSAGE1, Toast.LENGTH_SHORT).show();
                msession.createLoginSession(EMAIL, PASS, "3");
                Log.e("FLOW" , "CreateSession With 3");
                Intent i1 = new Intent(SignIn.this, MainHomeScreen.class);
                i1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i1);
                finish();
            }


        }

    }
}
