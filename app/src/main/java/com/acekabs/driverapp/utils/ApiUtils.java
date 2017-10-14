package com.acekabs.driverapp.utils;

import com.acekabs.driverapp.ApplicationConstants;
import com.acekabs.driverapp.restclient.RestApiService;
import com.acekabs.driverapp.restclient.RetrofitClient;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Adee09 on 4/15/2017.
 */

public class ApiUtils {

    public static String apiBaseUrl = ApplicationConstants.RESTBASEURL;
    private static Retrofit retrofit;

    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(apiBaseUrl);

    private static OkHttpClient.Builder httpClient =
            new OkHttpClient.Builder();

    // No need to instantiate this class.
    private ApiUtils() {

    }

    public static RestApiService getAPIService(String baseUrl)
    {
        return RetrofitClient.getClient(baseUrl).create(RestApiService.class);
    }


    public static void changeApiBaseUrl(String newApiBaseUrl) {
        apiBaseUrl = newApiBaseUrl;
        builder = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(apiBaseUrl);
    }

}