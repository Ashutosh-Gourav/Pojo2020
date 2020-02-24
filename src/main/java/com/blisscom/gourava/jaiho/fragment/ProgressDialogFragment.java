package com.blisscom.gourava.jaiho.fragment;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blisscom.gourava.jaiho.R;

/**
 * Created by gourava on 12/12/16.
 */

public class ProgressDialogFragment extends DialogFragment {
    public static final String tag = "ProgressDialogFragment";

    public static ProgressDialogFragment newInstance() {
        return new ProgressDialogFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_FRAME, android.R.style.Theme_Translucent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_progress_dialog, container, false);

    }
}
