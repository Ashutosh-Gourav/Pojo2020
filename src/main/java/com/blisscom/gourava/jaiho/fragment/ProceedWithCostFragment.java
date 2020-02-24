package com.blisscom.gourava.jaiho.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blisscom.gourava.jaiho.R;

/**
 * Created by gourava on 12/28/16.
 */

public class ProceedWithCostFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle bundle){
        super.onCreateView(inflater, viewGroup, bundle);
        View view = inflater.inflate(R.layout.fragment_proceed_with_cost, viewGroup, false);
        return view;

        //// TODO: 12/31/16 calculate charges 
        //// TODO: 12/31/16 on book button press::::::::
        // todo : user is registered , make async call and on success show confirmation message, add in history, redirect to history,on failure ask to try again
        // TODO: user is not registered, ask to register, on registration success with otp,  async call to book
    }
}
