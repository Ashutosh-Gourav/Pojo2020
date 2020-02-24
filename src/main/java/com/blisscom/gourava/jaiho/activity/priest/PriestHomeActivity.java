package com.blisscom.gourava.jaiho.activity.priest;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.blisscom.gourava.jaiho.R;
import com.blisscom.gourava.jaiho.activity.NewPoojaRequestAlarmActivity;
import com.blisscom.gourava.jaiho.infra.PojoBroadcastReceiver;

public class PriestHomeActivity extends FragmentActivity {
    public static final String POJO_ALARM_NEW_BOOKING_RECEIVED_ACTION = "newBookingReceived";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_priest_home);


        Intent intent = new Intent(POJO_ALARM_NEW_BOOKING_RECEIVED_ACTION);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this, 12121212, intent, 0);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), (30 * 1000), pendingIntent);
        Toast.makeText(this, "Alarm set in " + 30 + " seconds",Toast.LENGTH_LONG).show();

//        try {
//
//            //Create a new PendingIntent and add it to the AlarmManager
//            Intent intent = new Intent(this, NewPoojaRequestAlarmActivity.class);
//            PendingIntent pendingIntent = PendingIntent.getActivity(this,
//                    12345, intent, PendingIntent.FLAG_CANCEL_CURRENT);
//            AlarmManager am =
//                    (AlarmManager)getSystemService(Activity.ALARM_SERVICE);
//            am.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(),
//                    2*60*60,pendingIntent);
//
//        } catch (Exception e) {}
    }
}
