package com.acekabs.driverapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by srinivas on 10/12/16.
 */
public class Testing extends Activity {

    JSONParser jsonParser = new JSONParser();
    String firstName = "", lastName = "", phoneNumber = "", emailID = "", compName = "", compCity = "", deviceInfo = "",
            country = "", images = "", activated = "", vehiclePhotoDoc = "", plateNumberDoc = "", driverPhotoDoc = "",
            driverLicenseDoc = "", driverIDCardDocFront = "", driverIDCardDocBack = "", driverPassportDocFront = "",
            driverPassportDocBack = "", carRegistrationDocFront = "", carRegistrationDocBack = "", vehicleInsuranceDoc = "",
            Pincode = "", CarName = "", CarModel = "", CarColor = "", CarManu_year = "", NoPass = "", Driver_DOB = "",
            Vehicle_URL = "", LicensePlate_URL = "", DriverPhoto_URL = "", DriverLicenseNo_URL = "", IDFront_URL = "", IDBack_URL = "",
            PassportFront_URL = "", PassportBack_URL = "", VehicleRegFront_URL = "", VehicleRegBack_URL = "", Insurence_URL = "";
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);

        new GettingLocation().execute();
    }

    class OTPGen extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Testing.this);
            pDialog.setMessage("Please wait.......");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected String doInBackground(String... args) {

            try {

                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("phoneNumber", "+919948373639"));

                Log.e("URL", ApplicationConstants.DRIVER_OTP_REQUEST_URL + "919948373639");
                JSONObject url_res = jsonParser.makeHttpRequest(ApplicationConstants.DRIVER_OTP_REQUEST_URL + "+919948373639", "POST", params);
                Log.e("response", url_res.toString());


            } catch (Exception e) {

            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
            pDialog.dismiss();
            pDialog.cancel();

        }

    }

    class GettingLocation extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Testing.this);
            pDialog.setMessage("Please wait.......");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected Void doInBackground(Void... arg0) {

            try {
                String strjsn = jsonParser.makeServiceCall(ApplicationConstants.DRIVER_PENDINGIMAGES2_URL + "/" + "nivasdevarapalli@gmail.com");
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

                CarName = json_res.getString("carManufacturer");
                CarModel = json_res.getString("carModel");
                CarColor = json_res.getString("carColor");
                CarManu_year = json_res.getString("yearOfManufacture");
                NoPass = json_res.getString("noOfPass");
                Driver_DOB = json_res.getString("driverDOB");

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
                String json__pic_plate_status = json_pic_plate.getString("valid");
                Log.e("PLATE NUMBER", json__pic_plate_status);


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


            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;

        }

        protected void onPostExecute(Void file_url) {
            // dismiss the dialog once done
            pDialog.dismiss();
            pDialog.cancel();


        }

    }

}
