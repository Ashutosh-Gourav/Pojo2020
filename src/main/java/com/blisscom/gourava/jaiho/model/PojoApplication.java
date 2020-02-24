package com.blisscom.gourava.jaiho.model;

import android.app.Application;

/**
 * Created by gourava on 12/31/16.
 */

public class PojoApplication extends Application {

    private static PojoApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public static synchronized PojoApplication getInstance() {
        return mInstance;
    }

    public void setConnectivityListener(InternetConnectivityReceiver.ConnectivityReceiverListener listener) {
        InternetConnectivityReceiver.connectivityReceiverListener = listener;
    }
}
