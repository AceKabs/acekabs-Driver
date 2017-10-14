package com.acekabs.driverapp.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by ankit jain on 15-04-2017.
 */
public class Items implements Parcelable {
//    @SerializedName("elements")
//    @Expose
//    public ArrayList<Elements> elementsArrayList = new ArrayList<Elements>();

    @SerializedName("elements")
    @Expose
    public ArrayList<ElementsObjects> elementsObjectses = new ArrayList<ElementsObjects>();

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
