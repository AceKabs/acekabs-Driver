package com.acekabs.driverapp.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Adee09 on 4/29/2017.
 */

public class EarningData implements Parcelable {
    @SerializedName("TotalWeekEarn")
    private String totalWeekEarn;
    @SerializedName("Monday")
    private String monday;
    @SerializedName("Thursday")
    private String thursday;
    @SerializedName("Friday")
    private String friday;
    @SerializedName("LastTripEarn")
    private String lastTripEarn;
    @SerializedName("Sunday")
    private String sunday;
    @SerializedName("Wednesday")
    private String wednesday;
    @SerializedName("Tuesday")
    private String tuesday;
    @SerializedName("Saturday")
    private String saturday;

    public EarningData() {

    }

    public String getTotalWeekEarn() {
        return totalWeekEarn;
    }

    public void setTotalWeekEarn(String totalWeekEarn) {
        this.totalWeekEarn = totalWeekEarn;
    }

    public String getMonday() {
        return monday;
    }

    public void setMonday(String monday) {
        this.monday = monday;
    }

    public String getThursday() {
        return thursday;
    }

    public void setThursday(String thursday) {
        this.thursday = thursday;
    }

    public String getFriday() {
        return friday;
    }

    public void setFriday(String friday) {
        this.friday = friday;
    }

    public String getLastTripEarn() {
        return lastTripEarn;
    }

    public void setLastTripEarn(String lastTripEarn) {
        this.lastTripEarn = lastTripEarn;
    }

    public String getSunday() {
        return sunday;
    }

    public void setSunday(String sunday) {
        this.sunday = sunday;
    }

    public String getWednesday() {
        return wednesday;
    }

    public void setWednesday(String wednesday) {
        this.wednesday = wednesday;
    }

    public String getTuesday() {
        return tuesday;
    }

    public void setTuesday(String tuesday) {
        this.tuesday = tuesday;
    }

    public String getSaturday() {
        return saturday;
    }

    public void setSaturday(String saturday) {
        this.saturday = saturday;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
