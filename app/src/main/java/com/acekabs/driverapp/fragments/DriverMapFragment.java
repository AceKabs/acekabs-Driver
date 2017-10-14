package com.acekabs.driverapp.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.acekabs.driverapp.ApplicationConstants;
import com.acekabs.driverapp.JSONParser;
import com.acekabs.driverapp.R;
import com.acekabs.driverapp.activity.PaymentActivity;
import com.acekabs.driverapp.activity.Profile;
import com.acekabs.driverapp.activity.TaxiMeter;
import com.acekabs.driverapp.adapters.SideMenuAdapter;
import com.acekabs.driverapp.base.BaseFragment;
import com.acekabs.driverapp.constants.Constants;
import com.acekabs.driverapp.custom.FontManager;
import com.acekabs.driverapp.db.DBAdapter;
import com.acekabs.driverapp.db.SessionManager;
import com.acekabs.driverapp.entities.AceKabsDriver;
import com.acekabs.driverapp.firebase.FirebaseDriverUtil;
import com.acekabs.driverapp.firebase.IFirebaseCallback;
import com.acekabs.driverapp.interfaces.DriverConstants;
import com.acekabs.driverapp.pojo.FareDetails;
import com.acekabs.driverapp.pojo.FcmRequestObject;
import com.acekabs.driverapp.pojo.MenuData;
import com.acekabs.driverapp.pojo.RideSummaryData;
import com.acekabs.driverapp.pojo.RouteDetails;
import com.acekabs.driverapp.pojo.StatusChangedData;
import com.acekabs.driverapp.restclient.RestApiService;
import com.acekabs.driverapp.services.NetWorkConnection;
import com.acekabs.driverapp.ui.CustomMap;
import com.acekabs.driverapp.utils.ApiUtils;
import com.acekabs.driverapp.utils.CommonUtilities;
import com.acekabs.driverapp.utils.SharePreferanceWrapperSingleton;
import com.acekabs.driverapp.utils.UtilityMethods;
import com.acekabs.driverapp.utils.WakeLocker;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
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
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import io.github.krtkush.lineartimer.LinearTimer;
import io.github.krtkush.lineartimer.LinearTimerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.acekabs.driverapp.utils.CommonUtilities.EXTRA_MESSAGE;

/**
 * Created by Adee09 on 3/12/2017.
 */

public class DriverMapFragment extends BaseFragment implements LocationListener, OnMapReadyCallback, View.OnClickListener, RadioGroup.OnCheckedChangeListener, AdapterView.OnItemClickListener {
    /**
     * Constant used in the location settings dialog.
     */
    private static final String TAG = "DriverMapFragment";
    private static final long FASTEST_INTERVAL = 1000 * 5;
    private static final long DISPLACEMENT = 1000 * 2;//10000
    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    private static final int ACCESS_FINE_LOCATION_INTENT_ID = 3;
    private static final String BROADCAST_ACTION = "android.location.PROVIDERS_CHANGED";
    private static GoogleApiClient mGoogleApiClient;
    //LinearLayout slide_profile, slide_home;
    //TextView profile_tv;
    SessionManager msession;
    String SESSION_EMAIL = "", SESSION_TOKEN = "", SESSION_MOBILE = "";
    JSONParser jsonParser = new JSONParser();
    ImageButton mySwitch;
    TelephonyManager telephonyManager;
    String IMEI = "";
    boolean isOnline = false;
    String firstName = "", lastName = "", phoneNumber = "", emailID = "", compName = "", compCity = "", deviceInfo = "",
            country = "", images = "", activated = "", vehiclePhotoDoc = "", plateNumberDoc = "", driverPhotoDoc = "",
            driverLicenseDoc = "", driverIDCardDocFront = "", driverIDCardDocBack = "", driverPassportDocFront = "",
            driverPassportDocBack = "", carRegistrationDocFront = "", carRegistrationDocBack = "", vehicleInsuranceDoc = "",
            Pincode = "", CarName = "", CarModel = "", CarColor = "", CarManu_year = "", NoPass = "", Driver_DOB = "",
            Vehicle_URL = "", LicensePlate_URL = "", DriverPhoto_URL = "", DriverLicenseNo_URL = "", IDFront_URL = "", IDBack_URL = "",
            PassportFront_URL = "", PassportBack_URL = "", VehicleRegFront_URL = "", VehicleRegBack_URL = "", Insurence_URL = "",
            regDate = "", Res_PassWord = "", compAddr = "", iMEI = "", latLong = "";
    MapView mMapView;
    DBAdapter mydb;
    NetWorkConnection nc = new NetWorkConnection();
    TextView slide_fanme;
    Button profile_sign_out;
    View home_header;
    ImageView s_menu;
    Cursor mcursor;
    GoogleMap driverMap;
    ImageView mapoptions;
    ImageView mapcurentgpssettings;
    Location mCurrentLocation;
    String fcmToken = "";
    SharePreferanceWrapperSingleton prefrences;
    DatabaseReference ref;
    GeoFire geoFire;
    LinearTimerView linearTimerView;
    LinearTimer linearTimer;
    LocationManager locationManager;
    private ArrayList<LatLng> travelledLocations; //added
    private float totalDistanceCovered = 0;//added
    private DrawerLayout mDrawerLayout;
    private String passengerMobileNumber = "";
    private String passengerFcmToken = "";
    private FcmRequestObject fcmRequestObject;
    private Dialog rideRequestDialog;
    private Context context;
    /********
     * handle fcm message for ride now
     ***/
    private CustomMap mapView;
    private GoogleMap googleMap;
    private TextView rideTime;
    private TextView rideAddress;
    private TextView rideCharges;
    private TextView rideTimer;
    private Button btnAccept;
    private RideSummaryData invoiceData;
    /**
     * Receiving push messages
     */
    private boolean isSmsSend = true;
    private Button btnDecline;
    private LocationSettingsRequest mLocationSettingsRequest;
    /*Declare variables for show ride details */
    private Button btnCancelRider;
    private Button btnCallRider;
    private Button btnRiderLocated;
    private LinearLayout rideLayout;
    private LinearLayout rideDetails;
    private ImageView rideNavigation;
    private TextView passengerName;
    private TextView passngerAddress;
    private RelativeLayout headerLayout;
    private TextView menu_title;
    /*Declare variable for rider located dialog*/
    private Dialog dialogRiderLocated;
    private Button btnLocated;
    private Button btnNotLocated;
    /*Declare variable for cancel ride dialog*/
    private Dialog dialogCancelRide;
    private Button btncancelrideyes;
    private Button btncancelrideno;
    /*Declare variable for reason for cancel ride dialog*/
    private Dialog dialogcancelridereason;
    private RadioGroup reasonchoices;
    private RadioButton radiodonotcharge;
    private RadioButton radionotshow;
    private RadioButton radiocancel;
    private RadioButton radioaddress;
    private RadioButton radioothers;
    private Button btnSend;
    /*Declare Variable for fare calculation*/
    private double baseFare;
    private double costPerKM;
    private double costPerMinute;
    private double surgeRate;
    private String cabtype;
    private String cabnumber;
    private String cabmodel;
    private LinearLayout startRideLayout;
    private Button btnStartRides;
    private Button btnCancelRides;
    private Dialog dialogStartRide;
    private Button btnAcceptTrip;
    private Button btnCancelTrip;
    private LinearLayout stopRideLayout;
    private Button btnStopRides;
    private FareDetails fareDetails;
    private ArrayList<RouteDetails> routeDetails;
    private ArrayList<StatusChangedData> statusChangedData;
    private SimpleDateFormat sdf;
    /*Declare variables for fill data in fare calculations*/
    private String driverid;
    private String drivername;
    private String driveremailid;
    private String drivermobile;
    private String driverphoto;
    private String passengerid;
    private String passengername;
    private String passengeremailid;
    private boolean isRideNotCancelled = true;
    private boolean isRideCancelledByDriver = false;
    private String passengermobile;
    private Dialog dialogFareDetails;
    private TextView tvtotalfare;
    private TextView tvBaseFareAmount;
    private TextView extraKmFareAmount;
    private TextView extramTimeAmount;
    private TextView tvTotalFareAmount;
    private TextView tvServiceAmount;
    private ProgressDialog dialog;
    private RestApiService mAPIService;
    private double totalFareAmount;
    private Button btnSubmit;
    private Dialog dialogStopDetails;
    private Button btnStopTrip;
    private Button btnStopCancelTrip;
    private ArrayList<Polyline> polylines = new ArrayList<>();
    private String passengerMailId;
    private String cancelReasonDescription = "";
    private ListView sideMenu;
    private ArrayList<MenuData> menuDatas;
    private String[] titles;
    private TypedArray icons;
    private SideMenuAdapter adapter;
    private ImageButton myMeterSwitch;
    private String isDriverFree = "yes";
    private ProgressDialog pDialog;
    private MediaPlayer mp;
    //private boolean isAcceptedRide = true;
    private BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //if(isAcceptedRide) {

            fcmRequestObject = (FcmRequestObject) intent.getExtras().getSerializable(EXTRA_MESSAGE);
            Log.e(TAG, "fcmRequestObject=>" + new Gson().toJson(fcmRequestObject));
            //null check
            if (fcmRequestObject == null)
                return;

            if (!fcmRequestObject.getStatus().equals("rider_cancelled")) {//ride not cancelled
                passengerMobileNumber = fcmRequestObject.getUserMobileNumber();
                passengeremailid = fcmRequestObject.getUserEmailID();
                passengername = fcmRequestObject.getUsername();
                btnAccept.setEnabled(false);
                new getPassengerFcmToken().execute(passengerMobileNumber);//get passanger token and show accept dialog

                isRideNotCancelled = true;
                Log.e(TAG, "isRideNotCancelled=>" + true);
                // Waking up mobile if it is sleeping
                WakeLocker.acquire(context);
                try {
                    // new getPassengerFcmToken().execute();
                    rideAddress.setText(fcmRequestObject.getPickUpLocation().toString());
                    rideCharges.setText(fcmRequestObject.getSurge_multiplier() + " x Surge Charges");
                    rideCharges.setVisibility(View.GONE);
                    String distance = fcmRequestObject.getEstimateDistance() != null ? fcmRequestObject.getEstimateDistance() : "";
                    if (fcmRequestObject.getPickUpLatitude() != 0 && fcmRequestObject.getPickUpLongitude() != 0) {
                        // Creating a LatLng object for the current location
                        LatLng latLng = new LatLng(fcmRequestObject.getPickUpLatitude(), fcmRequestObject.getPickUpLongitude());
                        // Showing the current location in Google Map
                        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                        // Zoom in the Google Map
                        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                    }
                    int timer = fcmRequestObject.getTimer();
                    if (timer > 0) {
                        timer = timer * 1000;
                    } else {
                        timer = 15000;
                    }
                    rideTime.setText(distance);
                    linearTimerView = (LinearTimerView) rideRequestDialog.findViewById(R.id.linearTimer);
                    linearTimer = new LinearTimer.Builder()
                            .linearTimerView(linearTimerView)
                            .duration(10 * 1000)
                            .build();
                    if (linearTimerView.getAnimation() != null) {
                        linearTimerView.getAnimation().setRepeatMode(1);
                        linearTimerView.getAnimation().setRepeatCount(100);
                    }
                    CountDownTimer Count = new CountDownTimer(timer, 1000) {
                        public void onTick(long millisUntilFinished) {
                            int seconds = (int) ((millisUntilFinished / 1000));
                            rideTimer.setText(seconds + " seconds " + " Remaining");
                        }

                        public void onFinish() {
                            rideTimer.setText("Finish");
                            if (rideRequestDialog != null && rideRequestDialog.isShowing()) {
                                rideRequestDialog.dismiss();
                            }
                        }
                    };
                    Count.start();
                    linearTimer.startTimer();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else if (fcmRequestObject.getStatus().equals("rider_cancelled")) {//ride cancel by rider
                Log.e(TAG, "mHandleMessageReceiver else if=>");
                StatusChangedData st = new StatusChangedData();
                st.setStatus("rider_cancelled");
                st.setLocation(mCurrentLocation.getLatitude() + "," + mCurrentLocation.getLongitude());
                st.setTime(sdf.format(new Date()));
                statusChangedData.add(st);
                isRideNotCancelled = true;
                Log.e(TAG, "isRideNotCancelled=>" + true);
                Toast.makeText(context, "Ride Cancelled by rider.", Toast.LENGTH_SHORT).show();


                clearAllDataAndSetViewAfterCancell();

              /*  if (fareDetails != null)
                {
                    fareDetails.setTripDone(true);
                }*/
/*
                if (fareDetails != null && !routeDetails.isEmpty() && !statusChangedData.isEmpty()) {
                    Log.e(TAG, "if 2..1=>" );
                   // if (statusChangedData.get(0).getStatus().equals("driver_accepted")) {
                     //   Log.e(TAG, "if 2 if 2..=>" );
                     *//*   if (routeDetails.size() >= 1) {
                            Log.e(TAG, "if 2 if 3..=>" );
                            calculateDistanceAndFare(routeDetails.get(0).getLocation(), routeDetails.get(routeDetails.size() - 1).getLocation());
                        }*//*
                    //}
                }*/
            }
            WakeLocker.release();
            //}
        }
    };
    private String currency = "";
    private String driverId;
    private TextView tvcurrencyone;
    private TextView tvcurrencytwo;
    private TextView tvcurrencythree;
    private TextView tvcurrencyfour;
    private TextView tvcurrencyfive;
    private TextView tvcurrencysix;
    private TextView tvcurrencyseven;
    //Run on UI
    private Runnable sendUpdatesToUI = new Runnable() {
        public void run() {
            showSettingDialog();
        }
    };
    /* Broadcast receiver to check status of GPS */
    private BroadcastReceiver gpsLocationReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            //If Action is Location
            if (intent.getAction().matches(BROADCAST_ACTION)) {
                locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                //Check if GPS is turned ON or OFF
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, FASTEST_INTERVAL, DISPLACEMENT, DriverMapFragment.this);
                    Log.e("About GPS", "GPS is Enabled in your device");
                    updateGPSStatus("GPS is Enabled in your device");
                    if (mp != null) {
                        mp.release();
                        mp = null;
                    }
                } else {
                    //If GPS turned OFF show Location Dialog
                    new Handler().postDelayed(sendUpdatesToUI, 10);
                    // showSettingDialog();
                    updateGPSStatus("GPS is Disabled in your device");
                    Log.e("About GPS", "GPS is Disabled in your device");
                    try {
                        mp = MediaPlayer.create(DriverMapFragment.this.context, R.raw.alarm);
                        mp.setLooping(true);
                        mp.start();
                    } catch (Exception e) {
                        Log.e(TAG, "GPS Media play exception" + e.toString());
                    }


                }

            }
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_home_screen, container, false);
        context = getActivity();
        travelledLocations = new ArrayList<>();
        initGoogleAPIClient();//Init Google API Client
        checkPermissions();//Check Permission
        dialog = new ProgressDialog(context);
        dialog.setMessage("Please wait while calculating  trip charges.");
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        pDialog = new ProgressDialog(context);
        pDialog.setMessage("Please wait while getting driver details.");
        pDialog.setCancelable(false);
        prefrences = SharePreferanceWrapperSingleton.getSingletonInstance();
        prefrences.setPref(context);
        prefrences.setEditor();
        sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss", Locale.ENGLISH);
        Log.e("DateTime", sdf.format(new Date()));
        if (!TextUtils.isEmpty(prefrences.getStringValueFromSharedPref(DriverConstants.fcmToken))) {
            Log.e("FCM Token", prefrences.getStringValueFromSharedPref(DriverConstants.fcmToken));
        }
        createRideRequestDialog(context);
        mydb = DBAdapter.getInstance(context);
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        profile_sign_out = (Button) view.findViewById(R.id.profile_sign_out);
        msession = new SessionManager(context);
        HashMap<String, String> user = msession.getUserDetails();
        SESSION_EMAIL = user.get(SessionManager.KEY_NAME);
        SESSION_TOKEN = user.get(SessionManager.KEY_TOKEN);
        Log.e("FLOW" , "SEssionToken IS : "+SESSION_TOKEN);
        telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        IMEI = UtilityMethods.getIMEI(getActivity());// telephonyManager.getDeviceId();
        mDrawerLayout = (DrawerLayout) view.findViewById(R.id.drawerLayout);
        myMeterSwitch = (ImageButton) view.findViewById(R.id.myMeterSwitch);
        myMeterSwitch.setOnClickListener(this);
        myMeterSwitch.setEnabled(true);
        slide_fanme = (TextView) view.findViewById(R.id.slide_fanme);
        s_menu = (ImageView) view.findViewById(R.id.menu_menu);
        mySwitch = (ImageButton) view.findViewById(R.id.mySwitch);
        mySwitch.setTag("off");
        routeDetails = new ArrayList<>();
        statusChangedData = new ArrayList<>();
        /** added this code to handle location updates on fresh launch of application whether GPS is enabled 09/09/2017*/
        if (locationManager == null) {
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            if (locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Log.i("Start Location Updates", "Location Update");
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, FASTEST_INTERVAL, DISPLACEMENT, DriverMapFragment.this);
            }
        } else {
            if (locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Log.i("Start Location Updates", "Location Update");
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, FASTEST_INTERVAL, DISPLACEMENT, DriverMapFragment.this);
            }
        }
        if (SESSION_TOKEN.equalsIgnoreCase("1")) {
            if (prefrences.getBooleanValueFromSharedPref("driverStatus")) {
                mySwitch.setBackgroundResource(R.drawable.online);
                mySwitch.setTag("on");
                isOnline = true;
            }
            mySwitch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mySwitch.getTag().equals("off")) {
                        mySwitch.setBackgroundResource(R.drawable.online);
                        mySwitch.setTag("on");
                        isOnline = true;
                        prefrences.setBooleanValueToSharedPref("driverStatus", true);
                        if (locationManager == null) {
                            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                            if (locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                                Log.i("Start Location Updates", "Location Update");
                                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, FASTEST_INTERVAL, DISPLACEMENT, DriverMapFragment.this);
                            }
                        } else {
                            if (locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                                Log.i("Start Location Updates", "Location Update");
                                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, FASTEST_INTERVAL, DISPLACEMENT, DriverMapFragment.this);
                            }
                        }
                    } else if (mySwitch.getTag().equals("on")) {
                        mySwitch.setBackgroundResource(R.drawable.offline);
                        mySwitch.setTag("off");
                        isOnline = false;
                        prefrences.setBooleanValueToSharedPref("driverStatus", false);
                        deleteGeoFireData();
                        if (locationManager != null) {
                            locationManager.removeUpdates(DriverMapFragment.this);
                        }
                    }
                }
            });
        }
        mapoptions = (ImageView) view.findViewById(R.id.mapoptions);
        mapoptions.setOnClickListener(this);
        mapcurentgpssettings = (ImageView) view.findViewById(R.id.mapcurentgpssettings);
        mapcurentgpssettings.setOnClickListener(this);
        createFareDetailsDialog(context);


        try {
            Cursor mcursor = mydb.RecordExists(SESSION_EMAIL);
            if (mcursor.getCount() > 0) {
                if (mcursor.moveToFirst()) {
                    SESSION_MOBILE = mcursor.getString(mcursor.getColumnIndex("driver_mobileno"));
                    prefrences.setValueToSharedPref(Constants.mobileNumber, SESSION_MOBILE);
                }
                if (SESSION_TOKEN.equalsIgnoreCase("1")) {
                    getPendingTripDetails();
                }
            }
            // Validating  Driver Status
            if (SESSION_TOKEN.equalsIgnoreCase("1")) {
                if (mcursor.getCount() > 0) {
                    mydb.UpdatedDriver_Status(SESSION_EMAIL);
                    myMeterSwitch.setEnabled(true);
                    if (isNetworkAvailable()) {
                        pDialog.show();
                        FirebaseDriverUtil.getDriversByMobileNumber("location_id_1", SESSION_MOBILE, new IFirebaseCallback<List<AceKabsDriver>>() {
                            @Override
                            public void onDataReceived(List<AceKabsDriver> data) {
                                if (data != null && data.size() > 0) {
                                    driverId = data.get(0).getDriverId();
                                    baseFare = data.get(0).getBaseFare();
                                    costPerKM = data.get(0).getCostPerKM();
                                    costPerMinute = data.get(0).getCostPerMinute();
                                    surgeRate = data.get(0).getSurgeRate();
                                    cabtype = data.get(0).getCarType();
                                    cabmodel = data.get(0).getCarModelName();
                                    cabnumber = data.get(0).getCarRegistrationNumber();
                                    driverid = data.get(0).getDriverId();
                                    drivername = data.get(0).getDriverFullName();
                                    driveremailid = data.get(0).getEmailId();
                                    drivermobile = data.get(0).getMobileNumber();
                                    driverphoto = data.get(0).getDriverPhotoURL();
                                    isDriverFree = data.get(0).getIsDriverFree();
                                    currency = data.get(0).getCurrency();
                                    updateCurrencySymbol(currency);
                                    if (pDialog != null && pDialog.isShowing()) {
                                        pDialog.dismiss();
                                    }
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
                    if (TextUtils.isEmpty(prefrences.getStringValueFromSharedPref(DriverConstants.fcmToken).trim())) {
                        if (mcursor.moveToFirst()) {
                            SESSION_MOBILE = mcursor.getString(mcursor.getColumnIndex("driver_mobileno"));
                        }
                        if (isNetworkAvailable()) {
                            new saveFcmToken().execute();
                        }
                    } else {
                        Log.i("FCM Token", prefrences.getStringValueFromSharedPref(DriverConstants.fcmToken));
                    }
                } else {
                    if (isNetworkAvailable())
                    {
                        new PendingImages().execute();
                    }
                    else
                    {
                        ShowAlert1("ERROR !!", "Please Check Network Connection !!");
                    }
                }
            } else if (SESSION_TOKEN.equalsIgnoreCase("2")) {

                ShowAlertIMEI("ERROR !!", "You are Currently in Offline, \n Please update IMEI number in Profile Page ");

            } else if ((SESSION_TOKEN.equalsIgnoreCase("3"))) {

                ShowAlert1("ERROR !!", "Some Images are not approved by Support Team, \n Please reUpload in Profile Page");

            } else if ((SESSION_TOKEN.equalsIgnoreCase("4"))) {

                ShowAlert1("", "Currently your vehicle is under screening,you will not get any rides for now You will hit road soon");

            } else if ((SESSION_TOKEN.equalsIgnoreCase("5"))) {
                ShowAlert1("", "Currently your vehicle is under screening,you will not get any rides for now You will hit road soon");
            } else {

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        profile_sign_out.setOnClickListener(this);
        s_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerLayout.openDrawer(Gravity.LEFT);

            }
        });

        try {
            mcursor = mydb.getDriver_reg_Email(SESSION_EMAIL);
            if (mcursor != null) {
                if (mcursor.moveToFirst()) {
                    String d_fname = mcursor.getString(mcursor.getColumnIndex("driver_fname"));
                    slide_fanme.setText(d_fname);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

//        slide_home.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                mDrawerLayout.closeDrawer(Gravity.LEFT);
//            }
//        });

        /**************************Start map view intergration*************************************/
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mMapView = (MapView) view.findViewById(R.id.map);
        mMapView.getMapAsync(this);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume(); // needed to get the map to display immediately
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        /**************************End map view intergration*************************************/
        if (!isGooglePlayServicesAvailable()) {
            getActivity().finish();
        }
        /*initialise geo fire for save driver current location*/
        ref = FirebaseDatabase.getInstance().getReference(ApplicationConstants.GEOFIRE_NODE);
        geoFire = new GeoFire(ref);
        /***handle FCM notification ***/
        context.registerReceiver(mHandleMessageReceiver, new IntentFilter(CommonUtilities.DISPLAY_MESSAGE_ACTION));
        btnCancelRider = (Button) view.findViewById(R.id.btnCancelRider);
        btnCallRider = (Button) view.findViewById(R.id.btnCallRider);
        btnRiderLocated = (Button) view.findViewById(R.id.btnRiderLocated);
        rideLayout = (LinearLayout) view.findViewById(R.id.rideLayout);
        rideDetails = (LinearLayout) view.findViewById(R.id.pickupDetails);
        rideNavigation = (ImageView) view.findViewById(R.id.navigationview);
        passengerName = (TextView) view.findViewById(R.id.passenegrName);
        passngerAddress = (TextView) view.findViewById(R.id.passenegrAddress);
        headerLayout = (RelativeLayout) view.findViewById(R.id.headerLayouts);
        menu_title = (TextView) view.findViewById(R.id.menu_title);
        btnCallRider.setOnClickListener(this);
        btnCallRider.setOnClickListener(this);
        btnCancelRider.setOnClickListener(this);
        btnRiderLocated.setOnClickListener(this);
        rideNavigation.setOnClickListener(this);
        rideLayout.setVisibility(View.GONE);
        rideDetails.setVisibility(View.GONE);
        createRiderLocatedDialog(context);
        createCancelRideRequestDialog(context);
        createCancelReasonRideRequestDialog(context);
        StartRiderDialog(context);
        StopRiderDialog(context);
        startRideLayout = (LinearLayout) view.findViewById(R.id.startRideLayout);
        btnStartRides = (Button) view.findViewById(R.id.btnStartRides);
        btnCancelRides = (Button) view.findViewById(R.id.btnCancelRides);
        btnStartRides.setOnClickListener(this);
        btnCancelRides.setOnClickListener(this);
        stopRideLayout = (LinearLayout) view.findViewById(R.id.stopRideLayout);
        btnStopRides = (Button) view.findViewById(R.id.btnStopRides);
        btnStopRides.setOnClickListener(this);
        sideMenu = (ListView) view.findViewById(R.id.sideMenu);
        sideMenu.setOnItemClickListener(this);
        titles = context.getResources().getStringArray(R.array.menuTitles);
        icons = context.getResources().obtainTypedArray(R.array.menuIcons);
        menuDatas = new ArrayList<>();
        for (int i = 0; i < titles.length; i++) {
            MenuData m = new MenuData();
            m.setMenuTitle(titles[i]);
            m.setMenuIcon(icons.getResourceId(i, -1));
            menuDatas.add(m);
        }
        adapter = new SideMenuAdapter(menuDatas, context);
        sideMenu.setAdapter(adapter);
        // recycle the array
        icons.recycle();
        Log.e(TAG, "onCreate()");

        return view;
    }

    /**
     * added code to handle gps status properly
     */
     /* Initiate Google API Client  */
    private void initGoogleAPIClient() {
        //Without Google API Client Auto Location Dialog will not work
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    /* Check Location Permission for Marshmallow Devices */
    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(context,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED)
                requestLocationPermission();
            else
                showSettingDialog();
        } else
            showSettingDialog();

    }

    /*  Show Popup to access User Permission  */
    private void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    ACCESS_FINE_LOCATION_INTENT_ID);

        } else {
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    ACCESS_FINE_LOCATION_INTENT_ID);
        }
    }

    /* Show Location Access Dialog */
    private void showSettingDialog() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);//Setting priotity of Location request to high
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);//5 sec Time interval for location update
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true); //this is the key ingredient to show dialog always when GPS is off

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult((Activity) context, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            e.printStackTrace();
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        });
    }

    /* On Request permission method to check the permisison is granted or not for Marshmallow+ Devices  */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case ACCESS_FINE_LOCATION_INTENT_ID: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    //If permission granted show location dialog if APIClient is not null
                    if (mGoogleApiClient == null) {
                        initGoogleAPIClient();
                        showSettingDialog();
                    } else
                        showSettingDialog();


                } else {
                    updateGPSStatus("Location Permission denied.");
                    Toast.makeText(context, "Location Permission denied.", Toast.LENGTH_SHORT).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case RESULT_OK:
                        Log.e("Settings", "Result OK");
                        updateGPSStatus("GPS is Enabled in your device");
                        //startLocationUpdates();
                        break;
                    case RESULT_CANCELED:
                        Log.e("Settings", "Result Cancel");
                        updateGPSStatus("GPS is Disabled in your device");
                        break;
                }
                break;
        }
    }

    //Method to update GPS status text
    private void updateGPSStatus(String status) {

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

    private void createFareDetailsDialog(Context context) {
        dialogFareDetails = new Dialog(context);
        dialogFareDetails.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogFareDetails.setContentView(R.layout.dialog_faredeatails);
        dialogFareDetails.setCancelable(false);
        Window window = dialogFareDetails.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        window.setGravity(Gravity.CENTER);
        tvtotalfare = (TextView) dialogFareDetails.findViewById(R.id.tvtotalfare);
        tvBaseFareAmount = (TextView) dialogFareDetails.findViewById(R.id.tvBaseFareAmount);
        extraKmFareAmount = (TextView) dialogFareDetails.findViewById(R.id.extraKmFareAmount);
        extramTimeAmount = (TextView) dialogFareDetails.findViewById(R.id.extramTimeAmount);
        tvTotalFareAmount = (TextView) dialogFareDetails.findViewById(R.id.tvTotalFareAmount);
        tvServiceAmount = (TextView) dialogFareDetails.findViewById(R.id.tvServiceAmount);
        if (baseFare != 0) {
            tvBaseFareAmount.setText(String.valueOf(baseFare));
        }
        btnSubmit = (Button) dialogFareDetails.findViewById(R.id.btnokay);
        btnSubmit.setOnClickListener(this);
        tvcurrencyone = (TextView) dialogFareDetails.findViewById(R.id.tvcurrecnyone);
        tvcurrencytwo = (TextView) dialogFareDetails.findViewById(R.id.tvcurrecnytwo);
        tvcurrencythree = (TextView) dialogFareDetails.findViewById(R.id.tvcurrecnythree);
        tvcurrencyfour = (TextView) dialogFareDetails.findViewById(R.id.tvcurrecnyfour);
        tvcurrencyfive = (TextView) dialogFareDetails.findViewById(R.id.tvcurrecnyfive);
        tvcurrencysix = (TextView) dialogFareDetails.findViewById(R.id.tvcurrecnysix);
        tvcurrencyseven = (TextView) dialogFareDetails.findViewById(R.id.tvcurrecnyseven);
    }

    private void updateCurrencySymbol(String currency) {
        if (!TextUtils.isEmpty(currency)) {
            switch (currency) {
                case "INR":
                    tvcurrencyone.setText(context.getResources().getString(R.string.rupee));
                    tvcurrencytwo.setText(context.getResources().getString(R.string.rupee));
                    tvcurrencythree.setText(context.getResources().getString(R.string.rupee));
                    tvcurrencyfour.setText(context.getResources().getString(R.string.rupee));
                    tvcurrencyfive.setText(context.getResources().getString(R.string.rupee));
                    tvcurrencysix.setText(context.getResources().getString(R.string.rupee));
                    tvcurrencyseven.setText(context.getResources().getString(R.string.rupee));
                    tvcurrencyone.setTypeface(FontManager.getTypeface(context, FontManager.FONTAWESOME));
                    tvcurrencytwo.setTypeface(FontManager.getTypeface(context, FontManager.FONTAWESOME));
                    tvcurrencythree.setTypeface(FontManager.getTypeface(context, FontManager.FONTAWESOME));
                    tvcurrencyfour.setTypeface(FontManager.getTypeface(context, FontManager.FONTAWESOME));
                    tvcurrencyfive.setTypeface(FontManager.getTypeface(context, FontManager.FONTAWESOME));
                    tvcurrencysix.setTypeface(FontManager.getTypeface(context, FontManager.FONTAWESOME));
                    tvcurrencyseven.setTypeface(FontManager.getTypeface(context, FontManager.FONTAWESOME));
                    break;
                case "LKR":
                    tvcurrencyone.setText(context.getResources().getString(R.string.rupee));
                    tvcurrencytwo.setText(context.getResources().getString(R.string.rupee));
                    tvcurrencythree.setText(context.getResources().getString(R.string.rupee));
                    tvcurrencyfour.setText(context.getResources().getString(R.string.rupee));
                    tvcurrencyfive.setText(context.getResources().getString(R.string.rupee));
                    tvcurrencysix.setText(context.getResources().getString(R.string.rupee));
                    tvcurrencyseven.setText(context.getResources().getString(R.string.rupee));
                    tvcurrencyone.setTypeface(FontManager.getTypeface(context, FontManager.FONTAWESOME));
                    tvcurrencytwo.setTypeface(FontManager.getTypeface(context, FontManager.FONTAWESOME));
                    tvcurrencythree.setTypeface(FontManager.getTypeface(context, FontManager.FONTAWESOME));
                    tvcurrencyfour.setTypeface(FontManager.getTypeface(context, FontManager.FONTAWESOME));
                    tvcurrencyfive.setTypeface(FontManager.getTypeface(context, FontManager.FONTAWESOME));
                    tvcurrencysix.setTypeface(FontManager.getTypeface(context, FontManager.FONTAWESOME));
                    tvcurrencyseven.setTypeface(FontManager.getTypeface(context, FontManager.FONTAWESOME));
                    break;
                case "QAR":
                    tvcurrencyone.setText(context.getResources().getString(R.string.qar));
                    tvcurrencytwo.setText(context.getResources().getString(R.string.qar));
                    tvcurrencythree.setText(context.getResources().getString(R.string.qar));
                    tvcurrencyfour.setText(context.getResources().getString(R.string.qar));
                    tvcurrencyfive.setText(context.getResources().getString(R.string.qar));
                    tvcurrencysix.setText(context.getResources().getString(R.string.qar));
                    tvcurrencyseven.setText(context.getResources().getString(R.string.qar));

                    break;
                case "USD":
                    tvcurrencyone.setText(context.getResources().getString(R.string.dollar));
                    tvcurrencytwo.setText(context.getResources().getString(R.string.dollar));
                    tvcurrencythree.setText(context.getResources().getString(R.string.dollar));
                    tvcurrencyfour.setText(context.getResources().getString(R.string.dollar));
                    tvcurrencyfive.setText(context.getResources().getString(R.string.dollar));
                    tvcurrencysix.setText(context.getResources().getString(R.string.dollar));
                    tvcurrencyseven.setText(context.getResources().getString(R.string.dollar));
                    tvcurrencyone.setTypeface(FontManager.getTypeface(context, FontManager.FONTAWESOME));
                    tvcurrencytwo.setTypeface(FontManager.getTypeface(context, FontManager.FONTAWESOME));
                    tvcurrencythree.setTypeface(FontManager.getTypeface(context, FontManager.FONTAWESOME));
                    tvcurrencyfour.setTypeface(FontManager.getTypeface(context, FontManager.FONTAWESOME));
                    tvcurrencyfive.setTypeface(FontManager.getTypeface(context, FontManager.FONTAWESOME));
                    tvcurrencysix.setTypeface(FontManager.getTypeface(context, FontManager.FONTAWESOME));
                    tvcurrencyseven.setTypeface(FontManager.getTypeface(context, FontManager.FONTAWESOME));
                    break;
            }
        }
    }

    private void createCancelRideRequestDialog(Context ctx) {
        dialogCancelRide = new Dialog(ctx);
        dialogCancelRide.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogCancelRide.setContentView(R.layout.dialog_cancelride);
        dialogCancelRide.setCancelable(false);
        btncancelrideyes = (Button) dialogCancelRide.findViewById(R.id.dialog__cancel_yes);
        btncancelrideno = (Button) dialogCancelRide.findViewById(R.id.dialog_cancel_no);
        btncancelrideyes.setOnClickListener(this);
        btncancelrideno.setOnClickListener(this);
        dialogCancelRide.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        Window window = dialogCancelRide.getWindow();
        window.setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
    }

    private void createCancelReasonRideRequestDialog(Context ctx) {
        dialogcancelridereason = new Dialog(ctx);
        dialogcancelridereason.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogcancelridereason.setContentView(R.layout.dialog_reason_cancel_ride);
        dialogcancelridereason.setCancelable(false);
        dialogcancelridereason.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        Window window = dialogCancelRide.getWindow();
        window.setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        btnSend = (Button) dialogcancelridereason.findViewById(R.id.btnSend);
        btnSend.setOnClickListener(this);
        reasonchoices = (RadioGroup) dialogcancelridereason.findViewById(R.id.cancelchoices);
        reasonchoices.setOnCheckedChangeListener(this);
        radiodonotcharge = (RadioButton) dialogcancelridereason.findViewById(R.id.radiodonotcharge);
        radionotshow = (RadioButton) dialogcancelridereason.findViewById(R.id.radionotshow);
        radiocancel = (RadioButton) dialogcancelridereason.findViewById(R.id.radiocancel);
        radioaddress = (RadioButton) dialogcancelridereason.findViewById(R.id.radioaddress);
        radioothers = (RadioButton) dialogcancelridereason.findViewById(R.id.radioothers);
        reasonchoices.setOnCheckedChangeListener(this);
    }

    private void createRiderLocatedDialog(Context ctx) {
        dialogRiderLocated = new Dialog(ctx);
        dialogRiderLocated.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogRiderLocated.setContentView(R.layout.dialog_riderlocated);
        dialogRiderLocated.setCancelable(false);
        btnLocated = (Button) dialogRiderLocated.findViewById(R.id.dialog_ride_yes);
        btnNotLocated = (Button) dialogRiderLocated.findViewById(R.id.dialog_ride_no);
        btnLocated.setOnClickListener(this);
        btnNotLocated.setOnClickListener(this);
        dialogRiderLocated.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        Window window = dialogRiderLocated.getWindow();
        window.setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
    }

    private void StartRiderDialog(Context ctx) {
        dialogStartRide = new Dialog(ctx);
        dialogStartRide.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogStartRide.setContentView(R.layout.dialog_startride);
        dialogStartRide.setCancelable(false);
        btnAcceptTrip = (Button) dialogStartRide.findViewById(R.id.dialog_rides_yes);
        btnCancelTrip = (Button) dialogStartRide.findViewById(R.id.dialog_rides_no);
        btnAcceptTrip.setOnClickListener(this);
        btnCancelTrip.setOnClickListener(this);
        dialogStartRide.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        Window window = dialogStartRide.getWindow();
        window.setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
    }

    private void deleteGeoFireData() {
        try {
            if (!TextUtils.isEmpty(SESSION_MOBILE)) {
                geoFire.removeLocation(SESSION_MOBILE, new GeoFire.CompletionListener() {
                    @Override
                    public void onComplete(String key, DatabaseError error) {
                        if (error != null) {
                            Log.e("Error in Remove Location", error.getMessage().toString());
                        } else {
                            Log.d("Remove Location", key.toString());
                        }
                    }
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void createRideRequestDialog(Context context) {
        rideRequestDialog = new Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        rideRequestDialog.setContentView(R.layout.dialog_ride_request);
        mapView = (CustomMap) rideRequestDialog.findViewById(R.id.requestMapView);
        mapView.onCreate(rideRequestDialog.onSaveInstanceState());
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMaps) {
                googleMap = googleMaps;
                googleMap.setMyLocationEnabled(true);
            }
        });
        rideTime = (TextView) rideRequestDialog.findViewById(R.id.rideTime);
        rideAddress = (TextView) rideRequestDialog.findViewById(R.id.rideAddress);
        rideCharges = (TextView) rideRequestDialog.findViewById(R.id.rideCharges);
        rideTimer = (TextView) rideRequestDialog.findViewById(R.id.rideTimer);
        btnAccept = (Button) rideRequestDialog.findViewById(R.id.btnAccept);
        btnDecline = (Button) rideRequestDialog.findViewById(R.id.btnDecline);
        btnAccept.setOnClickListener(this);
        btnDecline.setOnClickListener(this);
        linearTimerView = (LinearTimerView) rideRequestDialog.findViewById(R.id.linearTimer);
        linearTimer = new LinearTimer.Builder()
                .linearTimerView(linearTimerView)
                .duration(10 * 1000)
                .build();
        rideRequestDialog.setCancelable(false);
    }


    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart fired ..............");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop fired ..............");

    }

    @Override
    public void onPause() {
        super.onPause();
        if (mapView != null)
            mapView.onPause();
        if (mMapView != null)
            mMapView.onPause();
        if (fareDetails != null) {
            upDateTripData();//update data to firebase
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        context.registerReceiver(gpsLocationReceiver, new IntentFilter(BROADCAST_ACTION));//Register broadcast receiver to check the status of GPS
        Log.e(TAG, "onResume()");
        if (mapView != null)
            mapView.onResume();
        if (mMapView != null)
            mMapView.onResume();
    }

    public void ShowAlert1(String title, String message) {
        final Dialog dialog = new Dialog(context);
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

    public void ShowAlertIMEI(String title, String message) {
        final Dialog dialog = new Dialog(context);
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
                Intent li = new Intent(context, Profile.class);
                li.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(li);

            }
        });
    }

    public void onClick(View v) {
        if (v == myMeterSwitch) {//click on meter switch
            if (SESSION_TOKEN.equals("1")) {
                deleteGeoFireData();
                prefrences.setValueToSharedPref(Constants.rideStatus, "");
                isOnline = false;
                mySwitch.setBackgroundResource(R.drawable.offline);
                mySwitch.setTag("off");
                startActivity(new Intent(context, TaxiMeter.class).putExtra("baseFare", baseFare).putExtra("costPerKM", costPerKM).putExtra("costPerMinute", costPerMinute).putExtra("currency", currency));
            } else if (SESSION_TOKEN.equalsIgnoreCase("2")) {

                ShowAlertIMEI("ERROR !!", "You are Currently in Offline, \n Please update IMEI number in Profile Page ");

            } else if ((SESSION_TOKEN.equalsIgnoreCase("3"))) {

                ShowAlert1("ERROR !!", "Some Images are not approved by Support Team, \n Please reUpload in Profile Page");

            } else if ((SESSION_TOKEN.equalsIgnoreCase("4"))) {

                ShowAlert1("", "Currently your vehicle is under screening,you will not get any rides for now You will hit road soon");

            } else if ((SESSION_TOKEN.equalsIgnoreCase("5"))) {
                ShowAlert1("", "Currently your vehicle is under screening,you will not get any rides for now You will hit road soon");
            }
        } else if (v.getId() == R.id.slide_profile) {
            Intent i1 = new Intent(context, Profile.class);
            context.startActivity(i1);
        } else if (v.getId() == R.id.slide_payment) {
            Intent i1 = new Intent(context, PaymentActivity.class);
            context.startActivity(i1);
        } else if (v.getId() == R.id.slide_history) {
            Intent i1 = new Intent(context, PaymentActivity.class);
            context.startActivity(i1);
        } else if (v.getId() == R.id.profile_sign_out) {
            msession.logoutUser();
            Toast.makeText(context, "You are logging Out ", Toast.LENGTH_SHORT).show();
            prefrences.setBooleanValueToSharedPref("driverStatus", false);
        } else if (mapcurentgpssettings == v) {
            if (driverMap != null) {
                if (mCurrentLocation != null) {
                    // Creating a LatLng object for the current location
                    LatLng latLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());

                    // Showing the current location in Google Map
                    driverMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

                    // Zoom in the Google Map
                    driverMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                }
            }
        } else if (mapoptions == v) {
            if (driverMap != null) {
                if (driverMap.getMapType() == GoogleMap.MAP_TYPE_NORMAL) {
                    driverMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                    mapoptions.setImageResource(R.drawable.globeblue);
                } else {
                    driverMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    mapoptions.setImageResource(R.drawable.globeblack);
                }
            }
        } else if (v == btnAccept) {//driver accept ride
            // isAcceptedRide = false;
            if (rideRequestDialog != null && rideRequestDialog.isShowing()) {
                rideRequestDialog.dismiss();
            }
            try {
                myMeterSwitch.setVisibility(View.INVISIBLE); //added as per discussion on 06/09/2017
                travelledLocations.add(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()));
                fareDetails = new FareDetails();
                StatusChangedData st = new StatusChangedData();
                st.setLocation(mCurrentLocation.getLatitude() + "," + mCurrentLocation.getLongitude());
                st.setTime(new Date().toString());
                st.setStatus("driver_accepted");
                statusChangedData.add(st);
                if (mCurrentLocation != null) {
                    RouteDetails rt = new RouteDetails();
                    rt.setLocation(mCurrentLocation.getLatitude() + "," + mCurrentLocation.getLongitude());
                    rt.setTime(sdf.format(new Date()));
                    routeDetails.add(rt);
                }
                if (routeDetails.size() > 0) {
                    fareDetails.setRoutes(routeDetails);
                }
                if (statusChangedData.size() > 0) {
                    fareDetails.setStatuschanged(statusChangedData);
                }
                fareDetails.setFareid(UUID.randomUUID().toString());//create random fareId
                Log.e("DriverMap", "FareDetail Fare Id : " + fareDetails.getFareid());
                fareDetails.setDrivername(drivername);
                fareDetails.setDriverid(driverid);
                fareDetails.setDriveremailid(driveremailid);
                fareDetails.setDrivermobile(drivermobile);
                fareDetails.setDriverphoto(driverphoto);
                fareDetails.setPassengermobile(passengerMobileNumber);
                fareDetails.setPassengeremailid(passengeremailid);
                fareDetails.setCabmodel(cabmodel);
                fareDetails.setCabtype(cabtype);
                fareDetails.setCabnumber(cabnumber);
                fareDetails.setBaseFare(String.valueOf(baseFare));
                fareDetails.setCostPerKM(String.valueOf(costPerKM));
                fareDetails.setCostPerMinute(String.valueOf(costPerMinute));
                fareDetails.setSurgeRate(String.valueOf(surgeRate));
                fareDetails.setPassengername(passengername);
                fareDetails.setTripDone(false);
                fareDetails.setPickUpLatLngs(fcmRequestObject.getPickUpLatitude() + "," + fcmRequestObject.getPickUpLongitude());
                fareDetails.setDropLatLngs(fcmRequestObject.getDropLatitude() + "," + fcmRequestObject.getDropLongitude());
                fareDetails.setPickUpLocation(fcmRequestObject.getPickUpLocation());
                fareDetails.setDropLocation(fcmRequestObject.getDropLocation());
                fareDetails.setCurrentLatLngs(mCurrentLocation.getLatitude() + "," + mCurrentLocation.getLongitude());

                fcmRequestObject.setFareid(fareDetails.getFareid());
                fcmRequestObject.setUserMobileNumber(SESSION_MOBILE);
                fcmRequestObject.setStatus("driver_accepted");
                fcmRequestObject.setUsername(slide_fanme.getText().toString());
                fcmRequestObject.setCurrentLatLngs(mCurrentLocation.getLatitude() + "," + mCurrentLocation.getLongitude());
                FirebaseDriverUtil.saveFareDetails(fareDetails);
                prefrences.setValueToSharedPref(Constants.rideStatus, "driver_accepted");
                if (isNetworkAvailable()) {
                    isRideNotCancelled = true;
                    Log.e(TAG, "on Accept isRideNotCancelled=>" + true);
                    new notifyToPassenger().execute();
                }
                rideLayout.setVisibility(View.VISIBLE);
                if (driverMap != null) {
                    if (fcmRequestObject.getPickUpLatitude() > 0 && fcmRequestObject.getPickUpLongitude() > 0) {
                        LatLng start = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
                        LatLng end = new LatLng(fcmRequestObject.getPickUpLatitude(), fcmRequestObject.getPickUpLongitude());
                        Routing routing = new Routing.Builder()
                                .travelMode(Routing.TravelMode.DRIVING)
                                .withListener(new RoutingListener() {
                                    @Override
                                    public void onRoutingFailure(RouteException e) {
                                        Log.e("Error At", e.getMessage());
                                    }

                                    @Override
                                    public void onRoutingStart() {

                                    }

                                    @Override
                                    public void onRoutingSuccess(ArrayList<Route> arrayList, int i) {
                                        if (arrayList.size() > 0) {
                                            for (int j = 0; j < arrayList.size(); j++) {
                                                PolylineOptions polyOptions = new PolylineOptions();
                                                polyOptions.color(Color.parseColor("#f8923d"));
                                                polyOptions.width(10 + j * 3);
                                                polyOptions.addAll(arrayList.get(i).getPoints());
                                                Polyline polyline = driverMap.addPolyline(polyOptions);
                                                polylines.add(polyline);
                                            }
                                        }
                                        // add  driver location marker to Map
                                        MarkerOptions options = new MarkerOptions();
                                        options.position(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()));
                                        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.car)).anchor(0.5f, 0.5f);
                                        driverMap.addMarker(options);
                                        // add  pickup location marker to Map
                                        MarkerOptions options1 = new MarkerOptions();
                                        options1.position(new LatLng(fcmRequestObject.getPickUpLatitude(), fcmRequestObject.getPickUpLongitude()));
                                        options1.icon(BitmapDescriptorFactory.fromResource(R.drawable.pickuppoint)).anchor(0.5f, 0.5f);
                                        driverMap.addMarker(options1);

                                    }

                                    @Override
                                    public void onRoutingCancelled() {

                                    }
                                })
                                .waypoints(start, end).key(ApplicationConstants.API_KEY)
                                .build();
                        routing.execute();
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            isDriverFree = "no";
            updateDriverStatus();

            Log.e(TAG, "FareId=>" + fareDetails.getFareid());
            Log.e(TAG, "DriverToken=>" + prefrences.getStringValueFromSharedPref(Constants.FCM_TOKEN));
            Log.e(TAG, "pessanger token=>" + passengerFcmToken);

            //upDateTripData();//added by Fdev01
        } else if (v == btnDecline) {//ride decline by driver
            if (rideRequestDialog != null && rideRequestDialog.isShowing()) {
                rideRequestDialog.dismiss();
            }
            fcmRequestObject.setStatus("driver_declined");
            if (isNetworkAvailable()) {
                new notifyToDeclinedByDriver().execute();
            }
        } else if (v == btnCallRider) {
            if (fcmRequestObject != null) {
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:+" + passengerMobileNumber.trim()));
                context.startActivity(intent);
            }
        } else if (v == rideNavigation) {//navigate to google map
            if (fcmRequestObject != null) {
                if (fcmRequestObject.getPickUpLatitude() != 0 && fcmRequestObject.getPickUpLongitude() != 0) {
                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                            Uri.parse("http://maps.google.com/maps?saddr=" + mCurrentLocation.getLatitude() + "," + mCurrentLocation.getLongitude() + "&daddr=" + fcmRequestObject.getPickUpLatitude() + "," + fcmRequestObject.getPickUpLongitude() + ""));
                    context.startActivity(intent);
                }
            }
        } else if (v == btnRiderLocated) {
            if (dialogRiderLocated != null && !dialogRiderLocated.isShowing()) {
                dialogRiderLocated.show();
            }
        } else if (v == btnLocated) {//driver located
            if (dialogRiderLocated != null && dialogRiderLocated.isShowing()) {
                dialogRiderLocated.dismiss();
            }
            if (mCurrentLocation != null && fcmRequestObject != null) {
                if (driverMap != null) {
                    if (fcmRequestObject.getDropLatitude() != 0 && fcmRequestObject.getDropLongitude() != 0) {
                        driverMap.clear();
                        driverMap.addPolyline(new PolylineOptions()
                                .add(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()), new LatLng(fcmRequestObject.getPickUpLatitude(), fcmRequestObject.getPickUpLongitude()))
                                .width(5)
                                .color(Color.parseColor("#f8923d")).width(5f));
                        // add  driver location marker to Map
                        driverMap.addMarker(new MarkerOptions().position(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()))
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.car)));
                        // add  pickup location marker to Map
                        driverMap.addMarker(new MarkerOptions().position(new LatLng(fcmRequestObject.getDropLatitude(), fcmRequestObject.getDropLongitude()))
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.droppoint)));
                    }
                }
                menu_title.setText("Drop Location");
                rideLayout.setVisibility(View.GONE);
                startRideLayout.setVisibility(View.VISIBLE);
                StatusChangedData st = new StatusChangedData();
                st.setStatus("driver_arrived");
                st.setLocation(mCurrentLocation.getLatitude() + "," + mCurrentLocation.getLongitude());
                st.setTime(sdf.format(new Date()));
                statusChangedData.add(st);
                fareDetails.setStatuschanged(statusChangedData);
                fcmRequestObject.setCurrentLatLngs(String.valueOf(mCurrentLocation + "," + mCurrentLocation.getLongitude()));
                fcmRequestObject.setStatus("driver_arrived");
                if (isNetworkAvailable()) {
                    isRideNotCancelled = true;
                    Log.e(TAG, "on located isRideNotCancelled=>" + true);
                    new notifyToPassenger().execute();
                }
                upDateTripData();//update data to firebase
            }
        } else if (v == btnNotLocated) {
            if (dialogRiderLocated != null && dialogRiderLocated.isShowing()) {
                dialogRiderLocated.dismiss();
            }
        } else if (v == btnCancelRider) {
            if (dialogCancelRide != null && !dialogCancelRide.isShowing()) {
                dialogCancelRide.show();
            }
        } else if (v == btncancelrideyes) {
            if (dialogCancelRide != null && dialogCancelRide.isShowing()) {
                dialogCancelRide.dismiss();
            }
            if (dialogcancelridereason != null && !dialogcancelridereason.isShowing()) {
                dialogcancelridereason.show();
            }
        } else if (v == btncancelrideno) {
            if (dialogCancelRide != null && dialogCancelRide.isShowing()) {
                dialogCancelRide.dismiss();
            }
        } else if (v == btnSend) {//ride cancel by driver
            Log.e(TAG, "btnSend click");
            if (!TextUtils.isEmpty(cancelReasonDescription)) {
                Log.e(TAG, "if 1......");
                if (isNetworkAvailable()) {
                    Log.e(TAG, "if 2......");
                    if (fcmRequestObject != null && !routeDetails.isEmpty() && !statusChangedData.isEmpty()) {
                        Log.e(TAG, "if 3......");
                        fcmRequestObject.setCancelDescription(cancelReasonDescription);
                        isRideNotCancelled = false;
                        Log.e(TAG, "isRideNotCancelled=>" + isRideNotCancelled);
                        isRideCancelledByDriver = true;
                        fcmRequestObject.setStatus("driver_cancelled");
                        StatusChangedData st = new StatusChangedData();
                        st.setStatus("driver_cancelled");
                        st.setLocation(mCurrentLocation.getLatitude() + "," + mCurrentLocation.getLongitude());
                        st.setTime(sdf.format(new Date()));
                        statusChangedData.add(st);
                        fareDetails.setTripDone(true);
                        if (routeDetails.size() >= 1) {
                            calculateDistanceAndFare(routeDetails.get(0).getLocation(), routeDetails.get(routeDetails.size() - 1).getLocation());
                        }
                        if (dialogcancelridereason != null && dialogcancelridereason.isShowing()) {
                            dialogcancelridereason.dismiss();
                        }
                        isDriverFree = "yes";
                        updateDriverStatus();

                        //added by Fdev02
                        //String driverToken=prefrences.getStringValueFromSharedPref(Constants.FCM_TOKEN);
                        // new NotifyCancelByDriver().execute(driverToken,passengerFcmToken,fareDetails.getFareid());

                    } else {
                        Toast.makeText(context, "Please connect with Internet!", Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                Toast.makeText(context, "Please select reason for cancel current ride", Toast.LENGTH_SHORT).show();
            }

        } else if (v == btnCancelRider) {
            if (dialogCancelRide != null && !dialogCancelRide.isShowing()) {
                dialogCancelRide.show();
            }
        } else if (btnStartRides == v) {
            if (dialogStartRide != null && !dialogStartRide.isShowing()) {
                dialogStartRide.show();
            }
        } else if (btnCancelRides == v) {
            if (dialogCancelRide != null && !dialogCancelRide.isShowing()) {
                dialogCancelRide.show();
            }
        } else if (btnAcceptTrip == v) {//for start ride
            startRideLayout.setVisibility(View.GONE);
            stopRideLayout.setVisibility(View.VISIBLE);
            if (dialogStartRide != null && dialogStartRide.isShowing()) {
                dialogStartRide.dismiss();
            }
            fareDetails.setStarttime(sdf.format(new Date()));
            RouteDetails rd = new RouteDetails();
            if (mCurrentLocation != null) {
                rd.setLocation(mCurrentLocation.getLatitude() + "," + mCurrentLocation.getLongitude());
                rd.setTime(sdf.format(new Date()));
                rd.setTotaltime("0");
                routeDetails.add(rd);
            }
            StatusChangedData st = new StatusChangedData();
            st.setLocation(mCurrentLocation.getLatitude() + "," + mCurrentLocation.getLongitude());
            st.setTime(sdf.format(new Date()));
            st.setStatus("began");
            statusChangedData.add(st);
            fcmRequestObject.setCurrentLatLngs(String.valueOf(mCurrentLocation + "," + mCurrentLocation.getLongitude()));
            fcmRequestObject.setStatus("began");
            if (isNetworkAvailable()) {
                isRideNotCancelled = true;
                Log.e(TAG, "on begin isRideNotCancelled=>" + true);
                new notifyToPassenger().execute();
            }
            upDateTripData();//update data to firebase
            isDriverFree = "no";
            updateDriverStatus();
        } else if (btnCancelTrip == v) {
            if (dialogStartRide != null && dialogStartRide.isShowing()) {
                dialogStartRide.dismiss();
            }
        } else if (btnStopRides == v) {
            if (dialogStopDetails != null && !dialogStopDetails.isShowing()) {
                dialogStopDetails.show();
            }
        } else if (btnStopTrip == v) {//stop the ride
            Log.e(TAG, "Stop Called=>>>>>");
            if (dialogStopDetails != null && dialogStopDetails.isShowing()) {
                dialogStopDetails.dismiss();
            }
            tvServiceAmount.setText("0.0");
            StatusChangedData st = new StatusChangedData();
            st.setLocation(mCurrentLocation.getLatitude() + "," + mCurrentLocation.getLongitude());
            st.setTime(sdf.format(new Date()));
            st.setStatus("completed");
            fareDetails.setEndtime(sdf.format(new Date()));
            fareDetails.setStatuschanged(statusChangedData);
            fareDetails.setRoutes(routeDetails);
            statusChangedData.add(st);
            fcmRequestObject.setCurrentLatLngs(String.valueOf(mCurrentLocation + "," + mCurrentLocation.getLongitude()));
            fcmRequestObject.setStatus("completed");
            fareDetails.setTripDone(true);
            if (isNetworkAvailable()) {
                isRideNotCancelled = true;
                Log.e(TAG, "on stop isRideNotCancelled=>" + true);
                Log.e(TAG, "Stop : notifyToPassenger called");
                new notifyToPassenger().execute();
            }
            if (routeDetails != null && !routeDetails.isEmpty()) {
                Log.e(TAG, "Stop : if 1... called");
                if (routeDetails.size() > 1) {
                    Log.e(TAG, "Stop : if 2... called");
                    String Origin = routeDetails.get(0).getLocation();
                    String Destination = routeDetails.get(routeDetails.size() - 1).getLocation();
                    if (isNetworkAvailable()) {
                        if (dialog != null && !dialog.isShowing()) {
                            Log.e(TAG, "Stop : if 3... called");
                            dialog.show();
                        }
                        calculateDistanceAndFare(Origin, Destination);
                    }
                }
            }
        } else if (btnStopCancelTrip == v) {
            if (dialogStopDetails != null && dialogStopDetails.isShowing()) {
                dialogStopDetails.dismiss();
            }
        } else if (btnSubmit == v) {//complete ride
            Log.e(TAG, "btnSubmit click=>");
            if (fareDetails != null) {
                if (isNetworkAvailable()) {
                    isRideNotCancelled = false;
                    Log.e(TAG, "isRideNotCancelled=>" + isRideNotCancelled);
                    new notifyToPassenger().execute();
                    String mobile = fareDetails.getPassengermobile();
                    String databody = fareDetails.getDrivername() + ", Car Model is " + fareDetails.getCabmodel();
                    String email = fareDetails.getPassengeremailid();
                    String fareid = fareDetails.getFareid();
                    new SaveInvoice().execute();//called save invoice api

                    TestApiCall testApiCall = new TestApiCall(fareid, mobile, email, databody);
                    testApiCall.execute();//end Ride
                    //new TestApiCall().execute();
                }
            }
        }
    }

    private void updateDriverStatus() {//update driver status : driver free or not
        Log.e(TAG, "updateDriverStatus called");
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference reference = firebaseDatabase.getReference("Drivers");
        Query query = reference.child("location_id_1").orderByChild("emailId").equalTo(driverId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {

                    if (!TextUtils.isEmpty(driverId)) {
                        HashMap<String, String> driverNode = (HashMap<String, String>) dataSnapshot.getValue();
                        String key = "";
                        Iterator it = driverNode.entrySet().iterator();
                        while (it.hasNext()) {
                            Map.Entry pair = (Map.Entry) it.next();
                            key = pair.getKey().toString();
                            it.remove(); // avoids a ConcurrentModificationException
                        }
                        String path = "/" + dataSnapshot.getKey() + "/" + key;
                        HashMap<String, Object> result = new HashMap<>();
                        result.put("isDriverFree", isDriverFree);
                        reference.child(path).updateChildren(result);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
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

    private void calculateDistanceAndFare(String origin, String destination) {
        try {
            Log.e(TAG, "calculateDistanceAndFare called");
            float distance = totalDistanceCovered;
            String startTime = routeDetails.get(0).getTime();
            String endTime = routeDetails.get(routeDetails.size() - 1).getTime();
            SimpleDateFormat sd = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss", Locale.ENGLISH);
            Date startDate = null;
            Date endDate = null;
            try {
                startDate = sd.parse(startTime);
                endDate = sd.parse(endTime);
            } catch (ParseException e) {
                e.printStackTrace();
                Log.e(TAG, "calculateDistanceAndFare Exception 1" + e.toString());
            }
            long durations = endDate.getTime() - startDate.getTime();
            long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(durations);
            if (diffInMinutes <= 3) {
                totalFareAmount = baseFare + (distance * costPerKM);
            } else {
                diffInMinutes = diffInMinutes - 3;
                totalFareAmount = baseFare + diffInMinutes * costPerMinute + (distance * costPerKM);
            }
            fareDetails.setTotalduration(diffInMinutes);
            tvtotalfare.setText(String.valueOf(totalFareAmount));
            tvTotalFareAmount.setText(String.valueOf(totalFareAmount));
            tvBaseFareAmount.setText(String.valueOf(baseFare));
            tvtotalfare.setText(String.valueOf(totalFareAmount));
            extraKmFareAmount.setText(String.valueOf((distance * costPerKM)));
            extramTimeAmount.setText(String.valueOf(diffInMinutes * costPerMinute));
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
            Log.e(TAG, "calculateDistanceAndFare isRideNotCancelled=>" + isRideNotCancelled);
            if (isRideNotCancelled) {
                if (dialogFareDetails != null && !dialogFareDetails.isShowing()) {
                    dialogFareDetails.show();
                }
            }
            driverMap.clear();
            invoiceData = new RideSummaryData();
            invoiceData.setFareid(fareDetails.getFareid());
            invoiceData.setBasefare(Double.parseDouble(fareDetails.getBaseFare()));
            invoiceData.setDiscount(0.0);
            invoiceData.setOtheres(0.0);
            invoiceData.setTotalfare(totalFareAmount);
            invoiceData.setDistancefare(Double.parseDouble(extraKmFareAmount.getText().toString()));
            invoiceData.setDurationfare(Double.parseDouble(extramTimeAmount.getText().toString().trim()));
            invoiceData.setDrivername(drivername);
            invoiceData.setDriverimage(driverphoto);
            invoiceData.setDriveremailid(driveremailid);
            invoiceData.setDriverid(driverid);
            invoiceData.setRidetime(fareDetails.getEndtime());
            if (isRideNotCancelled) {
                upDateTripData();//update data to firebase
            } else {
                fareDetails.setCancelDescription(fcmRequestObject.getCancelDescription());
                fareDetails.setTripDone(true);
                upDateTripData();//update data to firebase
                new notifyToPassenger().execute();
                new SaveInvoice().execute();//call save invoice api
            }
        } catch (Exception ex) {
            Log.e(TAG, "calculateDistanceAndFare Exception2" + ex.toString());
            ex.printStackTrace();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        driverMap = googleMap;
        driverMap.setMyLocationEnabled(true);
        // Enable / Disable zooming controls
        driverMap.getUiSettings().setZoomControlsEnabled(false);
        // Enable / Disable my location button
        driverMap.getUiSettings().setMyLocationButtonEnabled(false);
        // Enable / Disable Compass icon
        driverMap.getUiSettings().setCompassEnabled(true);
        // Enable / Disable Rotate gesture
        driverMap.getUiSettings().setRotateGesturesEnabled(true);
        // Enable / Disable zooming functionality
        driverMap.getUiSettings().setZoomGesturesEnabled(true);
        driverMap.isTrafficEnabled();
        driverMap.setTrafficEnabled(true);
    }

    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, getActivity(), 0).show();
            return false;
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Firing onLocationChanged..............................................");
        mCurrentLocation = location;
        if (isOnline && mCurrentLocation != null) {
            if (!TextUtils.isEmpty(SESSION_MOBILE)) {
                GeoLocation location1 = new GeoLocation(location.getLatitude(), location.getLongitude());
                geoFire.setLocation(SESSION_MOBILE, location1, new GeoFire.CompletionListener() {
                    @Override
                    public void onComplete(String key, DatabaseError error) {
                        if (error != null) {
                            Log.e("Error in Save Location", error.getMessage().toString());
                        } else {
                            Log.d("Save Location", key.toString());
                            //added code for update timestamp in geofire 09/09/2017
                            Map<String, Object> data = new HashMap<>();
                            data.put("timestamp", sdf.format(mCurrentLocation.getTime()));
                            geoFire.getDatabaseReference().child(SESSION_MOBILE).updateChildren(data);
                        }
                    }
                });
            }
        }
        if (mCurrentLocation != null && TextUtils.isEmpty(prefrences.getStringValueFromSharedPref(Constants.rideStatus))) {
            if (mCurrentLocation.getLatitude() != 0 && mCurrentLocation.getLongitude() != 0) {
                LatLng latLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
                if (driverMap != null) {
                    // Showing the current location in Google Map
                    driverMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    // Zoom in the Google Map
                    driverMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                }
            }
        } else if (!TextUtils.isEmpty(prefrences.getStringValueFromSharedPref(Constants.rideStatus))) {
            if (mCurrentLocation != null) {
                if (fcmRequestObject != null) {
                    //         fcmRequbtnAccestObject.setCurrentLatLngs(mCurrentLocation.getLatitude() + "," + mCurrentLocation.getLongitude());
                    LatLng latLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
                    // Showing the current location in Google Map
                    if (driverMap != null) {
                        driverMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                        driverMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                    }
                    travelledLocations.add(latLng);
                    if (travelledLocations.size() > 0) {
                        LatLng oldLocation = travelledLocations.get(travelledLocations.size() - 1);
                        totalDistanceCovered += UtilityMethods.calculateDistance(latLng, oldLocation);
                    }
                    RouteDetails rd = new RouteDetails();
                    rd.setLocation(mCurrentLocation.getLatitude() + "," + mCurrentLocation.getLongitude());
                    rd.setTime(sdf.format(new Date()));
                    rd.setTotaltime("0");
                    routeDetails.add(rd);
                    if (fcmRequestObject.getPickUpLatitude() > 0 && fcmRequestObject.getPickUpLongitude() > 0) {
                        Location startPoint = new Location("locationA");
                        startPoint.setLatitude(mCurrentLocation.getLatitude());
                        startPoint.setLongitude(mCurrentLocation.getLongitude());
                        Location endPoint = new Location("locationB");
                        endPoint.setLatitude(fcmRequestObject.getPickUpLatitude());
                        endPoint.setLongitude(fcmRequestObject.getPickUpLongitude());
                        double distance = startPoint.distanceTo(endPoint);
                        if (distance <= 10 && !prefrences.getBooleanValueFromSharedPref(Constants.isfirstSMSSend)) {
                            new SendSMSToPassnger().execute();
                        }
                        if (fareDetails != null) {
                            fareDetails.setCurrentLatLngs(mCurrentLocation.getLatitude() + "," + mCurrentLocation.getLongitude());
                        }
                    }
                } else {
                    LatLng latLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
                    // Showing the current location in Google Map
                    if (driverMap != null) {
                        driverMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                        driverMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                    }

                }
            }
        }
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


    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        switch (checkedId) {
            case R.id.radiodonotcharge:
                cancelReasonDescription = radiodonotcharge.getText().toString();
                break;
            case R.id.radioaddress:
                cancelReasonDescription = radioaddress.getText().toString();
                break;
            case R.id.radionotshow:
                cancelReasonDescription = radionotshow.getText().toString();
                break;
            case R.id.radiocancel:
                cancelReasonDescription = radiocancel.getText().toString();
                break;
            case R.id.radioothers:
                cancelReasonDescription = radioothers.getText().toString();
                break;
        }
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onDestroy() {
        try {
            context.unregisterReceiver(mHandleMessageReceiver);
            //Unregister receiver on destroy
            if (gpsLocationReceiver != null)
                context.unregisterReceiver(gpsLocationReceiver);
        } catch (Exception e) {
            Log.e("UnRegister Error", "> " + e.getMessage());
        }
        super.onDestroy();
        mapView.onDestroy();
        mMapView.onDestroy();
        if (isNetworkAvailable()) {
            deleteGeoFireData();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
        mMapView.onLowMemory();
    }

    private void clearAllDataAndSetViewAfterCancell() {
        try {
            if (dialogFareDetails != null && dialogFareDetails.isShowing()) {
                dialogFareDetails.dismiss();
            }
            prefrences.setValueToSharedPref(Constants.rideStatus, "");
            isOnline = true;
            myMeterSwitch.setVisibility(View.VISIBLE);
            mySwitch.setBackgroundResource(R.drawable.online);
            mySwitch.setTag("on");
            isBottomBarNeeded(true);
            startRideLayout.setVisibility(View.GONE);
            prefrences.setBooleanValueToSharedPref("driverStatus", true);
            menu_title.setText("");
            rideDetails.setVisibility(View.GONE);
            stopRideLayout.setVisibility(View.GONE);
            getActivity().findViewById(R.id.bottomBar).setVisibility(View.VISIBLE);
            mapcurentgpssettings.setVisibility(View.VISIBLE);
            mySwitch.setVisibility(View.VISIBLE);
            //clear data
            fareDetails = null;
            fcmRequestObject = null;
            routeDetails.clear();
            statusChangedData.clear();
            invoiceData = null;
            isDriverFree = "yes";
            updateDriverStatus();
            prefrences.setBooleanValueToSharedPref(Constants.isfirstSMSSend, false);
            rideLayout.setVisibility(View.GONE);
            isRideNotCancelled = true;
            Log.e(TAG, "isRideNotCancelled=>" + isRideNotCancelled);
            isRideCancelledByDriver = false;

            if(driverMap!=null)
                driverMap.clear();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void getPendingTripDetails() {
        if (isNetworkAvailable()) {
            HashMap<String, String> data = new HashMap<>();
            try {
                data.put("driverEmail", SESSION_EMAIL);
                mAPIService = ApiUtils.getAPIService(ApplicationConstants.RESTBASEURL);
                mAPIService.getPendingRide(SESSION_EMAIL).enqueue(new Callback<FareDetails>() {
                    @Override
                    public void onResponse(Call<FareDetails> call, Response<FareDetails> response) {
                        if (response.isSuccessful()) {
                            if (!TextUtils.isEmpty(response.body().getFareid()))
                            {
                                fareDetails = response.body();
                            }
                            if (fareDetails != null)
                            {
                                myMeterSwitch.setVisibility(View.INVISIBLE); //added as per discussion on 06/09/2017
                                //Issue Fix for null pointer exception
                                //Object should be created before it is used
                                fcmRequestObject = new FcmRequestObject();
                                statusChangedData.addAll(fareDetails.getStatuschanged());
                                routeDetails.addAll(fareDetails.getRoutes());
                                if (!TextUtils.isEmpty(fareDetails.getPickUpLatLngs())) {
                                    String[] picklatlongs = fareDetails.getPickUpLatLngs().split(",");
                                    if (picklatlongs != null && picklatlongs.length == 2) {
                                        fcmRequestObject.setPickUpLatitude(Double.parseDouble(picklatlongs[0].trim().toString()));
                                        fcmRequestObject.setPickUpLongitude(Double.parseDouble(picklatlongs[1].trim().toString()));
                                    }
                                }
                                if (!TextUtils.isEmpty(fareDetails.getDropLatLngs())) {
                                    String[] droplatlongs = fareDetails.getDropLatLngs().split(",");
                                    if (droplatlongs != null && droplatlongs.length == 2) {
                                        fcmRequestObject.setDropLatitude(Double.parseDouble(droplatlongs[0].trim().toString()));
                                        fcmRequestObject.setDropLongitude(Double.parseDouble(droplatlongs[1].trim().toString()));
                                    }
                                }
                                ArrayList<String> statusarrayList = new ArrayList<String>();
                                for (StatusChangedData myPoint : statusChangedData) {
                                    statusarrayList.add(myPoint.getStatus());
                                }
                                //fcmRequestObject = new FcmRequestObject();
                                fcmRequestObject.setFareid(fareDetails.getFareid());
                                fcmRequestObject.setCurrentLatLngs(fareDetails.getCurrentLatLngs());
                                fcmRequestObject.setPickUpLocation(fareDetails.getPickUpLocation());
                                fcmRequestObject.setDropLocation(fareDetails.getDropLocation());
                                fcmRequestObject.setEstimateDistance("");
                                fcmRequestObject.setTimer(0);
                                fcmRequestObject.setUserMobileNumber(fareDetails.getPassengermobile());
                                fcmRequestObject.setUsername(fareDetails.getDrivername());
                                fcmRequestObject.setUserEmailID(fareDetails.getDriveremailid());
                                fcmRequestObject.setCancelDescription("");
                                driveremailid = fareDetails.getDriveremailid();
                                driverid = fareDetails.getDriverid();
                                drivername = fareDetails.getDrivername();
                                drivermobile = fareDetails.getDrivermobile();
                                passengeremailid = fareDetails.getPassengeremailid();
                                passengerMailId = fareDetails.getPassengeremailid();
                                passengername = fareDetails.getPassengername();
                                passengerMobileNumber = fareDetails.getPassengermobile();
                                if (statusarrayList.contains("began")) {
                                    fcmRequestObject.setStatus("began");
                                    startRideLayout.setVisibility(View.GONE);
                                    stopRideLayout.setVisibility(View.VISIBLE);
                                }
                                if (statusarrayList.contains("driver_accepted")) {
                                    fcmRequestObject.setStatus("driver_accepted");
                                    prefrences.setValueToSharedPref(Constants.rideStatus, "driver_accepted");
                                    rideDetails.setVisibility(View.VISIBLE);
                                    passengerName.setText(fcmRequestObject.getUsername());
                                    passngerAddress.setText(fcmRequestObject.getPickUpLocation());
                                    mapcurentgpssettings.setVisibility(View.GONE);
                                    mySwitch.setVisibility(View.GONE);
                                    headerLayout.setBackgroundColor(Color.parseColor("#00a4cc"));
                                    menu_title.setText("Pickup Location");
                                    menu_title.setTextColor(Color.WHITE);
                                    menu_title.setVisibility(View.VISIBLE);
                                    getActivity().findViewById(R.id.bottomBar).setVisibility(View.GONE);
                                    isBottomBarNeeded(false);
                                    rideLayout.setVisibility(View.VISIBLE);
                                }
                                if (statusarrayList.contains("driver_arrived")) {
                                    fcmRequestObject.setStatus("driver_arrived");
                                    rideDetails.setVisibility(View.GONE);
                                    passengerName.setText(fcmRequestObject.getUsername());
                                    passngerAddress.setText(fcmRequestObject.getPickUpLocation());
                                    mapcurentgpssettings.setVisibility(View.GONE);
                                    mySwitch.setVisibility(View.GONE);
                                    headerLayout.setBackgroundColor(Color.parseColor("#00a4cc"));
                                    menu_title.setText("Pickup Location");
                                    menu_title.setTextColor(Color.WHITE);
                                    menu_title.setVisibility(View.VISIBLE);
                                    getActivity().findViewById(R.id.bottomBar).setVisibility(View.GONE);
                                    isBottomBarNeeded(false);
                                    rideLayout.setVisibility(View.GONE);
                                    startRideLayout.setVisibility(View.VISIBLE);
                                    stopRideLayout.setVisibility(View.GONE);
                                }
                                if (!statusarrayList.contains("complete")) {
                                    isDriverFree = "no";
                                    updateDriverStatus();
                                }
                                new getPassengerFcmTokens().execute();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<FareDetails> call, Throwable t) {
                        if (!TextUtils.isEmpty(t.getMessage())) {
                            Log.e("Error", t.getMessage());
                        }
                    }
                });
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    protected boolean isBottomBarNeeded(boolean yes) {
        return yes;
    }

    private synchronized void upDateTripData() {
        if (fareDetails != null) {
            Log.e(TAG, "upDateTripData() called");
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            final DatabaseReference reference = firebaseDatabase.getReference();
            Query query = reference.child("taxifare").orderByChild("fareid").equalTo(fareDetails.getFareid());
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (fareDetails != null) {
                        Log.e(TAG, "upDateTripData() -> onDataChange()");
                        DataSnapshot nodeDataSnapshot = dataSnapshot.getChildren().iterator().next();
                        String key = nodeDataSnapshot.getKey(); // this key is `K1NRz9l5PU_0CFDtgXz`
                        String path = "/" + dataSnapshot.getKey() + "/" + key;
                        HashMap<String, Object> result = new HashMap<>();
                        result.put("driverid", fareDetails.getDriverid());
                        result.put("drivername", fareDetails.getDrivername());
                        result.put("driveremailid", fareDetails.getDriveremailid());
                        result.put("drivermobile", fareDetails.getDrivermobile());
                        result.put("driverphoto", fareDetails.getDriverphoto());
                        result.put("passengerid", fareDetails.getPassengerid());
                        result.put("passengername", fareDetails.getPassengername());
                        result.put("passengeremailid", fareDetails.getPassengeremailid());
                        result.put("passengermobile", fareDetails.getPassengermobile());
                        result.put("cabtype", fareDetails.getCabtype());
                        result.put("cabnumber", fareDetails.getCabnumber());
                        result.put("cabmodel", fareDetails.getCabmodel());
                        result.put("baseFare", fareDetails.getBaseFare());
                        result.put("costPerKM", fareDetails.getCostPerKM());
                        result.put("costPerMinute", fareDetails.getCostPerMinute());
                        result.put("surgeRate", fareDetails.getSurgeRate());
                        result.put("starttime", fareDetails.getStarttime());
                        result.put("routes", fareDetails.getRoutes());
                        result.put("totalduration", fareDetails.getTotalduration());
                        result.put("statuschanged", fareDetails.getStatuschanged());
                        result.put("totalwaittime", fareDetails.getTotalwaittime());
                        result.put("endtime", fareDetails.getEndtime());
                        result.put("isTripDone", fareDetails.isTripDone());
                        result.put("cancelDescription", fareDetails.getCancelDescription());
                        result.put("pickUpLatLngs", fareDetails.getPickUpLatLngs());
                        result.put("dropLatLngs", fareDetails.getDropLatLngs());
                        result.put("pickUpLocation", fareDetails.getPickUpLocation());
                        result.put("dropLocation", fareDetails.getDropLocation());
                        result.put("currentLatLngs", fareDetails.getCurrentLatLngs());
                        reference.child(path).updateChildren(result);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    if (databaseError != null) {
                        Log.e("Error", "upDateTripData()=>" + databaseError.getMessage());
                    }
                }
            });
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        switch (i) {
            case 0:
                break;
            case 1:
                Intent i1 = new Intent(context, Profile.class);
                context.startActivity(i1);
                break;
            case 2:
                Intent i2 = new Intent(context, PaymentActivity.class);
                context.startActivity(i2);
                break;
        }
    }

    private class SaveInvoice extends AsyncTask {
        private int responseCode = 0;

        @Override
        protected Object doInBackground(Object[] params) {
            try {
                Log.e(TAG, "SaveInvoice background=>");
                Gson gson = new Gson();
                String param = gson.toJson(invoiceData);
                Log.e(TAG,"SaveInvoice Post Data=>"+param);
                responseCode = jsonParser.makeHttpPostRequest(ApplicationConstants.SAVEINVOICE_URL, new JSONObject(param), "application/json");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return String.valueOf(responseCode);
        }

        @Override
        protected void onPostExecute(Object o) {
            if (o.equals("200")) {
                try {
                    if (dialogFareDetails != null && dialogFareDetails.isShowing()) {
                        dialogFareDetails.dismiss();
                    }
                    prefrences.setValueToSharedPref(Constants.rideStatus, "");
                    isOnline = true;
                    mySwitch.setBackgroundResource(R.drawable.online);
                    mySwitch.setTag("on");
                    isBottomBarNeeded(true);
                    startRideLayout.setVisibility(View.GONE);
                    prefrences.setBooleanValueToSharedPref("driverStatus", true);
                    menu_title.setText("");
                    rideDetails.setVisibility(View.GONE);
                    stopRideLayout.setVisibility(View.GONE);
                    getActivity().findViewById(R.id.bottomBar).setVisibility(View.VISIBLE);
                    mapcurentgpssettings.setVisibility(View.VISIBLE);
                    mySwitch.setVisibility(View.VISIBLE);
                    //clear data
                    fareDetails = null;
                    fcmRequestObject = null;
                    routeDetails.clear();
                    statusChangedData.clear();
                    invoiceData = null;
                    isDriverFree = "yes";
                    updateDriverStatus();
                    prefrences.setBooleanValueToSharedPref(Constants.isfirstSMSSend, false);
                    if (!isRideNotCancelled) {
                        rideLayout.setVisibility(View.GONE);
                        isRideNotCancelled = true;
                        Log.e(TAG, "isRideNotCancelled=>" + isRideNotCancelled);
                        isRideCancelledByDriver = false;
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    /*class FetchingIMEI_Status extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainHomeScreen.this);
            pDialog.setMessage("Please wait,Fetching Information..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }
        protected Void doInBackground(Void... arg0) {

            try{
                String strjsn = jsonParser.makeServiceCall(ApplicationConstants.DRIVER_PENDINGIMAGES2_URL+"/"+SESSION_EMAIL);
                JSONObject json_res = new JSONObject(strjsn);

                Log.e("JSON Response",json_res.toString());
                firstName = json_res.getString("firstName");
                lastName = json_res.getString("lastName");
                phoneNumber = json_res.getString("phoneNumber");
                emailID = json_res.getString("emailID");

                Log.e("firstName",firstName);
                Log.e("lastName",lastName);
                Log.e("phoneNumber",phoneNumber);
                Log.e("emailID",emailID);




               // mydb.DriverRegRes_Details();

            }catch(Exception e){
                e.printStackTrace();
            }
            return null;

        }
        protected void onPostExecute(Void file_url) {
            // dismiss the dialog once done
            if(pDialog!=null){
                pDialog.dismiss();
                pDialog.cancel();

            }


        }

    }
    */
    class PendingImages extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(context);
            pDialog.setMessage("Please wait.......");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected Void doInBackground(Void... arg0) {

            try {
                String strjsn = jsonParser.makeServiceCall(ApplicationConstants.DRIVER_PENDINGIMAGES2_URL + "/" + SESSION_EMAIL);
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

                long isupdated = mydb.DriverRegRes_Details(firstName, lastName, phoneNumber, emailID, Res_PassWord, compName, compAddr, compCity, deviceInfo,
                        iMEI, latLong, country, activated, Pincode, CarName, CarModel, CarColor, CarManu_year, NoPass, Driver_DOB,
                        Vehicle_URL, LicensePlate_URL, DriverPhoto_URL, DriverLicenseNo_URL, IDFront_URL, IDBack_URL,
                        PassportFront_URL, PassportBack_URL, VehicleRegFront_URL, VehicleRegBack_URL, Insurence_URL,
                        regDate, json_car_pic_status, json_pic_plate_status, json_driver_pic_status, json_lic_pic_status,
                        json_frontid_pic_status, json_id_back_pic_status, carfront_pic_status, carpassback_pic_status,
                        carreg_front_pic_status, json_carreg_back_pic_status, json_insu_pic_status, true);
                if (isupdated > 0) {
                    SESSION_MOBILE = phoneNumber;
                }
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
            if (TextUtils.isEmpty(prefrences.getStringValueFromSharedPref(DriverConstants.fcmToken).trim())) {
                if (isNetworkAvailable()) {
                    new saveFcmToken().execute();
                }
            } else {
                Log.i("FCM Token", prefrences.getStringValueFromSharedPref(DriverConstants.fcmToken));
            }
        }

    }

    class saveFcmToken extends AsyncTask<String, String, String> {
        protected String doInBackground(String... args) {
            String token = FirebaseInstanceId.getInstance().getToken();
            try {
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("fcmId ", token));
                params.add(new BasicNameValuePair("driverId ", SESSION_MOBILE));
                int responseCode = jsonParser.makeHttpRequest(ApplicationConstants.SAVE_DRIVER_FCM_TOKEN_URL + "?fcmId=" + token + "&driverId=" + SESSION_MOBILE, "POST", params, true);
                if (responseCode != 0 && responseCode == 200) {
                    prefrences.setValueToSharedPref(DriverConstants.fcmToken, token);
                }
                Log.d("response code", String.valueOf(responseCode));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }


    class getPassengerFcmTokens extends AsyncTask<String, String, String> {
        protected String doInBackground(String... args) {
            try {
                if (!TextUtils.isEmpty(passengerMobileNumber)) {
                    String mobileNumber = passengerMobileNumber.replace("+", " ").trim();
                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("passengerId ", mobileNumber));
                    JSONObject responseData = jsonParser.makeHttpRequest(ApplicationConstants.GET_PASSENGER_FCM_TOKEN_URL + "?passengerId=" + mobileNumber, "POST", params);
                    Log.d("response code", responseData.toString());
                    passengerFcmToken = responseData.getString("fcm_TOKEN_ID");
                    passengerMobileNumber = responseData.getString("passenger_ID");
                    passengerMobileNumber = passengerMobileNumber.replaceAll("\\s+", "");
                    btnAccept.setEnabled(true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            if (s.equals("")) {

            }
        }
    }

    class getPassengerFcmToken extends AsyncTask<String, String, String> {
        protected String doInBackground(String... args) {
            try {
                Log.e(TAG, "getPassengerFcmToken=>>>");
                if (!TextUtils.isEmpty(passengerMobileNumber)) {
                    String mobileNumber = passengerMobileNumber.replace("+", " ").trim();
                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("passengerId ", mobileNumber));
                    Log.e(TAG, "Mobile number user=>" + mobileNumber);
                    JSONObject responseData = jsonParser.makeHttpRequest(ApplicationConstants.GET_PASSENGER_FCM_TOKEN_URL + "?passengerId=" + mobileNumber, "POST", params);
                    Log.d("response code", responseData.toString());
                    passengerFcmToken = responseData.getString("fcm_TOKEN_ID");
                    passengerMobileNumber = responseData.getString("passenger_ID");
                    passengerMobileNumber = passengerMobileNumber.replaceAll("\\s+", "");
                    btnAccept.setEnabled(true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            if (s.equals("")) {
                rideRequestDialog.show();
                Log.e(TAG, "rideRequestDialog show....");
            }
        }
    }

    class notifyToPassenger extends AsyncTask<String, String, String> {
        int responseData = 0;

        protected String doInBackground(String... args) {
            try {
                Log.e(TAG, "notifyToPassenger isRideNotCancelled=>" + isRideNotCancelled);
                Log.e(TAG, "notifyToPassenger isRideCancelledByDriver=>" + isRideCancelledByDriver);
                fcmRequestObject.setUserEmailID(SESSION_EMAIL);
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                Gson gson = new Gson();
                String fcmJson = "";
                if (isRideNotCancelled) {
                    fcmJson = gson.toJson(fcmRequestObject, FcmRequestObject.class);
                    Log.e(TAG, "Fcm Json , if=>" + fcmJson);
                } else if (!isRideNotCancelled && isRideCancelledByDriver) {
                    fcmRequestObject.setStatus("driver_cancelled");
                    fcmJson = gson.toJson(fcmRequestObject, FcmRequestObject.class);

                    Log.e(TAG, "Fcm Json , else if 1 =>" + fcmJson);
                } else if (!isRideNotCancelled && !isRideCancelledByDriver) {
                    fcmJson = gson.toJson(invoiceData, RideSummaryData.class);
                    Log.e(TAG, "Fcm Json , else if 2 =>" + fcmJson);
                }
                Log.e(TAG, "passengerFcmToken=>" + passengerFcmToken);
                params.add(new BasicNameValuePair("tokens ", passengerFcmToken));
                //       params.add(new BasicNameValuePair("fcmRequestObject", fcmJson));
                if (isRideNotCancelled) {
                    responseData = jsonParser.makeHttpRequest(ApplicationConstants.NOTIFY_URL + "?tokens=" + passengerFcmToken, "POST", params, "application/json", fcmJson);
                } else if (!isRideNotCancelled && isRideCancelledByDriver) {
                    String driverToken = prefrences.getStringValueFromSharedPref(Constants.FCM_TOKEN);
                    responseData = jsonParser.makeHttpRequest(ApplicationConstants.API_DRIVER_CANCEL + "?driverToken=" + driverToken + "&passengerToken=" + passengerFcmToken + "&fareId=" + fareDetails.getFareid(), "POST", params, "application/json", fcmJson);

                    String fcmInvoice = gson.toJson(invoiceData, RideSummaryData.class);
                    Log.e(TAG, "fcmInvoice JSON =>" + fcmInvoice);
                    jsonParser.makeHttpRequest(ApplicationConstants.SENTINVOICE_URL + "tokens=" + passengerFcmToken, "POST", params, "application/json", fcmInvoice);
                } else if (!isRideNotCancelled && !isRideCancelledByDriver) {
                    responseData = jsonParser.makeHttpRequest(ApplicationConstants.SENTINVOICE_URL + "tokens=" + passengerFcmToken, "POST", params, "application/json", fcmJson);
                }
                Log.d("response code", String.valueOf(responseData));
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "notifyToPassenger Exception=>" + e.toString());
            }
            return String.valueOf(responseData);
        }

        @Override
        protected void onPostExecute(String s) {
            if (s.equals("200")) {
                try {
                    if (isRideNotCancelled) {
                        rideDetails.setVisibility(View.VISIBLE);
                        passengerName.setText(fcmRequestObject.getUsername());
                        passngerAddress.setText(fcmRequestObject.getPickUpLocation());
                        mapcurentgpssettings.setVisibility(View.GONE);
                        mySwitch.setVisibility(View.GONE);
                        headerLayout.setBackgroundColor(Color.parseColor("#00a4cc"));
                        menu_title.setText("Pickup Location");
                        menu_title.setTextColor(Color.WHITE);
                        menu_title.setVisibility(View.VISIBLE);
                        getActivity().findViewById(R.id.bottomBar).setVisibility(View.GONE);
                        isBottomBarNeeded(false);
                    }
                    if (isRideCancelledByDriver) {
                        Toast.makeText(context, "Ride Successfully canceled.", Toast.LENGTH_SHORT).show();
                        myMeterSwitch.setVisibility(View.VISIBLE);
                    }
//                    ((Activity)context).findViewById(R.id.headerLayout).setBackgroundColor(Color.parseColor("#00a4cc"));
//                    ((Activity)context).findViewById(R.id.menu_title).setVisibility(View.VISIBLE);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }

    }

    class notifyToDeclinedByDriver extends AsyncTask<String, String, String> {
        int responseData = 0;

        protected String doInBackground(String... args) {
            try {
                fcmRequestObject.setUserEmailID(SESSION_EMAIL);
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                Gson gson = new Gson();
                String fcmJson = "";
                fcmJson = gson.toJson(fcmRequestObject, FcmRequestObject.class);
                params.add(new BasicNameValuePair("tokens ", passengerFcmToken));
                responseData = jsonParser.makeHttpRequest(ApplicationConstants.NOTIFY_URL + "?tokens=" + passengerFcmToken, "POST", params, "application/json", fcmJson);
                Log.d("response code", String.valueOf(responseData));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return String.valueOf(responseData);
        }

        @Override
        protected void onPostExecute(String s) {
            if (s.equals("200")) {

            }
            //isDriverFree = "yes";
            //updateDriverStatus();
        }

    }

    class NotifyCancelByDriver extends AsyncTask<String, String, String> {
        int responseData = 0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (pDialog != null) {
                pDialog.dismiss();
                pDialog = null;
            }
            pDialog = new ProgressDialog(context);
            pDialog.setMessage("Please wait.......");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            Log.e(TAG, "NotifyCancelByDriver called");
            List<NameValuePair> params2 = new ArrayList<NameValuePair>();
            params2.add(new BasicNameValuePair("driverToken", params[0]));
            params2.add(new BasicNameValuePair("passengerToken", params[1]));
            params2.add(new BasicNameValuePair("fareId", params[2]));
            Log.e(TAG, "Post param=>" + params2.toString());
            Gson gson = new Gson();
            String fcmJson = "";
            fcmJson = gson.toJson(fcmRequestObject, FcmRequestObject.class);
            Log.e(TAG, "FcmJson=>" + fcmJson);

            responseData = jsonParser.makeHttpRequest(ApplicationConstants.API_DRIVER_CANCEL + "?driverToken=" + params[0] + "&passengerToken=" + params[1] + "&fareId=" + params[2], "POST", params2, "application/json", fcmJson);
            Log.d("response code", String.valueOf(responseData));

            return String.valueOf(responseData);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (pDialog != null) {
                pDialog.dismiss();
                pDialog = null;
            }
            if (s.equals("200")) {
                Log.e(TAG, "Success -> 200");
                Toast.makeText(context, "Successfully Cancel Order", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class SendSMSToPassnger extends AsyncTask {
        private int responsecode;

        @Override
        protected Object doInBackground(Object[] params) {
            try {

                if (isSmsSend) {
                    if (fareDetails.getFareid() != null && passengeremailid != null) {
                        String bodyContent = "Your Ace Kabs is here. Chauffeur is " + fareDetails.getDrivername() + ", " + fareDetails.getDrivermobile() + ". Car is " + fareDetails.getCabmodel() + ", " + fareDetails.getCabnumber() + ". Have a pleasant ride!";
                        String url = "?emailid=" + URLEncoder.encode(passengeremailid) + "&fareid=" + URLEncoder.encode(fareDetails.getFareid()) + "&mobilenumber=" + passengerMobileNumber + "&databody=" + URLEncoder.encode(bodyContent);
                        if (isNetworkAvailable()) {
                            List<NameValuePair> param = new ArrayList<NameValuePair>();
                            param.add(new BasicNameValuePair("emailid ", passengeremailid));
                            param.add(new BasicNameValuePair("fareid ", fareDetails.getFareid()));
                            param.add(new BasicNameValuePair("mobilenumber ", passengerMobileNumber));
                            param.add(new BasicNameValuePair("databody ", bodyContent));
                            responsecode = jsonParser.makeHttpRequest(ApplicationConstants.SENSSMSONSTART_URL + url, "POST", param, true);
                        }
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return String.valueOf(responsecode);
        }

        @Override
        protected void onPostExecute(Object o) {
            if (o.equals("200")) {
                prefrences.setBooleanValueToSharedPref(Constants.isfirstSMSSend, true);
            }
        }
    }

    class TestApiCall extends AsyncTask {


        int responseCode = 0;
        String mobilenumber;// = "+919912297899";
        String databody;// = "The Driver is Rajinikanth, Car Model is Mahendra";
        String emailid;// = "sathish.radhandi@gmail.com";
        String fareid;// = "f3cbe20d-9581-4272-95a2-f4fbf0c38a7c";
        int resp = 0;
        String responseString;

        public TestApiCall(String fareid, String mobile, String email, String databody) {
            this.fareid = fareid;
            this.mobilenumber = mobile;
            this.emailid = email;
            this.databody = databody;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(context);
            pDialog.setMessage("Please wait.......");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected void onPostExecute(Object o) {
            if (pDialog != null) {
                pDialog.dismiss();
                pDialog.cancel();
            }
            myMeterSwitch.setVisibility(View.VISIBLE); //added as per discussion on 06/09/2017
            totalDistanceCovered = 0.0f;
            travelledLocations.clear();
            Toast.makeText(context, responseString, Toast.LENGTH_SHORT).show();
            super.onPostExecute(o);
        }

        @Override
        protected Object doInBackground(final Object[] params) {
            Log.d("APICall", responseCode + "");
            String str_url = "http://drivers.acekabs.com:8080/acekabs/generate/taxifare/currentRide/end";
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            requestQueue.start();
            Log.e("URL=>", str_url);
            StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST, str_url, new com.android.volley.Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    resp = 10;
                    responseString = response.toString();
                    Log.e("MyResp Success", response.toString());
                }

            }, new com.android.volley.Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("MyResponse error", error.toString());
                    responseString = "ERROR";
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> hashMap = new HashMap<>();
                    hashMap.put("mobilenumber", mobilenumber);
                    hashMap.put("databody", databody);
                    hashMap.put("emailid", emailid);
                    hashMap.put("fareid", fareid);
                    Log.e(TAG, "Post Data=>" + hashMap.toString());
                    return hashMap;
                }
            };
            requestQueue.add(jsonObjectRequest);
            Log.d("APICall", responseCode + "");

            return null;
        }
    }
}
