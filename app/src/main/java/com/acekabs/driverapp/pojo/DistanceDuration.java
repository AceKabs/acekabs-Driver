package com.acekabs.driverapp.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.acekabs.driverapp.utils.UtilityMethods;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by ankit jain on 15-04-2017.
 */
public class DistanceDuration implements Parcelable {

    @SerializedName("text")
    @Expose
    public String text;

    @SerializedName("value")
    @Expose
    public String value;

    public String getDuration() {
        if (UtilityMethods.isBlank(text)) {
            return "";
        }
        text = text.replace("hours", "H\n");
        text = text.replace("mins", "M");
        return text;
    }

    public String getDistance() {
        if (UtilityMethods.isBlank(text)) {
            return "";
        }
        text = text.replace("mi", "");
        return text;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
