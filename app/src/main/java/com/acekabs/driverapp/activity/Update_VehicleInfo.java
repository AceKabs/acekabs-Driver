package com.acekabs.driverapp.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import com.acekabs.driverapp.pojo.Images;
import com.acekabs.driverapp.pojo.PlateNumberDoc;
import com.acekabs.driverapp.pojo.VehiclePhotoDoc;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

//import org.springframework.beans.factory.annotation.Autowired;
//import com.acekabs.ApplicationConstants;
//import com.acekabs.entities.DriverEntity;
//import com.acekabs.firebase.AceKabsDriverPreRegistration;
//import com.acekabs.firebase.FirebaseDataRetriever;
//import com.acekabs.firebase.IFirebaseCallback;
//import com.acekabs.jpa.repository.DriverRepository;
//import com.firebase.client.Firebase;

public class Update_VehicleInfo extends Activity implements View.OnClickListener {

    private final String TAG = "Reply";
    ImageView back;
    View sign_header, update_sub_header;
    TextView title;// personal, vehicle, registration, profile_edit;
    SessionManager manager;
    String SESSION_MAIL = "";
    Cursor mcursor, ncursor, all_cursor;
    String NAME = "", MODEL = "", YEAR_MANU = "", VECHILE_NO = "", COLOR = "", NO_PASS = "";
    // DBAdapter mydb;
    //String dir;
    //private String fileOne, fileTwo;
    //Uri outputFileUri, outputFileUri2;
    Bitmap carImageBitmap;
    AceKabsDriverPreRegistration driver;
    Bitmap licensePlateImageBitmap;
    int width = 1366; // 1920
    int height = 768; // 1080
    ByteArrayOutputStream outputStream;
    String image_path1, image_path2;
    String[] str_list;
    String dir;
    ArrayList<String> list_url;
    DBAdapter mydb;
    String CarPhoto = "", Plate_Number = "", SESSION_EMAIL = "";
    SessionManager msession;
    GPSTracker gps;
    /*added code for handle image wiith exif interface*/
    ExifInterface exifInterfaceone;
    ExifInterface exifInterfacetwo;
    Context context;
    Uri outputFileUri, outputFileUri2;
    EditText profile_vehicle_edit_name, profile_vehicle_edit_model, profile_vehicle_edit_color,
            profile_vehicle_edit_manu, profile_vechile_edit_no_pass;
    ImageView profile_car_driver_pic1, profile_LisencePlateNo_driver_pic1, car_Img_photo_status, lisencePlateNo_photo_status;
    String car_photo_status, lisencePlateNo_status;
    Images images;
    JSONParser jsonParser = new JSONParser();
    RejectDriverRegistration rejectDriverRegistration;
    PlateNumberDoc plateNumberDoc;
    VehiclePhotoDoc vehiclePhotoDoc;
    double plateNumberDoc_lat, plateNumberDoc_long, vehiclePhotoDoc_lat, vehiclePhotoDoc_long;
    private ImageLoadingUtils utils;
    private String fileOne, fileTwo;
    private int x;
    private LinearLayout mainlayout;
    private String fileZero;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update__vehicle_info);
        mydb = DBAdapter.getInstance(getApplicationContext());
        utils = new ImageLoadingUtils(Update_VehicleInfo.this);
        gps = new GPSTracker(getApplicationContext());
        mydb = DBAdapter.getInstance(getApplicationContext());
        //   mydb.addNewColumn("driverLicensePlate_URL");
        msession = new SessionManager(getApplicationContext());
        context = this;
        manager = new SessionManager(getApplicationContext());
        HashMap<String, String> user = manager.getUserDetails();
        SESSION_MAIL = user.get(SessionManager.KEY_NAME);
        SESSION_EMAIL = user.get(SessionManager.KEY_NAME);
        str_list = new String[2];
        new GetDetails().execute();
        dir = Environment.getExternalStorageDirectory() + "/DriverApp/";
        profile_car_driver_pic1 = (ImageView) findViewById(R.id.profile_car_driver_pic1);
        profile_LisencePlateNo_driver_pic1 = (ImageView) findViewById(R.id.profile_LisencePlateNo_driver_pic1);
        car_Img_photo_status = (ImageView) findViewById(R.id.Car_Img_photo_status);
        lisencePlateNo_photo_status = (ImageView) findViewById(R.id.LisencePlateNo_photo_status);
        sign_header = findViewById(R.id.update_header);
        // update_sub_header = findViewById(R.id.update_sub_header);
        back = (ImageView) sign_header.findViewById(R.id.profile_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        title = (TextView) sign_header.findViewById(R.id.profile_title);
        //profile_edit = (TextView) sign_header.findViewById(R.id.profile_stan_edit);
        title.setText("UPDATE VEHICLE DETAILS");
        //personal = (TextView) update_sub_header.findViewById(R.id.tv_personal);
        //vehicle = (TextView) update_sub_header.findViewById(R.id.tv_vehicle);
        //registration = (TextView) update_sub_header.findViewById(R.id.tv_reg);
        profile_vehicle_edit_name = (EditText) findViewById(R.id.profile_vehicle_edit_name);
        profile_vehicle_edit_model = (EditText) findViewById(R.id.profile_vehicle_edit_model);
        profile_vehicle_edit_color = (EditText) findViewById(R.id.profile_vehicle_edit_color);
        profile_vehicle_edit_manu = (EditText) findViewById(R.id.profile_vehicle_edit_manu);
        profile_vechile_edit_no_pass = (EditText) findViewById(R.id.profile_vechile_edit_no_pass);


        // personal.setOnClickListener(this);
        // vehicle.setOnClickListener(this);
        // registration.setOnClickListener(this);
        // vehicle.setBackgroundColor(Color.parseColor("#191919"));
        // vehicle.setTextColor(Color.parseColor("#FFFFFF"));
        //registration.setBackgroundColor(Color.parseColor("#7f7f7f"));
        //personal.setBackgroundColor(Color.parseColor("#7f7f7f"));
        //personal.setTextColor(Color.parseColor("#FFFFFF"));

        try {
            mcursor = mydb.getDriver_reg_Email(SESSION_MAIL);
            if (mcursor != null) {
                if (mcursor.moveToFirst()) {
                    NAME = mcursor.getString(mcursor.getColumnIndex("car_manufacture"));
                    MODEL = mcursor.getString(mcursor.getColumnIndex("car_model"));
                    COLOR = mcursor.getString(mcursor.getColumnIndex("car_color"));
                    YEAR_MANU = mcursor.getString(mcursor.getColumnIndex("year_of_manufacturer"));
                    NO_PASS = mcursor.getString(mcursor.getColumnIndex("no_of_pass"));

                    profile_vehicle_edit_name.setText(NAME);
                    profile_vehicle_edit_model.setText(MODEL);
                    profile_vehicle_edit_color.setText(COLOR);
                    profile_vehicle_edit_manu.setText(YEAR_MANU);
                    profile_vechile_edit_no_pass.setText(NO_PASS);


                    // aceKabsDriverPreRegistration.setCompName();
                    //  Need to implement Logic for Images Showing

                } else
                    Log.e("Db value", "None");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            ncursor = mydb.Driver_Status(SESSION_MAIL);

            if (ncursor != null) {
                Log.e("MyChech", "Hi up");
                if (ncursor.moveToFirst()) {
                    String lisence_PlateNo = ncursor.getString(ncursor.getColumnIndex("driverLicensePlate_URL"));
                    Log.e("Lisence_plate", lisence_PlateNo);
                    UtilityMethods.displayImage(lisence_PlateNo, profile_LisencePlateNo_driver_pic1);
                    String car_Photo = ncursor.getString(ncursor.getColumnIndex("car_photo_url"));
                    UtilityMethods.displayImage(car_Photo, profile_car_driver_pic1);
                    //              new DownloadImageTask(profile_car_driver_pic1).execute(car_Photo);
                    Log.e("Car_phot", car_Photo);
                    //new DownloadImageTask(profile_LisencePlateNo_driver_pic1).execute(lisence_PlateNo);


                    //String car_color = mcursor.getString(mcursor.getColumnIndex("car_color"));
                    //String car_year_manu = mcursor.getString(mcursor.getColumnIndex("year_of_manufacturer"));
                    //String car_no_of_pass = mcursor.getString(mcursor.getColumnIndex("no_of_pass"));

                    //  Need to implement Logic for Images Showing

                } else
                    Log.e("Db value", "None");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        try {
            GettingImageStatus();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (profile_car_driver_pic1.isClickable()) {
            profile_car_driver_pic1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fileOne = dir + "Car_Photo" + ".jpg";
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
                }
            });
        }
        if (profile_LisencePlateNo_driver_pic1.isClickable()) {
            profile_LisencePlateNo_driver_pic1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fileTwo = dir + "License_Plate_Photo" + ".jpg";
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
                }
            });

        }

        while (driver == null) ;    // had to do else null pointer exception occurs
        Log.e("Driver Name", "hhiiii" + driver.getFirstName() + "");
        update_DriverDetails();                                 // Diver obj values beging initialized


    }

    public void onClick(View view) {

        if (view.getId() == R.id.tv_personal) {

            Intent per = new Intent(Update_VehicleInfo.this, Update_PersonalInfo.class);
            startActivity(per);

        } else if (view.getId() == R.id.tv_vehicle) {

            Toast.makeText(getApplicationContext(), "You are in same Screen", Toast.LENGTH_SHORT).show();

        } else if (view.getId() == R.id.tv_reg) {


            Intent per = new Intent(Update_VehicleInfo.this, Update_RegistrationInfo.class);
            startActivity(per);

        } else if (view.getId() == R.id.profile_stan_edit) {

            Toast.makeText(getApplicationContext(), "EDIT", Toast.LENGTH_SHORT).show();

        } else if (view.getId() == R.id.profile_personal_submit) {
            driver.setCarManufacturer(profile_vehicle_edit_name.getText().toString());
            driver.setCarModel(profile_vehicle_edit_model.getText().toString());
            driver.setCarColor(profile_vehicle_edit_color.getText().toString());
            driver.setYearOfManufacture(profile_vehicle_edit_manu.getText().toString());
            driver.setNoOfPass(profile_vechile_edit_no_pass.getText().toString());
            updateToDb();
            if (car_photo_status.equalsIgnoreCase("false") || lisencePlateNo_status.equalsIgnoreCase("false")) {
                rejectDriverRegistration = new RejectDriverRegistration(driver, context);
                startActivity(getIntent());
                finish();
            } else {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                builder1.setTitle("Confirm");
                builder1.setMessage("Are you sure you want to Proceed as you have no rejected Image?");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                               dialog.cancel();
                                rejectDriverRegistration = new RejectDriverRegistration(driver, context);
                            }
                        });

                builder1.setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();
            }
        }

    }

    public void updateToDb() {
        try {
            //  if (list_url.size() == 3) {
            //    CarPhoto = list_url.get(1);
            ///  Plate_Number = list_url.get(2);
            //  Log.e("CarPhoto", list_url.get(0));
            //  Log.e("Plate_Number", list_url.get(1));
            Log.e("Plate_Number", driver.getLicensePlateNumber());
            mydb.updateVehicleInfo(SESSION_EMAIL, driver.getCarManufacturer(), driver.getCarModel(), driver.getCarColor(), driver.getYearOfManufacture(), driver.getNoOfPass(), driver.getVehiclePhotoDocURL(), driver.getPlateNumberDocURL(), driver.getLicensePlateNumber());
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




  /*  private synchronized void upDateTripData() {
        if (str_list != null) {
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            final DatabaseReference reference = firebaseDatabase.getReference();
            Query query = reference.child("Drivers").orderByChild("emailId").equalTo(SESSION_EMAIL);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (fareDetails != null) {
                        DataSnapshot nodeDataSnapshot = dataSnapshot.getChildren().iterator().next();
                        String key = nodeDataSnapshot.getKey(); // this key is `K1NRz9l5PU_0CFDtgXz`
                        String path = "/" + dataSnapshot.getKey() + "/" + key;
                        HashMap<String, Object> result = new HashMap<>();
                        result.put("", fareDetails.getDriverid());
                        result.put("drivername", fareDetails.getDrivername());

                        reference.child(path).updateChildren(result);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    if (databaseError != null) {
                        Log.e("Error", databaseError.getMessage());
                    }
                }
            });
        }
    }
*/
 /* public synchronized void uploadVecDetalils(String emailId) {
      FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
      final DatabaseReference reference = firebaseDatabase.getReference();
      Query query = reference.child("emailID").orderByChild(emailId).equalTo(SESSION_EMAIL);
      query.addListenerForSingleValueEvent(new ValueEventListener() {
          @Override
          public void onDataChange(DataSnapshot dataSnapshot) {
              if (str_list != null) {
                  DataSnapshot nodeDataSnapshot = dataSnapshot.getChildren().iterator().next();
                  String key = nodeDataSnapshot.getKey(); // this key is `K1NRz9l5PU_0CFDtgXz`
                  String path = "/" + dataSnapshot.getKey() + "/" + key;
                  HashMap<String, Object> result = new HashMap<>();
                  result.put("car_pic", str_list.get(0));

                  reference.child(path).updateChildren(result);
              }

          }

          @Override
          public void onCancelled(DatabaseError databaseError) {
              if (databaseError != null) {
                  Log.e("Error", databaseError.getMessage());
              }
          }
      });
  }
*/


    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == RESULT_OK) {
            Log.d(TAG + "CameraDemo", "Pic saved");
            try {
                carImageBitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), Uri.parse(outputFileUri.toString()));
                profile_car_driver_pic1.setImageBitmap(carImageBitmap);
                width = carImageBitmap.getWidth();
                height = carImageBitmap.getHeight();
                image_path1 = MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), carImageBitmap, "Title", null);
//                Log.e(TAG + "image_path1", image_path1);
                if (gps.canGetLocation()) {
                    String path = UtilityMethods.getRealPathFromUri(Update_VehicleInfo.this, Uri.parse(image_path1));
                    exifInterfaceone = new ExifInterface(path);

                    ImageCompressAsyncTask imageCompressAsyncTask=new ImageCompressAsyncTask(Update_VehicleInfo.this,path,exifInterfaceone,path,utils);
                    imageCompressAsyncTask.setOnImageCompressed(new ImageCompressAsyncTask.OnImageCompressed() {
                        @Override
                        public void onCompressedImage(String path) {
                            image_path1= path;
                            Log.e("onCompressedImage  path=>",path);
                            str_list[0] = image_path1;
                            fireBase_Previous(0);
                        }
                    });


                   // image_path1 = compressImage(path, exifInterfaceone, path);

                    exifInterfaceone.setAttribute(ExifInterface.TAG_GPS_LATITUDE, UtilityMethods.convertToDegreeMinuteSeconds(gps.getLatitude()));
                    exifInterfaceone.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, UtilityMethods.convertToDegreeMinuteSeconds(gps.getLongitude()));
                    exifInterfaceone.setAttribute(ExifInterface.TAG_MAKE, UtilityMethods.getManufacturer());
                    exifInterfaceone.setAttribute(ExifInterface.TAG_MODEL, UtilityMethods.getDeviceName());
                    exifInterfaceone.setAttribute("imei", UtilityMethods.getIMEI(Update_VehicleInfo.this));
                    exifInterfaceone.setAttribute("UserComment", UtilityMethods.getIMEI(Update_VehicleInfo.this));
                    exifInterfaceone.saveAttributes();
                    mydb.UpdateVehicleCoord(SESSION_EMAIL, String.valueOf(gps.getLatitude()), String.valueOf(gps.getLongitude()), "true");
                    vehiclePhotoDoc_lat = gps.getLatitude();
                    vehiclePhotoDoc_long = gps.getLongitude();

                    imageCompressAsyncTask.execute();
                }

                // profile_LisencePlateNo_driver_pic1.setEnabled(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == 1 && resultCode == RESULT_OK) {
            try {
                licensePlateImageBitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), Uri.parse(outputFileUri2.toString()));
                profile_LisencePlateNo_driver_pic1.setImageBitmap(licensePlateImageBitmap);
                width = licensePlateImageBitmap.getWidth();
                height = licensePlateImageBitmap.getHeight();
                image_path2 = MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), licensePlateImageBitmap, "Title", null);
//                Log.e(TAG + "image_path2", image_path2);
                if (gps.canGetLocation()) {
                    String path = UtilityMethods.getRealPathFromUri(Update_VehicleInfo.this, Uri.parse(image_path2));
                    exifInterfacetwo = new ExifInterface(path);

                    ImageCompressAsyncTask imageCompressAsyncTask=new ImageCompressAsyncTask(Update_VehicleInfo.this,path,exifInterfacetwo,path,utils);
                    imageCompressAsyncTask.setOnImageCompressed(new ImageCompressAsyncTask.OnImageCompressed() {
                        @Override
                        public void onCompressedImage(String path) {
                            image_path2= path;
                            Log.e("onCompressedImage  path=>",path);
                            str_list[1] = image_path2;
                            Log.e("aa", "aa");
                            fireBase_Previous(1);
                        }
                    });

                   // image_path2 = compressImage(path, exifInterfacetwo, path);

                    exifInterfacetwo.setAttribute(ExifInterface.TAG_GPS_LATITUDE, UtilityMethods.convertToDegreeMinuteSeconds(gps.getLatitude()));
                    exifInterfacetwo.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, UtilityMethods.convertToDegreeMinuteSeconds(gps.getLongitude()));
                    exifInterfacetwo.setAttribute(ExifInterface.TAG_MAKE, UtilityMethods.getManufacturer());
                    exifInterfacetwo.setAttribute(ExifInterface.TAG_MODEL, UtilityMethods.getDeviceName());
                    exifInterfacetwo.setAttribute("imei", UtilityMethods.getIMEI(Update_VehicleInfo.this));
                    exifInterfacetwo.setAttribute("UserComment", UtilityMethods.getIMEI(Update_VehicleInfo.this));
                    exifInterfacetwo.saveAttributes();
                    mydb.UpdatelicenseCoord(SESSION_EMAIL, String.valueOf(gps.getLatitude()), String.valueOf(gps.getLongitude()), "true");
                    plateNumberDoc_lat = gps.getLatitude();
                    plateNumberDoc_long = gps.getLongitude();

                    imageCompressAsyncTask.execute();

                }

                Log.e("aa", "aa");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void fireBase_Previous(int i) {
        x = i;
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

    public void GettingImageStatus() {

        mcursor = mydb.getDriver_reg_Email(SESSION_MAIL);
        Log.e("My LLLLLLLLLLLLLLOOOOOOOOOOGGGGGGGGGGG1", SESSION_MAIL);
        Log.e("Count", "" + mcursor.getCount());
        if (mcursor != null) {
            if (mcursor.moveToFirst()) {
                Log.e("Count2", "" + mcursor.getCount());
                car_photo_status = mcursor.getString(mcursor.getColumnIndex("vehicle_image_status"));
                lisencePlateNo_status = mcursor.getString(mcursor.getColumnIndex("license_image_status"));
                Log.e("car_photo_status", car_photo_status);
                //Log.e("lisencePl")


                if (car_photo_status.equalsIgnoreCase("true")) {
                    profile_car_driver_pic1.setClickable(false);
                    car_Img_photo_status.setBackgroundResource(R.drawable.success4);
                } else {
                    profile_car_driver_pic1.setClickable(true);
                    car_Img_photo_status.setBackgroundResource(R.drawable.failure4);
                }

                if (lisencePlateNo_status.equalsIgnoreCase("true")) {
                    profile_LisencePlateNo_driver_pic1.setClickable(false);
                    lisencePlateNo_photo_status.setBackgroundResource(R.drawable.success4);
                } else {
                    profile_LisencePlateNo_driver_pic1.setClickable(true);
                    lisencePlateNo_photo_status.setBackgroundResource(R.drawable.failure4);
                }


            }

        }
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
                    driver.setVehiclePhotoDocURL(storageUrl);
                    vehiclePhotoDoc.setLatitude(vehiclePhotoDoc_lat);
                    vehiclePhotoDoc.setLongitude(vehiclePhotoDoc_long);
                } else if (i == 1) {
                    //driver.setDriverLicenseDocURL(storageUrl);
                    driver.setPlateNumberDocURL(storageUrl);
                    plateNumberDoc.setLatitude(plateNumberDoc_lat);
                    plateNumberDoc.setLongitude(plateNumberDoc_long);
                }

                Log.e("Car_pohot_after change", driver.getVehiclePhotoDocURL());
                Log.e("Email", driver.getEmailID() + "");
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
        Log.d("EXIF", "Exif: " + orientation);
        Matrix matrix = new Matrix();
        if (orientation == 6) {
            matrix.postRotate(90);
            Log.d("EXIF", "Exif: " + orientation);
        } else if (orientation == 3) {
            matrix.postRotate(180);
            Log.d("EXIF", "Exif: " + orientation);
        } else if (orientation == 8) {
            matrix.postRotate(270);
            Log.d("EXIF", "Exif: " + orientation);
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


    public void unbindDrawables(View view) {//pass your parent view here
        try {
            if (view.getBackground() != null)
                view.getBackground().setCallback(null);

            if (view instanceof ImageView) {
                ImageView imageView = (ImageView) view;
                imageView.setImageBitmap(null);
            } else if (view instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) view;
                for (int i = 0; i < viewGroup.getChildCount(); i++)
                    unbindDrawables(viewGroup.getChildAt(i));

                if (!(view instanceof AdapterView))
                    viewGroup.removeAllViews();
            }
            if (carImageBitmap != null) {
                carImageBitmap.recycle();
                carImageBitmap = null;
            }
            if (licensePlateImageBitmap != null) {
                licensePlateImageBitmap.recycle();
                licensePlateImageBitmap = null;
            }
        } catch (NullPointerException npe) {
            if (!TextUtils.isEmpty(npe.getMessage())) {
                Log.e(TAG, npe.getMessage());
            }
        }
    }


    /*   @Override
       public void onBackPressed() {
           new AlertDialog.Builder(this)
                   .setTitle("Really Exit?")
                   .setMessage("Are you sure you want to exit?")
                   .setNegativeButton(android.R.string.no, null)
                   .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                       public void onClick(DialogInterface arg0, int arg1) {
                           Update_VehicleInfo.super.onBackPressed();

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
   //                                    Log.e("Delete", "Delete");

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
       }*/
    @Override
    public void onPause() {
        super.onPause();
        if ((progressDialog != null) && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    @Override
    protected void onDestroy() {
        unbindDrawables(mainlayout);
        super.onDestroy();
    }

    void update_DriverDetails()                                       // values intialise
    {
        images = driver.getImages();
        vehiclePhotoDoc = images.getVehiclePhotoDoc();
        plateNumberDoc = images.getPlateNumberDoc();
       /* all_cursor = mydb.getDriver_reg_Email(SESSION_MAIL);
        try {

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





