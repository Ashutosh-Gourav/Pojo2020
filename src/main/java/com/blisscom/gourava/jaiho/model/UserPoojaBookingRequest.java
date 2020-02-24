package com.blisscom.gourava.jaiho.model;

import java.io.Serializable;

/**
 * Created by gourava on 1/8/17.
 */

public class UserPoojaBookingRequest implements Serializable {

    private String poojaBookingRequestId;
    private String userId;
    private String poojaAddress;
    private int poojaId;
    private String bookingRequestTime;
    private String poojaStartTime;
    private String poojaLanguage;
    private String prepBy;
    private float poojaAddressLongitude;
    private float poojaAddressLatitude;
    private String poojaBookingStatus;
    private int offeredPrice;

    public UserPoojaBookingRequest(String poojaBookingRequestId, String userId, String poojaAddress, int poojaId, String bookingRequestTime, String poojaStartTime, String poojaLanguage, String prepBy, float poojaAddressLongitude, float poojaAddressLatitude, String poojaBookingStatus, int offeredPrice) {
        this.poojaBookingRequestId = poojaBookingRequestId;
        this.userId = userId;
        this.poojaAddress = poojaAddress;
        this.poojaId = poojaId;
        this.bookingRequestTime = bookingRequestTime;
        this.poojaStartTime = poojaStartTime;
        this.poojaLanguage = poojaLanguage;
        this.prepBy = prepBy;
        this.poojaAddressLongitude = poojaAddressLongitude;
        this.poojaAddressLatitude = poojaAddressLatitude;
        this.poojaBookingStatus = poojaBookingStatus;
        this.offeredPrice = offeredPrice;
    }

    public UserPoojaBookingRequest() {
    }

    public String getPoojaBookingRequestId() {
        return poojaBookingRequestId;
    }

    public void setPoojaBookingRequestId(String poojaBookingRequestId) {
        this.poojaBookingRequestId = poojaBookingRequestId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPoojaAddress() {
        return poojaAddress;
    }

    public void setPoojaAddress(String poojaAddress) {
        this.poojaAddress = poojaAddress;
    }

    public int getPoojaId() {
        return poojaId;
    }

    public void setPoojaId(int poojaId) {
        this.poojaId = poojaId;
    }

    public String getBookingRequestTime() {
        return bookingRequestTime;
    }

    public void setBookingRequestTime(String bookingRequestTime) {
        this.bookingRequestTime = bookingRequestTime;
    }

    public String getPoojaStartTime() {
        return poojaStartTime;
    }

    public void setPoojaStartTime(String poojaStartTime) {
        this.poojaStartTime = poojaStartTime;
    }

    public String getPoojaLanguage() {
        return poojaLanguage;
    }

    public void setPoojaLanguage(String poojaLanguage) {
        this.poojaLanguage = poojaLanguage;
    }

    public String getPrepBy() {
        return prepBy;
    }

    public void setPrepBy(String prepBy) {
        this.prepBy = prepBy;
    }

    public float getPoojaAddressLongitude() {
        return poojaAddressLongitude;
    }

    public void setPoojaAddressLongitude(float poojaAddressLongitude) {
        this.poojaAddressLongitude = poojaAddressLongitude;
    }

    public float getPoojaAddressLatitude() {
        return poojaAddressLatitude;
    }

    public void setPoojaAddressLatitude(float poojaAddressLatitude) {
        this.poojaAddressLatitude = poojaAddressLatitude;
    }

    public String getPoojaBookingStatus() {
        return poojaBookingStatus;
    }

    public void setPoojaBookingStatus(String poojaBookingStatus) {
        this.poojaBookingStatus = poojaBookingStatus;
    }

    public int getOfferedPrice() {
        return offeredPrice;
    }

    public void setOfferedPrice(int offeredPrice) {
        this.offeredPrice = offeredPrice;
    }
}
