package com.acekabs.driverapp.pojo;

public class DriverSyncObject {
    private String driverId;
    private String driverName;
    private String latLocation;
    private String longLocation;
    private boolean onDuty;
    private boolean blocked;
    //here adding some extra fields for drivers duty
    private String driverFullName;
    private String driverPhotoURL;
    private String mobileNumber;
    private String emailId;
    private String carType;
    private String carModelName;
    private String carRegistrationNumber;
    private String costPerKM;
    private String costPerMinute;
    private String baseFare;
    private String surgeRate;
    private String driverRatting;
    private String driverstatus;

    public DriverSyncObject() {

    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getLatLocation() {
        return latLocation;
    }

    public void setLatLocation(String latLocation) {
        this.latLocation = latLocation;
    }

    public String getLongLocation() {
        return longLocation;
    }

    public void setLongLocation(String longLocation) {
        this.longLocation = longLocation;
    }

    public boolean isOnDuty() {
        return onDuty;
    }

    public void setOnDuty(boolean onDuty) {
        this.onDuty = onDuty;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public String getDriverFullName() {
        return driverFullName;
    }

    public void setDriverFullName(String driverFullName) {
        this.driverFullName = driverFullName;
    }

    public String getDriverPhotoURL() {
        return driverPhotoURL;
    }

    public void setDriverPhotoURL(String driverPhotoURL) {
        this.driverPhotoURL = driverPhotoURL;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getCarType() {
        return carType;
    }

    public void setCarType(String carType) {
        this.carType = carType;
    }

    public String getCarModelName() {
        return carModelName;
    }

    public void setCarModelName(String carModelName) {
        this.carModelName = carModelName;
    }

    public String getCarRegistrationNumber() {
        return carRegistrationNumber;
    }

    public void setCarRegistrationNumber(String carRegistrationNumber) {
        this.carRegistrationNumber = carRegistrationNumber;
    }

    public String getCostPerKM() {
        return costPerKM;
    }

    public void setCostPerKM(String costPerKM) {
        this.costPerKM = costPerKM;
    }

    public String getCostPerMinute() {
        return costPerMinute;
    }

    public void setCostPerMinute(String costPerMinute) {
        this.costPerMinute = costPerMinute;
    }

    public String getBaseFare() {
        return baseFare;
    }

    public void setBaseFare(String baseFare) {
        this.baseFare = baseFare;
    }

    public String getSurgeRate() {
        return surgeRate;
    }

    public void setSurgeRate(String surgeRate) {
        this.surgeRate = surgeRate;
    }

    public String getDriverRatting() {
        return driverRatting;
    }

    public void setDriverRatting(String driverRatting) {
        this.driverRatting = driverRatting;
    }

    public String getDriverstatus() {
        return driverstatus;
    }

    public void setDriverstatus(String driverstatus) {
        this.driverstatus = driverstatus;
    }
}
