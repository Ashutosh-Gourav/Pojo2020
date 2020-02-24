package com.blisscom.gourava.jaiho.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.MediaController;
import android.widget.VideoView;

import com.blisscom.gourava.jaiho.R;
import com.blisscom.gourava.jaiho.activity.priest.PriestHomeActivity;
import com.blisscom.gourava.jaiho.activity.priest.PriestVerificationActivity;
import com.blisscom.gourava.jaiho.activity.user.UserHomeActivity;
import com.blisscom.gourava.jaiho.model.PojoPreferredStrings;

public class SplashActivity extends Activity {

    private static int SPLASH_TIME_OUT = 2000;
    private VideoView myVideoView, myVideoView1;
    private MediaController mediaControls;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        sharedPreferences = getSharedPreferences(getString(R.string.shared_preference_name), MODE_PRIVATE);
//        myVideoView = (VideoView) findViewById(R.id.video_view);
//        myVideoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.flag_ohm));
//        myVideoView1 = (VideoView) findViewById(R.id.video_view1);
//        myVideoView1.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.flag_ohm));
        Log.d("packagenameSpamananager","::::::"+getPackageName());


//        myVideoView.requestFocus();
//        //we also set an setOnPreparedListener in order to know when the video file is ready for playback
//        myVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//
//            int position = 0;
//            public void onPrepared(MediaPlayer mediaPlayer) {
//                // close the progress bar and play the video
////                progressDialog.dismiss();
//                //if we have a position on savedInstanceState, the video playback should start from here
//                myVideoView.seekTo(position);
//                if (position == 0) {
//                    myVideoView.start();
//                } else {
//                    //if we come from a resumed activity, video playback will be paused
//                    myVideoView.pause();
//                }
//            }
//        });
//        myVideoView1.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//
//            int position = 0;
//            public void onPrepared(MediaPlayer mediaPlayer) {
//                // close the progress bar and play the video
////                progressDialog.dismiss();
//                //if we have a position on savedInstanceState, the video playback should start from here
//                myVideoView1.seekTo(position);
//                if (position == 0) {
//                    myVideoView1.start();
//                } else {
//                    //if we come from a resumed activity, video playback will be paused
//                    myVideoView1.pause();
//                }
//            }
//        });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // TODO: 12/30/16 if user already registered/ login go directly to home page for user/priest
                if(isPersonLoggedIn()){
                    if(sharedPreferences.getBoolean(PojoPreferredStrings.POJO_IS_USER.toString(), false)){
                        Intent i = new Intent(getApplicationContext(), UserHomeActivity.class);
                        startActivity(i);
                    }else {
                        // TODO: 12/30/16 check if priest is verified, if yes go to home else show verification activity
                        if(!sharedPreferences.getBoolean(PojoPreferredStrings.IS_PRIEST_VERIFIED.toString(), false)) {
                            Intent i = new Intent(getApplicationContext(), PriestVerificationActivity.class);
                            startActivity(i);
                        }else {
                            Intent i = new Intent(getApplicationContext(), PriestHomeActivity.class);
                            startActivity(i);
                        }
                    }
                }else {
                    Intent i = new Intent(SplashActivity.this, WelcomeActivity.class);
                    startActivity(i);
                }
                finish();
            }
        }, SPLASH_TIME_OUT);
    }

    public boolean isPersonLoggedIn(){
        if((sharedPreferences.getString(PojoPreferredStrings.POJO_USER_PHONE_NUMBER.toString(), null) == null
                || sharedPreferences.getString(PojoPreferredStrings.POJO_USER_PHONE_NUMBER.toString(), null).isEmpty())
                &&(sharedPreferences.getString(PojoPreferredStrings.POJO_PRIEST_PHONE_NUMBER.toString(), null) == null
                || sharedPreferences.getString(PojoPreferredStrings.POJO_PRIEST_PHONE_NUMBER.toString(), null).isEmpty())){
            return false;
        }
        return true;
    }
}
