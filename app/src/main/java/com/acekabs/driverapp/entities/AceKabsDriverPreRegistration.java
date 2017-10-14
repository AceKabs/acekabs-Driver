package com.acekabs.driverapp.entities;

import com.acekabs.driverapp.pojo.Images;


public class AceKabsDriverPreRegistration {

    private String emailID;
    private String password;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String country;
    private String branchId;

    private String carManufacturer;
    private String yearOfManufacture;
    private String carModel;
    private String licensePlateNumber;

    private String carRegistrationDocFrontURL;
    private String carRegistrationDocBackURL;
    private String vehicleInsuranceDocURL;
    private String driverPhotoDocURL;
    private String driverLicenseDocURL;
    private String driverIDCardDocBackURL;
    private String driverIDCardDocFrontURL;
    private String driverPassportDocBackURL;
    private String driverPassportDocFrontURL;
    private String plateNumberDocURL;
    private String vehiclePhotoDocURL;

    private boolean isActivated;

    private String compName;
    private String compAddr;
    private String compCity;
    private String pinCode;

    //New added
    private String carColor;
    private String noOfPass;
    private String driverDOB;
    private String iMEI;
    private String deviceInfo;
    private String latLong;

    private String regDate;
    private String metaDataMap;
    private String carType;
    private String currency;


    private Images images;

    public AceKabsDriverPreRegistration() {

    }

    public String getEmailID() {
        return emailID;
    }

    public void setEmailID(String emaildID) {
        this.emailID = emaildID;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getBranchId() {
        return branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }

    public String getCarManufacturer() {
        return carManufacturer;
    }

    public void setCarManufacturer(String carManufacturer) {
        this.carManufacturer = carManufacturer;
    }

    public String getYearOfManufacture() {
        return yearOfManufacture;
    }

    public void setYearOfManufacture(String yearOfManufacture) {
        this.yearOfManufacture = yearOfManufacture;
    }

    public String getCarModel() {
        return carModel;
    }

    public void setCarModel(String carModel) {
        this.carModel = carModel;
    }

    public String getLicensePlateNumber() {
        return licensePlateNumber;
    }

    public void setLicensePlateNumber(String licensePlateNumber) {
        this.licensePlateNumber = licensePlateNumber;
    }

    public String getCarRegistrationDocFrontURL() {
        return carRegistrationDocFrontURL;
    }

    public void setCarRegistrationDocFrontURL(String carRegistrationDocFrontURL) {
        this.carRegistrationDocFrontURL = carRegistrationDocFrontURL;
    }

    public String getCarRegistrationDocBackURL() {
        return carRegistrationDocBackURL;
    }

    public void setCarRegistrationDocBackURL(String carRegistrationDocBackURL) {
        this.carRegistrationDocBackURL = carRegistrationDocBackURL;
    }

    public String getVehicleInsuranceDocURL() {
        return vehicleInsuranceDocURL;
    }

    public void setVehicleInsuranceDocURL(String vehicleInsuranceDocURL) {
        this.vehicleInsuranceDocURL = vehicleInsuranceDocURL;
    }

    public String getDriverPhotoDocURL() {
        return driverPhotoDocURL;
    }

    public void setDriverPhotoDocURL(String driverPhotoDocURL) {
        this.driverPhotoDocURL = driverPhotoDocURL;
    }

    public String getDriverLicenseDocURL() {
        return driverLicenseDocURL;
    }

    public void setDriverLicenseDocURL(String driverLicenseDocURL) {
        this.driverLicenseDocURL = driverLicenseDocURL;
    }

    public String getDriverIDCardDocBackURL() {
        return driverIDCardDocBackURL;
    }

    public void setDriverIDCardDocBackURL(String driverIDCardDocBackURL) {
        this.driverIDCardDocBackURL = driverIDCardDocBackURL;
    }

    public String getDriverIDCardDocFrontURL() {
        return driverIDCardDocFrontURL;
    }

    public void setDriverIDCardDocFrontURL(String driverIDCardDocFrontURL) {
        this.driverIDCardDocFrontURL = driverIDCardDocFrontURL;
    }

    public String getMetaDataMap() {
        return metaDataMap;
    }

    public void setMetaDataMap(String metaDataMap) {
        this.metaDataMap = metaDataMap;
    }

    public String getDriverPassportDocBackURL() {
        return driverPassportDocBackURL;
    }

    public void setDriverPassportDocBackURL(String driverPassportDocBackURL) {
        this.driverPassportDocBackURL = driverPassportDocBackURL;
    }

    public String getDriverPassportDocFrontURL() {
        return driverPassportDocFrontURL;
    }

    public void setDriverPassportDocFrontURL(String driverPassportDocFrontURL) {
        this.driverPassportDocFrontURL = driverPassportDocFrontURL;
    }

    public String getPlateNumberDocURL() {
        return plateNumberDocURL;
    }

    public void setPlateNumberDocURL(String plateNumberDocURL) {
        this.plateNumberDocURL = plateNumberDocURL;
    }

    public String getVehiclePhotoDocURL() {
        return vehiclePhotoDocURL;
    }

    public void setVehiclePhotoDocURL(String vehiclePhotoDocURL) {
        this.vehiclePhotoDocURL = vehiclePhotoDocURL;
    }

    public String getCarType() {
        return  carType;
    }

    public void setCarType(String carType) {
        this.carType = carType;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public boolean isActivated() {
        return isActivated;
    }

    public void setActivated(boolean activated) {
        isActivated = activated;
    }

    public String getCompName() {
        return compName;
    }

    public void setCompName(String compName) {
        this.compName = compName;
    }

    public String getCompAddr() {
        return compAddr;
    }

    public void setCompAddr(String compAddr) {
        this.compAddr = compAddr;
    }

    public String getCompCity() {
        return compCity;
    }

    public void setCompCity(String compCity) {
        this.compCity = compCity;
    }

    public String getPinCode() {
        return pinCode;
    }

    public void setPinCode(String pinCode) {
        this.pinCode = pinCode;
    }

	/*public ConcurrentHashMap<String, ImageMetaData> getMetaDataMap() {
        return metaDataMap;
	}

	public void setMetaDataMap(ConcurrentHashMap<String, ImageMetaData> metaDataMap) {
		this.metaDataMap = metaDataMap;
	}*/

	/*
	 * public MetaData getMetaData() { return metaData; }
	 *
	 * public void setMetaData(MetaData metaData) { this.metaData = metaData; }
	 */

	/*
	 * public Map<String,MetaData> getMetaData() { return metaData; }
	 *
	 * public void setMetaData(Map<String,MetaData> metaData) { this.metaData =
	 * metaData; }
	 */

    public Images getImages() {
        return images;
    }

    public void setImages(Images images) {
        this.images = images;
    }

    public String getCarColor() {
        return carColor;
    }

    public void setCarColor(String carColor) {
        this.carColor = carColor;
    }

    public String getNoOfPass() {
        return noOfPass;
    }

    public void setNoOfPass(String noOfPass) {
        this.noOfPass = noOfPass;
    }

    public String getDriverDOB() {
        return driverDOB;
    }

    public void setDriverDOB(String driverDOB) {
        this.driverDOB = driverDOB;
    }

    public String getiMEI() {
        return iMEI;
    }

    public void setiMEI(String iMEI) {
        this.iMEI = iMEI;
    }

    public String getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(String deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public String getLatLong() {
        return latLong;
    }

    public void setLatLong(String latLong) {
        this.latLong = latLong;
    }

    public String getRegDate() {
        return regDate;
    }

    public void setRegDate(String regDate) {
        this.regDate = regDate;
    }

}
