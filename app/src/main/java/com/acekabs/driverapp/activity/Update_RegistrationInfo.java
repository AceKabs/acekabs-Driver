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
import android.support.design.widget.TextInputEditText;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.acekabs.driverapp.ApplicationConstants;
import com.acekabs.driverapp.JSONParser;
import com.acekabs.driverapp.R;
import com.acekabs.driverapp.db.DBAdapter;
import com.acekabs.driverapp.db.SessionManager;
import com.acekabs.driverapp.entities.AceKabsDriverPreRegistration;
import com.acekabs.driverapp.firebase.IFirebaseCallback;
import com.acekabs.driverapp.pojo.CarRegistrationDocBack;
import com.acekabs.driverapp.pojo.CarRegistrationDocFront;
import com.acekabs.driverapp.pojo.Images;
import com.acekabs.driverapp.pojo.VehicleInsuranceDoc;
import com.acekabs.driverapp.services.AceKabsDocumentUploadService;
import com.acekabs.driverapp.services.GPSTracker;
import com.acekabs.driverapp.utils.ImageCompressAsyncTask;
import com.acekabs.driverapp.utils.ImageLoadingUtils;
import com.acekabs.driverapp.utils.SharePreferanceWrapperSingleton;
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
import java.util.HashMap;

public class Update_RegistrationInfo extends Activity implements View.OnClickListener {

    ImageView back;
    View sign_header, update_sub_header;
    TextView title;  // personal, vehicle, registration, profile_edit;
    ImageView profile_car_registration_front_pic1, profile_car_registration_Back_pic1, profile_vehicle_Insurance_driver_pic1, Car_Registration_Front_photo_status, Car_Registration_Back_photo_status, Car_Vehicle_Insurance_photo_status;
    EditText signin_email;
    TextInputEditText signin_pass;
    Button signin_submit;
    String dir;
    String EMAIL = "", PASS = "";
    String SESSION_EMAIL;
    JSONParser jsonParser = new JSONParser();
    String RESULT = "", MESSAGE = "", RESPONSE_CODE = "", RESULT1 = "", MESSAGE1 = "", RESPONSE_CODE1 = "";
    DBAdapter mydb;
    Uri outputFileUri1, outputFileUri2, outputFileUri3;
    Cursor mcursor, ncursor;
    SessionManager msession;
    String Login_Email = "", Login_Pass = "", Driver_Status = "";
    Cursor all_cursor;
    String vehicle_Insurance_photo_status, reg_front_photo_status, reg_back_photo_status;
    SharePreferanceWrapperSingleton preferanceWrapperSingleton;
    Context context;
    Bitmap vehicle_InsuranceImageBitmap, _status, reg_frontBitmap, reg_back_photoBitmap;
    int width = 1366; // 1920
    int height = 768; // 1080
    String image_path1, image_path2, image_path3;
    GPSTracker gps;
    ExifInterface exifInterfaceone, exifInterfacetwo, exifInterfacethree;
    String[] str_list;
    AceKabsDriverPreRegistration driver;
    int x;
    //  Images url_img;
    Images images;
    RejectDriverRegistration rejectDriverRegistration;
    Button profile_reg_submit;
    CarRegistrationDocBack carRegistrationDocBack;
    CarRegistrationDocFront carRegistrationDocFront;
    VehicleInsuranceDoc vehicleInsuranceDoc;
    double carRegistrationDocBack_lat, carRegistrationDocBack_long, carRegistrationDocFront_lat, carRegistrationDocFront_long, vehicleInsuranceDoc_lat, vehicleInsuranceDoc_long;
    private ImageLoadingUtils utils;
    private String fileOne, fileTwo, fileThree;
    private ProgressDialog progressDialog;
    private ProgressDialog pDialog;
    private String car_Registration_front;
    private String vehicle_Insurance;
    private String car_Registration_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update__registration_info);
        profile_car_registration_Back_pic1 = (ImageView) findViewById(R.id.profile_car_registration_Back_pic1);
        profile_car_registration_front_pic1 = (ImageView) findViewById(R.id.profile_car_registration_front_pic1);
        profile_vehicle_Insurance_driver_pic1 = (ImageView) findViewById(R.id.profile_vehicle_Insurance_driver_pic1);
        Car_Registration_Front_photo_status = (ImageView) findViewById(R.id.Car_Registration_Front_photo_status);
        Car_Registration_Back_photo_status = (ImageView) findViewById(R.id.Car_Registration_Back_photo_status);
        Car_Vehicle_Insurance_photo_status = (ImageView) findViewById(R.id.Car_Vehicle_Insurance_photo_status);
        msession = new SessionManager(getApplicationContext());
        dir = Environment.getExternalStorageDirectory() + "/DriverApp/";
        str_list = new String[3];
        utils = new ImageLoadingUtils(Update_RegistrationInfo.this);

        context = this;

        // url_img=new Images();
        // get user data from session
        HashMap<String, String> user = msession.getUserDetails();
        SESSION_EMAIL = user.get(SessionManager.KEY_NAME);
        Log.e("My LLLLLLLLLLLLLLOOOOOOOOOOGGGGGGGGGGG1", SESSION_EMAIL);
        mydb = DBAdapter.getInstance(getApplicationContext());
        sign_header = findViewById(R.id.update_reg_header);

        //  update_sub_header = findViewById(R.id.update_reg_sub_header);

        back = (ImageView) sign_header.findViewById(R.id.profile_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        title = (TextView) sign_header.findViewById(R.id.profile_title);
        // profile_edit = (TextView) sign_header.findViewById(R.id.profile_stan_edit);
        title.setText("UPDATE VEHICLE DETAILS");
        //  personal = (TextView) update_sub_header.findViewById(R.id.tv_personal);
        // vehicle = (TextView) update_sub_header.findViewById(R.id.tv_vehicle);
        //registration = (TextView) update_sub_header.findViewById(R.id.tv_reg);

        gps = new GPSTracker(getApplicationContext());
        //registration.setBackgroundColor(Color.parseColor("#191919"));
        //vehicle.setBackgroundColor(Color.parseColor("#7f7f7f"));
        //personal.setBackgroundColor(Color.parseColor("#7f7f7f"));
        new GetDetails().execute();
        profile_reg_submit = (Button) findViewById(R.id.profile_personal_submit);
        profile_reg_submit.setOnClickListener(this);
        //personal.setTextColor(Color.parseColor("#FFFFFF"));
        // vehicle.setTextColor(Color.parseColor("#FFFFFF"));
        // registration.setTextColor(Color.parseColor("#FFFFFF"));

        //  personal.setOnClickListener(this);
        // vehicle.setOnClickListener(this);
        //registration.setOnClickListener(this);
        profile_car_registration_front_pic1.setOnClickListener(this);
        profile_car_registration_Back_pic1.setOnClickListener(this);
        profile_vehicle_Insurance_driver_pic1.setOnClickListener(this);
        try {
            ncursor = mydb.Driver_Status(SESSION_EMAIL);

            if (ncursor != null) {
                Log.e("MyChech", "Hi up");
                if (ncursor.moveToFirst()) {
                    car_Registration_front = ncursor.getString(ncursor.getColumnIndex("carRegistrationDocFrontURL"));
                    //     new DownloadImageTask(profile_car_registration_front_pic1).execute(car_Registration_front);
                    car_Registration_back = ncursor.getString(ncursor.getColumnIndex("carRegistrationDocBackURL"));
//                    new DownloadImageTask(profile_car_registration_Back_pic1).execute(car_Registration_back);
                    vehicle_Insurance = ncursor.getString(ncursor.getColumnIndex("vehicleInsuranceDocURL"));
//                    new DownloadImageTask(profile_vehicle_Insurance_driver_pic1).execute(vehicle_Insurance);
                    //    new DownloadImageTask(profile_vehicle_Insurance_driver_pic1).execute(vehicle_Insurance);
                    try {
                        UtilityMethods.displayImage(car_Registration_front, profile_car_registration_front_pic1);
                        UtilityMethods.displayImage(car_Registration_back, profile_car_registration_Back_pic1);
                        UtilityMethods.displayImage(vehicle_Insurance, profile_vehicle_Insurance_driver_pic1);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

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

        while (driver == null) ;
        Log.e("Driver Name", "hhiiii" + driver.getFirstName() + "");
        update_DriverDetails();
    }

    public void onClick(View view) {

      /*  if (view.getId() == R.id.tv_personal) {

            Intent per = new Intent(Update_RegistrationInfo.this, Update_PersonalInfo.class);
            startActivity(per);


        } else if (view.getId() == R.id.tv_vehicle) {

            Intent reg = new Intent(Update_RegistrationInfo.this, Update_VehicleInfo.class);
            startActivity(reg);


        } else if (view.getId() == R.id.tv_reg) {
            Toast.makeText(getApplicationContext(), "You are in same Screen", Toast.LENGTH_SHORT).show();
        } else if (view.getId() == R.id.profile_stan_edit) {
            Toast.makeText(getApplicationContext(), "EDIT", Toast.LENGTH_SHORT).show();
        } else*/
        if (view.getId() == R.id.profile_car_registration_front_pic1) {


            fileOne = dir + "Car_reg_front" + ".jpg";
            File newfile = new File(fileOne);
            try {
                newfile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Log.d("My Tag", "On Click:");
            outputFileUri1 = Uri.fromFile(newfile);
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri1);
            startActivityForResult(cameraIntent, 0);


        } else if (view.getId() == R.id.profile_car_registration_Back_pic1) {
            //OK

            fileTwo = dir + "Car_reg_back" + ".jpg";
            File newfile = new File(fileTwo);
            try {
                newfile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Log.d("My Tag", "On Click:");
            outputFileUri2 = Uri.fromFile(newfile);
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri2);
            startActivityForResult(cameraIntent, 1);

        } else if (view.getId() == R.id.profile_vehicle_Insurance_driver_pic1) {


            fileThree = dir + "Vehicle_Insurance" + ".jpg";
            File newfile = new File(fileThree);
            try {
                newfile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Log.d("My Tag", "On Click:");
            outputFileUri3 = Uri.fromFile(newfile);
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri3);
            startActivityForResult(cameraIntent, 2);
        } else if (view.getId() == R.id.profile_personal_submit) {
            if (reg_back_photo_status.equalsIgnoreCase("false") || reg_front_photo_status.equalsIgnoreCase("false") || vehicle_Insurance_photo_status.equalsIgnoreCase("false")) {
                mydb.UpdateVehicleDocs(SESSION_EMAIL, driver.getCarRegistrationDocFrontURL(), driver.getCarRegistrationDocBackURL(), driver.getVehicleInsuranceDocURL());
                Log.d("My ", driver.getCarRegistrationDocFrontURL());
                Log.d("My ", driver.getCarRegistrationDocBackURL());
                Log.d("My ", driver.getVehicleInsuranceDocURL());
                rejectDriverRegistration = new RejectDriverRegistration(driver, context);
                startActivity(getIntent());
                finish();
            } else
                Toast.makeText(context, "We have found No rejected Images", Toast.LENGTH_SHORT).show();
        }


    }


    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("onActivityResult","onActivityResult");
        if (requestCode == 0 && resultCode == RESULT_OK) {
            Log.d("My Log" + "CameraDemo", "Pic saved");
            try {
                reg_frontBitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), Uri.parse(outputFileUri1.toString()));
                profile_car_registration_front_pic1.setImageBitmap(reg_frontBitmap);
                width = reg_frontBitmap.getWidth();
                height = reg_frontBitmap.getHeight();
                Log.d("My", "onActivityResult: ");
                image_path1 = MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), reg_frontBitmap, "Title", null);
//                Log.e(TAG + "image_path1", image_path1);
                if (gps.canGetLocation()) {
                    String path = UtilityMethods.getRealPathFromUri(Update_RegistrationInfo.this, Uri.parse(image_path1));
                    exifInterfaceone = new ExifInterface(path);

                    ImageCompressAsyncTask imageCompressAsyncTask=new ImageCompressAsyncTask(Update_RegistrationInfo.this,path,exifInterfaceone,path,utils);
                    imageCompressAsyncTask.setOnImageCompressed(new ImageCompressAsyncTask.OnImageCompressed() {
                        @Override
                        public void onCompressedImage(String path) {
                            image_path1= path;
                            Log.e("onCompressedImage  path=>",path);
                            str_list[0] = image_path1;
                            fireBase_Previous(0);
                        }
                    });


                  //  image_path1 = compressImage(path, exifInterfaceone, path);



                    exifInterfaceone.setAttribute(ExifInterface.TAG_GPS_LATITUDE, UtilityMethods.convertToDegreeMinuteSeconds(gps.getLatitude()));
                    exifInterfaceone.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, UtilityMethods.convertToDegreeMinuteSeconds(gps.getLongitude()));
                    exifInterfaceone.setAttribute(ExifInterface.TAG_MAKE, UtilityMethods.getManufacturer());
                    exifInterfaceone.setAttribute(ExifInterface.TAG_MODEL, UtilityMethods.getDeviceName());
                    exifInterfaceone.setAttribute("imei", UtilityMethods.getIMEI(Update_RegistrationInfo.this));
                    exifInterfaceone.setAttribute("UserComment", UtilityMethods.getIMEI(Update_RegistrationInfo.this));
                    exifInterfaceone.saveAttributes();
                    Log.d("MY", "onActivityResult: " + String.valueOf(gps.getLatitude()) + "    " + String.valueOf(gps.getLongitude()));
                    carRegistrationDocFront_lat = gps.getLatitude();
                    carRegistrationDocFront_long = gps.getLongitude();
                    mydb.UpdateFrontcarregCoord(SESSION_EMAIL, String.valueOf(gps.getLatitude()), String.valueOf(gps.getLongitude()), "true");

                    imageCompressAsyncTask.execute();
                }
                // profile_LisencePlateNo_driver_pic1.setEnabled(true);
            } catch (Exception e) {
                Log.d("My", e.toString());
                e.printStackTrace();
            }
            Log.d("My Tag", "On Activity Result");
//            str_list[0] = image_path1;
//            fireBase_Previous(0);

        } else if (requestCode == 1 && resultCode == RESULT_OK) {
            try {
                reg_back_photoBitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), Uri.parse(outputFileUri2.toString()));
                profile_car_registration_Back_pic1.setImageBitmap(reg_back_photoBitmap);
                width = reg_back_photoBitmap.getWidth();
                height = reg_back_photoBitmap.getHeight();
                Log.d("My Tag", "On Activity Result");

                image_path2 = MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), reg_back_photoBitmap, "Title", null);
//                Log.e(TAG + "image_path2", image_path2);
                Log.d("My Tag", "On Activity Result");
                if (gps.canGetLocation()) {
                    String path = UtilityMethods.getRealPathFromUri(Update_RegistrationInfo.this, Uri.parse(image_path2));
                    exifInterfacetwo = new ExifInterface(path);

                    ImageCompressAsyncTask imageCompressAsyncTask=new ImageCompressAsyncTask(Update_RegistrationInfo.this,path,exifInterfacetwo,path,utils);
                    imageCompressAsyncTask.setOnImageCompressed(new ImageCompressAsyncTask.OnImageCompressed() {
                        @Override
                        public void onCompressedImage(String path) {
                            image_path2= path;
                            Log.e("onCompressedImage  path=>",path);
                            str_list[1] = image_path2;
                            Log.d("My Tag", "On Activity Result");
                            fireBase_Previous(1);
                        }
                    });


                    //image_path2 = compressImage(path, exifInterfacetwo, path);

                    exifInterfacetwo.setAttribute(ExifInterface.TAG_GPS_LATITUDE, UtilityMethods.convertToDegreeMinuteSeconds(gps.getLatitude()));
                    exifInterfacetwo.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, UtilityMethods.convertToDegreeMinuteSeconds(gps.getLongitude()));
                    exifInterfacetwo.setAttribute(ExifInterface.TAG_MAKE, UtilityMethods.getManufacturer());
                    exifInterfacetwo.setAttribute(ExifInterface.TAG_MODEL, UtilityMethods.getDeviceName());
                    exifInterfacetwo.setAttribute("imei", UtilityMethods.getIMEI(Update_RegistrationInfo.this));
                    exifInterfacetwo.setAttribute("UserComment", UtilityMethods.getIMEI(Update_RegistrationInfo.this));
                    exifInterfacetwo.saveAttributes();
                    mydb.UpdateBackcarregCoord(SESSION_EMAIL, String.valueOf(gps.getLatitude()), String.valueOf(gps.getLongitude()), "true");
                    carRegistrationDocBack_lat = gps.getLatitude();
                    Log.e("Loction", gps.getLatitude() + "");
                    carRegistrationDocBack_long = gps.getLongitude();
                    Log.e("Loction", gps.getLongitude() + "");

                    imageCompressAsyncTask.execute();
                }

            } catch (Exception e) {
                Log.d("My Tag", e.toString());

                e.printStackTrace();
            }
//            str_list[1] = image_path2;
//            Log.d("My Tag", "On Activity Result");
//            fireBase_Previous(1);
            Log.d("My Tag", "On Activity Result");
        } else if (requestCode == 2 && resultCode == RESULT_OK) {
            try {
                vehicle_InsuranceImageBitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), Uri.parse(outputFileUri3.toString()));
                profile_vehicle_Insurance_driver_pic1.setImageBitmap(vehicle_InsuranceImageBitmap);
                width = vehicle_InsuranceImageBitmap.getWidth();
                height = vehicle_InsuranceImageBitmap.getHeight();
                Log.d("My Tag", "On Activity Result");

                image_path3 = MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), vehicle_InsuranceImageBitmap, "Title", null);
//                Log.e(TAG + "image_path2", image_path2);
                Log.d("My Tag", "On Activity Result");
                if (gps.canGetLocation()) {
                    String path = UtilityMethods.getRealPathFromUri(Update_RegistrationInfo.this, Uri.parse(image_path3));
                    exifInterfacethree = new ExifInterface(path);

                    ImageCompressAsyncTask imageCompressAsyncTask=new ImageCompressAsyncTask(Update_RegistrationInfo.this,path,exifInterfacethree,path,utils);
                    imageCompressAsyncTask.setOnImageCompressed(new ImageCompressAsyncTask.OnImageCompressed() {
                        @Override
                        public void onCompressedImage(String path) {
                            image_path3= path;
                            Log.e("onCompressedImage  path=>",path);
                        }
                    });


                   // image_path3 = compressImage(path, exifInterfacetwo, path);

                    exifInterfacethree.setAttribute(ExifInterface.TAG_GPS_LATITUDE, UtilityMethods.convertToDegreeMinuteSeconds(gps.getLatitude()));
                    exifInterfacethree.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, UtilityMethods.convertToDegreeMinuteSeconds(gps.getLongitude()));
                    exifInterfacethree.setAttribute(ExifInterface.TAG_MAKE, UtilityMethods.getManufacturer());
                    exifInterfacethree.setAttribute(ExifInterface.TAG_MODEL, UtilityMethods.getDeviceName());
                    exifInterfacethree.setAttribute("imei", UtilityMethods.getIMEI(Update_RegistrationInfo.this));
                    exifInterfacethree.setAttribute("UserComment", UtilityMethods.getIMEI(Update_RegistrationInfo.this));
                    exifInterfacethree.saveAttributes();
                    mydb.UpdatelicenseCoord(SESSION_EMAIL, String.valueOf(gps.getLatitude()), String.valueOf(gps.getLongitude()), "true");
                    vehicleInsuranceDoc_lat = gps.getLatitude();
                    vehicleInsuranceDoc_long = gps.getLongitude();

                    imageCompressAsyncTask.execute();
                }

            } catch (Exception e) {
                Log.d("My Tag", e.toString());

                e.printStackTrace();
            }
            str_list[1] = image_path3;
            Log.d("My Tag", "On Activity Result");
            fireBase_Previous(2);
            Log.d("My Tag", "On Activity Result");
        }
    }

    public void fireBase_Previous(int i) {
        x = i;
        Log.d("My Tag", "FireBase_Previous");

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


    private void uploadDocToFirebase(Uri imageUri, final int i) {
//        progressDialog = new ProgressDialog(context);
//        Log.d("My Tag", "uploadDocToFireBase");
//        progressDialog.setMessage("Uploading ....");
//        progressDialog.setCancelable(false);
//        progressDialog.show();
        new AceKabsDocumentUploadService().uploadFileFromDisk(imageUri, new IFirebaseCallback<Uri>() {
            @Override
            public void onDataReceived(Uri data) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                String storageUrl = data.toString();
                if (i == 0) {
                    driver.setCarRegistrationDocFrontURL(storageUrl);
                    carRegistrationDocFront.setLatitude(carRegistrationDocFront_lat);
                    carRegistrationDocFront.setLongitude(carRegistrationDocFront_long);
                    Log.e("Car_pohot_after change", driver.getCarRegistrationDocFrontURL());
                } else if (i == 1) {
                    driver.setCarRegistrationDocBackURL(storageUrl);
                    carRegistrationDocBack.setLatitude(carRegistrationDocBack_lat);
                    carRegistrationDocBack.setLongitude(carRegistrationDocBack_long);
                    Log.e("Car_pohot_after change", driver.getCarRegistrationDocBackURL());

                } else if (i == 2) {
                    driver.setVehicleInsuranceDocURL(storageUrl);
                    vehicleInsuranceDoc.setLatitude(vehicleInsuranceDoc_lat);
                    vehicleInsuranceDoc.setLongitude(vehicleInsuranceDoc_long);
                    Log.e("Car_pohot_after change", driver.getVehicleInsuranceDocURL());
                }

                driver.setImages(images);
                Log.e("Email", driver.getEmailID() + "");


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


    public void GettingImageStatus() {

        mcursor = mydb.getDriver_reg_Email(SESSION_EMAIL);

        Log.e("Count", "" + mcursor.getCount());

        if (mcursor != null) {
            if (mcursor.moveToFirst()) {
                Log.e("Count2", "" + mcursor.getCount());
                reg_front_photo_status = mcursor.getString(mcursor.getColumnIndex("driver_car_regfront_image_status"));
                Log.e("photo1", reg_front_photo_status);
                reg_back_photo_status = mcursor.getString(mcursor.getColumnIndex("driver_car_regback_image_status"));
                vehicle_Insurance_photo_status = mcursor.getString(mcursor.getColumnIndex("driver_car_insure_image_status"));


                if (reg_front_photo_status.equalsIgnoreCase("true")) {
                    profile_car_registration_front_pic1.setClickable(false);
                    Car_Registration_Front_photo_status.setBackgroundResource(R.drawable.success4);
                } else {
                    profile_car_registration_front_pic1.setClickable(true);
                    Car_Registration_Front_photo_status.setBackgroundResource(R.drawable.failure4);
                }

                if (reg_back_photo_status.equalsIgnoreCase("true")) {
                    profile_car_registration_Back_pic1.setClickable(false);
                    Car_Registration_Back_photo_status.setBackgroundResource(R.drawable.success4);
                } else {
                    profile_car_registration_Back_pic1.setClickable(true);
                    Car_Registration_Back_photo_status.setBackgroundResource(R.drawable.failure4);
                }
                if (vehicle_Insurance_photo_status.equalsIgnoreCase("true")) {
                    profile_vehicle_Insurance_driver_pic1.setClickable(false);
                    Car_Vehicle_Insurance_photo_status.setBackgroundResource(R.drawable.success4);
                } else {
                    profile_vehicle_Insurance_driver_pic1.setClickable(true);
                    Car_Vehicle_Insurance_photo_status.setBackgroundResource(R.drawable.failure4);
                }


            }

        }
    }

    public String compressImage(String imageUri, ExifInterface exifInterface, String filenames) {
        String filePath = imageUri;
        Bitmap scaledBitmap = null;
        Log.d("My Tag", "compressImage: ");
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

    void update_DriverDetails() {
        Log.e("LOLLOLOLOLOLOL", driver.getVehiclePhotoDocURL());
        images = driver.getImages();
        carRegistrationDocFront = images.getCarRegistrationDocFront();
        carRegistrationDocBack = images.getCarRegistrationDocBack();
        vehicleInsuranceDoc = images.getVehicleInsuranceDoc();


       /* all_cursor = mydb.getDriver_reg_Email(SESSION_EMAIL);
        try {
            Log.d("My Tag", "Update Driver Details");

            if (all_cursor != null) {
                if (all_cursor.moveToFirst()) {
                    String fname = all_cursor.getString(all_cursor.getColumnIndex("driver_fname"));
                    String driver_lname = all_cursor.getString(all_cursor.getColumnIndex("driver_lname"));
                    String driver_mobileno = all_cursor.getString(all_cursor.getColumnIndex("driver_mobileno"));
                    String driver_emailid = all_cursor.getString(all_cursor.getColumnIndex("driver_emailid"));
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
                    driver.setEmailID(driver_emailid);
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

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
//            try {
//                InputStream in = new java.net.URL(urldisplay).openStream();
//                mIcon11 = BitmapFactory.decodeStream(in);
//            } catch (Exception e) {
//                Log.e("Error", e.getMess
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
//            bmImage.setImageBitmap(result);
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
