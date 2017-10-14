package com.acekabs.driverapp;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CALL_PHONE;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.GET_ACCOUNTS;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.SEND_SMS;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * Created by srinivas Devarapalli on 22/11/16.
 * Author  : Srinivas Devarapalli
 * Purpose : Displaying splashscreen for Driver app
 */
public class SplashScreen extends Activity {
    private static final int PERMISSION_REQUEST_CODE = 201;
    private static final String TAG = "SplashScreen";
    String firstName = "", lastName = "", phoneNumber = "", emailID = "", compName = "", compCity = "", deviceInfo = "",
            country = "", images = "", activated = "", vehiclePhotoDoc = "", plateNumberDoc = "", driverPhotoDoc = "",
            driverLicenseDoc = "", driverIDCardDocFront = "", driverIDCardDocBack = "", driverPassportDocFront = "",
            driverPassportDocBack = "", carRegistrationDocFront = "", carRegistrationDocBack = "", vehicleInsuranceDoc = "",
            Pincode = "", CarName = "", CarModel = "", CarColor = "", CarManu_year = "", NoPass = "", Driver_DOB = "",
            Vehicle_URL = "", LicensePlate_URL = "", DriverPhoto_URL = "", DriverLicenseNo_URL = "", IDFront_URL = "", IDBack_URL = "",
            PassportFront_URL = "", PassportBack_URL = "", VehicleRegFront_URL = "", VehicleRegBack_URL = "", Insurence_URL = "",
            regDate = "", Res_PassWord = "", compAddr = "", iMEI = "", latLong = "";
    private Context context;
    private String dir;
    private SharePreferanceWrapperSingleton preferanceWrapperSingleton;
    private Cursor all_cursor;
    private DBAdapter mydb;
    private String Driver_Status;
    private String Login_Email;
    private String Login_Pass;
    private SessionManager msession;
    private ProgressDialog pDialog;
    private String EMAIL;
    private String PWD;
    private String RESULT;
    private String MESSAGE;
    private String RESPONSE_CODE;
    private Cursor mcursor;
    private String RESULT1;
    private String MESSAGE1;
    private String RESPONSE_CODE1;
    private CoordinatorLayout rootLayout;
    private String SESSION_MOBILE;
    private String dataPayload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        context = this;
        msession = new SessionManager(getApplicationContext());
        preferanceWrapperSingleton = SharePreferanceWrapperSingleton.getSingletonInstance();
        preferanceWrapperSingleton.setPref(context);
        preferanceWrapperSingleton.setEditor();
        rootLayout = (CoordinatorLayout) findViewById(R.id.rootLayout);
        // zoom_in();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!checkPermission()) {
                requestPermission();
            } else {
                callThread();
            }
        } else {
            callThread();
        }

        if(getIntent().hasExtra("message"))
        {
            Log.e(TAG,"if 1......");
            dataPayload=getIntent().getStringExtra("message");
            Log.e(TAG,"Data=>"+dataPayload);
            Constants.isPushFromNotification=true;
        }
        else
        {
            Log.e(TAG,"else 1......");
        }
    }

    private void callThread() {
        //creating thread that will sleep for 5 seconds
        Thread t = new Thread() {
            public void run() {

                try {
                    dir = Environment.getExternalStorageDirectory() + "/DriverApp/";
                    File newdir = new File(dir);
                    if (!newdir.exists()) {
                        newdir.mkdirs();
                    }
                    //sleep thread for 5 seconds, time in milliseconds
                    sleep(3000);
                    //start new activity
                    if (TextUtils.isEmpty(preferanceWrapperSingleton.getStringValueFromSharedPref(Constants.userId)) && TextUtils.isEmpty(preferanceWrapperSingleton.getStringValueFromSharedPref(Constants.password))) {
                        Intent i = new Intent(SplashScreen.this, LandingPage.class);
                        startActivity(i);
                    } else {
//                        Intent i = new Intent(SplashScreen.this, SignIn.class);
//                        startActivity(i);
                        if (!TextUtils.isEmpty(preferanceWrapperSingleton.getStringValueFromSharedPref(Constants.userId)) &&
                                !TextUtils.isEmpty(preferanceWrapperSingleton.getStringValueFromSharedPref(Constants.password))) {
                            mydb = DBAdapter.getInstance(getApplicationContext());
                            CallAutoLogin(preferanceWrapperSingleton.getStringValueFromSharedPref(Constants.userId), preferanceWrapperSingleton.getStringValueFromSharedPref(Constants.password));
                        }
                    }
                    //destroying Splash activity
                    //   finish();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        //start thread
        t.start();
    }

    private void CallAutoLogin(String emailId, final String password) {
        EMAIL = emailId;
        PWD = password;
        if (isNetworkAvailable()) {
            try {
                all_cursor = mydb.getDriver_reg_Email(emailId);
                Log.e("Cursor0", "" + all_cursor.getCount());
                if (all_cursor != null) {
                    if (all_cursor.moveToFirst()) {
                        Log.e("Cursor1", "" + all_cursor.getCount());
                        Driver_Status = all_cursor.getString(all_cursor.getColumnIndex("driver_status"));
                        Log.e("FLOW" , "CallAutoLogin DriverStatus : "+Driver_Status);
                        Login_Email = all_cursor.getString(all_cursor.getColumnIndex("driver_emailid"));
                        Login_Pass = all_cursor.getString(all_cursor.getColumnIndex("password"));
                        SESSION_MOBILE = all_cursor.getString(all_cursor.getColumnIndex("driver_mobileno"));
                        Log.e("Driver_Status", Driver_Status);
                        Log.e("Login_Email", Login_Email);
                        Log.e("Login_Pass", Login_Pass);
                        if (Driver_Status.equalsIgnoreCase("0")) { // Driver Not Upload Documents,First time login
                            if (emailId.equalsIgnoreCase(Login_Email) && password.equalsIgnoreCase(Login_Pass)) {
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
                            if (emailId.equalsIgnoreCase(Login_Email) && password.equalsIgnoreCase(Login_Pass)) {
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
                        }
                        else if (Driver_Status.equalsIgnoreCase("false"))
                        {
                            msession.createLoginSession(emailId, password, "5");
                            Log.e("FLOW" , "CreateSession With 5");
                            Intent i1 = new Intent(SplashScreen.this, MainHomeScreen.class);
                            if(dataPayload!=null)
                            {
                                i1.putExtra("payload_data",dataPayload);
                            }

                            startActivity(i1);
                        }
                        else if (Driver_Status.equalsIgnoreCase("true"))
                        {
                            if (isNetworkAvailable()) {
                                new SignInVerificationCheck().execute();
                            }
                        }
                    }
                    else
                        {
                        //  null getting from Cursor Means User app Uninstalled and Installed
                        Log.e("Else", "Else");
                        if (isNetworkAvailable()) {
                            new EmailCheckTwo().execute();
                        } else {
                            startActivity(new Intent(context, SignIn.class));
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Snackbar snackbar = Snackbar
                    .make(rootLayout, "No internet connection!", Snackbar.LENGTH_INDEFINITE)
                    .setAction("RETRY", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            CallAutoLogin(EMAIL, PWD);
                        }
                    });
            // Changing message text color
            snackbar.setActionTextColor(Color.WHITE);
            // Changing action button text color
            View sbView = snackbar.getView();
            sbView.setBackgroundColor(getResources().getColor(R.color.blue));
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.WHITE);
            snackbar.show();
        }
    }

    public void zoom_in() {
        ImageView image = (ImageView) findViewById(R.id.splash_image);
        Animation animation1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_in);
        image.startAnimation(animation1);
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private boolean checkPermission() {
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA);
        int result2 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_PHONE_STATE);
        int result3 = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION);
        int result4 = ContextCompat.checkSelfPermission(getApplicationContext(), GET_ACCOUNTS);
        int result5 = ContextCompat.checkSelfPermission(getApplicationContext(), SEND_SMS);
        int result6 = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result7 = ContextCompat.checkSelfPermission(getApplicationContext(), CALL_PHONE);
        return result1 == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED && result3 == PackageManager.PERMISSION_GRANTED && result4 == PackageManager.PERMISSION_GRANTED && result5 == PackageManager.PERMISSION_GRANTED && result6 == PackageManager.PERMISSION_GRANTED && result7 == PackageManager.PERMISSION_GRANTED;
    }


    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{CAMERA, READ_PHONE_STATE, ACCESS_FINE_LOCATION, GET_ACCOUNTS, SEND_SMS, WRITE_EXTERNAL_STORAGE, CALL_PHONE}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean phoneAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean locationAccepted = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    boolean accountAccessAccepted = grantResults[3] == PackageManager.PERMISSION_GRANTED;
                    boolean smsAccepted = grantResults[4] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[5] == PackageManager.PERMISSION_GRANTED;
                    boolean callAccepted = grantResults[6] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && phoneAccepted && locationAccepted && accountAccessAccepted && smsAccepted && storageAccepted) {
                        callThread();
                    } else {
                        Toast.makeText(context, "Please enable all permissions  to run this app", Toast.LENGTH_LONG).show();
                        finish();
                    }
                }

                break;
        }
    }

    public void ShowAlert(String title, String message) {
        final Dialog dialog = new Dialog(SplashScreen.this);
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

    class PendingImages extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected Void doInBackground(Void... arg0) {
            try {
                JSONParser jsonParser = new JSONParser();
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
                String driverstatus = activated.equalsIgnoreCase("true") ? "2":"1";
                Log.e("FLOW" , "Saving DriverStatus : "+driverstatus+" , activated : "+activated);

                mydb.DriverRegRes_Details(firstName, lastName, phoneNumber, emailID, Res_PassWord, compName, compAddr, compCity, deviceInfo,
                        iMEI, latLong, country, driverstatus, Pincode, CarName, CarModel, CarColor, CarManu_year, NoPass, Driver_DOB,
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

        protected void onPostExecute(Void file_url)
        {
            // dismiss the dialog once done
            Toast.makeText(getApplicationContext(), MESSAGE1, Toast.LENGTH_SHORT).show();
            msession.createLoginSession(EMAIL, PWD, "3");
            Log.e("FLOW" , "CreateSession With 3");
            Intent i1 = new Intent(SplashScreen.this, MainHomeScreen.class);
            if(dataPayload!=null)
            {
                i1.putExtra("payload_data",dataPayload);
            }
            startActivity(i1);
        }
    }

    public class EmailCheck extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... args) {

            try {

                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("email", EMAIL));
                Log.e("URL", ApplicationConstants.DRIVER_EMAIL_VALIDATION_URL + EMAIL);
                JSONParser jsonParser = new JSONParser();
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
            if (RESPONSE_CODE.equalsIgnoreCase("101")) {
                // Toast.makeText(getApplicationContext(),MESSAGE,Toast.LENGTH_SHORT).show();
                ShowAlert("Failed !!", MESSAGE);
            } else if (RESPONSE_CODE.equalsIgnoreCase("100")) {
                Log.e("FLOW" , "CreateSession With 4");
                msession.createLoginSession(EMAIL, PWD, "4");
                try {
                    mcursor = mydb.getDriver_reg_Email(EMAIL);
                    if (mcursor != null) {
                        if (mcursor.moveToFirst()) {
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
                    Intent li = new Intent(SplashScreen.this, Vehicle.class);
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
        }

        protected String doInBackground(String... args) {

            try {

                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("email", EMAIL));
                JSONParser jsonParser = new JSONParser();
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
            if (RESPONSE_CODE.equalsIgnoreCase("101")) {
                // Toast.makeText(getApplicationContext(),MESSAGE,Toast.LENGTH_SHORT).show();
                ShowAlert("Failed !!", MESSAGE);
            } else if (RESPONSE_CODE.equalsIgnoreCase("100")) { // If Email Verified Then We are calling this one
                new SignInVerification().execute();
                Log.e("MESSAGE", MESSAGE);
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
        }

        protected String doInBackground(String... args) {

            try {

                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("email", EMAIL));
                JSONParser jsonParser = new JSONParser();
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
        }

        protected String doInBackground(String... args) {

            try {

                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("email", EMAIL));
                params.add(new BasicNameValuePair("password", PWD));
                params.add(new BasicNameValuePair("imeiNumber", UtilityMethods.getIMEI(SplashScreen.this)));
                Log.e("URL", ApplicationConstants.DRIVER_SIGNIN_URL + EMAIL + "/" + PWD);
                JSONParser jsonParser = new JSONParser();
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

            if (RESPONSE_CODE1.equalsIgnoreCase("110")) { // Success Status
                Intent i1 = new Intent(SplashScreen.this, MainHomeScreen.class);
                if(dataPayload!=null)
                {
                    i1.putExtra("payload_data",dataPayload);
                }
                startActivity(i1);
                preferanceWrapperSingleton.setValueToSharedPref(Constants.userId, EMAIL);
                preferanceWrapperSingleton.setValueToSharedPref(Constants.password, PWD);
                Log.e("FLOW" , "CreateSession With 1");
                msession.createLoginSession(EMAIL, PWD, "1");
            } else if (RESPONSE_CODE1.equalsIgnoreCase("111")) { // Input Issue
                mydb.DeleteAll(EMAIL); //  Deleting all records and Inserting new response
                new PendingImages().execute();

            } else if (RESPONSE_CODE1.equalsIgnoreCase("114")) { // Server issue
                ShowAlert("ERROR !!", MESSAGE1);

            } else if (RESPONSE_CODE1.equalsIgnoreCase("112")) { // IMEI Number Worong
                // ShowAlert("ERROR !!",MESSAGE1);
                Toast.makeText(getApplicationContext(), MESSAGE1, Toast.LENGTH_SHORT).show();
                Log.e("FLOW" , "CreateSession With 2");
                msession.createLoginSession(EMAIL, PWD, "2");
                mydb.InsertIMEI_Status(EMAIL);
                preferanceWrapperSingleton.setValueToSharedPref(Constants.userId, EMAIL);
                preferanceWrapperSingleton.setValueToSharedPref(Constants.password, PWD);
                Intent i1 = new Intent(SplashScreen.this, MainHomeScreen.class);
                if(dataPayload!=null)
                {
                    i1.putExtra("payload_data",dataPayload);
                }
                startActivity(i1);
            }

        }
    }

    class SignInVerificationCheck extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... args) {

            try {
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("email", EMAIL));
                params.add(new BasicNameValuePair("password", PWD));
                params.add(new BasicNameValuePair("imeiNumber", UtilityMethods.getIMEI(SplashScreen.this)));
                Log.e("URL", ApplicationConstants.DRIVER_SIGNIN_URL + EMAIL + "/" + PWD);
                JSONParser jsonParser = new JSONParser();
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
                    Intent i1 = new Intent(SplashScreen.this, MainHomeScreen.class);
                    if(dataPayload!=null)
                    {
                        i1.putExtra("payload_data",dataPayload);
                    }
                    startActivity(i1);
                    preferanceWrapperSingleton.setValueToSharedPref(Constants.userId, EMAIL);
                    preferanceWrapperSingleton.setValueToSharedPref(Constants.password, PWD);
                    preferanceWrapperSingleton.setValueToSharedPref(Constants.mobileNumber, SESSION_MOBILE);
                    msession.createLoginSession(EMAIL, PWD, "1"); //  Token Generated,Storing Session 1
                    Log.e("FLOW" , "CreateSession With 1");
                } else if (RESPONSE_CODE1.equalsIgnoreCase("111")) { // Input Issue
                    ShowAlert("ERROR !!", MESSAGE1);
                    preferanceWrapperSingleton.setValueToSharedPref(Constants.userId, "");
                    preferanceWrapperSingleton.setValueToSharedPref(Constants.password, "");
                    mydb.DeleteAll(EMAIL);
                } else if (RESPONSE_CODE1.equalsIgnoreCase("114")) { // Server issue
                    ShowAlert("ERROR !!", MESSAGE1);
                } else if (RESPONSE_CODE1.equalsIgnoreCase("112")) { // IMEI Number wrong
                    Toast.makeText(getApplicationContext(), MESSAGE1, Toast.LENGTH_SHORT).show();
                    msession.createLoginSession(EMAIL, PWD, "2"); // IMEI Number wrong
                    Log.e("FLOW" , "CreateSession With 2");
                    Intent i1 = new Intent(SplashScreen.this, MainHomeScreen.class);
                    if(dataPayload!=null)
                    {
                        i1.putExtra("payload_data",dataPayload);
                    }
                    startActivity(i1);
                } else if (RESPONSE_CODE1.equalsIgnoreCase("113")) {
                    preferanceWrapperSingleton.setValueToSharedPref(Constants.userId, "");
                    preferanceWrapperSingleton.setValueToSharedPref(Constants.password, "");
                    Toast.makeText(getApplicationContext(), MESSAGE1, Toast.LENGTH_SHORT).show();
                    Intent i1 = new Intent(SplashScreen.this, SignIn.class);
                    startActivity(i1);
                    finish();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
