package com.blisscom.gourava.jaiho.model;

/**
 * Created by gourava on 12/16/16.
 */

public enum PojoPreferredStrings {

    POJO_USER_PHONE_NUMBER("POJO_USER_PHONE_NUMBER"),
    POJO_USER_PASSWORD("POJO_USER_PASSWORD"),
    POJO_PRIEST_PHONE_NUMBER("POJO_PRIEST_PHONE_NUMBER"),
    POJO_PRIEST_PASSWORD("POJO_PRIEST_PASSWORD"),
    POJO_IS_USER("isUser"),
    IS_USER_REGISTRATION_OTP_CONFIRMED("isUserOtpConfirmedCorrect"),
    IS_PRIEST_REGISTRATION_OTP_CONFIRMED("isUserOtpConfirmedCorrect"),
    IS_DO_NOT_SHOW_GPS_REQUEST_CHECKED("isPressedDoNotShowGpsRequest"),
    LAST_FULL_ADDRESS_TAKEN_FROM_MAP("lastFullAddressReadFromMap"),
    LAST_LATTITUDE_TAKEN_FROM_MAP("lastAddressLattitudeTakenFromMap"),
    LAST_LONGITUDE_TAKEN_FROM_MAP("lastAddressLongitudeTakenFromMap"),
    IS_DRIVEN_TO_HISTORY("hasUserMadeNewBooking"),
    IS_DO_NOT_SHOW_DEVICE_ID_REQUEST_CHECKED("isPressedDoNotShowGpsRequest"),
    DEVICE_ID("deviceId"),
    POJO_USER_EMAIL("userEmail"),
    POJO_PRIEST_EMAIL("priestEmail"),
    SAVED_FCM_TOKEN("savedFcmToken"),
    IS_PRIEST_VERIFIED("isPriestVerified");


    private final String text;

    private PojoPreferredStrings(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
