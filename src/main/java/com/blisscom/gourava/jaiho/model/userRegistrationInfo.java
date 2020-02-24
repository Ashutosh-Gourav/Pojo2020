package com.blisscom.gourava.jaiho.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by gourava on 12/18/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class userRegistrationInfo {

    private String email;
    private String password;
    private String mobileNumber;
    private String fullname;
    private String address;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    //    {"email":"vidhivirmani1322@gmail.com",
//            "password":"password",
//            "mobileNumber":"919108679358",
//            "fullname":"vidhi virmani"
//    }
}
