package com.acekabs.driverapp.pojo;

import java.util.ArrayList;

/**
 * Created by Adee09 on 4/14/2017.
 */

public class FareDetails {
        private String fareid;
        private String appversion;
        private String driverid;
        private String drivername;
        private String driveremailid;
        private String drivermobile;
        private String driverphoto;

        private String passengerid;
        private String passengername;
        private String passengeremailid;
        private String passengermobile;

        private String cabtype;
        private String cabnumber;
        private String cabmodel;

        private String baseFare;
        private String costPerKM;
        private String costPerMinute;
        private String surgeRate;

        //when trip start[driver click on start]
        private String starttime;

        //from driver accept the ride and end of ride saving status with time & latlngs
        private ArrayList<StatusChangedData> statuschanged;
        //when trip start each latlngs with duration b/w points time & time
        private ArrayList<RouteDetails> routes;
        //total trip duration time
        private double totalduration;

        //total waiting time; if taxi stop between trip increase the waiting
        private double totalwaittime;

        //trip end time
        private String endtime;
        //trip is pending or complete
        private boolean isTripDone;
        //trip cancel reason
        private String cancelDescription;

        /*New Fields*/
        private String pickUpLatLngs;
        private String dropLatLngs;
        private String pickUpLocation;
        private String dropLocation;
        private String currentLatLngs;


    public String getAppversion() {
        return appversion;
    }

    public void setAppversion(String appversion) {
        this.appversion = appversion;
    }

    public String getDriverid() {
        return driverid;
    }

    public void setDriverid(String driverid) {
        this.driverid = driverid;
    }

    public String getDrivername() {
        return drivername;
    }

    public void setDrivername(String drivername) {
        this.drivername = drivername;
    }

    public String getDriveremailid() {
        return driveremailid;
    }

    public void setDriveremailid(String driveremailid) {
        this.driveremailid = driveremailid;
    }

    public String getDrivermobile() {
        return drivermobile;
    }

    public void setDrivermobile(String drivermobile) {
        this.drivermobile = drivermobile;
    }

    public String getDriverphoto() {
        return driverphoto;
    }

    public void setDriverphoto(String driverphoto) {
        this.driverphoto = driverphoto;
    }

    public String getPassengerid() {
        return passengerid;
    }

    public void setPassengerid(String passengerid) {
        this.passengerid = passengerid;
    }

    public String getPassengername() {
        return passengername;
    }

    public void setPassengername(String passengername) {
        this.passengername = passengername;
    }

    public String getPassengeremailid() {
        return passengeremailid;
    }

    public void setPassengeremailid(String passengeremailid) {
        this.passengeremailid = passengeremailid;
    }

    public String getPassengermobile() {
        return passengermobile;
    }

    public void setPassengermobile(String passengermobile) {
        this.passengermobile = passengermobile;
    }

    public String getCabtype() {
        return cabtype;
    }

    public void setCabtype(String cabtype) {
        this.cabtype = cabtype;
    }

    public String getCabnumber() {
        return cabnumber;
    }

    public void setCabnumber(String cabnumber) {
        this.cabnumber = cabnumber;
    }

    public String getCabmodel() {
        return cabmodel;
    }

    public void setCabmodel(String cabmodel) {
        this.cabmodel = cabmodel;
    }

    public String getBaseFare() {
        return baseFare;
    }

    public void setBaseFare(String baseFare) {
        this.baseFare = baseFare;
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

    public String getSurgeRate() {
        return surgeRate;
    }

    public void setSurgeRate(String surgeRate) {
        this.surgeRate = surgeRate;
    }

    public String getStarttime() {
        return starttime;
    }

    public void setStarttime(String starttime) {
        this.starttime = starttime;
    }

    public ArrayList<StatusChangedData> getStatuschanged() {
        return statuschanged;
    }

    public void setStatuschanged(ArrayList<StatusChangedData> statuschanged) {
        this.statuschanged = statuschanged;
    }

    public ArrayList<RouteDetails> getRoutes() {
        return routes;
    }

    public void setRoutes(ArrayList<RouteDetails> routes) {
        this.routes = routes;
    }

    public double getTotalduration() {
        return totalduration;
    }

    public void setTotalduration(double totalduration) {
        this.totalduration = totalduration;
    }

    public double getTotalwaittime() {
        return totalwaittime;
    }

    public void setTotalwaittime(double totalwaittime) {
        this.totalwaittime = totalwaittime;
    }

    public String getEndtime() {
        return endtime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }

    public String getFareid() {
        return fareid;
    }

    public void setFareid(String fareid) {
        this.fareid = fareid;
    }

    public boolean isTripDone() {
        return isTripDone;
    }

    public void setTripDone(boolean tripDone) {
        isTripDone = tripDone;
    }

    public String getCancelDescription() {
        return cancelDescription;
    }

    public void setCancelDescription(String cancelDescription) {
        this.cancelDescription = cancelDescription;
    }

    public String getPickUpLatLngs() {
        return pickUpLatLngs;
    }

    public void setPickUpLatLngs(String pickUpLatLngs) {
        this.pickUpLatLngs = pickUpLatLngs;
    }

    public String getDropLatLngs() {
        return dropLatLngs;
    }

    public void setDropLatLngs(String dropLatLngs) {
        this.dropLatLngs = dropLatLngs;
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

    public String getCurrentLatLngs() {
        return currentLatLngs;
    }

    public void setCurrentLatLngs(String currentLatLngs) {
        this.currentLatLngs = currentLatLngs;
    }
}
