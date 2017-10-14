package com.acekabs.driverapp.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.acekabs.driverapp.R;
import com.acekabs.driverapp.db.DBAdapter;
import com.acekabs.driverapp.db.SessionManager;
import com.acekabs.driverapp.firebase.IFirebaseCallback;
import com.acekabs.driverapp.services.AceKabsDocumentUploadService;
import com.acekabs.driverapp.services.GPSTracker;
import com.acekabs.driverapp.utils.ImageCompressAsyncTask;
import com.acekabs.driverapp.utils.ImageLoadingUtils;
import com.acekabs.driverapp.utils.UtilityMethods;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;


public class Vehicle extends Activity implements View.OnClickListener {

    private final String TAG = Vehicle.this.getClass().getSimpleName();
    EditText vehicle_edit_name, vehicle_edit_model, vehicle_edit_manu,
            vechile_edit_color, vechile_edit_noofpass, vechile_edit_manuyear, vechile_edit_number;
    Button company_btn_confirm;
    String NAME = "", MODEL = "", YEAR_MANU = "", VECHILE_NO = "", COLOR = "", VEHICLE_PHOTO = "", MANU_YEAR = "", NO_PASS = "", Lic = "";
    View vehicle_header;
    ImageView back;
    TextView title;
    ImageView vechile_edit_photo, vechile_edit_lice;
    Uri outputFileUri, outputFileUri2;
    Bitmap carImageBitmap;
    Bitmap licensePlateImageBitmap;
    int width = 1366; // 1920
    int height = 768; // 1080
    ByteArrayOutputStream outputStream;
    String image_path1, image_path2;
    String[] str_list=new String[2];
    String dir;
    ArrayList<String> list_url;
    DBAdapter mydb;
    String CarPhoto = "", Plate_Number = "", SESSION_EMAIL = "";
    SessionManager msession;
    GPSTracker gps;
    /*added code for handle image wiith exif interface*/
    ExifInterface exifInterfaceone;
    ExifInterface exifInterfacetwo;
    private ImageLoadingUtils utils;
    private String fileOne, fileTwo;
    private int x;
    private LinearLayout mainlayout;
    private String fileZero;
    private ProgressDialog progressDialog;
    private Context context;
    private Uri outputFileUri3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle);
        context = this;
        utils = new ImageLoadingUtils(Vehicle.this);
        gps = new GPSTracker(getApplicationContext());
        mydb = DBAdapter.getInstance(getApplicationContext());
        mydb.addNewColumn("driverLicensePlate_URL");
        msession = new SessionManager(getApplicationContext());
        mainlayout = (LinearLayout) findViewById(R.id.mainlayout);
        // get user data from session
        HashMap<String, String> user = msession.getUserDetails();
        SESSION_EMAIL = user.get(SessionManager.KEY_NAME);
        dir = Environment.getExternalStorageDirectory() + "/DriverApp/";
        vehicle_header = findViewById(R.id.vehicle_header);
        back = (ImageView) vehicle_header.findViewById(R.id.back);
        title = (TextView) vehicle_header.findViewById(R.id.title);
        title.setText("VEHICLE INFORMATION");
        list_url = new ArrayList<String>();
        fileZero = String.valueOf(UtilityMethods.getUrl(R.mipmap.ic_launcher));
        saveLogoToSdCard();
        vehicle_edit_name = (EditText) findViewById(R.id.vehicle_edit_name); // Name
        vehicle_edit_model = (EditText) findViewById(R.id.vehicle_edit_model); // Model
        vehicle_edit_manu = (EditText) findViewById(R.id.vehicle_edit_manu); // Manufactur
        vechile_edit_color = (EditText) findViewById(R.id.vehicle_edit_color); // color
        vechile_edit_noofpass = (EditText) findViewById(R.id.vechile_edit_no_pass); // Pass Count
        vechile_edit_photo = (ImageView) findViewById(R.id.vehicle_edit_photo);  //  Photo
        vechile_edit_lice = (ImageView) findViewById(R.id.vehicle_edit_license); // License
        vechile_edit_number = (EditText) findViewById(R.id.vechile_edit_plate_number);
        company_btn_confirm = (Button) findViewById(R.id.vechicle_btn_confirm);
        company_btn_confirm.setOnClickListener(this);
        back.setOnClickListener(this);
        vechile_edit_photo.setOnClickListener(this);
        vechile_edit_lice.setOnClickListener(this);
        vechile_edit_lice.setEnabled(false);
    }

    public void onClick(View v)
    {
        if (v.getId() == R.id.vechicle_btn_confirm)
        {
            NAME = vehicle_edit_name.getText().toString().trim();
            MODEL = vehicle_edit_model.getText().toString().trim();
            YEAR_MANU = vehicle_edit_manu.getText().toString().trim();

            NO_PASS = vechile_edit_noofpass.getText().toString().trim();
            COLOR = vechile_edit_color.getText().toString().trim();
            VECHILE_NO = vechile_edit_number.getText().toString().trim();
            //Lic= vechile_edit_lice.g
            int yearManufacturer=0;
            int thisYear= Calendar.getInstance().get(Calendar.YEAR);
            try {
                yearManufacturer= Integer.parseInt(YEAR_MANU);
            }catch (Exception ignored){}

            if (NAME.length() == 0 && MODEL.length() == 0 &&
                    YEAR_MANU.length() == 0 &&
                    VEHICLE_PHOTO.length() == 0 && NO_PASS.length() == 0
                    && COLOR.length() == 0) {
                ShowAlert("Warning !!", "Please Enter All Fields");
            } else if (NAME.length() == 0) {
                ShowAlert("Warning !!", "Please Enter Car Manufacturer");
            } else if (MODEL.length() == 0) {
                ShowAlert("Warning !!", "Please Enter Car Model");
            } else if (COLOR.length() == 0) {
                ShowAlert("Warning !!", "Please Enter Color");
            } else if (YEAR_MANU.length() == 0) {
                ShowAlert("Warning !!", "Please enter year of manufacturer");
            }
            else if(yearManufacturer>thisYear)
            {
                ShowAlert("Warning !!", "Manufacturer year can't be a future year.");
            }
            else if (NO_PASS.length() == 0) {
                ShowAlert("Warning !!", "Please enter passenger count");
            }
            else if(str_list[0]==null || str_list[1]==null)
            {
                ShowAlert("Warning !!", "Please upload car image and license plate image");
            }
            else {
                company_btn_confirm.setEnabled(false);
                try {
                    for (int i = 0; i < str_list.length; i++) {
                        x = i;
                        callUploadDocToFirebase(Uri.parse(str_list[i]), x);
                        /*final FirebaseAuth auth = FirebaseAuth.getInstance();
                        if (auth.getCurrentUser() != null) {
                            uploadDocToFirebase(Uri.parse(str_list.get(i)), x);
                        } else {
                            final FirebaseAuth auth1 = FirebaseAuth.getInstance();
                            if (auth1.getCurrentUser() != null) {
                                uploadDocToFirebase(Uri.parse(str_list.get(i)), x);
                            } else {
                                auth.signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        uploadDocToFirebase(Uri.parse(str_list.get(x)), x);
                                    }
                                });
                            }
                        }*/
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    company_btn_confirm.setEnabled(true);
                }


            }


        } else if (v.getId() == R.id.back) {
            Intent li = new Intent(Vehicle.this, OTPActivity.class);
            li.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(li);
            finish();
        } else if (v.getId() == R.id.vehicle_edit_photo) {
            fileOne = dir + "carphoto" + ".jpg";
            File newfile = new File(fileOne);
            try {
                newfile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.e(TAG,"File path 1"+newfile.getAbsolutePath());

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                outputFileUri = FileProvider.getUriForFile(this, "com.acekabs.driverapp", newfile);
            } else {
                outputFileUri = Uri.fromFile(newfile);
            }

            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            startActivityForResult(cameraIntent, 0);
//            Log.e("Output0", outputFileUri.toString());

        } else if (v.getId() == R.id.vehicle_edit_license) {

            fileTwo = dir + "licenceplatephoto" + ".jpg";
            File newfile = new File(fileTwo);
            try {
                newfile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.e(TAG,"File path 2"+newfile.getAbsolutePath());
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                outputFileUri2 = FileProvider.getUriForFile(this, "com.acekabs.driverapp", newfile);
            } else {
                outputFileUri2 = Uri.fromFile(newfile);
            }
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri2);
            startActivityForResult(cameraIntent, 1);

        }
    }

    private void saveLogoToSdCard() {
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        try {
            File file = new File(dir, "logo.jpg");
            FileOutputStream outStream = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            outStream.flush();
            outStream.close();
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                outputFileUri3 = FileProvider.getUriForFile(this, "com.acekabs.driverapp", file);
            } else {
                outputFileUri3 = Uri.fromFile(file);
            }

            Bitmap logoImageBitMap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), Uri.parse(outputFileUri3.toString()));
            String image_path3 = MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), logoImageBitMap, "Title", null);
           // str_list.add(image_path3);
            if (!TextUtils.isEmpty(image_path3)) {
                Log.e("Logo Image Path", image_path3);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
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


    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == RESULT_OK) {
            Log.d(TAG + "CameraDemo", "Pic saved");
            try {
                carImageBitmap=BitmapFactory.decodeFile(fileOne);
               // carImageBitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), Uri.parse(outputFileUri.toString()));
                vechile_edit_photo.setImageBitmap(carImageBitmap);
                width = carImageBitmap.getWidth();
                height = carImageBitmap.getHeight();
                image_path1 = MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), carImageBitmap, "Title", null);
//                Log.e(TAG + "image_path1", image_path1);
                if (gps.canGetLocation()) {
                    String path = UtilityMethods.getRealPathFromUri(Vehicle.this, Uri.parse(image_path1));
                    Log.e(TAG,"Media Bitmap 1 path=>"+path);
                    exifInterfaceone = new ExifInterface(path);

                    ImageCompressAsyncTask imageCompressAsyncTask=new ImageCompressAsyncTask(Vehicle.this,path,exifInterfaceone,path,utils);
                    imageCompressAsyncTask.setOnImageCompressed(new ImageCompressAsyncTask.OnImageCompressed() {
                        @Override
                        public void onCompressedImage(String path) {
                            image_path1= path;
                            Log.e("onCompressedImage  path=>",path);
                            str_list[0]=image_path1;
                        }
                    });
                    imageCompressAsyncTask.execute();

                   // image_path1 = compressImage(path, exifInterfaceone, path);
                    exifInterfaceone.setAttribute(ExifInterface.TAG_GPS_LATITUDE, UtilityMethods.convertToDegreeMinuteSeconds(gps.getLatitude()));
                    exifInterfaceone.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, UtilityMethods.convertToDegreeMinuteSeconds(gps.getLongitude()));
                    exifInterfaceone.setAttribute(ExifInterface.TAG_MAKE, UtilityMethods.getManufacturer());
                    exifInterfaceone.setAttribute(ExifInterface.TAG_MODEL, UtilityMethods.getDeviceName());
                    exifInterfaceone.setAttribute("imei", UtilityMethods.getIMEI(Vehicle.this));
                    exifInterfaceone.setAttribute("UserComment", UtilityMethods.getIMEI(Vehicle.this));
                    exifInterfaceone.saveAttributes();
                    mydb.UpdateVehicleCoord(SESSION_EMAIL, String.valueOf(gps.getLatitude()), String.valueOf(gps.getLongitude()), "true");
                }
                str_list[0]=image_path1;
                vechile_edit_lice.setEnabled(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == 1 && resultCode == RESULT_OK) {
            try {
                licensePlateImageBitmap=BitmapFactory.decodeFile(fileTwo);
                //licensePlateImageBitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), Uri.parse(outputFileUri2.toString()));
                vechile_edit_lice.setImageBitmap(licensePlateImageBitmap);
                width = licensePlateImageBitmap.getWidth();
                height = licensePlateImageBitmap.getHeight();
                image_path2 = MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), licensePlateImageBitmap, "Title", null);
//                Log.e(TAG + "image_path2", image_path2);
                if (gps.canGetLocation()) {
                    String path = UtilityMethods.getRealPathFromUri(Vehicle.this, Uri.parse(image_path2));
                    Log.e(TAG,"Media Bitmap 2 path=>"+path);
                    exifInterfacetwo = new ExifInterface(path);

                    ImageCompressAsyncTask imageCompressAsyncTask=new ImageCompressAsyncTask(Vehicle.this,path,exifInterfacetwo,path,utils);
                    imageCompressAsyncTask.setOnImageCompressed(new ImageCompressAsyncTask.OnImageCompressed() {
                        @Override
                        public void onCompressedImage(String path) {
                            image_path2= path;
                            Log.e("onCompressedImage  path=>",path);
                            str_list[1]=image_path2;
                        }
                    });
                    imageCompressAsyncTask.execute();

                    //image_path2 = compressImage(path, exifInterfacetwo, path);

                    exifInterfacetwo.setAttribute(ExifInterface.TAG_GPS_LATITUDE, UtilityMethods.convertToDegreeMinuteSeconds(gps.getLatitude()));
                    exifInterfacetwo.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, UtilityMethods.convertToDegreeMinuteSeconds(gps.getLongitude()));
                    exifInterfacetwo.setAttribute(ExifInterface.TAG_MAKE, UtilityMethods.getManufacturer());
                    exifInterfacetwo.setAttribute(ExifInterface.TAG_MODEL, UtilityMethods.getDeviceName());
                    exifInterfacetwo.setAttribute("imei", UtilityMethods.getIMEI(Vehicle.this));
                    exifInterfacetwo.setAttribute("UserComment", UtilityMethods.getIMEI(Vehicle.this));
                    exifInterfacetwo.saveAttributes();
                    mydb.UpdatelicenseCoord(SESSION_EMAIL, String.valueOf(gps.getLatitude()), String.valueOf(gps.getLongitude()), "true");
                }
                str_list[1]=image_path2;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        // TODO Auto-generated method stub
        if (outputFileUri != null)
            outState.putString("photopathone", String.valueOf(outputFileUri));
        if (outputFileUri2 != null)
            outState.putString("photopathtwo", String.valueOf(outputFileUri2));
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("photopathone")) {
                if (!TextUtils.isEmpty(savedInstanceState.getString("photopathone"))) {
                    outputFileUri = Uri.parse(savedInstanceState.getString("photopathone"));
                    try {
                        carImageBitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), Uri.parse(outputFileUri.toString()));
                        vechile_edit_photo.setImageBitmap(carImageBitmap);
                        width = carImageBitmap.getWidth();
                        height = carImageBitmap.getHeight();
                        image_path1 = MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), carImageBitmap, "Title", null);
                        if (gps.canGetLocation()) {
                            String path = UtilityMethods.getRealPathFromUri(Vehicle.this, Uri.parse(image_path1));
                            exifInterfaceone = new ExifInterface(path);
                            image_path1 = compressImage(path, exifInterfaceone, path);
                            exifInterfaceone.setAttribute(ExifInterface.TAG_GPS_LATITUDE, UtilityMethods.convertToDegreeMinuteSeconds(gps.getLatitude()));
                            exifInterfaceone.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, UtilityMethods.convertToDegreeMinuteSeconds(gps.getLongitude()));
                            exifInterfaceone.setAttribute(ExifInterface.TAG_MAKE, UtilityMethods.getManufacturer());
                            exifInterfaceone.setAttribute(ExifInterface.TAG_MODEL, UtilityMethods.getDeviceName());
                            exifInterfaceone.setAttribute("imei", UtilityMethods.getIMEI(Vehicle.this));
                            exifInterfaceone.setAttribute("UserComment", UtilityMethods.getIMEI(Vehicle.this));
                            exifInterfaceone.saveAttributes();
                            mydb.UpdateVehicleCoord(SESSION_EMAIL, String.valueOf(gps.getLatitude()), String.valueOf(gps.getLongitude()), "true");
                        }
                        str_list[0]=image_path1;
                        vechile_edit_lice.setEnabled(true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            if (savedInstanceState.containsKey("photopathtwo")) {
                if (!TextUtils.isEmpty(savedInstanceState.getString("photopathtwo"))) {
                    outputFileUri2 = Uri.parse(savedInstanceState.getString("photopathtwo"));
                    try {
                        licensePlateImageBitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), Uri.parse(outputFileUri2.toString()));
                        vechile_edit_lice.setImageBitmap(licensePlateImageBitmap);
                        width = licensePlateImageBitmap.getWidth();
                        height = licensePlateImageBitmap.getHeight();
                        image_path2 = MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), licensePlateImageBitmap, "Title", null);
                        if (gps.canGetLocation()) {
                            String path = UtilityMethods.getRealPathFromUri(Vehicle.this, Uri.parse(image_path2));
                            exifInterfacetwo = new ExifInterface(path);
                            image_path2 = compressImage(path, exifInterfacetwo, path);
                            exifInterfacetwo.setAttribute(ExifInterface.TAG_GPS_LATITUDE, UtilityMethods.convertToDegreeMinuteSeconds(gps.getLatitude()));
                            exifInterfacetwo.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, UtilityMethods.convertToDegreeMinuteSeconds(gps.getLongitude()));
                            exifInterfacetwo.setAttribute(ExifInterface.TAG_MAKE, UtilityMethods.getManufacturer());
                            exifInterfacetwo.setAttribute(ExifInterface.TAG_MODEL, UtilityMethods.getDeviceName());
                            exifInterfacetwo.setAttribute("imei", UtilityMethods.getIMEI(Vehicle.this));
                            exifInterfacetwo.setAttribute("UserComment", UtilityMethods.getIMEI(Vehicle.this));
                            exifInterfacetwo.saveAttributes();
                            mydb.UpdatelicenseCoord(SESSION_EMAIL, String.valueOf(gps.getLatitude()), String.valueOf(gps.getLongitude()), "true");
                        }
                        str_list[1]=image_path2;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        super.onRestoreInstanceState(savedInstanceState);
    }


    private void callUploadDocToFirebase(Uri imageUri, final int i) {
        x = i;
        final FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            uploadDocToFirebase(Uri.parse(str_list[i]), i);
            Log.e(TAG,"if 1...");
        } else {
            Log.e(TAG,"else 1...");
            final FirebaseAuth auth1 = FirebaseAuth.getInstance();
            if (auth1.getCurrentUser() != null) {
                uploadDocToFirebase(Uri.parse(str_list[i]), i);
                Log.e(TAG,"else if...");
            } else {
                Log.e(TAG,"else else...");
                auth.signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        uploadDocToFirebase(Uri.parse(str_list[i]), i);
                    }
                });
            }
        }
    }
    private void uploadDocToFirebase(Uri imageUri, final int i) {
        if(progressDialog!=null)
        {
            if(progressDialog.isShowing())
            {
                progressDialog.dismiss();
                progressDialog=null;
            }
        }
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Uploading ....");
        progressDialog.setCancelable(false);
        progressDialog.show();
        Log.e(TAG,"progressDialog....show.......!!");
        new AceKabsDocumentUploadService().uploadFileFromDisk(imageUri, new IFirebaseCallback<Uri>() {
            @Override
            public void onDataReceived(Uri data) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                    Log.e(TAG,"progressDialog....dismiss.......!!");
                }
                String storageUrl = data.toString();
                list_url.add(storageUrl);
                try {
                    if (i == 0) {
                        ContentValues initialValues = new ContentValues();
                        initialValues.put("car_manufacture", NAME);
                        initialValues.put("car_model", MODEL);
                        initialValues.put("car_color", COLOR);
                        initialValues.put("year_of_manufacturer", YEAR_MANU);
                        initialValues.put("no_of_pass", NO_PASS);
                        initialValues.put("car_photo_url", storageUrl);
                        initialValues.put("license_plate_no", VECHILE_NO);
                        mydb.updateVehicleRegistrationDetails(SESSION_EMAIL, initialValues);
                    } else if (i == 1) {
                        ContentValues initialValues = new ContentValues();
                        initialValues.put("car_manufacture", NAME);
                        initialValues.put("car_model", MODEL);
                        initialValues.put("car_color", COLOR);
                        initialValues.put("year_of_manufacturer", YEAR_MANU);
                        initialValues.put("no_of_pass", NO_PASS);
                        initialValues.put("driverLicensePlate_URL", storageUrl);
                        initialValues.put("license_plate_no", VECHILE_NO);
                        mydb.updateVehicleRegistrationDetails(SESSION_EMAIL, initialValues);

                       /* Intent li = new Intent(Vehicle.this, Personal.class);
                        li.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(li);*/
                    }
                    if (list_url.size() >= 2) {
                        Intent li = new Intent(Vehicle.this, Personal.class);
                        li.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(li);
                        finish();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Exception ex) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                if (!TextUtils.isEmpty(ex.getMessage())) {
                    Log.e("Upload Error", ex.getMessage());
                }
            }
        });
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
            Log.e(TAG, npe.getMessage());
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


    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Really Exit?")
                .setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        Vehicle.super.onBackPressed();

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
    }


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
}