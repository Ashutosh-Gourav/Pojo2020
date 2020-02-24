package com.blisscom.gourava.jaiho.activity.user;

import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;

import com.blisscom.gourava.jaiho.R;
import com.blisscom.gourava.jaiho.fragment.user.BookingFormFragment;
import com.blisscom.gourava.jaiho.model.PoojaDetails;

public class UserPoojaBookingActivity extends FragmentActivity {

    private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_pooja_booking);
        PoojaDetails selectedPoojaDetails = (PoojaDetails)getIntent().getSerializableExtra("selectedPoojaDetails");
        Bundle bundle1 = new Bundle();
        bundle1.putSerializable("selectedPoojaDetails", selectedPoojaDetails);
        sharedPreferences = getSharedPreferences(getString(R.string.shared_preference_name), MODE_PRIVATE);
        FragmentManager fm  = getSupportFragmentManager();
        Fragment bookingFormFragment = new BookingFormFragment();
        bookingFormFragment.setArguments(bundle1);
        FragmentTransaction ft = fm.beginTransaction().replace(R.id.boooking_form_container, bookingFormFragment, BookingFormFragment.tag);
        ft.commit();
    }
}
