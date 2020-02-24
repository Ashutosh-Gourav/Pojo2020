package com.blisscom.gourava.jaiho.activity.user;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.blisscom.gourava.jaiho.R;
import com.blisscom.gourava.jaiho.activity.WelcomeActivity;
import com.blisscom.gourava.jaiho.model.PojoPreferredStrings;

public class LoginActivity extends FragmentActivity {

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().hide();
        setContentView(R.layout.activity_login);
        sharedPreferences = getSharedPreferences(getString(R.string.shared_preference_name), MODE_PRIVATE);
//        FragmentManager fm  = getSupportFragmentManager();
//        Fragment loginFormFragment = new LoginFormFragment();
//        FragmentTransaction ft = fm.beginTransaction().add(R.id.login_container, loginFormFragment, LoginFormFragment.tag);
//        ft.addToBackStack(null);
//        ft.commit();
    }

//    @Override
//    public View onCreateView(String name, Context context, AttributeSet attrs){
//        super.onCreateView(name, context, attrs);
//        setContentView(R.layout.activity_login);
//
//        FragmentManager fm  = getFragmentManager();
//        LoginFormFragment loginFormFragment = new LoginFormFragment();
//        FragmentTransaction ft = fm.beginTransaction().replace(R.id.login_container, loginFormFragment);
//        ft.addToBackStack(LoginFormFragment.tag);
//        ft.commit();
//        return null;
//    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

//        if (getFragmentManager().getBackStackEntryCount() == 1){
//            finish();
//        }

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
