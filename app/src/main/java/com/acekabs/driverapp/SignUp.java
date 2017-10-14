package com.acekabs.driverapp;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.TextInputEditText;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.acekabs.driverapp.activity.OTPActivity;
import com.acekabs.driverapp.constants.Constants;
import com.acekabs.driverapp.db.DBAdapter;
import com.acekabs.driverapp.db.OTPManager;
import com.acekabs.driverapp.entities.AceKabsDriverPreRegistration;
import com.acekabs.driverapp.utils.SharePreferanceWrapperSingleton;
import com.acekabs.driverapp.utils.UtilityMethods;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by srinivas on 24/11/16.
 */
public class SignUp extends Activity implements View.OnClickListener {
    private static final int PERMISSION_REQUEST_CODE = 1;
    public static AceKabsDriverPreRegistration driver_reg;
    public static String PNUM = "";
    public static String DRIVER_EMAIL_ID = "";
    public static String DRIVER_FNAME = "";
    public static String TYPE = "Driver";
    public static ArrayList<AceKabsDriverPreRegistration> Signup_details;
    static boolean radio_on = false;
    EditText edit_fname, edit_lname, edit_mobile, edit_emailid,
            signup_edit_companyname, signup_edit_comadd, signup_edit_city, signup_edit_pincode;
    View signup_header;
    TextView title, signup_county_code;
    TextInputEditText edit_pass;
    ImageView back, flag;
    Button next;
    String FNAME = "", LNAME = "", MOBILE = "", EMAIL_ID = "", PASS = "", COMPANY_NAME = "", COMPANY_ADDR = "", CITY = "", PINCODE = "";
    Spinner sp_flags;
    ArrayAdapter<String> flag_adapter;
    JSONParser jsonParser = new JSONParser();
    String sle_item = "", RESULT = "", MESSAGE = "", My_Country = "", IMEI = "", RESPONSE_CODE = "", L_CountryName = "", L_CountryCode = "", L_Pincode = "", L_City = "";
    DBAdapter mydb;
    Cursor mcursor;
    String LOCATION_URL = "http://ip-api.com/json";
    TelephonyManager telephonyManager;
    double latitude, longitude;
    // Coordinate
    //GPSTracker gps;
    Calendar m_calender;
    SimpleDateFormat df;
    String formattedDate;
    String API_CITY = "";
    ArrayAdapter<String> adapter;
    Spinner code_spinner;
    String code;

    OTPManager otp_manager;
    private ProgressDialog pDialog;
    private Context context;
    private Activity activity;
    private View view;
    private SharePreferanceWrapperSingleton preferanceWrapperSingleton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);
        context = this;
        mydb = DBAdapter.getInstance(getApplicationContext());
        preferanceWrapperSingleton = SharePreferanceWrapperSingleton.getSingletonInstance();
        preferanceWrapperSingleton.setPref(context);
        preferanceWrapperSingleton.setEditor();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            latitude = extras.getDouble("lat");
            longitude = extras.getDouble("long");

        }

        context = getApplicationContext();
        activity = this;


        otp_manager = new OTPManager(getApplicationContext());

        Signup_details = new ArrayList<AceKabsDriverPreRegistration>();

        signup_header = findViewById(R.id.signup_header);
        back = (ImageView) signup_header.findViewById(R.id.back);
        title = (TextView) signup_header.findViewById(R.id.title);
        title.setText("SIGN UP");

        back = (ImageView) signup_header.findViewById(R.id.back);
        edit_fname = (EditText) findViewById(R.id.signup_edit_fname);
        edit_lname = (EditText) findViewById(R.id.signup_edit_lname);
        edit_mobile = (EditText) findViewById(R.id.signup_mobile);
        edit_emailid = (EditText) findViewById(R.id.signup_email);
        edit_pass = (TextInputEditText) findViewById(R.id.signup_pass);
        next = (Button) findViewById(R.id.signup_submit);
        signup_edit_companyname = (EditText) findViewById(R.id.signup_edit_companyname);
        signup_edit_comadd = (EditText) findViewById(R.id.signup_edit_comadd);
        signup_edit_city = (EditText) findViewById(R.id.signup_edit_city);
        signup_edit_pincode = (EditText) findViewById(R.id.signup_edit_pincode);
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        signup_county_code = (TextView) findViewById(R.id.signup_county_code);

        m_calender = Calendar.getInstance();
        df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        formattedDate = df.format(m_calender.getTime());
        //Log.e("formattedDate",formattedDate);


        // Getting Location using API
        if (isNetworkAvailable()) {
            //  GettingLocation();
            new GetMobileCode().execute();
//            AccountManager accManager = AccountManager.get(getApplicationContext());
//            Account acc[] = accManager.getAccountsByType("com.google");
//            int accCount = acc.length;
//            for (int i = 0; i < accCount; i++) {
//                edit_emailid.setText(acc[0].name);
//                EMAIL_ID = acc[0].name;
//            }
        } else {
            ShowAlert("Warning", "Please check Network connection !!");
        }

        driver_reg = new AceKabsDriverPreRegistration();
        back.setOnClickListener(this);
        next.setOnClickListener(this);

        if (otp_manager.isLoggedIn()) {
            Intent i2 = new Intent(SignUp.this, OTPActivity.class);
            startActivity(i2);
        } else {

        }


    }

    private Pattern pattern;
    private Matcher matcher;
    private static final String USERNAME_PATTERN = "^[a-zA-Z0-9@]{6,}$";

    public boolean validate(final String username){
        pattern = Pattern.compile(USERNAME_PATTERN);
        matcher = pattern.matcher(username);
        return matcher.matches();

    }

    public final static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public void onClick(View v) {
        if (v.getId() == R.id.back) {
            Intent li = new Intent(SignUp.this, LandingPage.class);
            li.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(li);

        } else if (v.getId() == R.id.signup_submit) {
            FNAME = edit_fname.getText().toString().trim();
            LNAME = edit_lname.getText().toString().trim();
            MOBILE = edit_mobile.getText().toString().trim();
            EMAIL_ID = edit_emailid.getText().toString().trim();
            PASS = edit_pass.getText().toString().trim();
            COMPANY_NAME = signup_edit_companyname.getText().toString().trim();
            COMPANY_ADDR = signup_edit_comadd.getText().toString().trim();
            CITY = signup_edit_city.getText().toString().trim();
            PINCODE = signup_edit_pincode.getText().toString().trim();

            PNUM = signup_county_code.getText().toString().trim() + MOBILE;

            if (FNAME.length() == 0 && LNAME.length() == 0 &&
                    MOBILE.length() == 0 && EMAIL_ID.length() == 0 &&
                    PASS.length() == 0) {
                ShowAlert("Warning !!", "Please Enter All Fields");

            } else if (FNAME.length() == 0) {
                ShowAlert("Warning !!", "Please enter first name");
            } else if (LNAME.length() == 0) {
                ShowAlert("Warning !!", "Please enter last name");
            } else if (MOBILE.length() == 0) {
                ShowAlert("Warning !!", "Please enter mobile number");
            } else if (EMAIL_ID.length() == 0) {
                ShowAlert("Warning !!", "Please enter email id");
            } else if (!isValidEmail(EMAIL_ID)) {
                ShowAlert("Warning !!", "Please enter Valid Email id");
            } else if (PASS.length() == 0) {
                ShowAlert("Warning !!", "Please enter password");
            } else if(!this.validate(PASS)) {
                ShowAlert("Warning !!", "Please enter min 6 Characters & Special Characters not Allowed");
            } else if (COMPANY_NAME.length() == 0) {
                ShowAlert("Warning !!", "Please enter Company Name");
            } else if (COMPANY_ADDR.length() == 0) {
                ShowAlert("Warning !!", "Please enter Company Address");
            } else if (CITY.length() == 0) {
                ShowAlert("Warning !!", "Please enter City");
            } else {
                if (isNetworkAvailable()) {
                    new OTPGen().execute();
                } else {

                    ShowAlert("Warning", "Please check Network connection !!");

                }
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

    public void ShowAlert_SignUp(String title, String message) {
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

                Intent i1 = new Intent(SignUp.this, LandingPage.class);
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

    public void GettingLocation() {
        try {
            if (latitude == 0 && longitude == 0) {

                ShowAlert_SignUp("ERROR!!", "Please Re-Open Signup Again , because your Location \n Latitude: " + latitude + "\n Longitude: " + longitude);

            } else {
                Geocoder geocoder;
                List<Address> addresses;
                geocoder = new Geocoder(this, Locale.getDefault());
                addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                Log.e("What is my address ", "" + addresses.toString());

                String address = addresses.get(0).toString();
                L_Pincode = addresses.get(0).getPostalCode();
                L_CountryName = addresses.get(0).getCountryName();
                L_City = addresses.get(0).getLocality();
                L_CountryCode = addresses.get(0).getCountryCode();

                if (L_CountryName != null) {
                    mcursor = mydb.getCountryName(L_CountryName);

                    if (mcursor != null) {
                        if (mcursor.moveToFirst()) {
                            String code;
                            code = mcursor.getString(mcursor.getColumnIndex("mobile_code"));
                            signup_county_code.setText("+" + code);
                            signup_edit_city.setText(L_City);
                            signup_edit_pincode.setText(L_Pincode);
                        }
                    } else {
                        GettingLocation();
                    }
                } else {
                    GettingLocation();
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (isNetworkAvailable()) {
            GettingLocation();

            AccountManager accManager = AccountManager.get(getApplicationContext());
            Account acc[] = accManager.getAccountsByType("com.google");
            int accCount = acc.length;

//            for (int i = 0; i < accCount; i++) {
//                edit_emailid.setText(acc[0].name);
//                EMAIL_ID = acc[0].name;
//            }
        } else {
            ShowAlert("Warning", "Please check Network connection !!");
        }

    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Message : ")
                .setMessage("Are you sure you want to exit ?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        SignUp.super.onBackPressed();

                        try {

                            String pnm = getApplicationContext().getPackageName();
                            File sd = Environment.getExternalStorageDirectory();


                            if (sd.canWrite()) {
                                File locDB = new File(getFilesDir(), "../databases/");
                                String backupDBPath = "Driver_test.db";
                                File currentDB = new File(locDB, DBAdapter.DATABASE_NAME);
                                File backupDB = new File(sd, backupDBPath);

                                File file = new File(sd + backupDBPath);
                                if (file.exists() == true) {
                                    boolean deleted = file.delete();
                                    Log.e("Delete", "Delete");

                                }

                                if (currentDB.exists()) {
                                    FileChannel src = new FileInputStream(currentDB).getChannel();
                                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                                    dst.transferFrom(src, 0, src.size());
                                    src.close();
                                    dst.close();
                                }

                                if (true) {
                                    File locPF = new File(getFilesDir(), "../shared_prefs/");
                                    File currentPF = new File(locPF, pnm + "_preferences.xml");
                                    File backupPF = new File(sd, "prefs.xml");
                                    FileChannel src = new FileInputStream(currentPF).getChannel();
                                    FileChannel dst = new FileOutputStream(backupPF).getChannel();
                                    dst.transferFrom(src, 0, src.size());
                                    src.close();
                                    dst.close();
                                }

                            }
                        } catch (Exception e) {

                        }

                    }
                }).create().show();
    }

    class OTPGen extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(SignUp.this);
            pDialog.setMessage("Please wait.......");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected String doInBackground(String... args) {

            try {

                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("phoneNumber", PNUM));
                Log.e("URL", ApplicationConstants.DRIVER_OTP_REQUEST_URL + EMAIL_ID + "/" + PNUM);
                JSONObject url_res = jsonParser.makeHttpRequest(ApplicationConstants.DRIVER_OTP_REQUEST_URL + EMAIL_ID + "/" + PNUM, "POST", params);
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

            try {
                if (RESPONSE_CODE.equalsIgnoreCase("101")) {
                    // Toast.makeText(getApplicationContext(),MESSAGE,Toast.LENGTH_SHORT).show();
                    ShowAlert("Failed !!", MESSAGE);
                } else if (RESPONSE_CODE.equalsIgnoreCase("100")) {

                    Log.e("Code Check Success", RESPONSE_CODE);
                    Log.e("FNAME", FNAME);
                    Log.e("LNAME", LNAME);
                    Log.e("PNUM", PNUM);
                    Log.e("EMAIL_ID", EMAIL_ID);
                    Log.e("PASS", COMPANY_NAME);
                    Log.e("COMPANY_ADDR", COMPANY_ADDR);
                    Log.e("pincode", signup_edit_pincode.getText().toString().trim());
                    Log.e("device id", telephonyManager.getDeviceId());
                    Log.e("COMPANY_ADDR", COMPANY_ADDR);
                    Log.e("Lat & long", String.valueOf(latitude) + "," + String.valueOf(longitude));
                    Log.e("formattedDate", formattedDate);

                    mydb.DriverRegDetails(FNAME, LNAME, PNUM, EMAIL_ID, PASS, COMPANY_NAME, COMPANY_ADDR, CITY, My_Country,
                            signup_edit_pincode.getText().toString().trim(), UtilityMethods.getIMEI(SignUp.this),
                            String.valueOf(latitude) + "," + String.valueOf(longitude), "NewDevice", formattedDate, "0");
                    //  SignUp.DRIVER_EMAIL_ID = EMAIL_ID;
                    //  SignUp.DRIVER_FNAME = FNAME;

                    otp_manager.createLoginSession(PNUM, EMAIL_ID, FNAME);
                    preferanceWrapperSingleton.setValueToSharedPref(Constants.userId, EMAIL_ID);
                    preferanceWrapperSingleton.setValueToSharedPref(Constants.password, PASS);
                    Toast.makeText(getApplicationContext(), MESSAGE, Toast.LENGTH_SHORT).show();
                    Intent si = new Intent(SignUp.this, OTPActivity.class);
                    startActivity(si);


                } else if (RESPONSE_CODE.equalsIgnoreCase("400")) {
                    ShowAlert("ERROR !!", MESSAGE);
                } else {

                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    class GetMobileCode extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(SignUp.this);
            pDialog.setMessage("Please wait,Fetching your Location...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected String doInBackground(String... args) {

            try {

                Geocoder geocoder;
                List<Address> addresses;
                geocoder = new Geocoder(SignUp.this, Locale.getDefault());

                addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                String address = addresses.get(0).toString();
                L_Pincode = addresses.get(0).getPostalCode();
                L_CountryName = addresses.get(0).getCountryName();
                My_Country = L_CountryName;
                L_City = addresses.get(0).getLocality();
                L_CountryCode = addresses.get(0).getCountryCode();

                if (L_CountryName != null) {
                    mcursor = mydb.getCountryName(L_CountryName);

                    if (mcursor != null) {
                        if (mcursor.moveToFirst()) {

                            code = mcursor.getString(mcursor.getColumnIndex("mobile_code"));

                        }
                    }
                } else {

                }


            } catch (Exception e) {

            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done

            if (pDialog != null) {
                pDialog.dismiss();
                pDialog.cancel();

                Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();

                try {
                    if (code.equalsIgnoreCase(null)) {
                        new GetMobileCode().execute();
                    } else {
                        signup_county_code.setText("+" + code);
                        signup_edit_city.setText(L_City);
                        signup_edit_pincode.setText(L_Pincode);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

}

