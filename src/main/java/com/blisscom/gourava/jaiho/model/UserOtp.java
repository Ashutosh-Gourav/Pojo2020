package com.blisscom.gourava.jaiho.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by gourava on 12/18/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserOtp {

    private String otp;


    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}
