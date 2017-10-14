package com.acekabs.driverapp.activity;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.acekabs.driverapp.ApplicationConstants;
import com.acekabs.driverapp.entities.AceKabsDriverPreRegistration;
import com.acekabs.driverapp.firebase.FirebaseDataRetriever;
import com.acekabs.driverapp.firebase.IFirebaseCallback;
import com.acekabs.driverapp.services.AceKabsDocumentUploadService;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.firebase.client.Firebase;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.HashMap;
import java.util.Map;

public class RejectDriverRegistration {
    private AceKabsDriverPreRegistration driverPreRegEntity;
    private  Context context;

    public RejectDriverRegistration(AceKabsDriverPreRegistration driverPreRegEntity, Context context) {
        this.driverPreRegEntity = driverPreRegEntity;
        this.context=context;
        Log.e("Emailid",this.driverPreRegEntity.getEmailID()+"");

        process();
    }

    public void process() {
        updateFirebaseEntryForDriver();
    }

    public boolean updateFirebaseEntryForDriver() {
        // fetch driver details from firebase
        try {
            Log.e("Emailid",driverPreRegEntity.getEmailID()+"");
            FirebaseDataRetriever.getDriverRegistrationObject_KEY(driverPreRegEntity.getEmailID(), new IFirebaseCallback<String>() {

                @Override
                public void onFailure(Exception ex) {
                    ex.printStackTrace();
                }

                @Override
                public void onDataReceived(String data) {
                    System.out.println(data);
                    pushToFireBaseForDriverRejection(data);

                }

            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return true;

    }

    public synchronized void pushToFireBaseForDriverRejection(String driver) {
        Log.e("Emailid",driverPreRegEntity.getEmailID());
        // update the comments & images validation in Fire Base
        Firebase ref = new Firebase(ApplicationConstants.FIREBASE_DRIVER_REGISTRATION_URL);
        Firebase driverNode = ref.child(driver);
        System.out.println(driverNode.getKey());
        //HashMap<String, Object> map = new HashMap<String, Object>();
        Map<String, Object> map = new ObjectMapper().convertValue(driverPreRegEntity, Map.class);
        //map.put("images", driverPreRegEntity);
        driverNode.updateChildren(map);
        Toast.makeText(context,"Successfully Uploaded the Changes",Toast.LENGTH_SHORT).show();
    }
}
