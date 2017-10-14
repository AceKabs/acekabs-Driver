package com.acekabs.driverapp.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.acekabs.driverapp.ApplicationConstants;
import com.acekabs.driverapp.JSONParser;
import com.acekabs.driverapp.R;
import com.acekabs.driverapp.db.DBAdapter;
import com.acekabs.driverapp.db.SessionManager;
import com.acekabs.driverapp.entities.AceKabsDriverPreRegistration;
import com.acekabs.driverapp.pojo.CarRegistrationDocBack;
import com.acekabs.driverapp.pojo.CarRegistrationDocFront;
import com.acekabs.driverapp.pojo.DriverIDCardDocBack;
import com.acekabs.driverapp.pojo.DriverIDCardDocFront;
import com.acekabs.driverapp.pojo.DriverLicenseDoc;
import com.acekabs.driverapp.pojo.DriverPassportDocBack;
import com.acekabs.driverapp.pojo.DriverPassportDocFront;
import com.acekabs.driverapp.pojo.DriverPhotoDoc;
import com.acekabs.driverapp.pojo.PlateNumberDoc;
import com.acekabs.driverapp.pojo.VehicleInsuranceDoc;
import com.acekabs.driverapp.pojo.VehiclePhotoDoc;
import com.acekabs.driverapp.utils.UtilityMethods;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;

public class Profile extends Activity implements View.OnClickListener {

    View home_header;
    ImageView back;
    TextView title, profile_tv_fname, profile_tv_lname, profile_tv_mobileno, profile_tv_email;
    LinearLayout profile_lay_update_imei, profile_update_lay, profile_lay_update_vehicle_details, profile_lay_update_vehicle_Registraion_details;

    String SESSION_EMAIL = "", MOBILE = "", NAME = "", IMEI = "", RESPONSE_CODE = "", INFO = "", DRIVER_STATUS = "";
    TelephonyManager telephonyManager;
    Dialog alert_dia;
    String mail_girish;
    EditText signin_email;
    TextInputEditText signin_pass;
    Button signin_submit;
    String EMAIL = "", PASS = "";
    AceKabsDriverPreRegistration aceKabsDriverPreRegistration;
    View sign_header;
    TextView forget_pass;
    JSONParser jsonParser = new JSONParser();
    String RESULT = "", MESSAGE = "", RESULT1 = "", MESSAGE1 = "", RESPONSE_CODE1 = "";
    DBAdapter mydb;
    Cursor mcursor;
    Cursor checkCoursor;
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
    DriverPhotoDoc driverPhotoDoc1;
    DriverLicenseDoc driverLicenseDoc1;
    DriverIDCardDocBack driverIDCardDocBack1;
    DriverIDCardDocFront driverIDCardDocFront1;
    DriverPassportDocBack driverPassportDocBack1;
    DriverPassportDocFront driverPassportDocFront1;
    PlateNumberDoc plateNumberDoc1;
    VehiclePhotoDoc vehiclePhotoDoc1;
    CarRegistrationDocBack carRegistrationDocBack1;
    CarRegistrationDocFront carRegistrationDocFront1;
    VehicleInsuranceDoc vehicleInsuranceDoc1;
    private ProgressDialog pDialog;
    private TextView tvappversion;
    private LinearLayout changepwdlyt;
    private String dir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        new PendingImages().execute();
        home_header = findViewById(R.id.profile_header);
        back = (ImageView) home_header.findViewById(R.id.back);
        title = (TextView) home_header.findViewById(R.id.title);
        title.setText("PROFILE");
        dir = Environment.getExternalStorageDirectory() + "/DriverApp/";
        try {
            File directory = new File(dir);
            if (directory.exists() && directory.isDirectory()) {
                FileUtils.cleanDirectory(directory);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        mydb = DBAdapter.getInstance(getApplicationContext());
        msession = new SessionManager(getApplicationContext());
        // get user data from session
        HashMap<String, String> user = msession.getUserDetails();
        SESSION_EMAIL = user.get(SessionManager.KEY_NAME);
        Log.e("My LLLLLLLLLLLLLLOOOOOOOOOOGGGGGGGGGGG1", SESSION_EMAIL);

        Button profile_signout = (Button) findViewById(R.id.profile_signout);
        profile_signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SessionManager sessionManager = new SessionManager(Profile.this);
                sessionManager.logoutUser();

            }
        });

        profile_lay_update_imei = (LinearLayout) findViewById(R.id.profile_lay_update_imei);
        profile_update_lay = (LinearLayout) findViewById(R.id.profile_update_lay);
        profile_lay_update_vehicle_details = (LinearLayout) findViewById(R.id.profile_lay_update_vehicle_details);
        profile_lay_update_vehicle_Registraion_details = (LinearLayout) findViewById(R.id.profile_lay_update_vehicle_Registraion_details);
        changepwdlyt = (LinearLayout) findViewById(R.id.changepwdlyt);
        profile_tv_fname = (TextView) findViewById(R.id.profile_tv_fname);
        profile_tv_lname = (TextView) findViewById(R.id.profile_tv_lname);
        profile_tv_mobileno = (TextView) findViewById(R.id.profile_tv_mobileno);
        profile_tv_email = (TextView) findViewById(R.id.profile_tv_email);
        tvappversion = (TextView) findViewById(R.id.tvappversion);
        profile_lay_update_imei.setOnClickListener(this);
        profile_update_lay.setOnClickListener(this);
        profile_lay_update_vehicle_Registraion_details.setOnClickListener(this);
        profile_lay_update_vehicle_details.setOnClickListener(this);
        PackageManager manager = getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(getPackageName(), 0);
            String version = info.versionName;
            tvappversion.setText("Version-:" + version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        try {
            mcursor = mydb.getDriver_reg_Email(SESSION_EMAIL);

            Log.e("cursor count", "" + mcursor.getCount());
            if (mcursor != null) {
                if (mcursor.moveToFirst()) {
                    String d_fname = mcursor.getString(mcursor.getColumnIndex("driver_fname"));
                    String d_lname = mcursor.getString(mcursor.getColumnIndex("driver_lname"));
                    String d_mobileno = mcursor.getString(mcursor.getColumnIndex("driver_mobileno"));
                    String d_emailid = mcursor.getString(mcursor.getColumnIndex("driver_emailid"));
                    DRIVER_STATUS = mcursor.getString(mcursor.getColumnIndex("driver_status"));

                    profile_tv_fname.setText(d_fname);
                    profile_tv_lname.setText(d_lname);
                    profile_tv_mobileno.setText(d_mobileno);
                    profile_tv_email.setText(d_emailid);
                    NAME = d_fname;
                    MOBILE = "+" + d_mobileno;
                    //  Need to implement Logic for Images Showing
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        IMEI = UtilityMethods.getIMEI(this);

        Log.e("DRIVER_STATUS", DRIVER_STATUS);


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent li = new Intent(Profile.this, MainHomeScreen.class);
                li.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(li);

            }
        });
        changepwdlyt.setOnClickListener(this);
    }

    public void onClick(View v) {
        if (v.getId() == R.id.profile_lay_update_imei) {

            if (SessionManager.imei_check.equalsIgnoreCase("2")) {
                Toast.makeText(Profile.this, "Success..!!" + SessionManager.imei_check + "------", Toast.LENGTH_SHORT).show();
                ShowAlert("", "Are you sure \n you want to update IMEI \n number?");
            } else {
                Toast.makeText(Profile.this, "Failed..!!" + SessionManager.imei_check + "------", Toast.LENGTH_SHORT).show();
                AlertBox("Alert !!", "Mobile Number and IMEI are already matched.. \n You are not able to update IMEI");
            }
        } else if (v.getId() == R.id.profile_update_lay) {


            Log.e("LicensePlate_URL", LicensePlate_URL);

            Log.e("DriverPhoto_URL", DriverPhoto_URL);
            Log.e("DriverLicenseNo_URL", DriverLicenseNo_URL);
            Log.e("IDFront_URL", IDFront_URL);
            Log.e("IDBack_URL", IDBack_URL);
            Log.e("PassportFront_URL", PassportFront_URL);
            Log.e("PassportBack_URL", PassportBack_URL);
            Log.e("hi", "hi");
            Toast.makeText(Profile.this, "Hello", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Profile.this, Update_PersonalInfo.class);
            // intent.putExtra("aceKabsObj",aceKabsDriverPreRegistration);
            startActivity(intent);
        } else if (v == changepwdlyt) {
            startActivity(new Intent(Profile.this, ChangePassword.class));
        } else if (v.getId() == R.id.profile_lay_update_vehicle_details) {
            Intent intent = new Intent(Profile.this, Update_VehicleInfo.class);
            startActivity(intent);
        } else if (v.getId() == R.id.profile_lay_update_vehicle_Registraion_details) {
            Intent intent = new Intent(Profile.this, Update_RegistrationInfo.class);
            startActivity(intent);
        }
    }

    public void ShowAlert(String title, String message) {
        alert_dia = new Dialog(this);
        alert_dia.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alert_dia.setContentView(R.layout.alert_dialog2);
        alert_dia.show();

        TextView tit = (TextView) alert_dia.findViewById(R.id.dialog_title);
        tit.setText(title);
        TextView mes = (TextView) alert_dia.findViewById(R.id.dialog_message);
        mes.setText(message);
        Button ok = (Button) alert_dia.findViewById(R.id.dialog_yes);
        Button cancel = (Button) alert_dia.findViewById(R.id.dialog_no);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SessionManager.imei_check = "";

                if (isNetworkAvailable()) {

                    Cursor mcursor = mydb.RecordExists(SESSION_EMAIL);

                    if (mcursor.getCount() < 0) {
                        if (mcursor.moveToFirst()) {
                            SESSION_EMAIL = mcursor.getString(mcursor.getColumnIndex("driver_emailid"));
                            NAME = mcursor.getString(mcursor.getColumnIndex("driver_fname"));
                            MOBILE = mcursor.getString(mcursor.getColumnIndex("driver_mobileno"));
                            Log.e("SESSION_EMAIL", SESSION_EMAIL);
                            Log.e("NAME", NAME);
                            Log.e("MOBILE", MOBILE);
                        }
                    } else {
                        alert_dia.dismiss();
                        alert_dia.cancel();
                        new UpdateIMEI().execute();
                    }

                } else {
                    AlertBox("ERROR !!", "Please check NetworkConnection !!");
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert_dia.dismiss();
                alert_dia.cancel();
            }
        });
    }

    public void AlertBox(String title, String message) {
        final Dialog al_dialog = new Dialog(this);
        al_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        al_dialog.setContentView(R.layout.alert_dialog);
        al_dialog.show();

        TextView tit = (TextView) al_dialog.findViewById(R.id.dialog_title);
        tit.setText(title);
        TextView mes = (TextView) al_dialog.findViewById(R.id.dialog_message);
        mes.setText(message);
        Button ok = (Button) al_dialog.findViewById(R.id.dialog_submit);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                al_dialog.dismiss();
                al_dialog.cancel();

            }
        });

    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    class UpdateIMEI extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Profile.this);
            pDialog.setMessage("Please wait sending request...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected Void doInBackground(Void... arg0) {
            try {
                Log.e("IMEI_URL", ApplicationConstants.DRIVER_CHANGE_IMEI_URL + NAME + "/" + SESSION_EMAIL + "/" + MOBILE + "/" + IMEI);
                String strjsn = jsonParser.makeServiceCall(ApplicationConstants.DRIVER_CHANGE_IMEI_URL + NAME + "/" + SESSION_EMAIL + "/" + MOBILE + "/" + IMEI);
                JSONObject json_res = new JSONObject(strjsn);

                Log.e("JSON Response", json_res.toString());

                RESPONSE_CODE = json_res.getString("code");
                INFO = json_res.getString("info");


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
            }
            try {
                if (RESPONSE_CODE.equalsIgnoreCase("100")) {
                    AlertBox("MESSAGE !!", INFO);

                } else if (RESPONSE_CODE.equalsIgnoreCase("101")) {
                    AlertBox("ERROR !!", INFO);

                } else if (RESPONSE_CODE.equalsIgnoreCase("104")) {
                    AlertBox("ERROR !!", INFO);
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
            pDialog = new ProgressDialog(Profile.this);
            pDialog.setMessage("Please wait.......");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected Void doInBackground(Void... arg0) {

            try {
                String strjsn = jsonParser.makeServiceCall(ApplicationConstants.DRIVER_PENDINGIMAGES2_URL + "/" + SESSION_EMAIL + "");
                Log.e("APPPPPIIIIII CALL", ApplicationConstants.DRIVER_PENDINGIMAGES2_URL + "/" + SESSION_EMAIL + "");
                Log.e("Response data", "" + strjsn);

                // if(strjsn.equalsIgnoreCase("null")) {
                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse response = httpclient.execute(new HttpGet(ApplicationConstants.DRIVER_PENDINGIMAGES2_URL + "/" + SESSION_EMAIL + ""));
                StatusLine statusLine = response.getStatusLine();
                if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    response.getEntity().writeTo(out);
                    String responseString = out.toString();
                    out.close();
                    //..more logic
                    Log.e("Response data", "" + responseString);
                    strjsn = responseString;
                } else {
                    //Closes the connection.
                    response.getEntity().getContent().close();
                    //throw new IOException(statusLine.getReasonPhrase());
                }
                // }

                JSONObject json_res = new JSONObject(strjsn);
                ObjectMapper m = new ObjectMapper();
                aceKabsDriverPreRegistration = m.readValue(json_res.toString(), AceKabsDriverPreRegistration.class);


                Log.e("JSON Response", json_res.toString());

                firstName = json_res.getString("firstName");
                lastName = json_res.getString("lastName");
                phoneNumber = json_res.getString("phoneNumber");
                emailID = json_res.getString("emailID");
                mail_girish = emailID;
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
                Log.e("country", country + "");
                Log.e("activated", activated);
                Log.e("images", images + "");
                Log.e("CarName", CarName);
                Log.e("CarModel", CarModel);
                Log.e("CarColor", CarColor);
                Log.e("CarManu_year", CarManu_year);
                Log.e("NoPass", NoPass);
                Log.e("Driver_DOB", Driver_DOB);
                Log.e("F_______Naamamammama", aceKabsDriverPreRegistration.getFirstName() + "heloooo");

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

                Log.e("vehiclePhotoDochiiiiiiiii", vehiclePhotoDoc);
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

                //added for change IMEI
                SESSION_EMAIL = emailID;
                NAME = firstName;
                MOBILE = "+" + phoneNumber;

                checkCoursor = mydb.CheckIsDataAlreadyInDBorNot(emailID);
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
                        carreg_front_pic_status, json_carreg_back_pic_status, json_insu_pic_status, isInsert);

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
               // msession.createLoginSession(SESSION_EMAIL, PASS, "3");
                //  Intent i1 = new Intent(Profile.this, MainHomeScreen.class);
                // startActivity(i1);
            }


        }

    }

}
