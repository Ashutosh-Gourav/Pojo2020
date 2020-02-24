package com.blisscom.gourava.jaiho.activity.user;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;

import com.blisscom.gourava.jaiho.R;
import com.blisscom.gourava.jaiho.activity.WelcomeActivity;
import com.blisscom.gourava.jaiho.fragment.user.AllPoojaFragment;
import com.blisscom.gourava.jaiho.fragment.user.BookingHistoryFragment;
import com.blisscom.gourava.jaiho.model.PojoPreferredStrings;

public class UserHomeActivity extends FragmentActivity {

    private FragmentTabHost mainTabHost;
    private SharedPreferences sharedPreferences;
    private MenuItem refreshMenuItem;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final TextView textView = prepareActionBarLayout();
        setContentView(R.layout.activity_user_home);
        sharedPreferences = getSharedPreferences(getString(R.string.shared_preference_name), MODE_PRIVATE);

        mainTabHost = (FragmentTabHost)findViewById(R.id.home_tabHost);
        mainTabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);
        final View bookingView = LayoutInflater.from(this).inflate(R.layout.booking_image_ll, null);
        bookingView.setAlpha(1.0f);
        mainTabHost.addTab(mainTabHost.newTabSpec("instrmentList").setIndicator(bookingView), AllPoojaFragment.class, null);
        final View historyView = LayoutInflater.from(this).inflate(R.layout.list_image_ll, null);
        historyView.setAlpha(0.3f);
        mainTabHost.addTab(mainTabHost.newTabSpec("instrumentScheduler").setIndicator(historyView), BookingHistoryFragment.class, null);
        mainTabHost.refreshDrawableState();
        mainTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                if(tabId.equalsIgnoreCase("instrmentList")) {
                    showCustomizedActionBar(textView, R.string.booking_tab_title);
//                    if(refreshMenuItem != null){
//                        refreshMenuItem.setVisible(false);
//                    }
                    bookingView.setAlpha(1.0f);
                    historyView.setAlpha(0.3f);
                } else {
                    showCustomizedActionBar(textView, R.string.history_tab_title);
//                    if(refreshMenuItem != null){
//                        refreshMenuItem.setVisible(true);
//                    }
                    bookingView.setAlpha(0.3f);
                    historyView.setAlpha(1.0f);
                }
            }
        });

    }

    private TextView prepareActionBarLayout(){
        ActionBar.LayoutParams layoutparams = new ActionBar.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT, Gravity.CENTER);
        TextView textview = new TextView(getApplicationContext());
        textview.setLayoutParams(layoutparams);
        textview.setText(getString(R.string.booking_tab_title));
        textview.setTextSize(20);
        textview.setTypeface(Typeface.SANS_SERIF, Typeface.NORMAL);
        textview.setGravity(Gravity.CENTER);
        ActionBar actionBar = getActionBar();
        actionBar.setCustomView(textview);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setIcon(R.drawable.app_icon);
        actionBar.setHomeButtonEnabled(true);
        actionBar.show();
        return textview;
    }

    private void showCustomizedActionBar(TextView textview, int titleStringId){

        textview.setText(titleStringId);
        ActionBar actionBar = getActionBar();
        actionBar.setCustomView(textview);
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        if(sharedPreferences.getBoolean(PojoPreferredStrings.POJO_IS_USER.toString(), false)) {
            if (sharedPreferences.getString(PojoPreferredStrings.POJO_USER_PHONE_NUMBER.toString(), null) == null
                    || sharedPreferences.getString(PojoPreferredStrings.POJO_USER_PHONE_NUMBER.toString(), null).isEmpty()) {
                Intent intent = new Intent(this, WelcomeActivity.class);
                startActivity(intent);
                finish();;
            }
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        if(sharedPreferences.getBoolean(PojoPreferredStrings.IS_DRIVEN_TO_HISTORY.toString(), false)){
            mainTabHost.setCurrentTab(1);
//            SharedPreferences.Editor editor = sharedPreferences.edit();
//            editor.putBoolean(PojoPreferredStrings.IS_DRIVEN_TO_HISTORY.toString(), false);
//            editor.commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_history_reresh, menu);
        refreshMenuItem = menu.findItem(R.id.refresh_list);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.refresh_list:
                BookingHistoryFragment historyFragment = new BookingHistoryFragment();
                if (mainTabHost.getCurrentTab() == 1) {
//                    getSupportFragmentManager().beginTransaction().replace(android.R.id.tabcontent, historyFragment).commit();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
