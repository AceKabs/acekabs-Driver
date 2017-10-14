package com.acekabs.driverapp;

public class ApplicationConstants {
    /*REST CALL URL*/
    public static final String RESTBASEURL = "http://drivers.acekabs.com:8080/";
    /*BASE URL*/
    public static final String BASEURL = "http://drivers.acekabs.com:8080/acekabs-registration-1/driver/";
    /*URL of AceKabs web application*/
    public static final String WEB_APP_ROOT_URL = "http://23.251.144.108/acekabs-registration-1/";
    public static final String WEB_APP_SUBMIT_DRIVER_TOKEN_URL = WEB_APP_ROOT_URL + "driver/tokens/register";

    /*URL of firebase database*/
    public static final String FIREBASE_URL = "https://acekabs-1df9e.firebaseio.com/";
    public static final String FIREBASE_DRIVER_URL = FIREBASE_URL + "Drivers";
    public static final String FIREBASE_DRIVER_REGISTRATION_URL = FIREBASE_URL + "Pending_Registrations";
    public static final String FIREBASE_SAVE_FAREDETAILS = FIREBASE_URL + "taxifare";
    public static final String FIREBASE_SAVE_METERDETAILS = FIREBASE_URL + "taxifare_eta";
    public static final String GEOFIRE_NODE = "geofire";
    /* URL of firebase storage*/
    public static final String FIREBASE_STORAGE_URL = "gs://acekabs-1df9e.appspot.com/";


    /* URL for Driver OTP Request */
    public static final String DRIVER_OTP_REQUEST_URL = "http://drivers.acekabs.com:8080/acekabs/generate/email/sms/";

    /* URL for Driver OTP verification */
    public static final String DRIVER_OTP_CHECK_URL = "http://drivers.acekabs.com:8080/acekabs/activate/smsOtp/";

    /* URL for Driver Resend OTP */
    public static final String DRIVER_OTP_RESEND_URL = "http://drivers.acekabs.com:8080/acekabs/generate/resend/smsOtp/";

    /* URL for Driver Email */
    public static final String DRIVER_EMAIL_URL = "http://drivers.acekabs.com:8080/acekabs/generate/emailLink/";

    /* URL for Driver Email exists or NOT */
    public static final String DRIVER_EMAIL_VALIDATION_URL = "http://drivers.acekabs.com:8080/acekabs/verify/email/sms/";

    /* URL for Driver ForgotPass URL */
    public static final String DRIVER_FORGOTPASS_URL = "http://drivers.acekabs.com:8080/acekabs/generate/forgot/emailLink/";

    /* URL for Driver SIGNIN URL */
    public static final String DRIVER_SIGNIN_URL = "http://app.aceclouds.com:8585/acekabs/driver/login/email/password/";
    /* URL for Driver SIGNIN_FIREBASE_URL */
    public static final String DRIVER_SIGNIN_FIREBASE_URL = "http://drivers.acekabs.com:8080/acekabs-registration-1/driver/register/authenticate/";
    /* URL for Driver SIGNIN_FIREBASE_URL */
    public static final String DRIVER_PENDINGIMAGES_URL = "http://drivers.acekabs.com:8080/acekabs-registration-1/driver/register/email/";
    /* URL for Driver Showing PenndingImages */
    public static final String DRIVER_PENDINGIMAGES2_URL = "http://drivers.acekabs.com:8080/acekabs-registration-1/driver/register/device/email/";
    /* URL for Changing IMEI number  */
    public static final String DRIVER_CHANGE_IMEI_URL = "http://drivers.acekabs.com:8080/acekabs/generate/emailLink/support/imei/";
    /*URL for save FCM  Token*/
    public static final String SAVE_DRIVER_FCM_TOKEN_URL = "http://drivers.acekabs.com:8080/acekabs-registration-1/driver/tokens/register/";
    /*URL for get Passnger fcm Token*/
    public static final String GET_PASSENGER_FCM_TOKEN_URL = "http://riders.acekabs.com:8080/acekabs-registration-1/passenger/tokens/";
    /*Url for change password */
    public static final String CHANGEPASSWORD_URL = "http://drivers.acekabs.com:8080/acekabs-registration-1/driver/profile/password";
    /*URL for notify passnger in every 10 seconds*/
    public static final String NOTIFY_URL = "http://drivers.acekabs.com:8080/acekabs-registration-1/notify/iostest";
    public static final String API_KEY = "AIzaSyBYJGrOifFv__Qh3QULabyLdP5wGpGv9vg";
    /*URL for send SMS */
    public static final String SENSSMSONSTART_URL = "http://drivers.acekabs.com:8080/acekabs/generate/taxifare/currentRide/start";
    /*URL for save invoice */
    public static final String SAVEINVOICE_URL = "http://drivers.acekabs.com:8080/taxi-fare-service-1/taxi/currentride/invoice";
    /*URL for earning history */
    public static final String EARNINGHISTORY_URL = "taxi-fare-service-1/taxi/driver/weekEarning?";
    /*URL for send invoice through fcm*/
    public static final String SENTINVOICE_URL = "http://drivers.acekabs.com:8080/taxi-fare-service-1/taxi/currentride/passeger/ios/summary?";
    /*GET DISTANCE FROM SERVER*/
    public static final String CALCULATIONONSERVER_URL = "http://drivers.acekabs.com:8080/DriverSimulator/check/gps/distance";
    /*PENDING RIDES*/
    public static final String PENDINIGRIDES_URL = "http://drivers.acekabs.com:8080/taxi-fare-service-1/taxi/ride/status/driver";
    // ALL TRIPS API
    public static final String ALLTRIPSAPI = RESTBASEURL + "taxi-fare-service-1/taxi/rides/driver";
    /* STORING MOBILE NUMBER */
  //  public static final String UPDATE_URL ="http://drivers.acekabs.com:8080/acekabs-registration-1/driver/register/device/email/srikanthshow@gmail.com ";
    public static String MOBILE_NO = "";


    //public static final String API_DRIVER_CANCEL = RESTBASEURL + "acekabs/taxifare/cancel/email";
    public static final String API_DRIVER_CANCEL = RESTBASEURL + "taxi-fare-service-1/taxi/ride/cancel/";



    public static  final String PAYMENT_API="http://52.205.112.111/testpayment/payment.php";
}
