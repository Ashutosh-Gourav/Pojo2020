package com.blisscom.gourava.jaiho.activity.user;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.blisscom.gourava.jaiho.R;
import com.blisscom.gourava.jaiho.activity.WelcomeActivity;
import com.blisscom.gourava.jaiho.fragment.user.UserRegistrationFragment;
import com.blisscom.gourava.jaiho.model.PojoPreferredStrings;

public class UserDirectRegistrationActivity extends FragmentActivity {

    private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registration);
        sharedPreferences = getSharedPreferences(getString(R.string.shared_preference_name), MODE_PRIVATE);
        FragmentManager fm  = getSupportFragmentManager();
        Fragment registrationFragment = new UserRegistrationFragment();
        FragmentTransaction ft = fm.beginTransaction().replace(R.id.registration_container, registrationFragment, UserRegistrationFragment.TAG);
        ft.commit();
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        if(sharedPreferences.getBoolean(PojoPreferredStrings.POJO_IS_USER.toString(), false)) {
            if (sharedPreferences.getString(PojoPreferredStrings.POJO_USER_PHONE_NUMBER.toString(), null) == null
                    || sharedPreferences.getString(PojoPreferredStrings.POJO_USER_PHONE_NUMBER.toString(), null).isEmpty()) {
                Intent intent = new Intent(this, WelcomeActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }
}
