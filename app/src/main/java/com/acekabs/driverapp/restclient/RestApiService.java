package com.acekabs.driverapp.restclient;

import com.acekabs.driverapp.ApplicationConstants;
import com.acekabs.driverapp.pojo.ApiResultData;
import com.acekabs.driverapp.pojo.CalculateDistance;
import com.acekabs.driverapp.pojo.Destinations;
import com.acekabs.driverapp.pojo.EarningData;
import com.acekabs.driverapp.pojo.Example;
import com.acekabs.driverapp.pojo.FareDetails;
import com.acekabs.driverapp.pojo.RouteDetails;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by Adee09 on 4/15/2017.
 */

public interface RestApiService {
    @Headers("Content-Type: application/json")
    @GET("maps/api/distancematrix/json")
    Call<Destinations> getDistanceData(@Query("origins") String origins, @Query("destinations") String destinations, @Query("key") String key);

    @Headers("Content-Type: application/json")
    @POST("profile/password")
    Call<ApiResultData> changePassword(@Query("emailId") String emailId, @Query("oldPassword") String oldPassword, @Query("newPassword") String newPassword);

    @Headers("Content-Type: application/json")
    @POST(ApplicationConstants.EARNINGHISTORY_URL)
    Call<EarningData> getEarningHistory(@Query("driverEmail") String driverEmail, @Query("searchDate") String searchDate);

    @Headers("Content-Type: application/json")
    @POST(ApplicationConstants.CALCULATIONONSERVER_URL)
    Call<CalculateDistance> getDistanceFromServer(@Body List<RouteDetails> data);

    @Headers("Content-Type: application/json")
    @GET()
    Call<Example> getTotalDistance(@Url String url, @Query("units") String units, @Query("origin") String origin, @Query("destination") String destination, @Query("mode") String mode, @Query("alternatives") boolean alternatives);

    @FormUrlEncoded
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @POST("taxi-fare-service-1/taxi/ride/status/driver")
    Call<FareDetails> getPendingRide(@Field("driverEmail") String driverEmail);
}
