package com.acekabs.driverapp.pojo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Adee09 on 4/14/2017.
 */

public class RouteDetails implements Parcelable {
    private String location;
    private String totaltime;
    private String time;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTotaltime() {
        return totaltime;
    }

    public void setTotaltime(String totaltime) {
        this.totaltime = totaltime;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
