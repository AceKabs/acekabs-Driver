package com.acekabs.driverapp.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by ankit on 25-06-2017.
 */

public class TripHistoryData implements Parcelable {

    @SerializedName("History")
    private List<Trips> History;

    public List<Trips> getHistory() {
        return History;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
