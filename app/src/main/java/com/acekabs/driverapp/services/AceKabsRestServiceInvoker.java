package com.acekabs.driverapp.services;

import android.os.AsyncTask;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;


public class AceKabsRestServiceInvoker extends AsyncTask<Request, Void, String> {
    @Override
    protected String doInBackground(Request... params) {
        Request request = params[0];
        invokeRestService(request);
        return "Done";
    }

    private void invokeRestService(Request request) {
        OkHttpClient client = new OkHttpClient();

        try {
            client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
