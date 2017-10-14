package com.acekabs.driverapp.firebase;

import android.util.Log;

import com.acekabs.driverapp.ApplicationConstants;
import com.acekabs.driverapp.entities.AceKabsDriver;
import com.acekabs.driverapp.entities.AceKabsDriverPreRegistration;
import com.acekabs.driverapp.pojo.FareDetails;
import com.acekabs.driverapp.pojo.TaxiMeterData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Utility class to communicate with Firebase to handle driver data.
 */
public class FirebaseDriverUtil {


    public static void addDriverRegistrationInformation(AceKabsDriverPreRegistration driver) {
        Firebase ref = new Firebase(ApplicationConstants.FIREBASE_DRIVER_REGISTRATION_URL);
        ref.push().setValue(driver);
    }

    public static void isDriverAccountActivated(String emailId, final IFirebaseCallback<Boolean> callback) {
        final Firebase ref = new Firebase(ApplicationConstants.FIREBASE_DRIVER_REGISTRATION_URL);
        ref.orderByChild("emailID").equalTo(emailId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        AceKabsDriverPreRegistration driver = (AceKabsDriverPreRegistration) snapshot.getValue(AceKabsDriverPreRegistration.class);
                        callback.onDataReceived(driver.isActivated());
                    }

                } catch (Exception ex) {
                    callback.onFailure(ex);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    /**
     * Creates a driver object in the firebase database
     */
    public static void addDriver(AceKabsDriver driver, String locationId) {
        Firebase ref = new Firebase(ApplicationConstants.FIREBASE_DRIVER_URL);
        ref.child(locationId).push().setValue(driver);
    }

    public static void updateDriverLocation(String locationId, String driverId, String latLocation, String longLocation) {
        Firebase ref = new Firebase(ApplicationConstants.FIREBASE_DRIVER_URL + "/" + locationId);
        Firebase driverNode = ref.child(driverId);
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("latLocation", latLocation);
        map.put("longLocation", longLocation);
        driverNode.updateChildren(map);
    }

    public static void getDriversForLocation(String locationId, final IFirebaseCallback<List<AceKabsDriver>> callback) {
        final Firebase ref = new Firebase(ApplicationConstants.FIREBASE_DRIVER_URL + "/" + locationId);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    final List<AceKabsDriver> drivers = new ArrayList<AceKabsDriver>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        AceKabsDriver driver = (AceKabsDriver) snapshot.getValue(AceKabsDriver.class);
                        drivers.add(driver);
                    }
                    callback.onDataReceived(drivers);
                } catch (Exception ex) {
                    callback.onFailure(ex);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public static void getDriversByMobileNumber(String locationId, String mobileNumber, final IFirebaseCallback<List<AceKabsDriver>> callback) {
        final Firebase ref = new Firebase(ApplicationConstants.FIREBASE_DRIVER_URL + "/" + locationId);
//        ref.equalTo("mobileNumber", mobileNumber).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                try {
//                    final List<AceKabsDriver> drivers = new ArrayList<AceKabsDriver>();
//                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                        AceKabsDriver driver = (AceKabsDriver) snapshot.getValue(AceKabsDriver.class);
//                        drivers.add(driver);
//                    }
//                    callback.onDataReceived(drivers);
//                } catch (Exception ex) {
//                    callback.onFailure(ex);
//                }
//            }
//
//            @Override
//            public void onCancelled(FirebaseError firebaseError) {
//
//            }
//        });
        ref.orderByChild("mobileNumber").equalTo(mobileNumber).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    final List<AceKabsDriver> drivers = new ArrayList<AceKabsDriver>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        AceKabsDriver driver = snapshot.getValue(AceKabsDriver.class);
                        drivers.add(driver);
                    }
                    callback.onDataReceived(drivers);
                } catch (Exception ex) {
                    Log.e("Error", ex.getMessage().toString());
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public static void saveFareDetails(FareDetails fareDetails) {
        Firebase ref = new Firebase(ApplicationConstants.FIREBASE_SAVE_FAREDETAILS);
        ref.push().setValue(fareDetails, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError != null) {
                    Log.e("FireBaseDriverUtil" ,"Data could not be saved=>"+ firebaseError.getMessage());
                }
                else
                    {
                    Log.e("FireBaseDriverUtil" ,"Data saved successfully.=>");
                }
            }
        });
    }

    public static void saveTaxiMeterDetails(TaxiMeterData taxiMeter) {
        Firebase ref = new Firebase(ApplicationConstants.FIREBASE_SAVE_METERDETAILS);
        ref.push().setValue(taxiMeter);
    }



}
