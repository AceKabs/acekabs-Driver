package com.acekabs.driverapp.services;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.acekabs.driverapp.ApplicationConstants;
import com.acekabs.driverapp.firebase.IFirebaseCallback;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.firebase.storage.UploadTask.TaskSnapshot;

/**
 * Uploads and stores documents to firebase storage.
 */

public class AceKabsDocumentUploadService {


    public void uploadFileFromDisk(final Uri file, final IFirebaseCallback<Uri> callback) {
        Log.d("upload file name", file.toString());
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl(ApplicationConstants.FIREBASE_STORAGE_URL);
        StorageReference fileRef = storageRef.child("driverimages/" + ApplicationConstants.MOBILE_NO + "/" + ApplicationConstants.MOBILE_NO + file.getLastPathSegment());
        UploadTask uploadTask = fileRef.putFile(file);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                callback.onFailure(exception);
            }
        }).addOnSuccessListener(new OnSuccessListener<TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                Log.d("Uploaded Doc URL", downloadUrl.toString());
                callback.onDataReceived(downloadUrl);
            }
        });

    }
}
