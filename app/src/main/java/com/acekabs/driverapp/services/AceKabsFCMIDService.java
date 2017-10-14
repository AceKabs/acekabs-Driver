package com.acekabs.driverapp.services;

import android.util.Log;

import com.acekabs.driverapp.ApplicationConstants;
import com.acekabs.driverapp.constants.Constants;
import com.acekabs.driverapp.utils.SharePreferanceWrapperSingleton;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;

public class AceKabsFCMIDService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("******FCMID******", "Token: " + refreshedToken);
        // TODO: Fetch the driver id
        submitToken(refreshedToken, "882348");
    }

    private void submitToken(String token, String driverId) {
        RequestBody body = new FormBody.Builder().add("fcmId", token).add("driverId", "123456").build();
        Request request = new Request.Builder().url(ApplicationConstants.WEB_APP_SUBMIT_DRIVER_TOKEN_URL).post(body).build();
        new AceKabsRestServiceInvoker().execute(request);
        SharePreferanceWrapperSingleton prefrences = SharePreferanceWrapperSingleton.getSingletonInstance();
        prefrences.setPref(this);
        prefrences.setValueToSharedPref(Constants.FCM_TOKEN,token);
    }
}
