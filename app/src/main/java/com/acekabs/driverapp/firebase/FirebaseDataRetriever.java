package com.acekabs.driverapp.firebase;

/**
 * Created by J Girish on 17-07-2017.
 */


        //import java.sql.Timestamp;
        import java.util.ArrayList;
        import java.util.List;
        //import java.util.Map;
        //import java.util.concurrent.CountDownLatch;

       // import org.hibernate.dialect.SAPDBDialect;

        //import com.acekabs.ApplicationConstants;
        import com.acekabs.driverapp.ApplicationConstants;
        import com.acekabs.driverapp.entities.AceKabsDriverPreRegistration;
        //import com.acekabs.firebase.register.CarRegistrationDocBack;
        //import com.acekabs.firebase.register.CarRegistrationDocFront;
        //import com.acekabs.firebase.register.Comment;
        //import com.acekabs.firebase.register.Images;
        import com.firebase.client.DataSnapshot;
        import com.firebase.client.Firebase;
        import com.firebase.client.FirebaseError;
        import com.firebase.client.ValueEventListener;
        import com.google.android.gms.tasks.OnSuccessListener;
        import com.google.firebase.auth.FirebaseAuth;
        //import com.google.firebase.tasks.OnSuccessListener;

/**
 * Fetches data from the firebase database.
 * */
public class FirebaseDataRetriever {

    public static void getDriverRegistrationObject(final String emailId, final IFirebaseCallback<AceKabsDriverPreRegistration> callback)
            throws InterruptedException {
        // final CountDownLatch done = new CountDownLatch(1);
        Firebase firebase = new Firebase(ApplicationConstants.FIREBASE_URL + "Pending_Registrations");
        firebase.orderByChild("emailID").equalTo(emailId).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                System.out.println("Here checking... "+dataSnapshot.getKey());

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    System.out.println(snapshot.getKey());
                    AceKabsDriverPreRegistration driver = (AceKabsDriverPreRegistration) snapshot.getValue(AceKabsDriverPreRegistration.class);
                    callback.onDataReceived(driver);
                    // done.countDown();
                }
            }
            @Override
            public void onCancelled(FirebaseError arg0) {
                // TODO Auto-generated method stub
                // done.countDown();
                System.out.println("onCancelled");

            }
        });
        // done.await();
    }

    // remain methods...

    public static void checkDriverAvalabulity(final String emailId, final IFirebaseCallback<Boolean> callback)
            throws InterruptedException {

        Firebase firebase = new Firebase(ApplicationConstants.FIREBASE_URL + "Pending_Registrations");
        firebase.orderByChild("emailID").equalTo(emailId).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildren() != null && dataSnapshot.getChildren().iterator().hasNext()) {
                    callback.onDataReceived(Boolean.TRUE);
                }else {
                    callback.onDataReceived(Boolean.FALSE);
                }
            }

            @Override
            public void onCancelled(FirebaseError arg0) {
                // TODO Auto-generated method stub

            }
        });

    }

    public static void getDriverRegistrationObjectList(final IFirebaseCallback<List<AceKabsDriverPreRegistration>> iFirebaseCallback)
            throws InterruptedException {
        Firebase firebase = new Firebase(ApplicationConstants.FIREBASE_URL + "Pending_Registrations");
        firebase.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<AceKabsDriverPreRegistration> drivers = new ArrayList<AceKabsDriverPreRegistration>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    AceKabsDriverPreRegistration driver = (AceKabsDriverPreRegistration) snapshot.getValue(AceKabsDriverPreRegistration.class);
                    drivers.add(driver);
                }
                iFirebaseCallback.onDataReceived(drivers);
            }

            @Override
            public void onCancelled(FirebaseError arg0) {

            }
        });
    }

    public static void getDriverRegistrationObject_KEY(final String emailId, final IFirebaseCallback<String> callback)
            throws InterruptedException {
        // final CountDownLatch done = new CountDownLatch(1);
        Firebase firebase = new Firebase(ApplicationConstants.FIREBASE_URL + "Pending_Registrations");
        firebase.orderByChild("emailID").equalTo(emailId).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                System.out.println("Here checking... "+dataSnapshot.getKey());

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    System.out.println(snapshot.getKey());
                    System.out.println(snapshot.toString());
                    //AceKabsDriverPreRegistration driver = (AceKabsDriverPreRegistration) snapshot.getValue(AceKabsDriverPreRegistration.class);
                    callback.onDataReceived(snapshot.getKey());
                    // done.countDown();
                }
            }
            @Override
            public void onCancelled(FirebaseError arg0) {
                // TODO Auto-generated method stub
                // done.countDown();
                System.out.println("onCancelled");

            }
        });
        // done.await();
    }

    public static void getActiveDriverObject_KEY(final String emailId, final IFirebaseCallback<String> callback)
            throws InterruptedException {
        // final CountDownLatch done = new CountDownLatch(1);
        Firebase firebase = new Firebase(ApplicationConstants.FIREBASE_URL + "Drivers/location_id_1");
        firebase.orderByChild("emailId").equalTo(emailId).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                System.out.println("Here checking... "+dataSnapshot.getKey());

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    System.out.println(snapshot.getKey());
                    System.out.println(snapshot.toString());
                    //AceKabsDriverPreRegistration driver = (AceKabsDriverPreRegistration) snapshot.getValue(AceKabsDriverPreRegistration.class);
                    callback.onDataReceived(snapshot.getKey());
                    // done.countDown();
                }
            }
            @Override
            public void onCancelled(FirebaseError arg0) {
                // TODO Auto-generated method stub
                // done.countDown();
                System.out.println("onCancelled");

            }
        });
        // done.await();
    }

    public static void getActiveDriverObject(final String emailId, final IFirebaseCallback<String> callback)
            throws InterruptedException {
        // final CountDownLatch done = new CountDownLatch(1);
        Firebase firebase = new Firebase(ApplicationConstants.FIREBASE_URL + "Drivers/location_id_1");
        firebase.orderByChild("emailId").equalTo(emailId).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                System.out.println("Here checking... "+dataSnapshot.getKey());

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    System.out.println(snapshot.getKey());
                    System.out.println(snapshot.toString());
                    AceKabsDriverPreRegistration driver = (AceKabsDriverPreRegistration) snapshot.getValue(AceKabsDriverPreRegistration.class);
                    callback.onDataReceived(snapshot.getKey());
                    // done.countDown();
                }
            }
            @Override
            public void onCancelled(FirebaseError arg0) {
                // TODO Auto-generated method stub
                // done.countDown();
                System.out.println("onCancelled");

            }
        });
        // done.await();
    }

    /*public static void getFireBaseToken(final String sessionId, final IFirebaseCallback<String> callback)
            throws InterruptedException {


        FirebaseAuth.getInstance().createCustomToken(sessionId)
                .addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String customToken) {
                        // Send token back to client
                        callback.onDataReceived(customToken);
                    }
                });*/

		/*Firebase firebase = new Firebase(ApplicationConstants.FIREBASE_DATABASE_URL + "Pending_Registrations");
		firebase.orderByChild("emailID").equalTo(emailId).addListenerForSingleValueEvent(new ValueEventListener() {

			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				if(dataSnapshot.getChildren() != null && dataSnapshot.getChildren().iterator().hasNext()) {
					callback.onDataReceived(Boolean.TRUE);
				}else {
					callback.onDataReceived(Boolean.FALSE);
				}
			}

			@Override
			public void onCancelled(FirebaseError arg0) {
				// TODO Auto-generated method stub

			}
		});

    }*/

    public static void getDriverRegistrationImage(String imagName, final IFirebaseCallback<String> iFirebaseCallback) throws InterruptedException {
        Firebase firebase = new Firebase(ApplicationConstants.FIREBASE_STORAGE_URL + "doc");
		/*
		 * firebase.getDownloadUrl().addOnSuccessListener(new
		 * OnSuccessListener<Uri>() {
		 *
		 * @Override public void onSuccess(Uri downloadUrl) { //do something
		 * with downloadurl } });
		 */
    }
    // public static void main(String[] args) throws InterruptedException {
    // System.out.println("inside main");
    // FirebaseDataRetriever.getDriverRegistrationObject("abc@acekabs.com", new
    // IFirebaseCallback<AceKabsDriverPreRegistration>() {
    //
    // @Override
    // public void onFailure(Exception ex) {
    // // TODO Auto-generated method stub
    //
    // }
    //
    // @Override
    // public void onDataReceived(AceKabsDriverPreRegistration data) {
    // System.out.println(data.getFirstName());
    // }
    // });
    // }

    // public static void main(String[] args) {
    // Firebase firebase = new
    // Firebase(ApplicationConstants.FIREBASE_DATABASE_URL+"Drivers/location_id_1");
    // firebase.addListenerForSingleValueEvent(new ValueEventListener() {
    //
    // @Override
    // public void onDataChange(DataSnapshot dataSnapshot) {
    // final List<AceKabsDriver> drivers = new ArrayList<AceKabsDriver>();
    // for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
    // AceKabsDriver driver = (AceKabsDriver)
    // snapshot.getValue(AceKabsDriver.class);
    // System.out.println(driver.getDriverName());
    // drivers.add(driver);
    // }
    // }
    //
    // @Override
    // public void onCancelled() {
    //
    //
    // }
    // });
    //
    // }
}
