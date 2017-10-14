package com.acekabs.driverapp.entities;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AceKabsDriver {

    private String driverId;
    private String driverName;
    private String driverFullName;
    private String driverPhotoURL;
    private String driverRatting;
    private String driverstatus;
    private String emailId;
    private String latLocation;
    private String longLocation;
    private boolean onDuty;
    private boolean blocked;
    private double baseFare;
    private double costPerKM;
    private double costPerMinute;
    private double surgeRate;
    private String carRegistrationNumber;
    private String carType;
    private String carModelName;
    private String mobileNumber;
    private String isDriverFree;
    private String currency;

    public AceKabsDriver() {

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

    public double getBaseFare() {
        return baseFare;
    }

    public void setBaseFare(double baseFare) {
        this.baseFare = baseFare;
    }

    public double getCostPerKM() {
        return costPerKM;
    }

    public void setCostPerKM(double costPerKM) {
        this.costPerKM = costPerKM;
    }

    public double getCostPerMinute() {
        return costPerMinute;
    }

    public void setCostPerMinute(double costPerMinute) {
        this.costPerMinute = costPerMinute;
    }

    public double getSurgeRate() {
        return surgeRate;
    }

    public void setSurgeRate(double surgeRate) {
        this.surgeRate = surgeRate;
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


    public String getDriverstatus() {
        return driverstatus;
    }

    public void setDriverstatus(String driverstatus) {
        this.driverstatus = driverstatus;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getCarRegistrationNumber() {
        return carRegistrationNumber;
    }

    public void setCarRegistrationNumber(String carRegistrationNumber) {
        this.carRegistrationNumber = carRegistrationNumber;
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

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getDriverRatting() {
        return driverRatting;
    }

    public void setDriverRatting(String driverRatting) {
        this.driverRatting = driverRatting;
    }

    public String getIsDriverFree() {
        return isDriverFree;
    }

    public void setIsDriverFree(String isDriverFree) {
        this.isDriverFree = isDriverFree;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @Override
    public String toString() {
        return "AceKabsDriver{" +
                "driverId='" + driverId + '\'' +
                ", driverName='" + driverName + '\'' +
                ", driverstatus='" + driverstatus + '\'' +
                ", baseFare=" + baseFare +
                ", costPerKM=" + costPerKM +
                ", costPerMinute=" + costPerMinute +
                ", currency='" + currency + '\'' +
                '}';
    }
}
