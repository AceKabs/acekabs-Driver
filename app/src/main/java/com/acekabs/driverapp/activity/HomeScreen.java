package com.acekabs.driverapp.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.acekabs.driverapp.R;
import com.acekabs.driverapp.db.DBAdapter;
import com.acekabs.driverapp.db.SessionManager;
import com.acekabs.driverapp.entities.AceKabsDriverPreRegistration;
import com.acekabs.driverapp.firebase.FirebaseDriverUtil;
import com.acekabs.driverapp.pojo.CarRegistrationDocBack;
import com.acekabs.driverapp.pojo.CarRegistrationDocFront;
import com.acekabs.driverapp.pojo.DriverIDCardDocBack;
import com.acekabs.driverapp.pojo.DriverIDCardDocFront;
import com.acekabs.driverapp.pojo.DriverLicenseDoc;
import com.acekabs.driverapp.pojo.DriverPassportDocBack;
import com.acekabs.driverapp.pojo.DriverPassportDocFront;
import com.acekabs.driverapp.pojo.DriverPhotoDoc;
import com.acekabs.driverapp.pojo.Images;
import com.acekabs.driverapp.pojo.PlateNumberDoc;
import com.acekabs.driverapp.pojo.VehicleInsuranceDoc;
import com.acekabs.driverapp.pojo.VehiclePhotoDoc;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class HomeScreen extends Activity {

    View home_header;
    ImageView menu_item;
    TextView title;
    LinearLayout slide_profile;
    LinearLayout slide_payment;
    LinearLayout slide_history;
    AceKabsDriverPreRegistration driver;
    DBAdapter mydb;
    SessionManager msessionManager;
    String SESS_EMAIL = "";
    Cursor all_cursor;
    Button home_dashboard;
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        mydb = DBAdapter.getInstance(getApplicationContext());
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        msessionManager = new SessionManager(getApplicationContext());

        HashMap<String, String> user = msessionManager.getUserDetails();
        SESS_EMAIL = user.get(SessionManager.KEY_NAME);
        all_cursor = mydb.getDriver_reg_Email(SESS_EMAIL);

        home_dashboard = (Button) findViewById(R.id.home_dashboard);


        //  driver_reginfo = new AceKabsDriverPreRegistration();
        // FirebaseDriverUtil.addDriverRegistrationInformation(driver_reginfo);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        home_header = findViewById(R.id.home_header);
        menu_item = (ImageView) home_header.findViewById(R.id.menu_menu);
        title = (TextView) home_header.findViewById(R.id.menu_title);
        title.setText("ACE CABS");
        slide_profile = (LinearLayout) findViewById(R.id.slide_profile);
        slide_payment = (LinearLayout) findViewById(R.id.slide_payment);
        slide_history = (LinearLayout) findViewById(R.id.slide_history);

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
                    Log.e("Home car_photo_url", "" + car_photo_url);
                    //Rajini
                    Log.e("Home License_plate_phot", "" + driverLicensePlate_URL);
                    Log.e("Home license_plate_no", license_plate_no);
                    Log.e("Home driver_dob", driver_dob);
                    Log.e("Home driverPhoto_URL", driverPhoto_URL);
                    Log.e("Home drLicenseNo_URL", "" + driverLicenseNo_URL);
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
                    Log.e("license_plate_lat", license_plate_lat);
                    Log.e("license_plate_long", license_plate_long);
                    Log.e("license_image_status", license_image_status);

                    Log.e("Ending....Home car_photo_url", "" + car_photo_url);


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


                    Images url_img = new Images();
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
                    driver = new AceKabsDriverPreRegistration();
                    driver.setActivated(false);//
                    driver.setFirstName(fname);
                    driver.setLastName(driver_lname);
                    driver.setPhoneNumber(driver_mobileno);
                    driver.setEmailID(driver_emailid);
                    driver.setPassword(password);
                    driver.setCompName(company_name);
                    driver.setCompAddr(company_address);
                    driver.setCompCity(city);
                    driver.setPinCode(pincode);
                    driver.setiMEI(imei);
                    driver.setLatLong(coordinates);
                    driver.setDeviceInfo(device_info);
                    driver.setCarManufacturer(car_manufacture);
                    driver.setCarModel(car_model);
                    driver.setCarColor(car_color);
                    driver.setYearOfManufacture(year_of_manufacturer);
                    driver.setNoOfPass(no_of_pass);
                    driver.setLicensePlateNumber(license_plate_no);
                    driver.setDriverDOB(driver_dob);
                    driver.setVehiclePhotoDocURL(car_photo_url);
                    driver.setPlateNumberDocURL(driverLicensePlate_URL);
                    driver.setDriverPhotoDocURL(driverPhoto_URL);
                    driver.setDriverLicenseDocURL(driverLicenseNo_URL);
                    driver.setDriverIDCardDocFrontURL(driverQatar_idFront_URL);
                    driver.setDriverIDCardDocBackURL(driverQatar_idBack_URL);
                    driver.setDriverPassportDocFrontURL(driverPassport_idFront_URL);
                    driver.setDriverPassportDocBackURL(driverPassport_idBack_URL);
                    driver.setCarRegistrationDocFrontURL(carRegistrationDocFrontURL);
                    driver.setCarRegistrationDocBackURL(carRegistrationDocBackURL);
                    driver.setVehicleInsuranceDocURL(vehicleInsuranceDocURL);
                    driver.setImages(url_img);
                    driver.setCountry(country);
                    driver.setRegDate(new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(new Date()));
                    FirebaseDriverUtil.addDriverRegistrationInformation(driver);
                    mydb.UpdatedDriverStatus(driver_emailid);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        menu_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mDrawerLayout.openDrawer(Gravity.LEFT);
            }
        });

        slide_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent li = new Intent(HomeScreen.this, Profile.class);
                li.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(li);

            }
        });

        slide_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent li = new Intent(HomeScreen.this, PaymentActivity.class);
                li.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(li);

            }
        });

        slide_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent li = new Intent(HomeScreen.this, PaymentActivity.class);
                li.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(li);

            }
        });

        home_dashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i1 = new Intent(HomeScreen.this, MainHomeScreen.class);
                startActivity(i1);
            }
        });


    }
}

