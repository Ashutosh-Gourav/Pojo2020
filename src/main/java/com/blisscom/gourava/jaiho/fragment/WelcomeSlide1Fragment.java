package com.blisscom.gourava.jaiho.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blisscom.gourava.jaiho.R;

/**
 * Created by gourava on 1/13/17.
 */

public class WelcomeSlide1Fragment extends android.support.v4.app.Fragment {

public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";
private Activity activity;

public static final WelcomeSlide1Fragment newInstance(String message) {
    WelcomeSlide1Fragment f = new WelcomeSlide1Fragment();
        Bundle bdl = new Bundle(1);
        bdl.putString(EXTRA_MESSAGE, message);
        f.setArguments(bdl);
        return f;
        }

@Override
public void onAttach(Context context) {
        super.onAttach(context);
        activity = getActivity();
        }

@Override
public void onDetach() {
        super.onDetach();
        }

@Override
public void onDestroy() {
        super.onDestroy();
        }

@Override
public View onCreateView(LayoutInflater inflater, ViewGroup container,
                         Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_welcome_slide1, container, false);
        return v;
        }
}
