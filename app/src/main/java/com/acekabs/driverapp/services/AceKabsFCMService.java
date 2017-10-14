package com.acekabs.driverapp.services;

import android.app.NotificationManager;
import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.acekabs.driverapp.R;
import com.acekabs.driverapp.pojo.FcmRequestObject;
import com.acekabs.driverapp.utils.CommonUtilities;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;


public class AceKabsFCMService extends FirebaseMessagingService {

    private RemoteMessage message;
    private FcmRequestObject fcmRequestObject;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        this.message = remoteMessage;
        Log.d("FCM Data", message.getData().toString());


        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d("FCM Notification", "Message Notification Body: " + remoteMessage.getNotification().getBody());

            String data = message.getData().get("message");
            Gson gson = new Gson();
            fcmRequestObject = gson.fromJson(data, FcmRequestObject.class);
            sendNotification(fcmRequestObject);
        }

    }

    private void sendNotification(FcmRequestObject messageBody) {
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(false)
                .setSound(defaultSoundUri);
        if (!TextUtils.isEmpty(messageBody.getPickUpLocation())) {
            notificationBuilder.setContentTitle(messageBody.getPickUpLocation().toString());
        }
        if (!TextUtils.isEmpty(messageBody.getEstimateDistance())) {
            notificationBuilder.setSubText(messageBody.getEstimateDistance());
        }
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notificationBuilder.build());
        CommonUtilities.displayMessage(this.getApplicationContext(), messageBody);
    }
}
