package com.acekabs.driverapp.pojo;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Adee09 on 5/13/2017.
 */

public class CalculateDistance {

    @SerializedName("Google")
    private String Google;
    @SerializedName("Degrees")
    private String Degrees;
    @SerializedName("Radians")
    private String Radians;


    public CalculateDistance() {

    }

    public String getGoogle() {
        return Google;
    }

    public void setGoogle(String google) {
        Google = google;
    }

    public String getDegrees() {
        return Degrees;
    }

    public void setDegrees(String degrees) {
        Degrees = degrees;
    }

    public String getRadians() {
        return Radians;
    }

    public void setRadians(String radians) {
        Radians = radians;
    }
}
