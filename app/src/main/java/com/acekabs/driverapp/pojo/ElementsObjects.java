package com.acekabs.driverapp.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by ankit on 14-04-2017.
 */
public class ElementsObjects implements Parcelable {


    @SerializedName("distance")
    @Expose
    public DistanceDuration distance;

    @SerializedName("duration")
    @Expose
    public DistanceDuration duration;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
