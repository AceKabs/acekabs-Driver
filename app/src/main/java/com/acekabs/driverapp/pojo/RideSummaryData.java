package com.acekabs.driverapp.pojo;

/**
 * Created by Adee09 on 4/23/2017.
 */

public class RideSummaryData {
    private int invoiceid;
    private String fareid;
    private double basefare;
    private double distancefare;
    private double durationfare;
    private double totalfare;
    private double discount;
    private double otheres;
    private String drivername;
    private String driverimage;
    private String driveremailid;
    private String driverid;
    private String ridetime;

    public RideSummaryData() {

    }

    public int getInvoiceid() {
        return invoiceid;
    }

    public void setInvoiceid(int invoiceid) {
        this.invoiceid = invoiceid;
    }

    public String getFareid() {
        return fareid;
    }

    public void setFareid(String fareid) {
        this.fareid = fareid;
    }

    public double getBasefare() {
        return basefare;
    }

    public void setBasefare(double basefare) {
        this.basefare = basefare;
    }

    public double getDistancefare() {
        return distancefare;
    }

    public void setDistancefare(double distancefare) {
        this.distancefare = distancefare;
    }

    public double getDurationfare() {
        return durationfare;
    }

    public void setDurationfare(double durationfare) {
        this.durationfare = durationfare;
    }

    public double getTotalfare() {
        return totalfare;
    }

    public void setTotalfare(double totalfare) {
        this.totalfare = totalfare;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public double getOtheres() {
        return otheres;
    }

    public void setOtheres(double otheres) {
        this.otheres = otheres;
    }

    public String getDrivername() {
        return drivername;
    }

    public void setDrivername(String drivername) {
        this.drivername = drivername;
    }

    public String getDriverimage() {
        return driverimage;
    }

    public void setDriverimage(String driverimage) {
        this.driverimage = driverimage;
    }

    public String getDriveremailid() {
        return driveremailid;
    }

    public void setDriveremailid(String driveremailid) {
        this.driveremailid = driveremailid;
    }

    public String getDriverid() {
        return driverid;
    }

    public void setDriverid(String driverid) {
        this.driverid = driverid;
    }

    public String getRidetime() {
        return ridetime;
    }

    public void setRidetime(String ridetime) {
        this.ridetime = ridetime;
    }
}
