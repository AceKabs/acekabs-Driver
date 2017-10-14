package com.acekabs.driverapp.pojo;


import java.io.Serializable;

public class FcmRequestObject implements Serializable {

    private int requestId;
    private String status;
    private double surge_multiplier;
    private String pickUpLocation;
    private String dropLocation;
    private double pickUpLatitude;
    private double pickUpLongitude;
    private double dropLatitude;
    private double dropLongitude;
    private String EstimateDistance;
    private int timer;
    private String userMobileNumber;
    private String currentLatLngs;
    private String username;
    private String userEmailID;
    private String cancelDescription;
    private String fareid;

    public String getFareid() {
        return fareid;
    }

    public void setFareid(String fareid) {
        this.fareid = fareid;
    }

    public FcmRequestObject() {

    }

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getSurge_multiplier() {
        return surge_multiplier;
    }

    public void setSurge_multiplier(double surge_multiplier) {
        this.surge_multiplier = surge_multiplier;
    }

    public String getPickUpLocation() {
        return pickUpLocation;
    }

    public void setPickUpLocation(String pickUpLocation) {
        this.pickUpLocation = pickUpLocation;
    }

    public String getDropLocation() {
        return dropLocation;
    }

    public void setDropLocation(String dropLocation) {
        this.dropLocation = dropLocation;
    }

    public double getPickUpLatitude() {
        return pickUpLatitude;
    }

    public void setPickUpLatitude(double pickUpLatitude) {
        this.pickUpLatitude = pickUpLatitude;
    }

    public double getPickUpLongitude() {
        return pickUpLongitude;
    }

    public void setPickUpLongitude(double pickUpLongitude) {
        this.pickUpLongitude = pickUpLongitude;
    }

    public double getDropLatitude() {
        return dropLatitude;
    }

    public void setDropLatitude(double dropLatitude) {
        this.dropLatitude = dropLatitude;
    }

    public double getDropLongitude() {
        return dropLongitude;
    }

    public void setDropLongitude(double dropLongitude) {
        this.dropLongitude = dropLongitude;
    }

    public String getEstimateDistance() {
        return EstimateDistance;
    }

    public void setEstimateDistance(String estimateDistance) {
        EstimateDistance = estimateDistance;
    }

    public int getTimer() {
        return timer;
    }

    public void setTimer(int timer) {
        this.timer = timer;
    }

    public String getUserMobileNumber() {
        return userMobileNumber;
    }

    public void setUserMobileNumber(String userMobileNumber) {
        this.userMobileNumber = userMobileNumber;
    }

    public String getCurrentLatLngs() {
        return currentLatLngs;
    }

    public void setCurrentLatLngs(String currentLatLngs) {
        this.currentLatLngs = currentLatLngs;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserEmailID() {
        return userEmailID;
    }

    public void setUserEmailID(String userEmailID) {
        this.userEmailID = userEmailID;
    }

    public String getCancelDescription() {
        return cancelDescription;
    }

    public void setCancelDescription(String cancelDescription) {
        this.cancelDescription = cancelDescription;
    }


}
