package com.acekabs.driverapp.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
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
import com.acekabs.driverapp.firebase.IFirebaseCallback;
import com.acekabs.driverapp.pojo.DriverIDCardDocBack;
import com.acekabs.driverapp.pojo.DriverIDCardDocFront;
import com.acekabs.driverapp.pojo.DriverLicenseDoc;
import com.acekabs.driverapp.pojo.DriverPassportDocBack;
import com.acekabs.driverapp.pojo.DriverPassportDocFront;
import com.acekabs.driverapp.pojo.DriverPhotoDoc;
import com.acekabs.driverapp.pojo.Images;
import com.acekabs.driverapp.services.AceKabsDocumentUploadService;
import com.acekabs.driverapp.services.GPSTracker;
import com.acekabs.driverapp.utils.ImageCompressAsyncTask;
import com.acekabs.driverapp.utils.ImageLoadingUtils;
import com.acekabs.driverapp.utils.UtilityMethods;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import static com.google.android.gms.wearable.DataMap.TAG;

public class Update_PersonalInfo extends Activity implements View.OnClickListener {

    public AceKabsDriverPreRegistration driver;
    ImageView back, profile_personal_driver_pic1, profile_personal_driver_pic2, profile_personal_driver_pic3, profile_personal_driver_pic4,
            profile_personal_driver_pic5, profile_personal_driver_pic6, driver_img_passfront_status,
            driver_img_passback_status, driver_img_idback_status, driver_img_idfront_status, driver_img_lic_status, driver_img_photo_status;
    View sign_header, update_sub_header;
    TextView title;//,personal, vehicle, registration, profile_edit;
    Button profile_personal_submit;
    boolean isEdit = false;
    LinearLayout update_per_lay1;
    Context context;
    SessionManager msession;
    RejectDriverRegistration rejectDriverRegistration;
    Uri outputFileUri, outputFileUri2, outputFileUri3, outputFileUri4, outputFileUri5, outputFileUri6;
    String SESSION_EMAIL = "";
    Bitmap Bitmap1, Bitmap2, Bitmap3, Bitmap4, Bitmap5, Bitmap6;
    String image_path1, image_path2, image_path3, image_path4, image_path5, image_path6;
    int width = 1366; // 1920
    int height = 768; // 1080
    GPSTracker gps;
    ExifInterface exifInterfaceone, exifInterfacetwo, exifInterfacethree, exifInterfacefour, exifInterfacefive, exifInterfacesix;
    JSONParser jsonParser = new JSONParser();
    String[] str_list;
    DBAdapter mydb;
    Cursor mcursor, ncursor, all_cursor;
    Images url_img;
    String driver_status = "", driver_licno = "", driver_idfront = "",
            driver_idback = "", driver_passfront = "", driver_passback = "", driver_dob = "";
    EditText profile_personal_edit_dob;
    String Driver_Photo;
    Images images;
    DriverPhotoDoc driverPhotoDoc;
    DriverLicenseDoc driverLicenseDoc;
    DriverIDCardDocBack driverIDCardDocBack;
    DriverIDCardDocFront driverIDCardDocFront;
    DriverPassportDocBack driverPassportDocBack;
    DriverPassportDocFront driverPassportDocFront;
    Double driverPhotoDoc_long, driverPhotoDoc_lat, driverLicenseDoc_long, driverLicenseDoc_lat, driverIDCardDocBack_long, driverIDCardDocBack_lat,
            driverIDCardDocFront_long, driverIDCardDocFront_lat, driverPassportDocBack_long, driverPassportDocBack_lat, driverPassportDocFront_long, driverPassportDocFront_lat;
    private ProgressDialog progressDialog;
    private String fileOne, fileTwo, fileThree, fileFour, fileFive, fileSix, dir;
    private ImageLoadingUtils utils;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_personal);

        context = this;

        utils = new ImageLoadingUtils(Update_PersonalInfo.this);
        url_img = new Images();

        dir = Environment.getExternalStorageDirectory() + "/DriverApp/";
        str_list = new String[6];
        gps = new GPSTracker(getApplicationContext());
        msession = new SessionManager(getApplicationContext());
        HashMap<String, String> user = msession.getUserDetails();
        SESSION_EMAIL = user.get(SessionManager.KEY_NAME);
        mydb = DBAdapter.getInstance(getApplicationContext());
        Log.e("My LLLLLLLLLLLLLLOOOOOOOOOOGGGGGGGGGGG1", SESSION_EMAIL);

        sign_header = findViewById(R.id.update_header);
        //update_sub_header = findViewById(R.id.update_sub_header);
        back = (ImageView) sign_header.findViewById(R.id.profile_back);
        title = (TextView) sign_header.findViewById(R.id.profile_title);
        //profile_edit = (TextView) sign_header.findViewById(R.id.profile_stan_edit);
        title.setText("UPDATE PERSONAL DETAILS");
        // personal = (TextView) update_sub_header.findViewById(R.id.tv_personal);
        // vehicle = (TextView) update_sub_header.findViewById(R.id.tv_vehicle);
        // registration = (TextView) update_sub_header.findViewById(R.id.tv_reg);
        profile_personal_submit = (Button) findViewById(R.id.profile_personal_submit);
        // profile_personal_edit_dob = (TextView)findViewById(R.id.profile_personal_edit_dob);
        new GetDetails().execute();
        profile_personal_driver_pic1 = (ImageView) findViewById(R.id.profile_personal_driver_pic1);
        profile_personal_driver_pic2 = (ImageView) findViewById(R.id.profile_personal_driver_pic2);
        profile_personal_driver_pic3 = (ImageView) findViewById(R.id.profile_personal_driver_pic3);
        profile_personal_driver_pic4 = (ImageView) findViewById(R.id.profile_personal_driver_pic4);
        profile_personal_driver_pic5 = (ImageView) findViewById(R.id.profile_personal_driver_pic5);
        profile_personal_driver_pic6 = (ImageView) findViewById(R.id.profile_personal_driver_pic6);

        driver_img_passback_status = (ImageView) findViewById(R.id.driver_img_passback_status);
        driver_img_passfront_status = (ImageView) findViewById(R.id.driver_img_passfront_status);
        driver_img_idback_status = (ImageView) findViewById(R.id.driver_img_idback_status);
        driver_img_idfront_status = (ImageView) findViewById(R.id.driver_img_idfront_status);
        driver_img_lic_status = (ImageView) findViewById(R.id.driver_img_lic_status);
        driver_img_photo_status = (ImageView) findViewById(R.id.driver_img_photo_status);

        update_per_lay1 = (LinearLayout) findViewById(R.id.update_per_lay1);

        profile_personal_edit_dob = (EditText) findViewById(R.id.profile_personal_edit_dob);

        // personal.setOnClickListener(this);
        // vehicle.setOnClickListener(this);
        //registration.setOnClickListener(this);
        //profile_edit.setOnClickListener(this);

        // personal.setBackgroundColor(Color.parseColor("#191919"));
        //vehicle.setBackgroundColor(Color.parseColor("#7f7f7f"));
        //registration.setBackgroundColor(Color.parseColor("#7f7f7f"));
        //personal.setTextColor(Color.parseColor("#FFFFFF"));
        profile_personal_submit.setOnClickListener(this);
        profile_personal_driver_pic1.setOnClickListener(this);
        profile_personal_driver_pic2.setOnClickListener(this);
        profile_personal_driver_pic3.setOnClickListener(this);
        profile_personal_driver_pic4.setOnClickListener(this);
        profile_personal_driver_pic5.setOnClickListener(this);
        profile_personal_driver_pic6.setOnClickListener(this);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i1 = new Intent(Update_PersonalInfo.this, Profile.class);
                i1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i1);
            }
        });

        try {
            GettingImageStatus();
        } catch (Exception e) {
            e.printStackTrace();
        }

       /* if (!isEdit) {
            isEdit = false;
            Toast.makeText(getApplicationContext(), "EDIT NOT Possible", Toast.LENGTH_SHORT).show();
            update_per_lay1.setBackgroundColor(Color.parseColor("#d3d3d3"));
            profile_personal_driver_pic1.setClickable(false);
            profile_personal_driver_pic2.setClickable(false);
            profile_personal_driver_pic3.setClickable(false);
            profile_personal_driver_pic4.setClickable(false);
            profile_personal_driver_pic5.setClickable(false);
            profile_personal_driver_pic6.setClickable(false);
            profile_personal_edit_dob.setClickable(false);
            profile_personal_submit.setClickable(false);
        }*/

        try {
            ncursor = mydb.Driver_Status(SESSION_EMAIL);

            if (ncursor != null) {
                Log.e("MyChech", "Hi up");
                if (ncursor.moveToFirst()) {
                    Driver_Photo = ncursor.getString(ncursor.getColumnIndex("driverPhoto_URL"));
                    UtilityMethods.displayImage(Driver_Photo, profile_personal_driver_pic1);
//                    new DownloadImageTask(profile_personal_driver_pic1).execute(Driver_Photo);
                    String DL = mcursor.getString(mcursor.getColumnIndex("driverLicenseNo_URL"));
//                    new DownloadImageTask(profile_personal_driver_pic2).execute(DL);
                    UtilityMethods.displayImage(DL, profile_personal_driver_pic2);
                    String QIDF = mcursor.getString(mcursor.getColumnIndex("driverQatar_idFront_URL"));
                    UtilityMethods.displayImage(QIDF, profile_personal_driver_pic3);
//   new DownloadImageTask(profile_personal_driver_pic3).execute(QIDF);
                    String QIDB = mcursor.getString(mcursor.getColumnIndex("driverQatar_idBack_URL"));
//                    new DownloadImageTask(profile_personal_driver_pic4).execute(QIDB);
                    UtilityMethods.displayImage(QIDB, profile_personal_driver_pic4);
                    String PF = mcursor.getString(mcursor.getColumnIndex("driverPassport_idFront_URL"));
//                    new DownloadImageTask(profile_personal_driver_pic5).execute(PF);
                    UtilityMethods.displayImage(PF, profile_personal_driver_pic5);
                    String PB = mcursor.getString(mcursor.getColumnIndex("driverPassport_idBack_URL"));
//                    new DownloadImageTask(profile_personal_driver_pic6).execute(PB);
                    UtilityMethods.displayImage(PB, profile_personal_driver_pic6);
                    //String car_color = mcursor.getString(mcursor.getColumnIndex("car_color"));
                    //String car_year_manu = mcursor.getString(mcursor.getColumnIndex("year_of_manufacturer"));
                    //String car_no_of_pass = mcursor.getString(mcursor.getColumnIndex("no_of_pass"));

                    //  Need to implement Logic for Images Showing

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            GettingImageStatus();
        } catch (Exception e) {
            e.printStackTrace();
        }


        //Log.e("Driver Name","hhiiii"+getDetails.driver1.getFirstName()+"");
        while (driver == null) ;
        Log.e("Driver Name", "hhiiii" + driver.getFirstName() + "");
        update_DriverDetails();


    }

    public void onClick(View view) {

       /*if (view.getId() == R.id.tv_personal) {

            Toast.makeText(getApplicationContext(), "PERSONAL", Toast.LENGTH_SHORT).show();

        } else if (view.getId() == R.id.tv_vehicle) {

            Intent veh = new Intent(Update_PersonalInfo.this, Update_VehicleInfo.class);
            startActivity(veh);

            Toast.makeText(getApplicationContext(), "VEHICLE", Toast.LENGTH_SHORT).show();

        } else if (view.getId() == R.id.tv_reg) {
            Intent reg = new Intent(Update_PersonalInfo.this, Update_RegistrationInfo.class);
            startActivity(reg);

            Toast.makeText(getApplicationContext(), "REGISTRATION", Toast.LENGTH_SHORT).show();

        } else if (view.getId() == R.id.profile_stan_edit) {

            /*if (!isEdit) {
                isEdit = true;
                update_per_lay1.setBackgroundColor(Color.parseColor("#FFFFFF"));
                Toast.makeText(getApplicationContext(), "EDIT  Possible", Toast.LENGTH_SHORT).show();
                profile_personal_driver_pic1.setClickable(true);
                profile_personal_driver_pic2.setClickable(true);
                profile_personal_driver_pic3.setClickable(true);
                profile_personal_driver_pic4.setClickable(true);
                profile_personal_driver_pic5.setClickable(true);
                profile_personal_driver_pic6.setClickable(true);
                profile_personal_edit_dob.setClickable(false);
                profile_personal_submit.setClickable(true);



            } else {
                isEdit = false;
                update_per_lay1.setBackgroundColor(Color.parseColor("#d3d3d3"));

                Toast.makeText(getApplicationContext(), "EDIT NOT Possible", Toast.LENGTH_SHORT).show();
                profile_personal_driver_pic1.setClickable(false);

                profile_personal_driver_pic2.setClickable(false);
                profile_personal_driver_pic3.setClickable(false);
                profile_personal_driver_pic4.setClickable(false);
                profile_personal_driver_pic5.setClickable(false);
                profile_personal_driver_pic6.setClickable(false);
                profile_personal_edit_dob.setClickable(false);
                profile_personal_submit.setClickable(false);

            }


        }else*/

        if (view.getId() == R.id.profile_personal_submit) {
            if (driver_status.equalsIgnoreCase("false") || driver_licno.equalsIgnoreCase("false") || driver_idfront.equalsIgnoreCase("false") || driver_idback.equalsIgnoreCase("false") || driver_passfront.equalsIgnoreCase("false") || driver_passback.equalsIgnoreCase("false")) {
                driver.setDriverDOB(profile_personal_edit_dob.getText().toString());
                updateToDb();
                rejectDriverRegistration = new RejectDriverRegistration(driver, context);
                startActivity(getIntent());
                finish();
            } else
                Toast.makeText(context, "We have found No rejected Images", Toast.LENGTH_SHORT).show();

            //   Toast.makeText(getApplicationContext(), "Update", Toast.LENGTH_SHORT).show();

        } else if (view.getId() == R.id.profile_personal_driver_pic1) {

            fileOne = dir + "personal_driver_pic1" + ".jpg";
            File newfile = new File(fileOne);
            try {
                newfile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            outputFileUri = Uri.fromFile(newfile);
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            startActivityForResult(cameraIntent, 0);

            //Toast.makeText(getApplicationContext(), "profile_personal_driver_pic1", Toast.LENGTH_SHORT).show();

        } else if (view.getId() == R.id.profile_personal_driver_pic2) {
            fileTwo = dir + "personal_driver_pic2" + ".jpg";
            File newfile = new File(fileTwo);
            try {
                newfile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            outputFileUri2 = Uri.fromFile(newfile);
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri2);
            startActivityForResult(cameraIntent, 1);

            // Toast.makeText(getApplicationContext(), "profile_personal_driver_pic2", Toast.LENGTH_SHORT).show();

        } else if (view.getId() == R.id.profile_personal_driver_pic3) {
            fileThree = dir + "personal_driver_pic3" + ".jpg";
            File newfile = new File(fileThree);
            try {
                newfile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            outputFileUri3 = Uri.fromFile(newfile);
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri3);
            startActivityForResult(cameraIntent, 2);


            //Toast.makeText(getApplicationContext(), "profile_personal_driver_pic3", Toast.LENGTH_SHORT).show();

        } else if (view.getId() == R.id.profile_personal_driver_pic4) {
            fileFour = dir + "personal_driver_pic4" + ".jpg";
            File newfile = new File(fileFour);
            try {
                newfile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            outputFileUri4 = Uri.fromFile(newfile);
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri4);
            startActivityForResult(cameraIntent, 3);


            // Toast.makeText(getApplicationContext(), "profile_personal_driver_pic4", Toast.LENGTH_SHORT).show();

        } else if (view.getId() == R.id.profile_personal_driver_pic5) {
            fileFive = dir + "personal_driver_pic5" + ".jpg";
            File newfile = new File(fileFive);
            try {
                newfile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            outputFileUri5 = Uri.fromFile(newfile);
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri5);
            startActivityForResult(cameraIntent, 4);

            // Toast.makeText(getApplicationContext(), "profile_personal_driver_pic5", Toast.LENGTH_SHORT).show();

        } else if (view.getId() == R.id.profile_personal_driver_pic6) {
            fileSix = dir + "personal_driver_pic6" + ".jpg";
            File newfile = new File(fileSix);
            try {
                newfile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            outputFileUri6 = Uri.fromFile(newfile);
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri6);
            startActivityForResult(cameraIntent, 5);

            // Toast.makeText(getApplicationContext(), "profile_personal_driver_pic6", Toast.LENGTH_SHORT).show();

        }


    }

    public void GettingImageStatus() {

        mcursor = mydb.getDriver_reg_Email(SESSION_EMAIL);

        Log.e("Count", "" + mcursor.getCount());
        if (mcursor != null) {
            if (mcursor.moveToFirst()) {
                Log.e("Count2", "" + mcursor.getCount());
                driver_status = mcursor.getString(mcursor.getColumnIndex("driver_photo_status"));
                driver_licno = mcursor.getString(mcursor.getColumnIndex("driver_license_image_status"));
                driver_idfront = mcursor.getString(mcursor.getColumnIndex("driver_id_front_image_status"));
                driver_idback = mcursor.getString(mcursor.getColumnIndex("driver_id_back_image_status"));
                driver_passfront = mcursor.getString(mcursor.getColumnIndex("driver_pass_front_image_status"));
                driver_passback = mcursor.getString(mcursor.getColumnIndex("driver_pass_back_image_status"));
                driver_dob = mcursor.getString(mcursor.getColumnIndex("driver_dob"));

                Log.e("driver_status", driver_status);
                Log.e("driver_licno", driver_licno);
                Log.e("driver_idfront", driver_idfront);
                Log.e("driver_idback", driver_idback);
                Log.e("driver_passfront", driver_passfront);
                Log.e("driver_passback", driver_passback);

                profile_personal_edit_dob.setText(driver_dob);

                if (driver_status.equalsIgnoreCase("true")) {
                    profile_personal_driver_pic1.setClickable(false);
                    driver_img_photo_status.setBackgroundResource(R.drawable.success4);
                } else {
                    profile_personal_driver_pic1.setClickable(true);
                    driver_img_photo_status.setBackgroundResource(R.drawable.failure4);
                }

                if (driver_licno.equalsIgnoreCase("true")) {
                    profile_personal_driver_pic2.setClickable(false);
                    driver_img_lic_status.setBackgroundResource(R.drawable.success4);
                } else {
                    profile_personal_driver_pic2.setClickable(true);
                    driver_img_lic_status.setBackgroundResource(R.drawable.failure4);
                }

                if (driver_idfront.equalsIgnoreCase("true")) {
                    profile_personal_driver_pic3.setClickable(false);
                    driver_img_idfront_status.setBackgroundResource(R.drawable.success4);
                } else {
                    profile_personal_driver_pic3.setClickable(true);
                    driver_img_idfront_status.setBackgroundResource(R.drawable.failure4);
                }

                if (driver_idback.equalsIgnoreCase("true")) {
                    profile_personal_driver_pic4.setClickable(false);
                    driver_img_idback_status.setBackgroundResource(R.drawable.success4);
                } else {
                    profile_personal_driver_pic4.setClickable(true);
                    driver_img_idback_status.setBackgroundResource(R.drawable.failure4);
                }

                if (driver_passfront.equalsIgnoreCase("true")) {
                    profile_personal_driver_pic5.setClickable(false);
                    driver_img_passfront_status.setBackgroundResource(R.drawable.success4);
                } else {
                    profile_personal_driver_pic5.setClickable(true);
                    driver_img_passfront_status.setBackgroundResource(R.drawable.failure4);
                }

                if (driver_passback.equalsIgnoreCase("true")) {

                    profile_personal_driver_pic6.setClickable(false);
                    driver_img_passback_status.setBackgroundResource(R.drawable.success4);

                } else {
                    profile_personal_driver_pic6.setClickable(true);
                    driver_img_passback_status.setBackgroundResource(R.drawable.failure4);

                }

            }

        }


    }

    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == RESULT_OK) {
            Log.d(TAG + "CameraDemo", "Pic saved");
            try {
                Bitmap1 = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), Uri.parse(outputFileUri.toString()));
                profile_personal_driver_pic1.setImageBitmap(Bitmap1);
                width = Bitmap1.getWidth();
                height = Bitmap1.getHeight();
                image_path1 = MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), Bitmap1, "Title", null);
//                Log.e(TAG + "image_path1", image_path1);
                if (gps.canGetLocation()) {
                    String path = UtilityMethods.getRealPathFromUri(Update_PersonalInfo.this, Uri.parse(image_path1));
                    exifInterfaceone = new ExifInterface(path);

                    ImageCompressAsyncTask imageCompressAsyncTask=new ImageCompressAsyncTask(Update_PersonalInfo.this,path,exifInterfaceone,path,utils);
                    imageCompressAsyncTask.setOnImageCompressed(new ImageCompressAsyncTask.OnImageCompressed() {
                        @Override
                        public void onCompressedImage(String path) {
                            image_path1= path;
                            Log.e("onCompressedImage  path=>",path);
                            str_list[0] = image_path1;
                            fireBase_Previous(0);
                        }
                    });


                    //image_path1 = compressImage(path, exifInterfaceone, path);

                    exifInterfaceone.setAttribute(ExifInterface.TAG_GPS_LATITUDE, UtilityMethods.convertToDegreeMinuteSeconds(gps.getLatitude()));
                    exifInterfaceone.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, UtilityMethods.convertToDegreeMinuteSeconds(gps.getLongitude()));
                    exifInterfaceone.setAttribute(ExifInterface.TAG_MAKE, UtilityMethods.getManufacturer());
                    exifInterfaceone.setAttribute(ExifInterface.TAG_MODEL, UtilityMethods.getDeviceName());
                    exifInterfaceone.setAttribute("imei", UtilityMethods.getIMEI(Update_PersonalInfo.this));
                    exifInterfaceone.setAttribute("UserComment", UtilityMethods.getIMEI(Update_PersonalInfo.this));
                    exifInterfaceone.saveAttributes();
                    mydb.UpdateDriverPICCoord(SESSION_EMAIL, String.valueOf(gps.getLatitude()), String.valueOf(gps.getLongitude()), "true");
                    driverPhotoDoc_long = gps.getLongitude();
                    driverPhotoDoc_lat = gps.getLatitude();

                    imageCompressAsyncTask.execute();
                }
                //str_list[0] = image_path1;
                //fireBase_Previous(0);
                // profile_LisencePlateNo_driver_pic1.setEnabled(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == 1 && resultCode == RESULT_OK) {
            try {
                Bitmap2 = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), Uri.parse(outputFileUri2.toString()));
                profile_personal_driver_pic2.setImageBitmap(Bitmap2);
                width = Bitmap2.getWidth();
                height = Bitmap2.getHeight();
                image_path2 = MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), Bitmap2, "Title", null);
//                Log.e(TAG + "image_path1", image_path1);
                if (gps.canGetLocation()) {
                    String path = UtilityMethods.getRealPathFromUri(Update_PersonalInfo.this, Uri.parse(image_path2));
                    exifInterfacetwo = new ExifInterface(path);

                    ImageCompressAsyncTask imageCompressAsyncTask=new ImageCompressAsyncTask(Update_PersonalInfo.this,path,exifInterfacetwo,path,utils);
                    imageCompressAsyncTask.setOnImageCompressed(new ImageCompressAsyncTask.OnImageCompressed() {
                        @Override
                        public void onCompressedImage(String path) {
                            image_path2= path;
                            Log.e("onCompressedImage  path=>",path);
                            str_list[1] = image_path2;
                            fireBase_Previous(1);
                        }
                    });


                   // image_path2 = compressImage(path, exifInterfacetwo, path);


                    exifInterfacetwo.setAttribute(ExifInterface.TAG_GPS_LATITUDE, UtilityMethods.convertToDegreeMinuteSeconds(gps.getLatitude()));
                    exifInterfacetwo.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, UtilityMethods.convertToDegreeMinuteSeconds(gps.getLongitude()));
                    exifInterfacetwo.setAttribute(ExifInterface.TAG_MAKE, UtilityMethods.getManufacturer());
                    exifInterfacetwo.setAttribute(ExifInterface.TAG_MODEL, UtilityMethods.getDeviceName());
                    exifInterfacetwo.setAttribute("imei", UtilityMethods.getIMEI(Update_PersonalInfo.this));
                    exifInterfacetwo.setAttribute("UserComment", UtilityMethods.getIMEI(Update_PersonalInfo.this));
                    exifInterfacetwo.saveAttributes();
                    mydb.UpdatelicenseNOCoord(SESSION_EMAIL, String.valueOf(gps.getLatitude()), String.valueOf(gps.getLongitude()), "true");
                    driverLicenseDoc_long = gps.getLongitude();
                    driverLicenseDoc_lat = gps.getLatitude();

                    imageCompressAsyncTask.execute();
                }
//                str_list[1] = image_path2;
//                fireBase_Previous(1);
                // profile_LisencePlateNo_driver_pic1.setEnabled(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == 2 && resultCode == RESULT_OK) {
            try {
                Bitmap3 = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), Uri.parse(outputFileUri3.toString()));
                profile_personal_driver_pic3.setImageBitmap(Bitmap3);
                width = Bitmap3.getWidth();
                height = Bitmap3.getHeight();
                image_path3 = MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), Bitmap3, "Title", null);
//                Log.e(TAG + "image_path1", image_path1);
                if (gps.canGetLocation()) {
                    String path = UtilityMethods.getRealPathFromUri(Update_PersonalInfo.this, Uri.parse(image_path3));
                    exifInterfacethree = new ExifInterface(path);

                    ImageCompressAsyncTask imageCompressAsyncTask=new ImageCompressAsyncTask(Update_PersonalInfo.this,path,exifInterfacethree,path,utils);
                    imageCompressAsyncTask.setOnImageCompressed(new ImageCompressAsyncTask.OnImageCompressed() {
                        @Override
                        public void onCompressedImage(String path) {
                            image_path3= path;
                            Log.e("onCompressedImage  path=>",path);
                            str_list[2] = image_path3;
                            fireBase_Previous(2);
                        }
                    });



                    //image_path3 = compressImage(path, exifInterfacethree, path);

                    exifInterfacethree.setAttribute(ExifInterface.TAG_GPS_LATITUDE, UtilityMethods.convertToDegreeMinuteSeconds(gps.getLatitude()));
                    exifInterfacethree.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, UtilityMethods.convertToDegreeMinuteSeconds(gps.getLongitude()));
                    exifInterfacethree.setAttribute(ExifInterface.TAG_MAKE, UtilityMethods.getManufacturer());
                    exifInterfacethree.setAttribute(ExifInterface.TAG_MODEL, UtilityMethods.getDeviceName());
                    exifInterfacethree.setAttribute("imei", UtilityMethods.getIMEI(Update_PersonalInfo.this));
                    exifInterfacethree.setAttribute("UserComment", UtilityMethods.getIMEI(Update_PersonalInfo.this));
                    exifInterfacethree.saveAttributes();
                    mydb.UpdateIDFrontCoord(SESSION_EMAIL, String.valueOf(gps.getLatitude()), String.valueOf(gps.getLongitude()), "true");
                    driverIDCardDocFront_long = gps.getLongitude();
                    driverIDCardDocFront_lat = gps.getLatitude();

                    imageCompressAsyncTask.execute();
                }
//                str_list[2] = image_path3;
//                fireBase_Previous(2);
                // profile_LisencePlateNo_driver_pic1.setEnabled(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == 3 && resultCode == RESULT_OK) {
            try {
                Bitmap4 = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), Uri.parse(outputFileUri4.toString()));
                profile_personal_driver_pic4.setImageBitmap(Bitmap4);
                width = Bitmap4.getWidth();
                height = Bitmap4.getHeight();
                image_path4 = MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), Bitmap4, "Title", null);
//                Log.e(TAG + "image_path1", image_path1);
                if (gps.canGetLocation()) {
                    String path = UtilityMethods.getRealPathFromUri(Update_PersonalInfo.this, Uri.parse(image_path4));
                    exifInterfacefour = new ExifInterface(path);

                    ImageCompressAsyncTask imageCompressAsyncTask=new ImageCompressAsyncTask(Update_PersonalInfo.this,path,exifInterfacefour,path,utils);
                    imageCompressAsyncTask.setOnImageCompressed(new ImageCompressAsyncTask.OnImageCompressed() {
                        @Override
                        public void onCompressedImage(String path) {
                            image_path4= path;
                            Log.e("onCompressedImage  path=>",path);
                            str_list[3] = image_path4;
                            fireBase_Previous(3);
                        }
                    });


                    //image_path4 = compressImage(path, exifInterfacefour, path);

                    exifInterfacefour.setAttribute(ExifInterface.TAG_GPS_LATITUDE, UtilityMethods.convertToDegreeMinuteSeconds(gps.getLatitude()));
                    exifInterfacefour.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, UtilityMethods.convertToDegreeMinuteSeconds(gps.getLongitude()));
                    exifInterfacefour.setAttribute(ExifInterface.TAG_MAKE, UtilityMethods.getManufacturer());
                    exifInterfacefour.setAttribute(ExifInterface.TAG_MODEL, UtilityMethods.getDeviceName());
                    exifInterfacefour.setAttribute("imei", UtilityMethods.getIMEI(Update_PersonalInfo.this));
                    exifInterfacefour.setAttribute("UserComment", UtilityMethods.getIMEI(Update_PersonalInfo.this));
                    exifInterfacefour.saveAttributes();
                    mydb.UpdateIDBackCoord(SESSION_EMAIL, String.valueOf(gps.getLatitude()), String.valueOf(gps.getLongitude()), "true");
                    driverIDCardDocBack_long = gps.getLongitude();
                    driverIDCardDocBack_lat = gps.getLatitude();

                    imageCompressAsyncTask.execute();
                }
//                str_list[3] = image_path4;
//                fireBase_Previous(3);
                // profile_LisencePlateNo_driver_pic1.setEnabled(true);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (requestCode == 4 && resultCode == RESULT_OK) {
            try {
                Bitmap5 = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), Uri.parse(outputFileUri5.toString()));
                profile_personal_driver_pic5.setImageBitmap(Bitmap5);
                width = Bitmap5.getWidth();
                height = Bitmap5.getHeight();
                image_path5 = MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), Bitmap5, "Title", null);
//                Log.e(TAG + "image_path1", image_path1);
                if (gps.canGetLocation()) {
                    String path = UtilityMethods.getRealPathFromUri(Update_PersonalInfo.this, Uri.parse(image_path5));
                    exifInterfacefive = new ExifInterface(path);

                    ImageCompressAsyncTask imageCompressAsyncTask=new ImageCompressAsyncTask(Update_PersonalInfo.this,path,exifInterfacefive,path,utils);
                    imageCompressAsyncTask.setOnImageCompressed(new ImageCompressAsyncTask.OnImageCompressed() {
                        @Override
                        public void onCompressedImage(String path) {
                            image_path5= path;
                            Log.e("onCompressedImage  path=>",path);
                            str_list[4] = image_path5;
                            fireBase_Previous(4);
                        }
                    });


                   // image_path5 = compressImage(path, exifInterfacefive, path);

                    exifInterfacefive.setAttribute(ExifInterface.TAG_GPS_LATITUDE, UtilityMethods.convertToDegreeMinuteSeconds(gps.getLatitude()));
                    exifInterfacefive.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, UtilityMethods.convertToDegreeMinuteSeconds(gps.getLongitude()));
                    exifInterfacefive.setAttribute(ExifInterface.TAG_MAKE, UtilityMethods.getManufacturer());
                    exifInterfacefive.setAttribute(ExifInterface.TAG_MODEL, UtilityMethods.getDeviceName());
                    exifInterfacefive.setAttribute("imei", UtilityMethods.getIMEI(Update_PersonalInfo.this));
                    exifInterfacefive.setAttribute("UserComment", UtilityMethods.getIMEI(Update_PersonalInfo.this));
                    exifInterfacefive.saveAttributes();
                    mydb.UpdatePassFrontCoord(SESSION_EMAIL, String.valueOf(gps.getLatitude()), String.valueOf(gps.getLongitude()), "true");
                    driverPassportDocFront_long = gps.getLongitude();
                    Log.e("Lat", driverPassportDocFront_lat + "");
                    driverPassportDocFront_lat = gps.getLatitude();
                    Log.e("Lat", driverPassportDocFront_long + "");

                    imageCompressAsyncTask.execute();
                }
//                str_list[4] = image_path5;
//                fireBase_Previous(4);
                // profile_LisencePlateNo_driver_pic1.setEnabled(true);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (requestCode == 5 && resultCode == RESULT_OK) {
            try {
                Bitmap6 = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), Uri.parse(outputFileUri6.toString()));
                profile_personal_driver_pic6.setImageBitmap(Bitmap6);
                width = Bitmap6.getWidth();
                height = Bitmap6.getHeight();
                image_path6 = MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), Bitmap6, "Title", null);
//                Log.e(TAG + "image_path1", image_path1);
                if (gps.canGetLocation()) {
                    String path = UtilityMethods.getRealPathFromUri(Update_PersonalInfo.this, Uri.parse(image_path6));
                    exifInterfacesix = new ExifInterface(path);

                    ImageCompressAsyncTask imageCompressAsyncTask=new ImageCompressAsyncTask(Update_PersonalInfo.this,path,exifInterfacesix,path,utils);
                    imageCompressAsyncTask.setOnImageCompressed(new ImageCompressAsyncTask.OnImageCompressed() {
                        @Override
                        public void onCompressedImage(String path) {
                            image_path6= path;
                            Log.e("onCompressedImage  path=>",path);
                            str_list[5] = image_path6;
                            fireBase_Previous(5);
                        }
                    });


                   // image_path6 = compressImage(path, exifInterfacesix, path);

                    exifInterfacesix.setAttribute(ExifInterface.TAG_GPS_LATITUDE, UtilityMethods.convertToDegreeMinuteSeconds(gps.getLatitude()));
                    exifInterfacesix.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, UtilityMethods.convertToDegreeMinuteSeconds(gps.getLongitude()));
                    exifInterfacesix.setAttribute(ExifInterface.TAG_MAKE, UtilityMethods.getManufacturer());
                    exifInterfacesix.setAttribute(ExifInterface.TAG_MODEL, UtilityMethods.getDeviceName());
                    exifInterfacesix.setAttribute("imei", UtilityMethods.getIMEI(Update_PersonalInfo.this));
                    exifInterfacesix.setAttribute("UserComment", UtilityMethods.getIMEI(Update_PersonalInfo.this));
                    exifInterfacesix.saveAttributes();
                    mydb.UpdatePassBackCoord(SESSION_EMAIL, String.valueOf(gps.getLatitude()), String.valueOf(gps.getLongitude()), "true");
                    driverPassportDocBack_long = gps.getLongitude();
                    driverPassportDocBack_lat = gps.getLatitude();

                    imageCompressAsyncTask.execute();
                }
//                str_list[5] = image_path6;
//                fireBase_Previous(5);
                // profile_LisencePlateNo_driver_pic1.setEnabled(true);
            } catch (Exception e) {
                e.printStackTrace();
            }


        }

    }

    public void fireBase_Previous(int i) {
        final int x = i;
        final FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            uploadDocToFirebase(Uri.parse(str_list[i]), i);
        } else {
            final FirebaseAuth auth1 = FirebaseAuth.getInstance();
            if (auth1.getCurrentUser() != null) {
                uploadDocToFirebase(Uri.parse(str_list[i]), i);
            } else {
                auth.signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        uploadDocToFirebase(Uri.parse(str_list[x]), x);
                    }
                });
            }
        }
    }

    public String compressImage(String imageUri, ExifInterface exifInterface, String filenames) {
        String filePath = imageUri;
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inSampleSize = 4;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;
        float maxHeight = 816.0f;
        float maxWidth = 612.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

        options.inSampleSize = utils.calculateInSampleSize(options, actualWidth, actualHeight);
        options.inJustDecodeBounds = false;
        options.inDither = false;
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);
        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2,
                new Paint(Paint.FILTER_BITMAP_FLAG));
        int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
        Matrix matrix = new Matrix();
        if (orientation == 6) {
            matrix.postRotate(90);
        } else if (orientation == 3) {
            matrix.postRotate(180);
        } else if (orientation == 8) {
            matrix.postRotate(270);
        }
        scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(),
                scaledBitmap.getHeight(), matrix, true);
        FileOutputStream out;
        try {
            out = new FileOutputStream(filenames);
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return Uri.fromFile(new File(filenames)).toString();
    }

    private void uploadDocToFirebase(Uri imageUri, final int i) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Uploading ....");
        progressDialog.setCancelable(false);
        progressDialog.show();
        new AceKabsDocumentUploadService().uploadFileFromDisk(imageUri, new IFirebaseCallback<Uri>() {
            @Override
            public void onDataReceived(Uri data) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                String storageUrl = data.toString();

//                Toast.makeText(Vehicle.this, "Image Uploaded at : " + storageUrl, Toast.LENGTH_SHORT).show();
                // list_url.add(storageUrl);
                Log.e(TAG + "URL", storageUrl);
                if (i == 0) {
                    driver.setDriverPhotoDocURL(storageUrl);
                    driverPhotoDoc.setLatitude(driverPhotoDoc_lat);
                    driverPhotoDoc.setLongitude(driverPhotoDoc_long);
                } else if (i == 1) {
                    //driver.setPlateNumberDocURL(storageUrl);
                    driver.setDriverLicenseDocURL(storageUrl);
                    driverLicenseDoc.setLongitude(driverLicenseDoc_long);
                    driverLicenseDoc.setLatitude(driverLicenseDoc_lat);
                } else if (i == 2) {
                    driver.setDriverIDCardDocFrontURL(storageUrl);
                    driverIDCardDocFront.setLatitude(driverIDCardDocFront_lat);
                    driverIDCardDocFront.setLongitude(driverIDCardDocFront_long);
                } else if (i == 3) {
                    driver.setDriverIDCardDocBackURL(storageUrl);
                    driverIDCardDocBack.setLatitude(driverIDCardDocBack_lat);
                    driverIDCardDocBack.setLongitude(driverIDCardDocBack_long);
                } else if (i == 4) {
                    driver.setDriverPassportDocFrontURL(storageUrl);

                    driverPassportDocFront.setLatitude(driverPassportDocFront_lat);
                    driverPassportDocFront.setLongitude(driverPassportDocFront_long);

                } else if (i == 5) {
                    driver.setDriverPassportDocBackURL(storageUrl);
                    driverPassportDocBack.setLongitude(driverPassportDocBack_long);
                    driverPassportDocBack.setLatitude(driverPassportDocBack_lat);
                }
                driver.setImages(images);


                //  rejectDriverRegistration.process();

            }


            @Override
            public void onFailure(Exception ex) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
//                Toast.makeText(Vehicle.this, "Upload Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void update_DriverDetails()                                       // values intialise
    {
        Log.e("LOLLOLOLOLOLOL", driver.getVehiclePhotoDocURL());
        images = driver.getImages();
        driverPhotoDoc = images.getDriverPhotoDoc();
        driverLicenseDoc = images.getDriverLicenseDoc();
        driverIDCardDocFront = images.getDriverIDCardDocFront();
        driverIDCardDocBack = images.getDriverIDCardDocBack();
        driverPassportDocFront = images.getDriverPassportDocFront();
        driverPassportDocBack = images.getDriverPassportDocBack();


        /*all_cursor = mydb.getDriver_reg_Email(SESSION_EMAIL);
        try {

            if (all_cursor != null) {
                if (all_cursor.moveToFirst()) {
                    String fname = all_cursor.getString(all_cursor.getColumnIndex("driver_fname"));
                    String driver_lname = all_cursor.getString(all_cursor.getColumnIndex("driver_lname"));
                    String driver_mobileno = all_cursor.getString(all_cursor.getColumnIndex("driver_mobileno"));
                    String driver_emailid = all_cursor.getString(all_cursor.getColumnIndex("driver_emailid"));
                    Log.e("LOLLLLLLLLLLLLLLLLLLLL",driver_emailid);
                    String password = all_cursor.getString(all_cursor.getColumnIndex("password"));
                    String company_name = all_cursor.getString(all_cursor.getColumnIndex("company_name"));
                    String company_address = all_cursor.getString(all_cursor.getColumnIndex("company_address"));
                    String city = all_cursor.getString(all_cursor.getColumnIndex("city"));
                    String country = all_cursor.getString(all_cursor.getColumnIndex("driver_country"));
                    String pincode = all_cursor.getString(all_cursor.getColumnIndex("pincode"));
                    String imei = all_cursor.getString(all_cursor.getColumnIndex("imei"));
                    String coordinates = all_cursor.getString(all_cursor.getColumnIndex("coordinates"));
                    String device_info = all_cursor.getString(all_cursor.getColumnIndex("device_info"));
                    String car_manufacture = all_cursor.getString(all_cursor.getColumnIndex("car_manufacture"));
                    String car_model = all_cursor.getString(all_cursor.getColumnIndex("car_model"));
                    String car_color = all_cursor.getString(all_cursor.getColumnIndex("car_color"));
                    String year_of_manufacturer = all_cursor.getString(all_cursor.getColumnIndex("year_of_manufacturer"));
                    String no_of_pass = all_cursor.getString(all_cursor.getColumnIndex("no_of_pass"));
                    String license_plate_no = all_cursor.getString(all_cursor.getColumnIndex("license_plate_no"));
                    String driver_dob = all_cursor.getString(all_cursor.getColumnIndex("driver_dob"));
                    String car_photo_url = all_cursor.getString(all_cursor.getColumnIndex("car_photo_url"));
                    String driverLicensePlate_URL = all_cursor.getString(all_cursor.getColumnIndex("driverLicensePlate_URL"));
                    String driverPhoto_URL = all_cursor.getString(all_cursor.getColumnIndex("driverPhoto_URL"));
                    String driverLicenseNo_URL = all_cursor.getString(all_cursor.getColumnIndex("driverLicenseNo_URL"));
                    String driverQatar_idFront_URL = all_cursor.getString(all_cursor.getColumnIndex("driverQatar_idFront_URL"));
                    String driverQatar_idBack_URL = all_cursor.getString(all_cursor.getColumnIndex("driverQatar_idBack_URL"));
                    String driverPassport_idFront_URL = all_cursor.getString(all_cursor.getColumnIndex("driverPassport_idFront_URL"));
                    String driverPassport_idBack_URL = all_cursor.getString(all_cursor.getColumnIndex("driverPassport_idBack_URL"));
                    String carRegistrationDocFrontURL = all_cursor.getString(all_cursor.getColumnIndex("carRegistrationDocFrontURL"));
                    String carRegistrationDocBackURL = all_cursor.getString(all_cursor.getColumnIndex("carRegistrationDocBackURL"));
                    String vehicleInsuranceDocURL = all_cursor.getString(all_cursor.getColumnIndex("vehicleInsuranceDocURL"));

                    String vehicle_pic_lat = all_cursor.getString(all_cursor.getColumnIndex("vehicle_pic_lat"));
                    String vehicle_pic_long = all_cursor.getString(all_cursor.getColumnIndex("vehicle_pic_long"));
                    String vehicle_image_status = all_cursor.getString(all_cursor.getColumnIndex("vehicle_image_status"));

                    String license_plate_lat = all_cursor.getString(all_cursor.getColumnIndex("license_plate_lat"));
                    String license_plate_long = all_cursor.getString(all_cursor.getColumnIndex("license_plate_long"));
                    String license_image_status = all_cursor.getString(all_cursor.getColumnIndex("license_image_status"));


                    String driver_pic_lat = all_cursor.getString(all_cursor.getColumnIndex("driver_pic_lat"));
                    String driver_pic_long = all_cursor.getString(all_cursor.getColumnIndex("driver_pic_long"));
                    String driver_reg1 = all_cursor.getString(all_cursor.getColumnIndex("driver_reg1"));

                    String driver_license_lat = all_cursor.getString(all_cursor.getColumnIndex("driver_license_lat"));
                    String driver_license_long = all_cursor.getString(all_cursor.getColumnIndex("driver_license_long"));
                    String driver_license_image_status = all_cursor.getString(all_cursor.getColumnIndex("driver_license_image_status"));

                    String driver_id_front_lat = all_cursor.getString(all_cursor.getColumnIndex("driver_id_front_lat"));
                    String driver_id_front_long = all_cursor.getString(all_cursor.getColumnIndex("driver_id_front_long"));
                    String driver_id_front_image_status = all_cursor.getString(all_cursor.getColumnIndex("driver_id_front_image_status"));

                    String driver_id_back_lat = all_cursor.getString(all_cursor.getColumnIndex("driver_id_back_lat"));
                    String driver_id_back_long = all_cursor.getString(all_cursor.getColumnIndex("driver_id_back_long"));
                    String driver_id_back_image_status = all_cursor.getString(all_cursor.getColumnIndex("driver_id_back_image_status"));

                    String driver_passfront_lat = all_cursor.getString(all_cursor.getColumnIndex("driver_passfront_lat"));
                    String driver_passfront_long = all_cursor.getString(all_cursor.getColumnIndex("driver_passfront_long"));
                    String driver_pass_front_image_status = all_cursor.getString(all_cursor.getColumnIndex("driver_pass_front_image_status"));

                    String driver_pass_back_lat = all_cursor.getString(all_cursor.getColumnIndex("driver_pass_back_lat"));
                    String driver_pass_back_long = all_cursor.getString(all_cursor.getColumnIndex("driver_pass_back_long"));
                    String driver_pass_back_image_status = all_cursor.getString(all_cursor.getColumnIndex("driver_pass_back_image_status"));

                    String driver_car_regfront_lat = all_cursor.getString(all_cursor.getColumnIndex("driver_car_regfront_lat"));
                    String driver_car_regfront_long = all_cursor.getString(all_cursor.getColumnIndex("driver_car_regfront_long"));
                    String driver_car_regfront_image_status = all_cursor.getString(all_cursor.getColumnIndex("driver_car_regfront_image_status"));

                    String driver_car_regback_lat = all_cursor.getString(all_cursor.getColumnIndex("driver_car_regback_lat"));
                    String driver_car_regback_long = all_cursor.getString(all_cursor.getColumnIndex("driver_car_regback_long"));
                    String driver_car_regback_image_status = all_cursor.getString(all_cursor.getColumnIndex("driver_car_regback_image_status"));

                    String driver_car_insure_lat = all_cursor.getString(all_cursor.getColumnIndex("driver_car_insure_lat"));
                    String driver_car_insure_long = all_cursor.getString(all_cursor.getColumnIndex("driver_car_insure_long"));
                    String driver_car_insure_image_status = all_cursor.getString(all_cursor.getColumnIndex("driver_car_insure_image_status"));
                    driver.setActivated(false);
                    driver.setFirstName(fname);
                    driver.setLastName(driver_lname);
                    driver.setPhoneNumber(driver_mobileno);
                    driver.setEmailID(SESSION_EMAIL);
                    Log.e("Email",driver_emailid+"");
                    driver.setPassword(password);
                    driver.setCompName(company_name);
                    driver.setCompAddr(company_address);
                    driver.setCompCity(city);
                    Log.e("city",city+"");
                    driver.setPinCode(pincode);
                    Log.e("pincode",pincode+"");
                    driver.setiMEI(imei);
                    driver.setLatLong(coordinates);
                    driver.setDeviceInfo(device_info);
                    driver.setCarManufacturer(car_manufacture);
                    driver.setCarModel(car_model);
                    driver.setCarColor(car_color);
                    driver.setYearOfManufacture(year_of_manufacturer);
                    driver.setNoOfPass(no_of_pass);
                    driver.setLicensePlateNumber("Hd");
                    driver.setDriverDOB(driver_dob);
                    driver.setVehiclePhotoDocURL(car_photo_url);
                    driver.setPlateNumberDocURL(license_plate_no);
                    driver.setDriverPhotoDocURL(driverPhoto_URL);
                    driver.setDriverLicenseDocURL(driverLicenseNo_URL);
                    driver.setDriverIDCardDocFrontURL(driverQatar_idFront_URL);
                    driver.setDriverIDCardDocBackURL(driverQatar_idBack_URL);
                    driver.setDriverPassportDocFrontURL(driverPassport_idFront_URL);
                    driver.setDriverPassportDocBackURL(driverPassport_idBack_URL);
                    driver.setCarRegistrationDocFrontURL(carRegistrationDocFrontURL);
                    driver.setCarRegistrationDocBackURL(carRegistrationDocBackURL);
                    driver.setVehicleInsuranceDocURL(vehicleInsuranceDocURL);

                    driver.setCountry(country);
                    driver.setRegDate(new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(new Date()));


                    Log.e("Home fname", fname);
                    Log.e("Home lname", driver_lname);
                    Log.e("Home driver_mobileno", driver_mobileno);
                    Log.e("Home driver_emailid", driver_emailid);
                    Log.e("Home password", password);
                    Log.e("Home company_name", company_name);
                    Log.e("Home company_address", company_address);
                    Log.e("Home city", city);
                    Log.e("Home country", country);
                    Log.e("Home pincode", pincode);
                    Log.e("Home imei", imei);
                    Log.e("Home coordinates", coordinates);
                    Log.e("Home device_info", device_info);
                    Log.e("Home car_manufacture", car_manufacture);
                    Log.e("Home car_model", car_model);
                    Log.e("Home car_color", car_color);
                    Log.e("Home year_of_manu", year_of_manufacturer);
                    Log.e("Home no_of_pass", no_of_pass);
                    Log.e("Home car_photo_url", car_photo_url);
                    Log.e("Home license_plate_no", license_plate_no);
                    Log.e("Home driver_dob", driver_dob);
                    Log.e("Home driverPhoto_URL", driverPhoto_URL);
                    Log.e("Home drLicenseNo_URL", driverLicenseNo_URL);
                    Log.e("Home driverFront", driverQatar_idFront_URL);
                    Log.e("driverQatar_idBack_URL", driverQatar_idBack_URL);
                    Log.e("driverPassporFront_URL", driverPassport_idFront_URL);
                    Log.e("driverPassportBack_URL", driverPassport_idBack_URL);
                    Log.e("carRegisFrontURL", carRegistrationDocFrontURL);
                    Log.e("carRegBackURL", carRegistrationDocBackURL);
                    Log.e("vehicleInsuranceDocURL", vehicleInsuranceDocURL);
                    Log.e("vehicle_pic_lat", vehicle_pic_lat);
                    Log.e("vehicle_pic_long", vehicle_pic_long);
                    Log.e("vehicle_image_status", vehicle_image_status);
//                    Log.e("license_plate_lat", license_plate_lat);
                    //           Log.e("license_plate_long", license_plate_long);
                    Log.e("license_image_status", license_image_status);
                    Log.e("image_status", driver_reg1);

                    VehiclePhotoDoc vehiclephoto = new VehiclePhotoDoc();
                    vehiclephoto.setLatitude(Double.parseDouble(vehicle_pic_lat));
                    vehiclephoto.setLongitude(Double.parseDouble(vehicle_pic_long));
//                    vehiclephoto.setValid(false);

                    PlateNumberDoc platephoto = new PlateNumberDoc();
                    platephoto.setLatitude(Double.parseDouble(license_plate_lat));
                    platephoto.setLongitude(Double.parseDouble(license_plate_long));
//                    platephoto.setValid(false);

                    DriverPhotoDoc driverpic = new DriverPhotoDoc();
                    driverpic.setLatitude(Double.parseDouble(driver_pic_lat));
                    driverpic.setLongitude(Double.parseDouble(driver_pic_long));
//                    driverpic.setValid(false);

                    DriverLicenseDoc driverlic = new DriverLicenseDoc();
                    driverlic.setLatitude(Double.parseDouble(driver_license_lat));
                    driverlic.setLongitude(Double.parseDouble(driver_license_long));
//                    driverlic.setValid(false);

                    DriverIDCardDocFront driverfront_id = new DriverIDCardDocFront();
                    driverfront_id.setLatitude(Double.parseDouble(driver_id_front_lat));
                    driverfront_id.setLongitude(Double.parseDouble(driver_id_front_long));
//                    driverfront_id.setValid(false);

                    DriverIDCardDocBack driverback_id = new DriverIDCardDocBack();
                    driverback_id.setLatitude(Double.parseDouble(driver_id_back_lat));
                    driverback_id.setLongitude(Double.parseDouble(driver_id_back_long));
//                    driverback_id.setValid(false);

                    DriverPassportDocFront driver_passfront = new DriverPassportDocFront();
                    driver_passfront.setLatitude(Double.parseDouble(driver_passfront_lat));
                    driver_passfront.setLongitude(Double.parseDouble(driver_passfront_long));
//                    driver_passfront.setValid(false);

                    DriverPassportDocBack driver_passback = new DriverPassportDocBack();
                    driver_passback.setLatitude(Double.parseDouble(driver_pass_back_lat));
                    driver_passback.setLongitude(Double.parseDouble(driver_pass_back_long));
//                    driver_passback.setValid(false);


                    CarRegistrationDocFront carreg_front = new CarRegistrationDocFront();
                    carreg_front.setLatitude(Double.parseDouble(driver_car_regfront_lat));
                    carreg_front.setLongitude(Double.parseDouble(driver_car_regfront_long));
//                    carreg_front.setValid(false);


                    CarRegistrationDocBack carreg_back = new CarRegistrationDocBack();
                    carreg_back.setLatitude(Double.parseDouble(driver_car_regback_lat));
                    carreg_back.setLongitude(Double.parseDouble(driver_car_regback_long));
//                    carreg_back.setValid(false);


                    VehicleInsuranceDoc driver_insu = new VehicleInsuranceDoc();
                    driver_insu.setLatitude(Double.parseDouble(driver_car_insure_lat));
                    driver_insu.setLongitude(Double.parseDouble(driver_car_insure_long));
//                    driver_insu.setValid(false);



                    url_img.setVehiclePhotoDoc(vehiclephoto);
                    url_img.setPlateNumberDoc(platephoto);
                    url_img.setDriverPhotoDoc(driverpic);
                    url_img.setDriverLicenseDoc(driverlic);
                    url_img.setDriverIDCardDocFront(driverfront_id);
                    url_img.setDriverIDCardDocBack(driverback_id);
                    url_img.setDriverPassportDocFront(driver_passfront);
                    url_img.setDriverPassportDocBack(driver_passback);
                    url_img.setCarRegistrationDocFrent(carreg_front);
                    url_img.setCarRegistrationDocBack(carreg_back);
                    url_img.setVehicleInsuranceDoc(driver_insu);
                    driver.setImages(url_img);
                    // driver = new AceKabsDriverPreRegistration();

                    //FirebaseDriverUtil.addDriverRegistrationInformation(driver);
                    // mydb.UpdatedDriverStatus(driver_emailid);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }*/

    }

    public void updateToDb() {
        try {
            //  if (list_url.size() == 3) {
            //    CarPhoto = list_url.get(1);
            ///  Plate_Number = list_url.get(2);
            //  Log.e("CarPhoto", list_url.get(0));
            //  Log.e("Plate_Number", list_url.get(1));

            Log.e("Plate_Number", driver.getLicensePlateNumber());
            mydb.UpdatePersonalInfo(SESSION_EMAIL, driver.getDriverPhotoDocURL(), driver.getDriverLicenseDocURL(), driver.getDriverIDCardDocFrontURL(), driver.getDriverIDCardDocBackURL(), driver.getDriverPassportDocFrontURL(), driver.getDriverPassportDocBackURL(), driver.getDriverDOB());
//                        Log.e("CarPhoto2", CarPhoto);
//                        Log.e("Plate_Number3", Plate_Number);
//                        Log.e("SESSION_EMAIL", SESSION_EMAIL);
            //  Intent li = new Intent(Update_VehicleInfo.this, Update_RegistrationInfo.class);
            // li.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            // startActivity(li);
            //finish();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    class GetDetails extends AsyncTask<Void, Void, Void> {

        // public AceKabsDriverPreRegistration driver1;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        protected Void doInBackground(Void... arg0) {

            try {
                Log.e("in do in back", "hello");
                String strjsn = jsonParser.makeServiceCall(ApplicationConstants.DRIVER_PENDINGIMAGES2_URL + "/" + SESSION_EMAIL);
                Log.e("APPPPPIIIIII CALL", ApplicationConstants.DRIVER_PENDINGIMAGES2_URL + "/" + SESSION_EMAIL + "");
                JSONObject json_res = new JSONObject(strjsn);
                ObjectMapper m = new ObjectMapper();
                driver = m.readValue(json_res.toString(), AceKabsDriverPreRegistration.class);

                Log.e("Driver Name", "hhiiii" + driver.getFirstName() + "");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Log.e("Completed", "completed");
            Log.e("Completed", driver.getFirstName());
            super.onPostExecute(aVoid);
        }
    }


}