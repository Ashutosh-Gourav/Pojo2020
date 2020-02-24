package com.blisscom.gourava.jaiho.activity;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blisscom.gourava.jaiho.activity.priest.PriestHomeActivity;
import com.blisscom.gourava.jaiho.activity.user.LoginActivity;
import com.blisscom.gourava.jaiho.activity.user.UserDirectRegistrationActivity;
import com.blisscom.gourava.jaiho.activity.user.UserHomeActivity;
import com.blisscom.gourava.jaiho.fragment.LoginFormFragment;
import com.blisscom.gourava.jaiho.fragment.WelcomeSlide1Fragment;
import com.blisscom.gourava.jaiho.fragment.WelcomeSlide2Fragment;
import com.blisscom.gourava.jaiho.fragment.WelcomeSlide3Fragment;
import com.blisscom.gourava.jaiho.fragment.WelcomeSlide4Fragment;
import com.blisscom.gourava.jaiho.fragment.priest.PriestRegistrationFragment;
import com.blisscom.gourava.jaiho.model.PojoPreferredStrings;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;

import com.blisscom.gourava.jaiho.BuildConfig;
import com.blisscom.gourava.jaiho.R;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class WelcomeActivity extends FragmentActivity implements View.OnClickListener{

    private int screenWidth, screenHeight;
    private DisplayMetrics displaymetrics;
    private TextView userLoginTV, createUserAccountTV, fbLoginTV, googleLoginTV, noAccountTV, priestLoginTV, priestCreateAccountTV, testPriestTV;
    private CallbackManager callbackManager;
    private LoginButton fb_loginButton;
    private ImageView priestRadioIV, userRadioIV;
    private LinearLayout priestLL, userLL;
    private Animation enteryAnimation, exitAnimation;
    private SharedPreferences sharedPreferences;
    private ViewPager slidePicPager;
    private MyPageAdapter myPageAdapter;
    private List<ImageView> dots;
    private int numberOfImages = 4;
    private Timer timer;
    private int nextPage = 1, prevPage = numberOfImages-2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences(getString(R.string.shared_preference_name), MODE_PRIVATE);
        if ((sharedPreferences.getString(PojoPreferredStrings.POJO_USER_PHONE_NUMBER.toString(), null) == null
                    || sharedPreferences.getString(PojoPreferredStrings.POJO_USER_PHONE_NUMBER.toString(), null).isEmpty())
            &&(sharedPreferences.getString(PojoPreferredStrings.POJO_PRIEST_PHONE_NUMBER.toString(), null) == null
                || sharedPreferences.getString(PojoPreferredStrings.POJO_PRIEST_PHONE_NUMBER.toString(), null).isEmpty())) {

            //        getKeyHashForFbAccess();
            FacebookSdk.sdkInitialize(getApplicationContext());
            callbackManager = CallbackManager.Factory.create();

//        displaymetrics = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
//        screenHeight = displaymetrics.heightPixels;
//        screenWidth = displaymetrics.widthPixels;
//        LinearLayout.LayoutParams editTextLayoutParams = new LinearLayout.LayoutParams(screenWidth/2, screenHeight/2);
            Log.d("packagename buildconfig", "::::::" + BuildConfig.APPLICATION_ID);
            Log.d("packagename pamananager", "::::::" + getPackageName());
            Log.d("packagename buildconfig", "::::::" + BuildConfig.APPLICATION_ID);

            showCustomizedActionBar();
            setContentView(R.layout.activity_welcome);

            userLoginTV = (TextView) findViewById(R.id.user_login_tv);
            userLoginTV.setOnClickListener(this);
            createUserAccountTV = (TextView) findViewById(R.id.create_user_account_tv);
            createUserAccountTV.setOnClickListener(this);
            priestLoginTV = (TextView) findViewById(R.id.priest_login_tv);
            priestLoginTV.setOnClickListener(this);
            priestCreateAccountTV = (TextView) findViewById(R.id.priest_create_account_tv);
            priestCreateAccountTV.setOnClickListener(this);
            fb_loginButton = (LoginButton) findViewById(R.id.login_button);
//        fbLoginTV.setOnClickListener(this);
            fb_loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {

                    Log.d("onSuccess", "User ID: "
                            + loginResult.getAccessToken().getUserId()
                            + "\n" +
                            "Auth Token: "
                            + loginResult.getAccessToken().getToken());
                }

                @Override
                public void onCancel() {
                    Log.d("packagename buildconfig", "::::::" + BuildConfig.APPLICATION_ID);

                }

                @Override
                public void onError(FacebookException e) {
                    Log.d("packagename buildconfig", "::::::" + BuildConfig.APPLICATION_ID);

                }
            });
            noAccountTV = (TextView) findViewById(R.id.no_account_tv);
            noAccountTV.setOnClickListener(this);

            enteryAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.coming_in_rtl_animation);
            exitAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.going_out_rtl_animation);
            priestLL = (LinearLayout) findViewById(R.id.priest_entry_ll);
            userLL = (LinearLayout) findViewById(R.id.user_entry_ll);
            priestRadioIV = (ImageView) findViewById(R.id.priest_radio_iv);
            priestRadioIV.setOnClickListener(this);
            userRadioIV = (ImageView) findViewById(R.id.user_radio_iv);
            userRadioIV.setOnClickListener(this);
            testPriestTV = (TextView)findViewById(R.id.priest_test_account_tv);
            testPriestTV.setOnClickListener(this);

            List<Fragment> fragments = getFragments();
            slidePicPager = (ViewPager)findViewById(R.id.welcome_slide_viewPager);
            myPageAdapter = new MyPageAdapter(getSupportFragmentManager(), fragments);
            slidePicPager.setOffscreenPageLimit(numberOfImages);
            slidePicPager.setAdapter(myPageAdapter);
            addDots();
            selectDot(0);
            pageSwitcher(5);
//            slidePicPager.beginFakeDrag();
//            slidePicPager.fakeDragBy(0);
        }else {
            finish();
        }
    }

    private List<Fragment> getFragments() {
        List<Fragment> fList = new ArrayList<Fragment>();
        WelcomeSlide1Fragment welcomeSlide1Fragment = WelcomeSlide1Fragment.newInstance("WelcomeSlide1Fragment");
        WelcomeSlide2Fragment welcomeSlide2Fragment = WelcomeSlide2Fragment.newInstance("WelcomeSlide1Fragment");
        WelcomeSlide3Fragment welcomeSlide3Fragment = WelcomeSlide3Fragment.newInstance("WelcomeSlide1Fragment");
        WelcomeSlide4Fragment welcomeSlide4Fragment = WelcomeSlide4Fragment.newInstance("WelcomeSlide1Fragment");

        fList.add(welcomeSlide1Fragment);
        fList.add(welcomeSlide2Fragment);
        fList.add(welcomeSlide3Fragment);
        fList.add(welcomeSlide4Fragment);

//        fList.add(WelcomeSlide2Fragment.newInstance("WelcomeSlide2Fragment"));
//        fList.add(WelcomeSlide3Fragment.newInstance("WelcomeSlide3Fragment"));
        return fList;
    }

    private class MyPageAdapter extends FragmentStatePagerAdapter {
        private List<Fragment> fragments;

        public MyPageAdapter(FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            this.fragments = fragments;
        }
        @Override
        public Fragment getItem(int position) {
            return this.fragments.get(position);
        }

        @Override
        public int getCount() {
            return this.fragments.size();
        }
    }

    public void addDots() {
        dots = new ArrayList<>();
        LinearLayout dotsLayout = (LinearLayout) findViewById(R.id.preview_dots);

        for (int i = 0; i < numberOfImages; i++) {
            ImageView dot = new ImageView(this);
            dot.setImageResource(R.drawable.trans_circle);
            dot.setPadding(10, 0, 10, 0);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            dotsLayout.addView(dot, params);
            dots.add(dot);
        }

        slidePicPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if(position == numberOfImages-1 && positionOffsetPixels<0){
                    slidePicPager.setCurrentItem(0);
                }
            }

            @Override
            public void onPageSelected(int position) {
                selectDot(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

    }

    public void selectDot(int idx) {

        for (int i = 0; i < numberOfImages; i++) {
            int drawableId = (i == idx) ? (R.drawable.circular_back_white) : (R.drawable.trans_circle);
            dots.get(i).setImageResource(drawableId);
        }
    }
    public void pageSwitcher(int seconds) {
        timer = new Timer();
        timer.scheduleAtFixedRate(new SlideTask(), 1000, seconds * 1000);
    }
    class SlideTask extends TimerTask {

        @Override
        public void run() {

            // As the TimerTask run on a seprate thread from UI thread we have
            // to call runOnUiThread to do work on UI thread.
            runOnUiThread(new Runnable() {
                public void run() {

                    if (nextPage > numberOfImages-1) { // In my case the number of pages are 5
//                        if (prevPage!=0) {
//                            slidePicPager.setCurrentItem(prevPage--);
//                        }else {
                            nextPage = 0;
                            slidePicPager.setCurrentItem(nextPage++);
//                            prevPage = numberOfImages-1;
//                        }
                    } else {
                        slidePicPager.setCurrentItem(nextPage++);
                    }
                }
            });

        }
    }
    private void showCustomizedActionBar(){
        ActionBar.LayoutParams layoutparams = new ActionBar.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT, Gravity.CENTER);
        TextView textview;
        textview = new TextView(getApplicationContext());
        textview.setLayoutParams(layoutparams);
        textview.setText(getString(R.string.app_name));
        textview.setTextSize(18);
        textview.setGravity(Gravity.CENTER_VERTICAL);
        ActionBar actionBar = getActionBar();
        actionBar.setCustomView(textview);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
//        actionBar.setIcon(R.drawable.app_icon);
//        actionBar.setHomeButtonEnabled(true);

    }

    private String getKeyHashForFbAccess(){
        try {

            PackageInfo info = getPackageManager().getPackageInfo(

                    "com.blisscom.gourava.jaiho",

                    PackageManager.GET_SIGNATURES);

            for (Signature signature : info.signatures) {

                MessageDigest md = MessageDigest.getInstance("SHA");

                md.update(signature.toByteArray());

                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));

            }

        } catch (PackageManager.NameNotFoundException e) {
            Log.d("KeyHash:", e.getMessage());


        } catch (NoSuchAlgorithmException e) {

            Log.d("KeyHash:", e.getMessage());
//            9cUHeY+SBk7JTuZxiKP0KTommpQ=
        } catch (Exception ex){

        }
        return null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.user_login_tv:

                Intent loginIntent = new Intent(this, LoginActivity.class);
                startActivity(loginIntent);
                timer.cancel();
                finish();
                break;

            case R.id.create_user_account_tv:
                Intent regIntent = new Intent(this, UserDirectRegistrationActivity.class);
                startActivity(regIntent);
                timer.cancel();
                finish();
                break;

//            case R.id.fb_login_tv:
//                break;
//            case R.id.google_login_tv:
//                break;
            case R.id.no_account_tv:
                Intent intent = new Intent(this, UserHomeActivity.class);
                startActivity(intent);
                timer.cancel();
                finish();
                break;
            case R.id.priest_radio_iv:
                if(sharedPreferences.getBoolean(PojoPreferredStrings.POJO_IS_USER.toString(), false)) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean(PojoPreferredStrings.POJO_IS_USER.toString(), false);
                    editor.commit();
                    userRadioIV.setImageResource(R.drawable.circular_back_white);
                    priestRadioIV.setImageResource(R.drawable.filled_radio_circle);
                    runExitAnimation(userLL);
                    runEntryAnimation(priestLL);
                }else {
                    priestRadioIV.setImageResource(R.drawable.filled_radio_circle);
                    priestLL.setVisibility(View.VISIBLE);
//                    userLL.setVisibility(View.INVISIBLE);
                }
                break;
            case R.id.user_radio_iv:
                if(!    sharedPreferences.getBoolean(PojoPreferredStrings.POJO_IS_USER.toString(), false)) {
                    SharedPreferences.Editor editor2 = sharedPreferences.edit();
                    editor2.putBoolean(PojoPreferredStrings.POJO_IS_USER.toString(), true);
                    editor2.commit();
                    userRadioIV.setImageResource(R.drawable.filled_radio_circle);
                    priestRadioIV.setImageResource(R.drawable.circular_back_white);
//                animateUserAppearance();
                    runExitAnimation(priestLL);
                    runEntryAnimation(userLL);
                }else {
//                    priestLL.setVisibility(View.INVISIBLE);
                    userRadioIV.setImageResource(R.drawable.filled_radio_circle);
                    userLL.setVisibility(View.VISIBLE);
                }
                break;

            case R.id.priest_login_tv:
                getSupportFragmentManager()
                        .beginTransaction().replace(android.R.id.content, new LoginFormFragment()).addToBackStack(null).commit();
                break;
            case R.id.priest_create_account_tv:
                getSupportFragmentManager()
                        .beginTransaction().replace(android.R.id.content, new PriestRegistrationFragment()).addToBackStack(null).commit();
                break;
            case R.id.priest_test_account_tv:
                Intent intent1 = new Intent(this, PriestHomeActivity.class);
                startActivity(intent1);
                break;

        }
    }

    private void runEntryAnimation(View v){
        v.setVisibility(View.VISIBLE);
        v.startAnimation(enteryAnimation);
    }
    private void runExitAnimation(View v){
        v.setVisibility(View.VISIBLE);
        v.startAnimation(exitAnimation);
        v.setVisibility(View.GONE);
    }

    private void animatePriestAppearance(){

        Animation enter = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.coming_in_rtl_animation);
        Animation exit = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.going_out_rtl_animation);
        userLL.setVisibility(View.VISIBLE);
        userLL.startAnimation(exit);
        userLL.setVisibility(View.GONE);
        priestLL.setVisibility(View.VISIBLE);
        priestLL.startAnimation(enter);

    }

    private void animateUserAppearance(){

        Animation enter = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.coming_in_rtl_animation);
        Animation exit = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.going_out_rtl_animation);
        priestLL.setVisibility(View.VISIBLE);
        priestLL.startAnimation(exit);
        priestLL.setVisibility(View.GONE);
        userLL.setVisibility(View.VISIBLE);
        userLL.startAnimation(enter);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
