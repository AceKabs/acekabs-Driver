package com.acekabs.driverapp.activity;


import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.acekabs.driverapp.R;
import com.acekabs.driverapp.db.DBAdapter;
import com.acekabs.driverapp.db.SessionManager;
import com.acekabs.driverapp.entities.AceKabsDriverPreRegistration;
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class VehicleRegDetails extends Activity implements View.OnClickListener {
    private final String TAG = VehicleRegDetails.this.getClass().getSimpleName();
    View reg_header;
    ImageView back, vel_insurance_docs, car_reg_back, car_reg_front;
    TextView title;
    Button reg_btn_confirm;
    Uri outputFileUri, outputFileUri2, outputFileUri3;
    Bitmap carFrontImageBitmap;
    Bitmap carBackImageBitmap;
    Bitmap insuranceImageBitmap;
    ImageLoadingUtils utils;
    String image_path1, image_path2, image_path3;
    String[] str_list=new String[3];
    String dir;
    ArrayList<String> list_url;
    AceKabsDriverPreRegistration driver_reg;
    DBAdapter mydb;
    SessionManager msessionManager;
    String SESS_EMAIL = "";
    //ProgressDialog dialog;
    GPSTracker gps;
    private ExifInterface exifInterfaceone;
    private ExifInterface exifInterfacethree;
    private ExifInterface exifInterfacetwo;
    private int x = 0;
    private LinearLayout mainlayout;
    private ProgressDialog progressDialog;
    private Context context;
    private String file;
    private String file2;
    private String file3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_reg_details);
        context = this;
        mainlayout = (LinearLayout) findViewById(R.id.mainlayout);
        gps = new GPSTracker(getApplicationContext());
        utils = new ImageLoadingUtils(VehicleRegDetails.this);
        dir = Environment.getExternalStorageDirectory() + "/DriverApp/";
        mydb = DBAdapter.getInstance(getApplicationContext());
        msessionManager = new SessionManager(getApplicationContext());
        HashMap<String, String> user = msessionManager.getUserDetails();
        SESS_EMAIL = user.get(SessionManager.KEY_NAME);
        reg_header = findViewById(R.id.reg_header);
        back = (ImageView) reg_header.findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent li = new Intent(VehicleRegDetails.this, Personal.class);
                li.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(li);
            }
        });

        title = (TextView) reg_header.findViewById(R.id.title);
        title.setText("PERSONAL INFORMATION");
        vel_insurance_docs = (ImageView) findViewById(R.id.vel_insurance_docs);
        car_reg_back = (ImageView) findViewById(R.id.car_reg_back);
        car_reg_front = (ImageView) findViewById(R.id.car_reg_front);
        list_url = new ArrayList<String>();
        driver_reg = new AceKabsDriverPreRegistration();
        reg_btn_confirm = (Button) findViewById(R.id.reg_btn_confirm);
        reg_btn_confirm.setOnClickListener(this);
        back.setOnClickListener(this);
        vel_insurance_docs.setOnClickListener(this);
        car_reg_front.setOnClickListener(this);
        car_reg_back.setOnClickListener(this);
        car_reg_back.setEnabled(false);
        vel_insurance_docs.setEnabled(false);
    }

    public void onClick(View v) {
       if (v.getId() == R.id.reg_btn_confirm) {
            if(str_list[0]==null
                    || str_list[1]==null
                     ||   str_list[2]==null)
            {
                ShowAlert("Warning !!", "Please upload all images");
            }
            else {
                reg_btn_confirm.setEnabled(false);
                try {
                    for (int i = 0; i < str_list.length; i++) {
                        x = i;
                        final FirebaseAuth auth = FirebaseAuth.getInstance();
                        if (auth.getCurrentUser() != null) {
                            uploadDocToFirebase(Uri.parse(str_list[i]), x);
                            //Log.e("new_uri ok", outputFileUri.toString());
                        } else {
                            auth.signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    uploadDocToFirebase(Uri.parse(str_list[x]), x);
                                }
                            });

                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    reg_btn_confirm.setEnabled(true);
                }
            }

        } else if (v.getId() == R.id.car_reg_front) {

            file = dir + "car_front" + ".jpg";
            File newfile = new File(file);
            try {
                newfile.createNewFile();
            } catch (IOException e) {
            }
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                outputFileUri = FileProvider.getUriForFile(this, "com.acekabs.driverapp", newfile);
            } else {
                outputFileUri = Uri.fromFile(newfile);
            }

            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            startActivityForResult(cameraIntent, 1);
            Log.e("OutputTest", outputFileUri.toString());


        } else if (v.getId() == R.id.car_reg_back) {

            file2 = dir + "car_back" + ".jpg";
            File newfile = new File(file2);
            try {
                newfile.createNewFile();
            } catch (IOException e) {
            }

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                outputFileUri2 = FileProvider.getUriForFile(this, "com.acekabs.driverapp", newfile);
            } else {
                outputFileUri2 = Uri.fromFile(newfile);
            }

            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri2);
            startActivityForResult(cameraIntent, 2);


        } else if (v.getId() == R.id.vel_insurance_docs) {

            file3 = dir + "insurance" + ".jpg";
            File newfile = new File(file3);
            try {
                newfile.createNewFile();
            } catch (IOException e) {
            }

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                outputFileUri3 = FileProvider.getUriForFile(this, "com.acekabs.driverapp", newfile);
            } else {
                outputFileUri3 = Uri.fromFile(newfile);
            }


            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri3);
            startActivityForResult(cameraIntent, 3);


        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG,"onActivityResult");
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Log.d("CameraDemo", "Pic saved");
            try {
                carFrontImageBitmap=BitmapFactory.decodeFile(file);
                //carFrontImageBitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), Uri.parse(outputFileUri.toString()));
                car_reg_front.setImageBitmap(carFrontImageBitmap);
                image_path1 = MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), carFrontImageBitmap, "Title", null);
                if (gps.canGetLocation()) {
                    String path = UtilityMethods.getRealPathFromUri(VehicleRegDetails.this, Uri.parse(image_path1));
                    exifInterfaceone = new ExifInterface(path);

                    ImageCompressAsyncTask imageCompressAsyncTask=new ImageCompressAsyncTask(VehicleRegDetails.this,path,exifInterfaceone,path,utils);
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
                    exifInterfaceone.setAttribute("imei", UtilityMethods.getIMEI(VehicleRegDetails.this));
                    exifInterfaceone.setAttribute("UserComment", UtilityMethods.getIMEI(VehicleRegDetails.this));
                    exifInterfaceone.saveAttributes();
                    mydb.UpdateFrontcarregCoord(SESS_EMAIL, String.valueOf(gps.getLatitude()), String.valueOf(gps.getLongitude()), "true");
                }
                str_list[0]=image_path1;
                car_reg_back.setEnabled(true);
                vel_insurance_docs.setEnabled(false);
                // Login Anonymously
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (requestCode == 2 && resultCode == RESULT_OK) {
            try {
                carBackImageBitmap=BitmapFactory.decodeFile(file2);
                //carBackImageBitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), Uri.parse(outputFileUri2.toString()));
                car_reg_back.setImageBitmap(carBackImageBitmap);
                image_path2 = MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), carBackImageBitmap, "Title", null);
                if (gps.canGetLocation()) {
                    String path = UtilityMethods.getRealPathFromUri(VehicleRegDetails.this, Uri.parse(image_path2));
                    exifInterfacetwo = new ExifInterface(path);

                    ImageCompressAsyncTask imageCompressAsyncTask=new ImageCompressAsyncTask(VehicleRegDetails.this,path,exifInterfacetwo,path,utils);
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
                    exifInterfacetwo.setAttribute("imei", UtilityMethods.getIMEI(VehicleRegDetails.this));
                    exifInterfacetwo.setAttribute("UserComment", UtilityMethods.getIMEI(VehicleRegDetails.this));
                    exifInterfacetwo.saveAttributes();
                    mydb.UpdateBackcarregCoord(SESS_EMAIL, String.valueOf(gps.getLatitude()), String.valueOf(gps.getLongitude()), "true");
                }
                str_list[1]=image_path2;
                car_reg_back.setEnabled(true);
                vel_insurance_docs.setEnabled(true);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (requestCode == 3 && resultCode == RESULT_OK) {
            try {
                insuranceImageBitmap=BitmapFactory.decodeFile(file3);
                //insuranceImageBitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), Uri.parse(outputFileUri3.toString()));
                vel_insurance_docs.setImageBitmap(insuranceImageBitmap);
                image_path3 = MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), insuranceImageBitmap, "Title", null);
                if (gps.canGetLocation()) {
                    String path = UtilityMethods.getRealPathFromUri(VehicleRegDetails.this, Uri.parse(image_path3));
                    exifInterfacethree = new ExifInterface(path);


                    ImageCompressAsyncTask imageCompressAsyncTask=new ImageCompressAsyncTask(VehicleRegDetails.this,path,exifInterfacethree,path,utils);
                    imageCompressAsyncTask.setOnImageCompressed(new ImageCompressAsyncTask.OnImageCompressed() {
                        @Override
                        public void onCompressedImage(String path) {
                            image_path3= path;
                            Log.e("onCompressedImage  path=>",path);
                            str_list[2]=image_path3;
                        }
                    });
                    imageCompressAsyncTask.execute();

                    //image_path3 = compressImage(path, exifInterfacethree, path);

                    exifInterfacethree.setAttribute(ExifInterface.TAG_GPS_LATITUDE, UtilityMethods.convertToDegreeMinuteSeconds(gps.getLatitude()));
                    exifInterfacethree.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, UtilityMethods.convertToDegreeMinuteSeconds(gps.getLongitude()));
                    exifInterfacethree.setAttribute(ExifInterface.TAG_MAKE, UtilityMethods.getManufacturer());
                    exifInterfacethree.setAttribute(ExifInterface.TAG_MODEL, UtilityMethods.getDeviceName());
                    exifInterfacethree.setAttribute("imei", UtilityMethods.getIMEI(VehicleRegDetails.this));
                    exifInterfacethree.setAttribute("UserComment", UtilityMethods.getIMEI(VehicleRegDetails.this));
                    exifInterfacethree.saveAttributes();
                    mydb.UpdateVehicleInsuCoord(SESS_EMAIL, String.valueOf(gps.getLatitude()), String.valueOf(gps.getLongitude()), "true");
                }
                str_list[2]=image_path3;
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub
        if (outputFileUri != null)
            outState.putString("photopathone", String.valueOf(outputFileUri));
        if (outputFileUri2 != null)
            outState.putString("photopathtwo", String.valueOf(outputFileUri2));
        if (outputFileUri3 != null)
            outState.putString("photopaththree", String.valueOf(outputFileUri3));
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
                        carFrontImageBitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), Uri.parse(outputFileUri.toString()));
                        car_reg_front.setImageBitmap(carFrontImageBitmap);
                        image_path1 = MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), carFrontImageBitmap, "Title", null);
                        if (gps.canGetLocation()) {
                            String path = UtilityMethods.getRealPathFromUri(VehicleRegDetails.this, Uri.parse(image_path1));
                            exifInterfaceone = new ExifInterface(path);
                            image_path1 = compressImage(path, exifInterfaceone, path);
                            exifInterfaceone.setAttribute(ExifInterface.TAG_GPS_LATITUDE, UtilityMethods.convertToDegreeMinuteSeconds(gps.getLatitude()));
                            exifInterfaceone.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, UtilityMethods.convertToDegreeMinuteSeconds(gps.getLongitude()));
                            exifInterfaceone.setAttribute(ExifInterface.TAG_MAKE, UtilityMethods.getManufacturer());
                            exifInterfaceone.setAttribute(ExifInterface.TAG_MODEL, UtilityMethods.getDeviceName());
                            exifInterfaceone.setAttribute("imei", UtilityMethods.getIMEI(VehicleRegDetails.this));
                            exifInterfaceone.setAttribute("UserComment", UtilityMethods.getIMEI(VehicleRegDetails.this));
                            exifInterfaceone.saveAttributes();
                            mydb.UpdateFrontcarregCoord(SESS_EMAIL, String.valueOf(gps.getLatitude()), String.valueOf(gps.getLongitude()), "true");
                        }
                        str_list[0]=image_path1;
                        car_reg_back.setEnabled(true);
                        vel_insurance_docs.setEnabled(false);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            if (savedInstanceState.containsKey("photopathtwo")) {
                if (!TextUtils.isEmpty(savedInstanceState.getString("photopathtwo"))) {
                    outputFileUri2 = Uri.parse(savedInstanceState.getString("photopathtwo"));
                    try {
                        carBackImageBitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), Uri.parse(outputFileUri2.toString()));
                        car_reg_back.setImageBitmap(carBackImageBitmap);
                        image_path2 = MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), carBackImageBitmap, "Title", null);
                        if (gps.canGetLocation()) {
                            String path = UtilityMethods.getRealPathFromUri(VehicleRegDetails.this, Uri.parse(image_path2));
                            exifInterfacetwo = new ExifInterface(path);
                            image_path2 = compressImage(path, exifInterfacetwo, path);
                            exifInterfacetwo.setAttribute(ExifInterface.TAG_GPS_LATITUDE, UtilityMethods.convertToDegreeMinuteSeconds(gps.getLatitude()));
                            exifInterfacetwo.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, UtilityMethods.convertToDegreeMinuteSeconds(gps.getLongitude()));
                            exifInterfacetwo.setAttribute(ExifInterface.TAG_MAKE, UtilityMethods.getManufacturer());
                            exifInterfacetwo.setAttribute(ExifInterface.TAG_MODEL, UtilityMethods.getDeviceName());
                            exifInterfacetwo.setAttribute("imei", UtilityMethods.getIMEI(VehicleRegDetails.this));
                            exifInterfacetwo.setAttribute("UserComment", UtilityMethods.getIMEI(VehicleRegDetails.this));
                            exifInterfacetwo.saveAttributes();
                            mydb.UpdateBackcarregCoord(SESS_EMAIL, String.valueOf(gps.getLatitude()), String.valueOf(gps.getLongitude()), "true");
                        }
                        str_list[1]=image_path2;
                        car_reg_back.setEnabled(true);
                        vel_insurance_docs.setEnabled(true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        if (savedInstanceState.containsKey("photopaththree")) {
            if (!TextUtils.isEmpty(savedInstanceState.getString("photopaththree"))) {
                outputFileUri3 = Uri.parse(savedInstanceState.getString("photopaththree"));
                try {
                    insuranceImageBitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), Uri.parse(outputFileUri3.toString()));
                    vel_insurance_docs.setImageBitmap(insuranceImageBitmap);
                    image_path3 = MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), insuranceImageBitmap, "Title", null);
                    if (gps.canGetLocation()) {
                        String path = UtilityMethods.getRealPathFromUri(VehicleRegDetails.this, Uri.parse(image_path3));
                        exifInterfacethree = new ExifInterface(path);
                        image_path3 = compressImage(path, exifInterfacethree, path);
                        exifInterfacethree.setAttribute(ExifInterface.TAG_GPS_LATITUDE, UtilityMethods.convertToDegreeMinuteSeconds(gps.getLatitude()));
                        exifInterfacethree.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, UtilityMethods.convertToDegreeMinuteSeconds(gps.getLongitude()));
                        exifInterfacethree.setAttribute(ExifInterface.TAG_MAKE, UtilityMethods.getManufacturer());
                        exifInterfacethree.setAttribute(ExifInterface.TAG_MODEL, UtilityMethods.getDeviceName());
                        exifInterfacethree.setAttribute("imei", UtilityMethods.getIMEI(VehicleRegDetails.this));
                        exifInterfacethree.setAttribute("UserComment", UtilityMethods.getIMEI(VehicleRegDetails.this));
                        exifInterfacethree.saveAttributes();
                        mydb.UpdateVehicleInsuCoord(SESS_EMAIL, String.valueOf(gps.getLatitude()), String.valueOf(gps.getLongitude()), "true");
                    }
                    str_list[2]=image_path3;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        super.onRestoreInstanceState(savedInstanceState);
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
        new AceKabsDocumentUploadService().uploadFileFromDisk(imageUri, new IFirebaseCallback<Uri>() {
            @Override
            public void onDataReceived(Uri data) {
                if (progressDialog.isShowing() && progressDialog != null) {
                    progressDialog.dismiss();
                }
                String storageUrl = data.toString();
                list_url.add(storageUrl);
                Log.d("URL", storageUrl);
                try {
                    if (i == 0) {
                        ContentValues initialValues = new ContentValues();
                        initialValues.put("carRegistrationDocFrontURL", storageUrl);
                        Log.e(TAG,"carRegistrationDocFrontURL=>"+storageUrl);
                        mydb.updateVehicleRegistrationDetails(SESS_EMAIL, initialValues);
                    } else if (i == 1) {
                        ContentValues initialValues = new ContentValues();
                        initialValues.put("carRegistrationDocBackURL", storageUrl);
                        Log.e(TAG,"carRegistrationDocBackURL=>"+storageUrl);
                        mydb.updateVehicleRegistrationDetails(SESS_EMAIL, initialValues);
                    } else if (i == 2) {
                        ContentValues initialValues = new ContentValues();
                        initialValues.put("vehicleInsuranceDocURL", storageUrl);
                        Log.e(TAG,"vehicleInsuranceDocURL=>"+storageUrl);
                        mydb.updateVehicleRegistrationDetails(SESS_EMAIL, initialValues);
                    }

                    if (list_url.size() == 3) {
                        Intent li = new Intent(VehicleRegDetails.this, HomeScreen.class);
                        li.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(li);
                    } else {
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Exception ex) {
                if (progressDialog.isShowing() && progressDialog != null) {
                    progressDialog.dismiss();
                }
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
            if (carFrontImageBitmap != null) {
                carFrontImageBitmap.recycle();
                carFrontImageBitmap = null;
            }
            if (carBackImageBitmap != null) {
                carBackImageBitmap.recycle();
                carBackImageBitmap = null;
            }
            if (insuranceImageBitmap != null) {
                insuranceImageBitmap.recycle();
                insuranceImageBitmap = null;
            }
        } catch (NullPointerException npe) {
            Log.e(TAG, npe.getMessage());
        }

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
}
