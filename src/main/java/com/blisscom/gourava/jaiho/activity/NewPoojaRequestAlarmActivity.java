package com.blisscom.gourava.jaiho.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.blisscom.gourava.jaiho.R;
import com.blisscom.gourava.jaiho.model.LoginAsyncCall;

public class NewPoojaRequestAlarmActivity extends Activity {

    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_pooja_request_alarm);
        TextView aaceptRequestTV = (TextView) findViewById(R.id.accept_reqest_tv);
        TextView rejectRequestTV = (TextView) findViewById(R.id.reject_reqest_tv);


        mediaPlayer = MediaPlayer.create(getBaseContext(),R.raw.pojo_templebell_alarm);


        aaceptRequestTV.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                // TODO Auto-generated method stub
                mediaPlayer.stop();
                LoginAsyncCall loginAsyncCall = new LoginAsyncCall(NewPoojaRequestAlarmActivity.this);
                Object[] params = {"ab","cd", false, false};
                loginAsyncCall.execute(params);
                return false;
            }
        });
        rejectRequestTV.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                // TODO Auto-generated method stub
                mediaPlayer.stop();
                finish();
                return false;
            }
        });

        playSound(this, getAlarmUri());
    }

    private void playSound(final Context context, Uri alert) {


        Thread background = new Thread(new Runnable() {
            public void run() {
                try {

                    mediaPlayer.start();

                } catch (Throwable t) {
                    Log.i("Animation", "Thread  exception "+t);
                }
            }
        });
        background.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
    }
    //Get an alarm sound. Try for an alarm. If none set, try notification,
    //Otherwise, ringtone.
    private Uri getAlarmUri() {

        Uri alert = RingtoneManager
                .getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alert == null) {
            alert = RingtoneManager
                    .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            if (alert == null) {
                alert = RingtoneManager
                        .getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            }
        }
        return alert;
    }

}
