package com.acekabs.driverapp.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog;
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
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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

import org.joda.time.LocalDate;
import org.joda.time.Years;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class Personal extends Activity implements View.OnClickListener {

    static final int DATE_DIALOG_ID = 0;
    private final String TAG = Personal.this.getClass().getSimpleName();
    EditText driver_dob;
    Calendar calendar;
    View personal_header;
    ImageView back, personal_driver_pic1, personal_driver_pic2, personal_driver_pic3, personal_driver_pic4,
            personal_driver_pic5, personal_driver_pic6;
    TextView title;
    Button personal_btn_confirm;
    Uri outputFileUri, outputFileUri2, outputFileUri3, outputFileUri4, outputFileUri5, outputFileUri6;
    Bitmap driverPhotoImageBitmap;
    Bitmap licensePhotoImageBitmap;
    Bitmap qatarIdFrontImageBitmap;
    Bitmap qatarIdBackImageBitmap;
    Bitmap passportIdFrontImageBitmap;
    Bitmap passportIdBackImageBitmap;
    int width = 1366; // 1920
    int height = 768; // 1080
    ImageLoadingUtils utils;
    String image_path1, image_path2, image_path3, image_path4, image_path5, image_path6;
    String[] str_list=new String[6];
    String dir;
    ArrayList<String> list_url= new ArrayList<String>();;
    AceKabsDriverPreRegistration driver_reg;
    ExifInterface exifInterfaceone, exifInterfacetwo, exifInterfacethree, exifInterfacefour, exifInterfacefive, exifInterfacesix;
    GPSTracker gps;
    double latitude, longitude;
    DBAdapter mydb;
    SessionManager msessionManager;
    String SESSION_EMAIL = "";
    // DatePicker
    private int x;
    private Calendar cal = Calendar.getInstance(Locale.ENGLISH);
    private int year = cal.get(Calendar.YEAR);
    private int month = cal.get(Calendar.MONTH);
    private int day = cal.get(Calendar.DATE);
    private DatePickerDialog.OnDateSetListener datePickerListener
            = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            year = selectedYear;
            month = selectedMonth;
            day = selectedDay;

            calendar=Calendar.getInstance();
            calendar.set(Calendar.YEAR,year);
            calendar.set(Calendar.MONTH,month);
            calendar.set(Calendar.DAY_OF_MONTH,day);

            Calendar today=Calendar.getInstance();
            today.set(Calendar.HOUR_OF_DAY,23);
            today.set(Calendar.MINUTE,59);

            LocalDate birthdate=new LocalDate(year,month+1,day);
            LocalDate now = new LocalDate();
            Years age = Years.yearsBetween(birthdate, now);
            if(calendar.before(today))
            {
                if(age.getYears()>=21)
                {
                    driver_dob.setText(new StringBuilder().append(month + 1)
                            .append("-").append(day).append("-").append(year)
                            .append(" "));
                }
                else {
                    Toast.makeText(getApplicationContext(), "You must be 21 years old or above to register.", Toast.LENGTH_SHORT).show();
                }

            }
            else {
                Toast.makeText(getApplicationContext(), "Date of Birth can't be a future date.", Toast.LENGTH_SHORT).show();
            }

            // set selected date into textview


            // set selected date into datepicker also
            // dpResult.init(year, month, day, null);

        }
    };
    private RelativeLayout mainlayout;
    private ProgressDialog progressDialog;
    private Context context;
    private String file5;
    private String file4;
    private String file6;
    private String file3;
    private String file2;
    private String file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_screen);
        context = this;
        mainlayout = (RelativeLayout) findViewById(R.id.personal_lay1);
        gps = new GPSTracker(getApplicationContext());
        mydb = DBAdapter.getInstance(getApplicationContext());
        msessionManager = new SessionManager(getApplicationContext());
        utils = new ImageLoadingUtils(Personal.this);
        HashMap<String, String> user = msessionManager.getUserDetails();
        SESSION_EMAIL = user.get(SessionManager.KEY_NAME);
        Log.e("Session mmmmmmaillll", SESSION_EMAIL);
        dir = Environment.getExternalStorageDirectory() + "/DriverApp/";

        driver_reg = new AceKabsDriverPreRegistration();
        personal_header = findViewById(R.id.per_header);
        back = (ImageView) personal_header.findViewById(R.id.back);
        title = (TextView) personal_header.findViewById(R.id.title);
        title.setText("PERSONAL INFORMATION");
        driver_dob = (EditText) findViewById(R.id.personal_edit_dob);
        personal_driver_pic1 = (ImageView) findViewById(R.id.personal_driver_pic1);
        personal_driver_pic2 = (ImageView) findViewById(R.id.personal_driver_pic2);
        personal_driver_pic3 = (ImageView) findViewById(R.id.personal_driver_pic3);
        personal_driver_pic4 = (ImageView) findViewById(R.id.personal_driver_pic4);
        personal_driver_pic5 = (ImageView) findViewById(R.id.personal_driver_pic5);
        personal_driver_pic6 = (ImageView) findViewById(R.id.personal_driver_pic6);
        personal_btn_confirm = (Button) findViewById(R.id.personal_submit);
        back.setOnClickListener(this);
        personal_btn_confirm.setOnClickListener(this);
        personal_driver_pic1.setOnClickListener(this);
        personal_driver_pic2.setOnClickListener(this);
        personal_driver_pic3.setOnClickListener(this);
        personal_driver_pic4.setOnClickListener(this);
        personal_driver_pic5.setOnClickListener(this);
        personal_driver_pic6.setOnClickListener(this);

        /*added code  for enable all image button to upload images sequentaly*/
        personal_driver_pic2.setEnabled(false);
        personal_driver_pic3.setEnabled(false);
        personal_driver_pic4.setEnabled(false);
        personal_driver_pic5.setEnabled(false);
        personal_driver_pic6.setEnabled(false);

        driver_dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(DATE_DIALOG_ID);
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

    public void onClick(View v) {
        if (v.getId() == R.id.back) {
            Intent li = new Intent(Personal.this, Vehicle.class);
            li.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(li);

        } else if (v.getId() == R.id.personal_submit) {
            Log.e(TAG,"Images=>>>"+str_list[0]+","+str_list[1]+","+str_list[2]+","+str_list[3]+","+str_list[4]+","+str_list[5]);
            if (driver_dob.getText().toString().length() == 0) {
                Toast.makeText(getApplicationContext(), "Please enter DOB", Toast.LENGTH_SHORT).show();
            }
            else if(str_list[0]==null
                    ||str_list[1]==null
                    ||str_list[2]==null
                    ||str_list[3]==null
                    ||str_list[4]==null
                    ||str_list[5]==null)
            {
                ShowAlert("Warning !!", "Please upload all images");
            }
            else {
                personal_btn_confirm.setEnabled(false);
                try {
                    for (int i = 0; i < str_list.length; i++) {
                        x = i;
                        final FirebaseAuth auth = FirebaseAuth.getInstance();
                        if (auth.getCurrentUser() != null) {
                            uploadDocToFirebase(Uri.parse(str_list[i]), x);
                           // Log.e("Uploading URL", outputFileUri.toString());
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
                    personal_btn_confirm.setEnabled(true);
                }
            }


        } else if (v.getId() == R.id.personal_driver_pic1) {

            file = dir + "driverphoto" + ".jpg";
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
            startActivityForResult(cameraIntent, 0);

            Log.e("Output0", outputFileUri.toString());

        } else if (v.getId() == R.id.personal_driver_pic2) {
            file2 = dir + "driverlicenseno" + ".jpg";
            File newfile2 = new File(file2);
            try {
                newfile2.createNewFile();
            } catch (IOException e) {
            }

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                outputFileUri2 = FileProvider.getUriForFile(this, "com.acekabs.driverapp", newfile2);
            } else {
                outputFileUri2 = Uri.fromFile(newfile2);
            }

            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri2);
            startActivityForResult(cameraIntent, 1);
            Log.e("Output1", outputFileUri.toString());

        } else if (v.getId() == R.id.personal_driver_pic3) {

            file3 = dir + "qataridfront" + ".jpg";
            File newfile3 = new File(file3);
            try {
                newfile3.createNewFile();
            } catch (IOException e) {
            }


            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                outputFileUri3 = FileProvider.getUriForFile(this, "com.acekabs.driverapp", newfile3);
            } else {
                outputFileUri3 = Uri.fromFile(newfile3);
            }
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri3);
            startActivityForResult(cameraIntent, 2);
            Log.e("Output2", outputFileUri.toString());

        } else if (v.getId() == R.id.personal_driver_pic4) {

            file4 = dir + "qataridback" + ".jpg";
            File newfile4 = new File(file4);
            try {
                newfile4.createNewFile();
            } catch (IOException e) {
            }

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                outputFileUri4 = FileProvider.getUriForFile(this, "com.acekabs.driverapp", newfile4);
            } else {
                outputFileUri4 = Uri.fromFile(newfile4);
            }


            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri4);
            startActivityForResult(cameraIntent, 3);
            Log.e("Output3", outputFileUri.toString());

        } else if (v.getId() == R.id.personal_driver_pic5) {

            file5 = dir + "passportidfront" + ".jpg";
            File newfile5 = new File(file5);
            try {
                newfile5.createNewFile();
            } catch (IOException e) {
            }

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                outputFileUri5 = FileProvider.getUriForFile(this, "com.acekabs.driverapp", newfile5);
            } else {
                outputFileUri5 = Uri.fromFile(newfile5);
            }

            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri5);
            startActivityForResult(cameraIntent, 4);
            Log.e("Output4", outputFileUri.toString());

        } else if (v.getId() == R.id.personal_driver_pic6) {
            file6 = dir + "passportidback" + ".jpg";
            File newfile5 = new File(file6);
            try {
                newfile5.createNewFile();
            } catch (IOException e) {
            }
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                outputFileUri6 = FileProvider.getUriForFile(this, "com.acekabs.driverapp", newfile5);
            } else {
                outputFileUri6 = Uri.fromFile(newfile5);
            }

            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri6);
            startActivityForResult(cameraIntent, 5);
            Log.e("Output5", outputFileUri6.toString());
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == RESULT_OK) {
            Log.d("CameraDemo", "Pic saved");
            try {
                //driverPhotoImageBitmap=BitmapFactory.decodeFile(file);
                driverPhotoImageBitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), Uri.parse(outputFileUri.toString()));

                personal_driver_pic1.setImageBitmap(driverPhotoImageBitmap);
                width = driverPhotoImageBitmap.getWidth();
                height = driverPhotoImageBitmap.getHeight();
                image_path1 = MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), driverPhotoImageBitmap, "Title", null);
                Log.e("image_path1", image_path1);
                if (gps.canGetLocation()) {
                    String path = UtilityMethods.getRealPathFromUri(Personal.this, Uri.parse(image_path1));
                    exifInterfaceone = new ExifInterface(path);

                    ImageCompressAsyncTask imageCompressAsyncTask=new ImageCompressAsyncTask(Personal.this,path,exifInterfaceone,path,utils);
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
                    exifInterfaceone.setAttribute("imei", UtilityMethods.getIMEI(Personal.this));
                    exifInterfaceone.setAttribute("UserComment", UtilityMethods.getIMEI(Personal.this));
                    exifInterfaceone.saveAttributes();
                    mydb.UpdateDriverPICCoord(SESSION_EMAIL, String.valueOf(gps.getLatitude()), String.valueOf(gps.getLongitude()), "true");
                }
                str_list[0]=image_path1;
                personal_driver_pic2.setEnabled(true);
                personal_driver_pic3.setEnabled(false);
                personal_driver_pic4.setEnabled(false);
                personal_driver_pic5.setEnabled(false);
                personal_driver_pic6.setEnabled(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == 1 && resultCode == RESULT_OK) {
            try {
                //licensePhotoImageBitmap=BitmapFactory.decodeFile(file2);
                licensePhotoImageBitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), Uri.parse(outputFileUri2.toString()));
                personal_driver_pic2.setImageBitmap(licensePhotoImageBitmap);
                width = licensePhotoImageBitmap.getWidth();
                height = licensePhotoImageBitmap.getHeight();
                image_path2 = MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), licensePhotoImageBitmap, "Title", null);
                Log.e("image_path2", image_path2);
                if (gps.canGetLocation()) {
                    String path = UtilityMethods.getRealPathFromUri(Personal.this, Uri.parse(image_path2));
                    exifInterfacetwo = new ExifInterface(path);

                    ImageCompressAsyncTask imageCompressAsyncTask=new ImageCompressAsyncTask(Personal.this,path,exifInterfacetwo,path,utils);
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
                    exifInterfacetwo.setAttribute("imei", UtilityMethods.getIMEI(Personal.this));
                    exifInterfacetwo.setAttribute("UserComment", UtilityMethods.getIMEI(Personal.this));
                    exifInterfacetwo.saveAttributes();
                    mydb.UpdatelicenseNOCoord(SESSION_EMAIL, String.valueOf(gps.getLatitude()), String.valueOf(gps.getLongitude()), "true");
                }
                str_list[1]=image_path2;
                personal_driver_pic2.setEnabled(true);
                personal_driver_pic3.setEnabled(true);
                personal_driver_pic4.setEnabled(false);
                personal_driver_pic5.setEnabled(false);
                personal_driver_pic6.setEnabled(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == 2 && resultCode == RESULT_OK) {
            try {
                //qatarIdFrontImageBitmap=BitmapFactory.decodeFile(file3);
                qatarIdFrontImageBitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), Uri.parse(outputFileUri3.toString()));
                personal_driver_pic3.setImageBitmap(qatarIdFrontImageBitmap);
                width = qatarIdFrontImageBitmap.getWidth();
                height = qatarIdFrontImageBitmap.getHeight();
                image_path3 = MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), qatarIdFrontImageBitmap, "Title", null);
                Log.e("image_path3", image_path3);
                if (gps.canGetLocation()) {
                    String path = UtilityMethods.getRealPathFromUri(Personal.this, Uri.parse(image_path3));
                    exifInterfacethree = new ExifInterface(path);

                    ImageCompressAsyncTask imageCompressAsyncTask=new ImageCompressAsyncTask(Personal.this,path,exifInterfacethree,path,utils);
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
                    exifInterfacethree.setAttribute("imei", UtilityMethods.getIMEI(Personal.this));
                    exifInterfacethree.setAttribute("UserComment", UtilityMethods.getIMEI(Personal.this));
                    exifInterfacethree.saveAttributes();
                    mydb.UpdateIDFrontCoord(SESSION_EMAIL, String.valueOf(gps.getLatitude()), String.valueOf(gps.getLongitude()), "true");
                }
                str_list[2]=image_path3;
                personal_driver_pic2.setEnabled(true);
                personal_driver_pic3.setEnabled(true);
                personal_driver_pic4.setEnabled(true);
                personal_driver_pic5.setEnabled(false);
                personal_driver_pic6.setEnabled(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == 3 && resultCode == RESULT_OK) {
            try {
                //qatarIdBackImageBitmap=BitmapFactory.decodeFile(file4);
               qatarIdBackImageBitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), Uri.parse(outputFileUri4.toString()));
                personal_driver_pic4.setImageBitmap(qatarIdBackImageBitmap);
                width = qatarIdBackImageBitmap.getWidth();
                height = qatarIdBackImageBitmap.getHeight();
                image_path4 = MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), qatarIdBackImageBitmap, "Title", null);
                Log.e("image_path4", image_path4);
                if (gps.canGetLocation()) {
                    String path = UtilityMethods.getRealPathFromUri(Personal.this, Uri.parse(image_path4));
                    exifInterfacefour = new ExifInterface(path);

                    ImageCompressAsyncTask imageCompressAsyncTask=new ImageCompressAsyncTask(Personal.this,path,exifInterfacefour,path,utils);
                    imageCompressAsyncTask.setOnImageCompressed(new ImageCompressAsyncTask.OnImageCompressed() {
                        @Override
                        public void onCompressedImage(String path) {
                            image_path4= path;
                            Log.e("onCompressedImage  path=>",path);
                            str_list[3]=image_path4;
                        }
                    });
                    imageCompressAsyncTask.execute();

                    //image_path4 = compressImage(path, exifInterfacefour, path);

                    exifInterfacefour.setAttribute(ExifInterface.TAG_GPS_LATITUDE, UtilityMethods.convertToDegreeMinuteSeconds(gps.getLatitude()));
                    exifInterfacefour.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, UtilityMethods.convertToDegreeMinuteSeconds(gps.getLongitude()));
                    exifInterfacefour.setAttribute(ExifInterface.TAG_MAKE, UtilityMethods.getManufacturer());
                    exifInterfacefour.setAttribute(ExifInterface.TAG_MODEL, UtilityMethods.getDeviceName());
                    exifInterfacefour.setAttribute("imei", UtilityMethods.getIMEI(Personal.this));
                    exifInterfacefour.setAttribute("UserComment", UtilityMethods.getIMEI(Personal.this));
                    exifInterfacefour.saveAttributes();
                    mydb.UpdateIDBackCoord(SESSION_EMAIL, String.valueOf(gps.getLatitude()), String.valueOf(gps.getLongitude()), "true");
                }
                str_list[3]=image_path4;
                personal_driver_pic2.setEnabled(true);
                personal_driver_pic3.setEnabled(true);
                personal_driver_pic4.setEnabled(true);
                personal_driver_pic5.setEnabled(true);
                personal_driver_pic6.setEnabled(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == 4 && resultCode == RESULT_OK) {
            try {
               // passportIdFrontImageBitmap=BitmapFactory.decodeFile(file5);
                passportIdFrontImageBitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), Uri.parse(outputFileUri5.toString()));
                personal_driver_pic5.setImageBitmap(passportIdFrontImageBitmap);
                width = passportIdFrontImageBitmap.getWidth();
                height = passportIdFrontImageBitmap.getHeight();
                image_path5 = MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), passportIdFrontImageBitmap, "Title", null);
                Log.e("image_path5", image_path5);
                if (gps.canGetLocation()) {
                    String path = UtilityMethods.getRealPathFromUri(Personal.this, Uri.parse(image_path5));
                    exifInterfacefive = new ExifInterface(path);


                    ImageCompressAsyncTask imageCompressAsyncTask=new ImageCompressAsyncTask(Personal.this,path,exifInterfacefive,path,utils);
                    imageCompressAsyncTask.setOnImageCompressed(new ImageCompressAsyncTask.OnImageCompressed() {
                        @Override
                        public void onCompressedImage(String path) {
                            image_path5= path;
                            Log.e("onCompressedImage  path=>",path);
                            str_list[4]=image_path5;
                        }
                    });
                    imageCompressAsyncTask.execute();

                    //image_path5 = compressImage(path, exifInterfacefive, path);

                    exifInterfacefive.setAttribute(ExifInterface.TAG_GPS_LATITUDE, UtilityMethods.convertToDegreeMinuteSeconds(gps.getLatitude()));
                    exifInterfacefive.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, UtilityMethods.convertToDegreeMinuteSeconds(gps.getLongitude()));
                    exifInterfacefive.setAttribute(ExifInterface.TAG_MAKE, UtilityMethods.getManufacturer());
                    exifInterfacefive.setAttribute(ExifInterface.TAG_MODEL, UtilityMethods.getDeviceName());
                    exifInterfacefive.setAttribute("imei", UtilityMethods.getIMEI(Personal.this));
                    exifInterfacefive.setAttribute("UserComment", UtilityMethods.getIMEI(Personal.this));
                    exifInterfacefive.saveAttributes();
                    mydb.UpdatePassFrontCoord(SESSION_EMAIL, String.valueOf(gps.getLatitude()), String.valueOf(gps.getLongitude()), "true");
                }
                str_list[4]=image_path5;
                personal_driver_pic2.setEnabled(true);
                personal_driver_pic3.setEnabled(true);
                personal_driver_pic4.setEnabled(true);
                personal_driver_pic5.setEnabled(true);
                personal_driver_pic6.setEnabled(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == 5 && resultCode == RESULT_OK) {
            try {
//                passportIdBackImageBitmap=BitmapFactory.decodeFile(file6);
                passportIdBackImageBitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), Uri.parse(outputFileUri6.toString()));
                personal_driver_pic6.setImageBitmap(passportIdBackImageBitmap);
                width = passportIdBackImageBitmap.getWidth();
                height = passportIdBackImageBitmap.getHeight();
                image_path6 = MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), passportIdBackImageBitmap, "Title", null);
                Log.e("image_path6", image_path6);
                if (gps.canGetLocation()) {
                    String path = UtilityMethods.getRealPathFromUri(Personal.this, Uri.parse(image_path6));
                    exifInterfacesix = new ExifInterface(path);

                    ImageCompressAsyncTask imageCompressAsyncTask=new ImageCompressAsyncTask(Personal.this,path,exifInterfacesix,path,utils);
                    imageCompressAsyncTask.setOnImageCompressed(new ImageCompressAsyncTask.OnImageCompressed() {
                        @Override
                        public void onCompressedImage(String path) {
                            image_path6= path;
                            Log.e("onCompressedImage  path=>",path);
                            str_list[5]=image_path6;
                        }
                    });
                    imageCompressAsyncTask.execute();

                    //image_path6 = compressImage(path, exifInterfacesix, path);

                    exifInterfacesix.setAttribute(ExifInterface.TAG_GPS_LATITUDE, UtilityMethods.convertToDegreeMinuteSeconds(gps.getLatitude()));
                    exifInterfacesix.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, UtilityMethods.convertToDegreeMinuteSeconds(gps.getLongitude()));
                    exifInterfacesix.setAttribute(ExifInterface.TAG_MAKE, UtilityMethods.getManufacturer());
                    exifInterfacesix.setAttribute(ExifInterface.TAG_MODEL, UtilityMethods.getDeviceName());
                    exifInterfacesix.setAttribute("imei", UtilityMethods.getIMEI(Personal.this));
                    exifInterfacesix.setAttribute("UserComment", UtilityMethods.getIMEI(Personal.this));
                    exifInterfacesix.saveAttributes();
                    mydb.UpdatePassBackCoord(SESSION_EMAIL, String.valueOf(gps.getLatitude()), String.valueOf(gps.getLongitude()), "true");
                }
                str_list[5]=image_path6;
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
        if (outputFileUri4 != null)
            outState.putString("photopathfour", String.valueOf(outputFileUri4));
        if (outputFileUri5 != null)
            outState.putString("photopatfive", String.valueOf(outputFileUri5));
        if (outputFileUri6 != null)
            outState.putString("photopathsix", String.valueOf(outputFileUri6));
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
                        driverPhotoImageBitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), Uri.parse(outputFileUri.toString()));
                        personal_driver_pic1.setImageBitmap(driverPhotoImageBitmap);
                        width = driverPhotoImageBitmap.getWidth();
                        height = driverPhotoImageBitmap.getHeight();
                        image_path1 = MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), driverPhotoImageBitmap, "Title", null);
                        Log.e("image_path1", image_path1);
                        if (gps.canGetLocation()) {
                            String path = UtilityMethods.getRealPathFromUri(Personal.this, Uri.parse(image_path1));
                            exifInterfaceone = new ExifInterface(path);
                            image_path1 = compressImage(path, exifInterfaceone, path);
                            exifInterfaceone.setAttribute(ExifInterface.TAG_GPS_LATITUDE, UtilityMethods.convertToDegreeMinuteSeconds(gps.getLatitude()));
                            exifInterfaceone.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, UtilityMethods.convertToDegreeMinuteSeconds(gps.getLongitude()));
                            exifInterfaceone.setAttribute(ExifInterface.TAG_MAKE, UtilityMethods.getManufacturer());
                            exifInterfaceone.setAttribute(ExifInterface.TAG_MODEL, UtilityMethods.getDeviceName());
                            exifInterfaceone.setAttribute("imei", UtilityMethods.getIMEI(Personal.this));
                            exifInterfaceone.setAttribute("UserComment", UtilityMethods.getIMEI(Personal.this));
                            exifInterfaceone.saveAttributes();
                            mydb.UpdateDriverPICCoord(SESSION_EMAIL, String.valueOf(gps.getLatitude()), String.valueOf(gps.getLongitude()), "true");
                        }
                        str_list[0]=image_path1;
                        personal_driver_pic2.setEnabled(true);
                        personal_driver_pic3.setEnabled(false);
                        personal_driver_pic4.setEnabled(false);
                        personal_driver_pic5.setEnabled(false);
                        personal_driver_pic6.setEnabled(false);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            if (savedInstanceState.containsKey("photopathtwo")) {
                if (!TextUtils.isEmpty(savedInstanceState.getString("photopathtwo"))) {
                    outputFileUri2 = Uri.parse(savedInstanceState.getString("photopathtwo"));
                    try {
                        licensePhotoImageBitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), Uri.parse(outputFileUri2.toString()));
                        personal_driver_pic2.setImageBitmap(licensePhotoImageBitmap);
                        width = licensePhotoImageBitmap.getWidth();
                        height = licensePhotoImageBitmap.getHeight();
                        image_path2 = MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), licensePhotoImageBitmap, "Title", null);
                        Log.e("image_path2", image_path2);
                        if (gps.canGetLocation()) {
                            String path = UtilityMethods.getRealPathFromUri(Personal.this, Uri.parse(image_path2));
                            exifInterfacetwo = new ExifInterface(path);
                            image_path2 = compressImage(path, exifInterfacetwo, path);
                            exifInterfacetwo.setAttribute(ExifInterface.TAG_GPS_LATITUDE, UtilityMethods.convertToDegreeMinuteSeconds(gps.getLatitude()));
                            exifInterfacetwo.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, UtilityMethods.convertToDegreeMinuteSeconds(gps.getLongitude()));
                            exifInterfacetwo.setAttribute(ExifInterface.TAG_MAKE, UtilityMethods.getManufacturer());
                            exifInterfacetwo.setAttribute(ExifInterface.TAG_MODEL, UtilityMethods.getDeviceName());
                            exifInterfacetwo.setAttribute("imei", UtilityMethods.getIMEI(Personal.this));
                            exifInterfacetwo.setAttribute("UserComment", UtilityMethods.getIMEI(Personal.this));
                            exifInterfacetwo.saveAttributes();
                            mydb.UpdatelicenseNOCoord(SESSION_EMAIL, String.valueOf(gps.getLatitude()), String.valueOf(gps.getLongitude()), "true");
                        }
                        str_list[1]=image_path2;
                        personal_driver_pic2.setEnabled(true);
                        personal_driver_pic3.setEnabled(true);
                        personal_driver_pic4.setEnabled(false);
                        personal_driver_pic5.setEnabled(false);
                        personal_driver_pic6.setEnabled(false);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            if (savedInstanceState.containsKey("photopaththree")) {
                if (!TextUtils.isEmpty(savedInstanceState.getString("photopaththree"))) {
                    outputFileUri3 = Uri.parse(savedInstanceState.getString("photopaththree"));
                    try {
                        qatarIdFrontImageBitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), Uri.parse(outputFileUri3.toString()));
                        personal_driver_pic3.setImageBitmap(qatarIdFrontImageBitmap);
                        width = qatarIdFrontImageBitmap.getWidth();
                        height = qatarIdFrontImageBitmap.getHeight();
                        image_path3 = MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), qatarIdFrontImageBitmap, "Title", null);
                        Log.e("image_path3", image_path3);
                        if (gps.canGetLocation()) {
                            String path = UtilityMethods.getRealPathFromUri(Personal.this, Uri.parse(image_path3));
                            exifInterfacethree = new ExifInterface(path);
                            image_path3 = compressImage(path, exifInterfacethree, path);
                            exifInterfacethree.setAttribute(ExifInterface.TAG_GPS_LATITUDE, UtilityMethods.convertToDegreeMinuteSeconds(gps.getLatitude()));
                            exifInterfacethree.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, UtilityMethods.convertToDegreeMinuteSeconds(gps.getLongitude()));
                            exifInterfacethree.setAttribute(ExifInterface.TAG_MAKE, UtilityMethods.getManufacturer());
                            exifInterfacethree.setAttribute(ExifInterface.TAG_MODEL, UtilityMethods.getDeviceName());
                            exifInterfacethree.setAttribute("imei", UtilityMethods.getIMEI(Personal.this));
                            exifInterfacethree.setAttribute("UserComment", UtilityMethods.getIMEI(Personal.this));
                            exifInterfacethree.saveAttributes();
                            mydb.UpdateIDFrontCoord(SESSION_EMAIL, String.valueOf(gps.getLatitude()), String.valueOf(gps.getLongitude()), "true");
                        }
                        str_list[2]=image_path3;
                        personal_driver_pic2.setEnabled(true);
                        personal_driver_pic3.setEnabled(true);
                        personal_driver_pic4.setEnabled(true);
                        personal_driver_pic5.setEnabled(false);
                        personal_driver_pic6.setEnabled(false);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            if (savedInstanceState.containsKey("photopathfour")) {
                if (!TextUtils.isEmpty(savedInstanceState.getString("photopathfour"))) {
                    outputFileUri4 = Uri.parse(savedInstanceState.getString("photopathfour"));
                    try {
                        qatarIdBackImageBitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), Uri.parse(outputFileUri4.toString()));
                        personal_driver_pic4.setImageBitmap(qatarIdBackImageBitmap);
                        width = qatarIdBackImageBitmap.getWidth();
                        height = qatarIdBackImageBitmap.getHeight();
                        image_path4 = MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), qatarIdBackImageBitmap, "Title", null);
                        Log.e("image_path4", image_path4);
                        if (gps.canGetLocation()) {
                            String path = UtilityMethods.getRealPathFromUri(Personal.this, Uri.parse(image_path4));
                            exifInterfacefour = new ExifInterface(path);
                            image_path4 = compressImage(path, exifInterfacefour, path);
                            exifInterfacefour.setAttribute(ExifInterface.TAG_GPS_LATITUDE, UtilityMethods.convertToDegreeMinuteSeconds(gps.getLatitude()));
                            exifInterfacefour.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, UtilityMethods.convertToDegreeMinuteSeconds(gps.getLongitude()));
                            exifInterfacefour.setAttribute(ExifInterface.TAG_MAKE, UtilityMethods.getManufacturer());
                            exifInterfacefour.setAttribute(ExifInterface.TAG_MODEL, UtilityMethods.getDeviceName());
                            exifInterfacefour.setAttribute("imei", UtilityMethods.getIMEI(Personal.this));
                            exifInterfacefour.setAttribute("UserComment", UtilityMethods.getIMEI(Personal.this));
                            exifInterfacefour.saveAttributes();
                            mydb.UpdateIDBackCoord(SESSION_EMAIL, String.valueOf(gps.getLatitude()), String.valueOf(gps.getLongitude()), "true");
                        }
                        str_list[3]=image_path4;
                        personal_driver_pic2.setEnabled(true);
                        personal_driver_pic3.setEnabled(true);
                        personal_driver_pic4.setEnabled(true);
                        personal_driver_pic5.setEnabled(true);
                        personal_driver_pic6.setEnabled(false);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            if (savedInstanceState.containsKey("photopathfive")) {
                if (!TextUtils.isEmpty(savedInstanceState.getString("photopathfive"))) {
                    outputFileUri5 = Uri.parse(savedInstanceState.getString("photopathfive"));
                    try {
                        passportIdFrontImageBitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), Uri.parse(outputFileUri5.toString()));
                        personal_driver_pic5.setImageBitmap(passportIdFrontImageBitmap);
                        width = passportIdFrontImageBitmap.getWidth();
                        height = passportIdFrontImageBitmap.getHeight();
                        image_path5 = MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), passportIdFrontImageBitmap, "Title", null);
                        Log.e("image_path5", image_path5);
                        if (gps.canGetLocation()) {
                            String path = UtilityMethods.getRealPathFromUri(Personal.this, Uri.parse(image_path5));
                            exifInterfacefive = new ExifInterface(path);
                            image_path5 = compressImage(path, exifInterfacefive, path);
                            exifInterfacefive.setAttribute(ExifInterface.TAG_GPS_LATITUDE, UtilityMethods.convertToDegreeMinuteSeconds(gps.getLatitude()));
                            exifInterfacefive.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, UtilityMethods.convertToDegreeMinuteSeconds(gps.getLongitude()));
                            exifInterfacefive.setAttribute(ExifInterface.TAG_MAKE, UtilityMethods.getManufacturer());
                            exifInterfacefive.setAttribute(ExifInterface.TAG_MODEL, UtilityMethods.getDeviceName());
                            exifInterfacefive.setAttribute("imei", UtilityMethods.getIMEI(Personal.this));
                            exifInterfacefive.setAttribute("UserComment", UtilityMethods.getIMEI(Personal.this));
                            exifInterfacefive.saveAttributes();
                            mydb.UpdatePassFrontCoord(SESSION_EMAIL, String.valueOf(gps.getLatitude()), String.valueOf(gps.getLongitude()), "true");
                        }
                        str_list[4]=image_path5;
                        personal_driver_pic2.setEnabled(true);
                        personal_driver_pic3.setEnabled(true);
                        personal_driver_pic4.setEnabled(true);
                        personal_driver_pic5.setEnabled(true);
                        personal_driver_pic6.setEnabled(true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            if (savedInstanceState.containsKey("photopathsix")) {
                if (!TextUtils.isEmpty(savedInstanceState.getString("photopathsix"))) {
                    outputFileUri6 = Uri.parse(savedInstanceState.getString("photopathsix"));
                    try {
                        passportIdBackImageBitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), Uri.parse(outputFileUri6.toString()));
                        personal_driver_pic6.setImageBitmap(passportIdBackImageBitmap);
                        width = passportIdBackImageBitmap.getWidth();
                        height = passportIdBackImageBitmap.getHeight();
                        image_path6 = MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), passportIdBackImageBitmap, "Title", null);
                        Log.e("image_path6", image_path6);
                        if (gps.canGetLocation()) {
                            String path = UtilityMethods.getRealPathFromUri(Personal.this, Uri.parse(image_path6));
                            exifInterfacesix = new ExifInterface(path);
                            image_path6 = compressImage(path, exifInterfacesix, path);
                            exifInterfacesix.setAttribute(ExifInterface.TAG_GPS_LATITUDE, UtilityMethods.convertToDegreeMinuteSeconds(gps.getLatitude()));
                            exifInterfacesix.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, UtilityMethods.convertToDegreeMinuteSeconds(gps.getLongitude()));
                            exifInterfacesix.setAttribute(ExifInterface.TAG_MAKE, UtilityMethods.getManufacturer());
                            exifInterfacesix.setAttribute(ExifInterface.TAG_MODEL, UtilityMethods.getDeviceName());
                            exifInterfacesix.setAttribute("imei", UtilityMethods.getIMEI(Personal.this));
                            exifInterfacesix.setAttribute("UserComment", UtilityMethods.getIMEI(Personal.this));
                            exifInterfacesix.saveAttributes();
                            mydb.UpdatePassBackCoord(SESSION_EMAIL, String.valueOf(gps.getLatitude()), String.valueOf(gps.getLongitude()), "true");
                        }
                        str_list[5]=image_path6;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

   /* @Override
    public void onPause() {
        super.onPause();

        if ((dialog != null) && dialog.isShowing())
            dialog.dismiss();
        dialog = null;
    }*/

    private void uploadDocToFirebase(Uri imageUri, final int i) {
        if(progressDialog!=null)
        {
            if(progressDialog.isShowing())
            {
                progressDialog.dismiss();
            }
            progressDialog=null;
        }
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Uploading ....");
        progressDialog.show();
        new AceKabsDocumentUploadService().uploadFileFromDisk(imageUri, new IFirebaseCallback<Uri>() {
            @Override
            public void onDataReceived(Uri data) {
                String storageUrl = data.toString();
                list_url.add(storageUrl);
                try {
                    if (i == 0) {
                        ContentValues initialValues = new ContentValues();
                        initialValues.put("driverPhoto_URL", storageUrl);
                        Log.e(TAG,"driverPhoto_URL="+storageUrl);
                        mydb.updateVehicleRegistrationDetails(SESSION_EMAIL, initialValues);
                    } else if (i == 1) {
                        ContentValues initialValues = new ContentValues();
                        initialValues.put("driverLicenseNo_URL", storageUrl);
                        Log.e(TAG,"driverLicenseNo_URL="+storageUrl);
                        mydb.updateVehicleRegistrationDetails(SESSION_EMAIL, initialValues);
                    } else if (i == 2) {
                        ContentValues initialValues = new ContentValues();
                        initialValues.put("driverQatar_idFront_URL", storageUrl);
                        Log.e(TAG,"driverQatar_idFront_URL="+storageUrl);
                        mydb.updateVehicleRegistrationDetails(SESSION_EMAIL, initialValues);
                    } else if (i == 3) {
                        ContentValues initialValues = new ContentValues();
                        initialValues.put("driverQatar_idBack_URL", storageUrl);
                        Log.e(TAG,"driverQatar_idBack_URL="+storageUrl);
                        mydb.updateVehicleRegistrationDetails(SESSION_EMAIL, initialValues);
                    } else if (i == 4) {
                        ContentValues initialValues = new ContentValues();
                        initialValues.put("driverPassport_idFront_URL", storageUrl);
                        Log.e(TAG,"driverPassport_idFront_URL="+storageUrl);
                        mydb.updateVehicleRegistrationDetails(SESSION_EMAIL, initialValues);
                    } else if (i == 5) {
                        ContentValues initialValues = new ContentValues();
                        initialValues.put("driverPassport_idBack_URL", storageUrl);
                        Log.e(TAG,"driverPassport_idBack_URL="+storageUrl);
                        mydb.updateVehicleRegistrationDetails(SESSION_EMAIL, initialValues);
                    }
                    if (list_url.size() == 5) {
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        ContentValues initialValues = new ContentValues();
                        initialValues.put("driver_dob", driver_dob.getText().toString().trim());
                        mydb.updateVehicleRegistrationDetails(SESSION_EMAIL, initialValues);
                        Intent li = new Intent(Personal.this, VehicleRegDetails.class);
                        li.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(li);
                        finish();
                    }
                    else {
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
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
            }
        });
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:
                // set date picker as current date
                return new DatePickerDialog(this, datePickerListener,
                        year, month, day);
        }
        return null;
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
            if (driverPhotoImageBitmap != null) {
                driverPhotoImageBitmap.recycle();
                driverPhotoImageBitmap = null;
            }
            if (licensePhotoImageBitmap != null) {
                licensePhotoImageBitmap.recycle();
                licensePhotoImageBitmap = null;
            }
            if (qatarIdFrontImageBitmap != null) {
                qatarIdFrontImageBitmap.recycle();
                qatarIdFrontImageBitmap = null;
            }
            if (qatarIdBackImageBitmap != null) {
                qatarIdBackImageBitmap.recycle();
                qatarIdBackImageBitmap = null;
            }
            if (passportIdFrontImageBitmap != null) {
                passportIdFrontImageBitmap.recycle();
                passportIdFrontImageBitmap = null;
            }
            if (passportIdBackImageBitmap != null) {
                passportIdBackImageBitmap.recycle();
                passportIdBackImageBitmap = null;
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
}
