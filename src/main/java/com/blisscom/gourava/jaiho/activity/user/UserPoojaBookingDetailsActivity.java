package com.blisscom.gourava.jaiho.activity.user;

import android.app.ActionBar;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blisscom.gourava.jaiho.R;
import com.blisscom.gourava.jaiho.fragment.user.UserBookedPoojaDetailsFragment;

public class UserPoojaBookingDetailsActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final TextView textView = prepareActionBarLayout();
        showCustomizedActionBar(textView, R.string.booking_details_title);
        setContentView(R.layout.activity_pooja_booking_details);
        Fragment historyDetailsFragment = new UserBookedPoojaDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("selectedPoojaBookingDetails", getIntent().getSerializableExtra("selectedPoojaBookingDetails"));
        bundle.putSerializable("selectedPoojaInBooking", getIntent().getSerializableExtra("selectedPoojaInBooking"));
        historyDetailsFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().add(R.id.booking_detail_container, historyDetailsFragment).addToBackStack(null).commit();

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
        actionBar.setHomeButtonEnabled(false);
        actionBar.show();
        return textview;
    }

    private void showCustomizedActionBar(TextView textview, int titleStringId){

        textview.setText(titleStringId);
        ActionBar actionBar = getActionBar();
        actionBar.setCustomView(textview);
    }

//    @Override
//    public void onBackPressed(){
//        super.onBackPressed();
//        finish();
//    }
}
