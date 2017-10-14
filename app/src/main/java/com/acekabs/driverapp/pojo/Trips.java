package com.acekabs.driverapp.pojo;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by ankit on 25-06-2017.
 */

public class Trips implements Parcelable {

    @SerializedName("totalfare")
    private String totalfare;

    @SerializedName("totalduration")
    private String totalduration;

    @SerializedName("pickupLatitude")
    private String pickupLatitude;

    @SerializedName("pickupLongitude")
    private String pickupLongitude;

    @SerializedName("pickuplocationname")
    private String pickuplocationname;

    @SerializedName("droplocationname")
    private String droplocationname;

    @SerializedName("dropLatitude")
    private String dropLatitude;

    @SerializedName("dropLongitude")
    private String dropLongitude;

    @SerializedName("starttime")
    private long starttime;

    @SerializedName("Routes")
    private List<RouteDetails> routeDetailsList;

    public String getTotalfare() {
        return totalfare;
    }

    public String getTotalduration() {
        return totalduration;
    }

    public String getPickupLatitude() {
        return pickupLatitude;
    }

    public String getPickupLongitude() {
        return pickupLongitude;
    }

    public String getPickuplocationname() {
        return pickuplocationname;
    }

    public String getDroplocationname() {
        return droplocationname;
    }

    public String getDropLatitude() {
        return dropLatitude;
    }

    public String getDropLongitude() {
        return dropLongitude;
    }

    public long getStarttime() {
        return starttime;
    }


    public List<RouteDetails> getRouteDetailsList() {
        return routeDetailsList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }

    public String getDistance() {
        if (!TextUtils.isEmpty(totalduration)) {
            Double durationKM = Double.parseDouble(totalduration) / 1000;
            return String.format("%.2f", durationKM);
        }
        return "";
    }
}
