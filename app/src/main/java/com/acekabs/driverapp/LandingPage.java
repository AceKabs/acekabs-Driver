package com.acekabs.driverapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.acekabs.driverapp.db.DBAdapter;
import com.acekabs.driverapp.services.GPSTracker2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

public class LandingPage extends Activity {

    Button sign_in, sign_up;

    GPSTracker2 gps;
    LocationManager locationManager;
    double longitudeNetwork, latitudeNetwork;
    Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.landing_page);

        gps = new GPSTracker2(mContext, LandingPage.this);
        mContext = this;


        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        sign_in = (Button) findViewById(R.id.land_signin);
        sign_up = (Button) findViewById(R.id.land_signup);
        sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signin = new Intent(LandingPage.this, SignIn.class);
                signin.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(signin);
            }
        });

        sign_up.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(LandingPage.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

                } else {
                    //  Toast.makeText(mContext,"You need have granted permission",Toast.LENGTH_SHORT).show();
                    gps = new GPSTracker2(mContext, LandingPage.this);

                    // Check if GPS enabled
                    if (gps.canGetLocation()) {

                        double latitude = gps.getLatitude();
                        double longitude = gps.getLongitude();

                        Intent signup = new Intent(LandingPage.this, SignUp.class);
                        signup.putExtra("lat", latitude);
                        signup.putExtra("long", longitude);
                        startActivity(signup);

                        // \n is for new line
                        Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
                    } else {
                        // Can't get location.
                        // GPS or network is not enabled.
                        // Ask user to enable GPS/network in settings.
                        gps.showSettingsAlert();
                    }
                }

            }
        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the

                    // contacts-related task you need to do.

                    gps = new GPSTracker2(mContext, LandingPage.this);

                    // Check if GPS enabled
                    if (gps.canGetLocation()) {

                        double latitude = gps.getLatitude();
                        double longitude = gps.getLongitude();

                        // \n is for new line
                        Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
                    } else {
                        // Can't get location.
                        // GPS or network is not enabled.
                        // Ask user to enable GPS/network in settings.
                        gps.showSettingsAlert();
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                    Toast.makeText(mContext, "You need to grant permission", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }


    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Really Exit?")
                .setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        LandingPage.super.onBackPressed();

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

}