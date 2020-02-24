package com.blisscom.gourava.jaiho.infra;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.widget.Toast;

import com.blisscom.gourava.jaiho.R;
import com.blisscom.gourava.jaiho.activity.NewPoojaRequestAlarmActivity;
import com.blisscom.gourava.jaiho.model.LoginAsyncCall;

/**
 * Created by gourava on 1/17/17.
 */

public class PojoBroadcastReceiver extends BroadcastReceiver {
    public static final String POJO_ALARM_NEW_BOOKING_RECEIVED_ACTION = "newBookingReceived";

    MediaPlayer mp;
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equalsIgnoreCase(POJO_ALARM_NEW_BOOKING_RECEIVED_ACTION)){
            LoginAsyncCall loginAsyncCall = new LoginAsyncCall(context);
            Object[] params = {"1234567890", "pass", false};
            loginAsyncCall.execute(params);
//            Intent gotoDecide = new Intent(context.getApplicationContext(), NewPoojaRequestAlarmActivity.class);
//            gotoDecide.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(gotoDecide);
//            mp= MediaPlayer.create(context, R.raw.pojo_templebell_alarm);
//            mp.start();
//            Toast.makeText(context, "Alarm....", Toast.LENGTH_LONG).show();
        }

    }
}