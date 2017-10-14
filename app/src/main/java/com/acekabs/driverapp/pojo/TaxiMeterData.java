package com.acekabs.driverapp.pojo;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by ankit.jain on 5/5/2017.
 */

public class TaxiMeterData {
    private String meterId;
    private String mobileNumber;
    private String startTime;
    private String endTime;
    private double baseFare;
    private double costPerMinute;
    private double costPerKM;
    private double totalDistance;
    private double TotalAmount;
    private ArrayList<RouteDetails> routes;
    private boolean isTripDone;
    private String mobileNumber_isTripDone;
    private double lastLat;
    private double lastLng;

    public double getLastLat() {
        return lastLat;
    }

    public void setLastLat(double lastLat) {
        this.lastLat = lastLat;
    }

    public double getLastLng() {
        return lastLng;
    }

    public void setLastLng(double lastLng) {
        this.lastLng = lastLng;
    }

    public TaxiMeterData() {

    }

    public String getMeterId() {
        return meterId;
    }

    public void setMeterId(String meterId) {
        this.meterId = meterId;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public double getBaseFare() {
        return baseFare;
    }

    public void setBaseFare(double baseFare) {
        this.baseFare = baseFare;
    }

    public double getCostPerMinute() {
        return costPerMinute;
    }

    public void setCostPerMinute(double costPerMinute) {
        this.costPerMinute = costPerMinute;
    }

    public double getCostPerKM() {
        return costPerKM;
    }

    public void setCostPerKM(double costPerKM) {
        this.costPerKM = costPerKM;
    }

    public double getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(double totalDistance) {
        this.totalDistance = totalDistance;
    }

    public double getTotalAmount() {
        return TotalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        TotalAmount = totalAmount;
    }

    public ArrayList<RouteDetails> getRoutes() {
        return routes;
    }

    public void setRoutes(ArrayList<RouteDetails> routes) {
        this.routes = routes;
    }

    public boolean isTripDone() {
        return isTripDone;
    }

    public void setTripDone(boolean tripDone) {
        isTripDone = tripDone;
    }

    public String getMobileNumber_isTripDone() {
        return mobileNumber_isTripDone;
    }

    public void setMobileNumber_isTripDone(String mobileNumber_isTripDone) {
        this.mobileNumber_isTripDone = mobileNumber_isTripDone;
    }
}
