package com.acekabs.driverapp.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by ankit jain on 15-04-2017.
 */
public class Destinations implements Parcelable {

    @SerializedName("destination_addresses")
    @Expose
    public ArrayList<String> destination_addresses = new ArrayList<String>();

    @SerializedName("origin_addresses")
    @Expose
    public ArrayList<String> origin_addresses = new ArrayList<String>();

    @SerializedName("rows")
    @Expose
    public ArrayList<Items> rows = new ArrayList<Items>();

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
