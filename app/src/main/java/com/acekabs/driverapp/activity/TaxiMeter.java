package com.acekabs.driverapp.activity;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.acekabs.driverapp.ApplicationConstants;
import com.acekabs.driverapp.R;
import com.acekabs.driverapp.constants.Constants;
import com.acekabs.driverapp.constants.TaxiMeterConstants;
import com.acekabs.driverapp.custom.FontManager;
import com.acekabs.driverapp.entities.AceKabsDriver;
import com.acekabs.driverapp.firebase.FirebaseDriverUtil;
import com.acekabs.driverapp.firebase.IFirebaseCallback;
import com.acekabs.driverapp.interfaces.MapInteractor;
import com.acekabs.driverapp.pojo.Destinations;
import com.acekabs.driverapp.pojo.Example;
import com.acekabs.driverapp.pojo.RouteDetails;
import com.acekabs.driverapp.pojo.TaxiMeterData;
import com.acekabs.driverapp.restclient.RestApiService;
import com.acekabs.driverapp.utils.ApiUtils;
import com.acekabs.driverapp.utils.SharePreferanceWrapperSingleton;
import com.acekabs.driverapp.utils.UtilityMethods;
import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.maps.android.SphericalUtil;
import com.tuyenmonkey.mkloader.MKLoader;

import org.joda.time.DateTime;
import org.joda.time.Period;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class TaxiMeter extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener, LocationListener, MapInteractor {
    protected static final int REQUEST_CHECK_SETTINGS = 2;
    private static final int LOCATION_REQUEST_CODE = 29;
    MapView mapView;
    DatabaseReference ref;
    private GoogleMap googleMap;
    private Context context;
    private TextView tvTotalDistance;
    private TextView tvTotalTime;
    private TextView tvTotalAmount;
    private Button btnStartRide;
    private Button btnStopRide;
    private String TAG = "Taxi Meter";
    private Location mCurrentLocation;
    private SimpleDateFormat sdf;
    private ArrayList<RouteDetails> routeDetailses;
    private DateTime startDateTime;
    private DateTime endDateTime;
    private ArrayList<Location> locations;
    private RestApiService mAPIService;
    private double baseFare = 0.0;
    private double costPerMinute = 0.0;
    private Double costPerKM = 0.0;
    private double TotalAmount;
    private Timer timer = new Timer();
    private TimerTask timerTask;
    private Period period;
    private Button btnCancelRider;
    private double totalDistance;
    private TaxiMeterData taxiMeterData;
    private String taxiMeterId;
    private String mobileNumber = "";
    private String alldestinations = "";
    private TextView tvcurrecnysymbol;
    private String currency = "";
    private Set<LatLng> mLocations;
    private boolean isStart = false;
    private ArrayList<LatLng> data;
    private ArrayList<RouteDetails> myData;
    private LatLng origin;
    private Polyline line;
    private ArrayList<LatLng> allwaypoints;
    private Dialog dialogStopDetails;
    private Button btnStopTrip;
    private Button btnStopCancelTrip;
    private TextView tvgpsstatus;
    private MKLoader gpsicons;
    private MediaPlayer mp;
    private ProgressDialog pDialog;
    private SharePreferanceWrapperSingleton preferanceWrapperSingleton;
    private double distanceValue = 0.0;
    private ArrayList<LatLng> travelledLocations; //added
    private float totalDistanceCovered = 0;//added
    private LocationManager locationManager;
    private BroadcastReceiver checkGPSReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Log.e("About GPS", "GPS is Enabled in your device");
                if (mp != null) {
                    mp.release();
                    mp=null;
                }
            } else {
                Log.e("About GPS", "GPS is Disabled in your device");
                try {
                    mp = MediaPlayer.create(TaxiMeter.this.context, R.raw.alarm);
                    mp.setLooping(true);
                    mp.start();
                }catch (Exception e)
                {
                    Log.e(TAG,"GPS Media play exception"+e.toString());
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taxi_meter);
        travelledLocations = new ArrayList<>();
        mapView = (MapView) findViewById(R.id.requestMapView);
        mapView.onCreate(savedInstanceState);
        locations = new ArrayList<>();
        mapView.getMapAsync(this);
        mLocations = new HashSet<>();
        data = new ArrayList<>();
        routeDetailses = new ArrayList<>();
        myData = new ArrayList<>();
        allwaypoints = new ArrayList<>();
        init();
        StopRiderDialog(context);
        if (isNetworkAvailable()) {
            getPendingTaxiMeterDetails();
        }
    }

    private void getPendingTaxiMeterDetails() {
        Log.e(TAG,"getPendingTaxiMeterDetails called");
        try {
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            final DatabaseReference reference = firebaseDatabase.getReference();
            Query query = reference.child("taxifare_eta").orderByChild("mobileNumber_isTripDone").equalTo(mobileNumber + "_" + false);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.e(TAG,"onDataChange called");
                    try {
                        if (dataSnapshot.getChildrenCount() > 0) {
                            DataSnapshot nodeDataSnapshot = dataSnapshot.getChildren().iterator().next();
                            String key = nodeDataSnapshot.getKey(); // this key is `K1NRz9l5PU_0CFDtgXz`
                            String path = "/" + dataSnapshot.getKey() + "/" + key;
                            HashMap<String, Object> meterData = (HashMap<String, Object>) nodeDataSnapshot.getValue();
                            taxiMeterData = new TaxiMeterData();
                            taxiMeterData.setMeterId(meterData.get(TaxiMeterConstants.meterId).toString());
                            taxiMeterData.setMobileNumber(meterData.get(TaxiMeterConstants.mobileNumber).toString());

                            if (meterData.get(TaxiMeterConstants.endTime) != null) {
                                taxiMeterData.setEndTime(meterData.get(TaxiMeterConstants.endTime).toString());
                            } else {
                                taxiMeterData.setEndTime("");
                            }
                            if (meterData.get(TaxiMeterConstants.baseFare) != null) {
                                taxiMeterData.setBaseFare(Double.parseDouble(meterData.get(TaxiMeterConstants.baseFare).toString()));
                            } else {
                                taxiMeterData.setBaseFare(0.0f);
                            }
                            if (meterData.get(TaxiMeterConstants.costPerKM) != null) {
                                taxiMeterData.setCostPerKM(Double.parseDouble(meterData.get(TaxiMeterConstants.costPerKM).toString()));
                            } else {
                                taxiMeterData.setCostPerKM(0.0f);
                            }
                            if (meterData.get(TaxiMeterConstants.costPerMinute) != null) {
                                taxiMeterData.setCostPerMinute(Double.parseDouble(meterData.get(TaxiMeterConstants.costPerMinute).toString()));
                            } else {
                                taxiMeterData.setCostPerMinute(0.0f);
                            }
                            if (meterData.get(TaxiMeterConstants.totalDistance) != null) {
                                taxiMeterData.setTotalDistance(Double.parseDouble(meterData.get(TaxiMeterConstants.totalDistance).toString()));
                            } else {
                                taxiMeterData.setTotalDistance(0.0f);
                            }
                            if (meterData.get(TaxiMeterConstants.TotalAmount) != null) {
                                taxiMeterData.setTotalAmount(Double.parseDouble(meterData.get(TaxiMeterConstants.TotalAmount).toString()));
                            } else {
                                taxiMeterData.setTotalAmount(0.0f);
                            }
                            if (meterData.get(TaxiMeterConstants.routes) != null) {
                                taxiMeterData.setRoutes((ArrayList<RouteDetails>) meterData.get(TaxiMeterConstants.routes));
                            } else {
                                taxiMeterData.setRoutes(routeDetailses);
                            }
                            if (meterData.get(TaxiMeterConstants.isTripDone) != null) {
                                taxiMeterData.setTripDone(Boolean.parseBoolean(meterData.get(TaxiMeterConstants.isTripDone).toString()));
                            } else {
                                taxiMeterData.setTripDone(false);
                            }
                            if (meterData.get(TaxiMeterConstants.mobileNumber_isTripDone) != null) {
                                taxiMeterData.setMobileNumber_isTripDone(meterData.get(TaxiMeterConstants.mobileNumber_isTripDone).toString());
                            } else {
                                taxiMeterData.setMobileNumber_isTripDone(mobileNumber + "_" + false);
                            }
                            if (taxiMeterData != null) {
                                isStart = true;
                                TaxiMeter.this.onRideStart();
                                timer.schedule(timerTask, 0, 30000);
                                Toast.makeText(context, "Start Ride", Toast.LENGTH_SHORT).show();
                                btnStopRide.setEnabled(true);
                                btnStartRide.setEnabled(false);
                                btnStartRide.setVisibility(View.GONE);
                                btnStopRide.setVisibility(View.VISIBLE);
                                endDateTime = new DateTime();

                                try {
                                    long milliSec= Long.parseLong(meterData.get(TaxiMeterConstants.startTime).toString());
                                    startDateTime=new DateTime(milliSec);
                                }
                                catch (Exception e){
                                    Log.e(TAG,"Start TIme not FDound==>"+e.toString());
                                    startDateTime=new DateTime();
                                }
                                taxiMeterData.setStartTime(startDateTime.getMillis()+"");
                                //period = new Period(new DateTime(taxiMeterData.getStartTime()), endDateTime);
                                period = new Period(startDateTime, endDateTime);
                                TotalAmount = baseFare + period.getMinutes() * costPerMinute + (taxiMeterData.getTotalDistance() * costPerKM);
                                tvTotalTime.setText(period.getHours() + ":" + period.getMinutes());
                                tvTotalAmount.setText(String.format("%.2f", TotalAmount));
                                tvTotalDistance.setText(String.format("%.2f", taxiMeterData.getTotalDistance()) + " K.M.");
                                tvTotalDistance.setTag(taxiMeterData.getTotalDistance());

                                totalDistanceCovered= (float) taxiMeterData.getTotalDistance();

                                Log.e(TAG," TotalAmount=>"+TotalAmount);
                                Log.e(TAG," totalDistanceCovered=>"+totalDistanceCovered);


                                if(travelledLocations!=null)
                                {
                                    Log.e(TAG,"Last lat long addedd"+meterData.get("last_lat").toString()+","+meterData.get("last_lng").toString());

                                    try {
                                        taxiMeterData.setLastLat(Double.parseDouble(meterData.get("last_lat").toString()));
                                        taxiMeterData.setLastLng(Double.parseDouble(meterData.get("last_lng").toString()));
                                        Log.e(TAG,"Last latLng=>"+taxiMeterData.getLastLat()+","+taxiMeterData.getLastLng());
                                        if(taxiMeterData.getLastLat()>0 && taxiMeterData.getLastLng()>0)
                                        {
                                            LatLng latLng=new LatLng(taxiMeterData.getLastLat(),taxiMeterData.getLastLng());
                                            travelledLocations.add(latLng);
                                        }
                                    }catch (Exception e)
                                    {
                                        Log.e(TAG,"lastLatLon error=>"+e.toString());
                                    }

                                }
                            }
                        }
                        else {
                            Log.e(TAG,"dataSnapshot is empty");
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    if (databaseError != null) {
                        Log.e("getPendingTaxiMeterDetails Error", databaseError.getMessage());
                    }
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart fired ..............");
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    }


    private void init() {
        Log.e(TAG,"init called");
        routeDetailses = new ArrayList<>();
        context = this;
        preferanceWrapperSingleton = SharePreferanceWrapperSingleton.getSingletonInstance();
        preferanceWrapperSingleton.setPref(context);
        preferanceWrapperSingleton.setEditor();
        pDialog = new ProgressDialog(context);
        pDialog.setMessage("Please wait while getting driver details.");
        pDialog.setCancelable(false);
        baseFare = getIntent().getDoubleExtra("baseFare", 0.0);
        costPerKM = getIntent().getDoubleExtra("costPerKM", 0.0);
        costPerMinute = getIntent().getDoubleExtra("costPerMinute", 0.0);
        currency = getIntent().getStringExtra("currency");
        if (baseFare == 0 || costPerKM == 0 || costPerMinute == 0) {
            if (isNetworkAvailable()) {
                if (!TextUtils.isEmpty(preferanceWrapperSingleton.getStringValueFromSharedPref(Constants.mobileNumber))) {
                    pDialog.show();
                    FirebaseDriverUtil.getDriversByMobileNumber("location_id_1", preferanceWrapperSingleton.getStringValueFromSharedPref(Constants.mobileNumber), new IFirebaseCallback<List<AceKabsDriver>>() {
                        @Override
                        public void onDataReceived(List<AceKabsDriver> data) {
                            Log.e(TAG,"onDataReceived->");
                            Log.e(TAG,"Response Data=>"+data);
                            if (pDialog != null && pDialog.isShowing()) {
                                pDialog.dismiss();
                            }
                            if (data != null && data.size() > 0) {
                                baseFare = data.get(0).getBaseFare();
                                costPerKM = data.get(0).getCostPerKM();
                                costPerMinute = data.get(0).getCostPerMinute();
                                currency = data.get(0).getCurrency();
                                updateCurrencySymbol(currency);
                            }

                        }

                        @Override
                        public void onFailure(Exception ex) {
                            Log.e("Error", ex.getMessage().toString());
                            if (pDialog != null && pDialog.isShowing()) {
                                pDialog.dismiss();
                            }
                        }
                    });
                }
            }
        }
        tvgpsstatus = (TextView) findViewById(R.id.tvgpsstatus);
        gpsicons = (MKLoader) findViewById(R.id.gpsicons);
        tvcurrecnysymbol = (TextView) findViewById(R.id.tvcurrecnysymbol);
        if (!TextUtils.isEmpty(currency)) {
            updateCurrencySymbol(currency);
        }
        sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss", Locale.ENGLISH);
        tvTotalDistance = (TextView) findViewById(R.id.tvDistance);
        tvTotalTime = (TextView) findViewById(R.id.tvTime);
        tvTotalDistance.setText("0.00 KM.");
        tvTotalTime.setText("00:00:00");
        tvTotalAmount = (TextView) findViewById(R.id.tvTotalFareAmount);
        btnStartRide = (Button) findViewById(R.id.btnStartRides);
        btnStopRide = (Button) findViewById(R.id.btnStopRides);
        btnStartRide.setOnClickListener(this);
        btnStopRide.setOnClickListener(this);
        tvTotalAmount.setText(String.valueOf(baseFare));
        if (!isGooglePlayServicesAvailable()) {
            finish();
        }
        /*initialise geo fire for save driver current location*/
        ref = FirebaseDatabase.getInstance().getReference(ApplicationConstants.GEOFIRE_NODE);
        preferanceWrapperSingleton = SharePreferanceWrapperSingleton.getSingletonInstance();
        preferanceWrapperSingleton.setPref(context);
        preferanceWrapperSingleton.setEditor();
        btnCancelRider = (Button) findViewById(R.id.btnCancelRider);
        btnCancelRider.setOnClickListener(this);
        mobileNumber = preferanceWrapperSingleton.getStringValueFromSharedPref(Constants.mobileNumber);
        tvTotalDistance.setTag("0");
        if (UtilityMethods.isLocationEnabled(context)) {
            tvgpsstatus.setText("Connected");
            gpsicons.setVisibility(View.VISIBLE);
        } else {
            tvgpsstatus.setText("Disconnected");
            gpsicons.setVisibility(View.GONE);
        }
        timerTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (UtilityMethods.isLocationEnabled(context)) {
                            tvgpsstatus.setText("Connected");
                            gpsicons.setVisibility(View.VISIBLE);
                        } else {
                            tvgpsstatus.setText("Disconnected");
                            gpsicons.setVisibility(View.GONE);
                        }
                    }
                });
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.location.PROVIDERS_CHANGED");
        registerReceiver(checkGPSReceiver, filter);
    }


    private void updateCurrencySymbol(String currency) {
        if (!TextUtils.isEmpty(currency)) {
            switch (currency) {
                case "INR":
                    tvcurrecnysymbol.setText(context.getResources().getString(R.string.rupee));
                    tvcurrecnysymbol.setTypeface(FontManager.getTypeface(context, FontManager.FONTAWESOME));
                    break;
                case "LKR":
                    tvcurrecnysymbol.setText(context.getResources().getString(R.string.rupee));
                    tvcurrecnysymbol.setTypeface(FontManager.getTypeface(context, FontManager.FONTAWESOME));
                    break;
                case "QAR":
                    tvcurrecnysymbol.setText(context.getResources().getString(R.string.qar));
                    break;
                case "USD":
                    tvcurrecnysymbol.setText(context.getResources().getString(R.string.dollar));
                    tvcurrecnysymbol.setTypeface(FontManager.getTypeface(context, FontManager.FONTAWESOME));
                    break;
            }
        }
    }


    @Override
    public void onClick(View v) {
        if (btnStartRide == v) {
            isStart = true;
            this.showToast("Ride Started");
            this.onRideStart();
            taxiMeterId = UUID.randomUUID().toString();
            taxiMeterData = new TaxiMeterData();
            taxiMeterData.setMobileNumber(mobileNumber);
            taxiMeterData.setMeterId(taxiMeterId);
            taxiMeterData.setBaseFare(baseFare);
            taxiMeterData.setCostPerKM(costPerKM);
            taxiMeterData.setCostPerMinute(costPerMinute);
            startDateTime = new DateTime();
           // taxiMeterData.setStartTime(startDateTime.toString("yyyy-MM-dd hh:mm:ss"));
            taxiMeterData.setStartTime(startDateTime.getMillis()+"");

            taxiMeterData.setMobileNumber(mobileNumber);
            taxiMeterData.setTripDone(false);
            taxiMeterData.setMobileNumber_isTripDone(mobileNumber + "_" + false);
            if (mCurrentLocation != null) {
                origin = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
            }
            FirebaseDriverUtil.saveTaxiMeterDetails(taxiMeterData);
            timer.schedule(timerTask, 0, 30000);
            btnStopRide.setEnabled(true);
            btnStartRide.setEnabled(false);
            btnStartRide.setVisibility(View.GONE);
            btnStopRide.setVisibility(View.VISIBLE);
        } else if (btnStopRide == v) {
            if (dialogStopDetails != null && !dialogStopDetails.isShowing()) {
                dialogStopDetails.show();
            }
        } else if (btnStopTrip == v) {
            if (dialogStopDetails != null && dialogStopDetails.isShowing()) {
                dialogStopDetails.dismiss();
            }
            this.showToast("Ride Ended");
            this.onRideEnd();
            isStart = false;
            if(timerTask!=null)
                timerTask.cancel();
            btnStopRide.setEnabled(false);
            btnStartRide.setEnabled(false);
            if (taxiMeterData != null) {
                taxiMeterData.setTripDone(true);
                taxiMeterData.setMobileNumber_isTripDone(mobileNumber + "_" + true);
            }
           /* if (alldestinations.length() > 0) {
                getDistanceFromServer();
            }*/
            allwaypoints.clear();
            upDateMeterTripData(true);
            btnStartRide.setVisibility(View.GONE);
            btnStopRide.setVisibility(View.GONE);
        } else if (btnStopCancelTrip == v) {
            if (dialogStopDetails != null && dialogStopDetails.isShowing()) {
                dialogStopDetails.dismiss();
            }
        } else if (btnCancelRider == v) {
            finish();
        }
    }

    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
        }
    }

    private void getDistances() {
        data.clear();
        data.addAll(mLocations);
        Log.e("Locations Size ==", "" + mLocations.size());
        Log.e("Locations", Arrays.toString(data.toArray()));
        Double distanceValue = SphericalUtil.computeLength(data) / 1000;
        endDateTime = new DateTime();
        period = new Period(startDateTime, endDateTime);
        Log.i("Time", period.getHours() + ":" + period.getMinutes() + ":" + period.getSeconds());
        TotalAmount = baseFare + period.getMinutes() * costPerMinute + (distanceValue * costPerKM);
        tvTotalTime.setText(period.getHours() + ":" + period.getMinutes());
        tvTotalAmount.setText(String.format("%.2f", TotalAmount));
        tvTotalDistance.setText(String.format("%.2f", distanceValue) + " K.M.");
        tvTotalDistance.setTag(distanceValue);
//        taxiMeterData.setEndTime(endDateTime.toString("yyyy-MM-dd hh:mm:ss"));
//        taxiMeterData.setTotalAmount(Double.parseDouble(tvTotalAmount.getText().toString()));
//        taxiMeterData.setTotalDistance(Math.round(totalDistance / 1000));
    }

    private synchronized void calculateDistanceAndFare(String origin, String destination) {
        if (isNetworkAvailable()) {
            mAPIService = ApiUtils.getAPIService("https://maps.googleapis.com/");
            mAPIService.getDistanceData(origin, destination, ApplicationConstants.API_KEY).enqueue(new Callback<Destinations>() {
                @Override
                public void onResponse(Call<Destinations> call, Response<Destinations> response) {
                    if (response.isSuccessful()) {
                        Destinations dst = response.body();
                        if (dst != null) {
                            totalDistance = 0;
                            for (int i = 0; i < dst.rows.get(0).elementsObjectses.size(); i++) {
                                totalDistance += Math.round(Float.valueOf(dst.rows.get(0).elementsObjectses.get(i).distance.value) / 1000);
                            }
                            endDateTime = new DateTime();
                            period = new Period(startDateTime, endDateTime);
                            Log.i("Time", period.getHours() + ":" + period.getMinutes() + ":" + period.getSeconds());
                            TotalAmount = baseFare + period.getMinutes() * costPerMinute + (totalDistance * costPerKM);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tvTotalTime.setText(period.getHours() + ":" + period.getMinutes() + ":" + period.getSeconds());
                                    tvTotalAmount.setText(String.format("%.2f", TotalAmount));
                                    tvTotalDistance.setText(String.valueOf(Math.round(totalDistance)) + " K.M.");
                                    taxiMeterData.setEndTime(endDateTime.toString("yyyy-MM-dd hh:mm:ss"));
                                    taxiMeterData.setTotalAmount(Double.parseDouble(tvTotalAmount.getText().toString()));
                                    taxiMeterData.setTotalDistance(Math.round(totalDistance));
                                    taxiMeterData.setRoutes(routeDetailses);
                                    //upDateMeterTripData();
                                }
                            });

                        }
                    }
                }

                @Override
                public void onFailure(Call<Destinations> call, Throwable t) {
                    Log.e(TAG, "Unable to submit post to API.");
                }
            });
        }
    }

    private synchronized void finalCalculations(String origin, String destination) {
        if (isNetworkAvailable()) {
            mAPIService = ApiUtils.getAPIService("https://maps.googleapis.com/");
            mAPIService.getDistanceData(origin, destination, ApplicationConstants.API_KEY).enqueue(new Callback<Destinations>() {
                @Override
                public void onResponse(Call<Destinations> call, Response<Destinations> response) {
                    if (response.isSuccessful()) {
                        Destinations dst = response.body();
                        if (dst != null) {
                            for (int i = 0; i < dst.rows.get(0).elementsObjectses.size(); i++) {
                                totalDistance += Math.round(Float.valueOf(dst.rows.get(0).elementsObjectses.get(i).distance.value) / 1000);
                            }
                            endDateTime = new DateTime();
                            period = new Period(startDateTime, endDateTime);
                            Log.i("Time", period.getHours() + ":" + period.getMinutes() + ":" + period.getSeconds());
                            TotalAmount = baseFare + period.getMinutes() * costPerMinute + (totalDistance * costPerKM);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tvTotalTime.setText(period.getHours() + ":" + period.getMinutes() + ":" + period.getSeconds());
                                    tvTotalAmount.setText(String.format("%.2f", TotalAmount));
                                    tvTotalDistance.setText(String.valueOf(Math.round(totalDistance)) + " K.M.");
                                    taxiMeterData.setEndTime(endDateTime.toString("yyyy-MM-dd hh:mm:ss"));
                                    taxiMeterData.setTotalAmount(Double.parseDouble(tvTotalAmount.getText().toString()));
                                    taxiMeterData.setTotalDistance(Math.round(totalDistance));
                                    //upDateMeterTripData();
                                }
                            });

                        }
                    }
                }

                @Override
                public void onFailure(Call<Destinations> call, Throwable t) {
                    Log.e(TAG, "Unable to submit post to API.");
                }
            });
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Firing onLocationChanged..............................................");
        mCurrentLocation = location;
        if (mCurrentLocation != null) {
            LatLng latLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
            // Showing the current location in Google Map

            googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            // Zoom in the Google Map
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(16));
            Log.d("mCurrentLocation", String.valueOf(mCurrentLocation.getLatitude() + "  " + mCurrentLocation.getLongitude()));
            locations.add(mCurrentLocation);
            //   taxiMeterData.setRoutes(locations);
            if (isStart) {
                Log.e(TAG,"onLocationChanged if 1...");
//                int speed = (int) ((mCurrentLocation.getSpeed() * 3600) / 1000);
//                if (speed > 0) {
//                    allwaypoints.add(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()));
//                    mLocations.add(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()));
//                    RouteDetails routeDetails = new RouteDetails();
//                    routeDetails.setLocation(mCurrentLocation.getLatitude() + "," + mCurrentLocation.getLongitude());
//                    routeDetails.setTime(sdf.format(new Date()));
//                    routeDetails.setTotaltime("0");
//                    routeDetailses.add(routeDetails);
//                    alldestinations += String.valueOf(mCurrentLocation.getLatitude()) + "," + String.valueOf(mCurrentLocation.getLongitude()) + "|";
//                } else {
//                    endDateTime = new DateTime();
//                    period = new Period(startDateTime, endDateTime);
//                    Log.i("Time", period.getHours() + ":" + period.getMinutes() + ":" + period.getSeconds());
//                    tvTotalTime.setText(period.getHours() + ":" + period.getMinutes());
//                    double distance = Double.parseDouble(tvTotalDistance.getTag().toString());
//                    TotalAmount = baseFare + period.getMinutes() * costPerMinute + (distance * costPerKM);
//                    tvTotalAmount.setText(String.format("%.2f", TotalAmount));
//                }


                if (travelledLocations.size() > 0) {
                    Log.e(TAG,"onLocationChanged if if 1...");
                    LatLng oldLocation = travelledLocations.get(travelledLocations.size() - 1);
                    totalDistanceCovered += UtilityMethods.calculateDistance(latLng, oldLocation);
                    RouteDetails routeDetails = new RouteDetails();
                    routeDetails.setLocation(mCurrentLocation.getLatitude() + "," + mCurrentLocation.getLongitude());
                    routeDetails.setTime(sdf.format(new Date()));
                    routeDetails.setTotaltime("0");
                    routeDetailses.add(routeDetails);
                }
                if (totalDistanceCovered > 0) {
                    Log.e(TAG,"onLocationChanged if if 2...");
                    endDateTime = new DateTime();
                    period = new Period(startDateTime, endDateTime);

                    Log.e("Time",period.getHours()+ ":" + period.getMinutes() + ":" +period.getSeconds());
                    tvTotalTime.setText( getFormatted(period.getHours()) + ":" + getFormatted(period.getMinutes()) + ":" + getFormatted(period.getSeconds()));
                    TotalAmount = baseFare + period.getMinutes() * costPerMinute + (totalDistanceCovered * costPerKM);
                    tvTotalAmount.setText(String.format("%.2f", TotalAmount, Locale.ENGLISH));
                    tvTotalDistance.setText(String.format("%.2f", totalDistanceCovered, Locale.ENGLISH) + " Km.");

                    taxiMeterData.setTotalAmount(TotalAmount);
                    taxiMeterData.setTotalDistance(totalDistanceCovered);

                    Log.e(TAG,"Amount=>"+taxiMeterData.getTotalAmount());
                    Log.e(TAG,"Distance=>"+taxiMeterData.getTotalDistance());

                    if (taxiMeterData != null) {
                        upDateMeterTripData(false);
                    }
                }
                travelledLocations.add(latLng);
                taxiMeterData.setLastLat(latLng.latitude);
                taxiMeterData.setLastLng(latLng.longitude);

            }
        }
    }

    private String getFormatted(int unit) {
        if(unit<10)
            return "0"+unit;
        return unit+"";
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private synchronized void getDistanceFromServer() {
        if (isNetworkAvailable()) {
            if (allwaypoints.size() > 2) {
                mAPIService = ApiUtils.getAPIService("https://maps.googleapis.com/maps/");
                mAPIService.getTotalDistance("https://maps.googleapis.com/maps/" + "api/directions/json?key=" + ApplicationConstants.API_KEY, "metric", allwaypoints.get(allwaypoints.size() - 2).latitude + "," + allwaypoints.get(allwaypoints.size() - 2).longitude, allwaypoints.get(allwaypoints.size() - 1).latitude + "," + allwaypoints.get(allwaypoints.size() - 1).longitude, "driving", false).enqueue(new Callback<Example>() {
                    @Override
                    public void onResponse(Call<Example> call, Response<Example> response) {
                        if (response.isSuccessful()) {
                            endDateTime = new DateTime();
                            period = new Period(startDateTime, endDateTime);
                            Log.i("Time", period.getHours() + ":" + period.getMinutes() + ":" + period.getSeconds());
                            tvTotalTime.setText(period.getHours() + ":" + period.getMinutes());
                            for (int i = 0; i < response.body().getRoutes().size(); i++) {
                                String distance = response.body().getRoutes().get(i).getLegs().get(i).getDistance().getText();
                                distanceValue += response.body().getRoutes().get(i).getLegs().get(i).getDistance().getValue() / 1000;
                                tvTotalDistance.setText(distanceValue + "");
//                                if (!response.body().getRoutes().get(0).getOverviewPolyline().getPoints().isEmpty() && response.body().getRoutes().get(0).getOverviewPolyline().getPoints().length() > 0) {
//                                    String encodedString = response.body().getRoutes().get(0).getOverviewPolyline().getPoints();
//                                    List<LatLng> list = decodePoly(encodedString);
//                                    googleMap.clear();
//                                    line = googleMap.addPolyline(new PolylineOptions()
//                                            .addAll(list)
//                                            .width(10)
//                                            .color(Color.parseColor("#f8923d"))
//                                            .geodesic(true)
//                                    );
//                                }
                            }
                            TotalAmount = baseFare + period.getMinutes() * costPerMinute + (distanceValue * costPerKM);
                            tvTotalAmount.setText(String.format("%.2f", TotalAmount));
                            tvTotalDistance.setTag(distanceValue);
                            taxiMeterData.setTotalAmount(Double.parseDouble(tvTotalAmount.getText().toString()));
                            taxiMeterData.setTotalDistance(Math.round(distanceValue));
                            endDateTime = new DateTime();
                            taxiMeterData.setEndTime(endDateTime.toString("yyyy-MM-dd hh:mm:ss"));
                            taxiMeterData.setMobileNumber_isTripDone(taxiMeterData.getMobileNumber() + "_" + taxiMeterData.isTripDone());
                        }
                    }

                    @Override
                    public void onFailure(Call<Example> call, Throwable t) {
                        if (!TextUtils.isEmpty(t.getMessage())) {
                            Log.e("Error at", t.getMessage().toString());
                        }
                    }
                });
            }
        }
    }


    private void StopRiderDialog(Context context) {
        dialogStopDetails = new Dialog(context);
        dialogStopDetails.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogStopDetails.setContentView(R.layout.dialog_askstopride);
        dialogStopDetails.setCancelable(false);
        dialogStopDetails.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        btnStopTrip = (Button) dialogStopDetails.findViewById(R.id.dialog_stoprides_yes);
        btnStopCancelTrip = (Button) dialogStopDetails.findViewById(R.id.dialog_stoprides_no);
        btnStopTrip.setOnClickListener(this);
        btnStopCancelTrip.setOnClickListener(this);
        dialogStopDetails.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        Window window = dialogStopDetails.getWindow();
        window.setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
    }

    private List<LatLng> decodePoly(String encoded) {
        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }


    private synchronized void CalculateDistances() {
        if (mLocations != null && mLocations.size() > 1) {
            List<LatLng> alllpoints = new ArrayList<>(mLocations);
            Routing routing = new Routing.Builder()
                    .travelMode(Routing.TravelMode.DRIVING).avoid(AbstractRouting.AvoidKind.FERRIES)
                    .withListener(new RoutingListener() {
                        @Override
                        public void onRoutingFailure(RouteException e) {
//                            if (!TextUtils.isEmpty(e.getMessage())) {
//                                Log.e("Error At", e.getMessage());
//                            }
                        }

                        @Override
                        public void onRoutingStart() {

                        }

                        @Override
                        public void onRoutingSuccess(ArrayList<Route> arrayList, int i) {
                            if (!arrayList.isEmpty() && arrayList.size() > 0) {
                                String distanceText = arrayList.get(0).getDistanceText();
                                double distanceValue = arrayList.get(0).getDistanceValue() / 1000;
                                endDateTime = new DateTime();
                                period = new Period(startDateTime, endDateTime);
                                Log.i("Time", period.getHours() + ":" + period.getMinutes() + ":" + period.getSeconds());
                                TotalAmount = baseFare + period.getMinutes() * costPerMinute + (distanceValue * costPerKM);
                                tvTotalTime.setText(period.getHours() + ":" + period.getMinutes());
                                tvTotalAmount.setText(String.format("%.2f", TotalAmount));
                                tvTotalDistance.setText(distanceText);
                                tvTotalDistance.setTag(distanceValue);
                                taxiMeterData.setEndTime(endDateTime.toString("yyyy-MM-dd hh:mm:ss"));
                                taxiMeterData.setTotalAmount(Double.parseDouble(tvTotalAmount.getText().toString()));
                                taxiMeterData.setTotalDistance(Math.round(totalDistance / 1000));
                            }
                        }

                        @Override
                        public void onRoutingCancelled() {

                        }
                    })
                    .waypoints(alllpoints).key(ApplicationConstants.API_KEY)
                    .build();
            routing.execute();
            //    upDateMeterTripData();
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        this.googleMap.setMyLocationEnabled(true);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}
                    , LOCATION_REQUEST_CODE);
        } else {
            setLastKnownLoc();
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if (mapView != null)
            mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        if (checkGPSReceiver != null) {
            unregisterReceiver(checkGPSReceiver);
        }

    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }


    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private synchronized void upDateMeterTripData(final boolean isDOne) {
        Log.e(TAG,"upDateMeterTripData called");

        if (taxiMeterData != null) {
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            final DatabaseReference reference = firebaseDatabase.getReference();
            Query query = reference.child("taxifare_eta").orderByChild("meterId").equalTo(taxiMeterData.getMeterId());
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.e(TAG,"upDateMeterTripData => called");
                    if (taxiMeterData != null) {
                        Log.e(TAG,"upDateMeterTripData => if 1");
                        Log.e(TAG,"Taximeter data => "+new Gson().toJson(taxiMeterData));
                        DataSnapshot nodeDataSnapshot = dataSnapshot.getChildren().iterator().next();
                        String key = nodeDataSnapshot.getKey(); // this key is `K1NRz9l5PU_0CFDtgXz`
                        String path = "/" + dataSnapshot.getKey() + "/" + key;
                        HashMap<String, Object> result = new HashMap<>();
                        result.put("startTime", taxiMeterData.getStartTime());
                        result.put("endTime", taxiMeterData.getEndTime());
                        result.put("baseFare", taxiMeterData.getBaseFare());
                        result.put("costPerMinute", taxiMeterData.getCostPerMinute());
                        result.put("totalDistance", taxiMeterData.getTotalDistance());
                        result.put("TotalAmount", taxiMeterData.getTotalAmount());
                        result.put("mobileNumber", taxiMeterData.getMobileNumber());
                        result.put("isTripDone", isDOne);
                        result.put("mobileNumber_isTripDone", taxiMeterData.getMobileNumber_isTripDone());
                        result.put("last_lat",taxiMeterData.getLastLat());
                        result.put("last_lng",taxiMeterData.getLastLng());
                        reference.child(path).updateChildren(result);
                        Log.e(TAG,"upDateMeterTripData UPDATE DONE =>");
                        Log.e(TAG,"isDOne => "+isDOne);
                        if(isDOne)
                            taxiMeterData=null;
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    if (databaseError != null) {
                        Log.e("upDateMeterTripData Error", databaseError.getMessage());
                    }
                }
            });
        }
    }

    private Location getLastBestLocation() throws SecurityException {
        Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Location locationNet = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        long GPSLocationTime = 0;
        if (null != locationGPS) {
            GPSLocationTime = locationGPS.getTime();
        }
        long NetLocationTime = 0;
        if (null != locationNet) {
            NetLocationTime = locationNet.getTime();
        }
        if (0 < GPSLocationTime - NetLocationTime) {
            return locationGPS;
        } else {
            return locationNet;
        }

    }

    //Enable Gps

    private void displayLocationSettingsRequest(final Context context) {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000L / 2);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);
        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        resolutionRequired(status);
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                    default:
                        resolutionRequired(status);
                        break;
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case LOCATION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    displayLocationSettingsRequest(this);
                }
        }
    }

    private void proceedFurther() throws SecurityException {
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        String locationProvider = locationManager.getBestProvider(criteria, false);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, (LocationListener) this);
        locationManager.requestLocationUpdates(locationProvider, 0, 1, (LocationListener) this);
    }

    private void resolutionRequired(Status status) {
        try {
            status.startResolutionForResult(this, REQUEST_CHECK_SETTINGS);
        } catch (IntentSender.SendIntentException e) {
            Log.d("error", e.getMessage());
        }
    }

    private void setLastKnownLoc() {
        Location loc = getLastBestLocation();
        double currentLatitude = 17.473566F;
        double currentLongitude = 78.570368F;
        if (loc != null) {
            currentLatitude = loc.getLatitude();
            currentLongitude = loc.getLongitude();
        }
        animateCameraToLoc(new LatLng(currentLatitude, currentLongitude));
    }

    private void animateCameraToLoc(LatLng latLng) {
        LatLng currentLocation = new LatLng(latLng.latitude, latLng.longitude);
        googleMap.addMarker(new MarkerOptions().position(currentLocation).title("Source")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.car)));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(16));
    }

    @Override
    public void onRideEnd() {
        try {
            locationManager.removeUpdates(this);
        } catch (SecurityException ignored) {
            Log.d("Error", ignored.getMessage());
        }
        travelledLocations.clear();
    }

    @Override
    public void onRideStart() {
        googleMap.clear();
        travelledLocations.clear();
        totalDistanceCovered = 0F;
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        try {
            proceedFurther();
        } catch (SecurityException ignored) {
            Log.d("Error", ignored.getMessage());
        }
    }

    @Override
    public void showToast(String message) {
        if (!isStart) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    }
}
