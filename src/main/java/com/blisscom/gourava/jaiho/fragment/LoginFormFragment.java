package com.blisscom.gourava.jaiho.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.blisscom.gourava.jaiho.R;
import com.blisscom.gourava.jaiho.activity.PhoneVerificationActivity;
import com.blisscom.gourava.jaiho.activity.user.UserRegistrationActivity;
import com.blisscom.gourava.jaiho.fragment.priest.PriestRegistrationFragment;
import com.blisscom.gourava.jaiho.model.LoginAsyncCall;
import com.blisscom.gourava.jaiho.model.PojoPreferredStrings;
import com.blisscom.gourava.jaiho.model.RegistrationAsyncCall;

/**
 * Created by gourava on 12/12/16.
 */

public class LoginFormFragment extends Fragment implements View.OnClickListener{

    private static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 1;
    private TelephonyManager telephonyManager;

    public static final String tag = "LoginFormFragment";;
    private Button loginButton, createAccountButton;
    private EditText phoneET, passwordET;
    private SharedPreferences sharedPreferences;
    private TextView forgotPwdTV;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup vg,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, vg, savedInstanceState);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        View v = inflater.inflate(R.layout.fragment_login_form, vg, false);
        sharedPreferences = getActivity().getSharedPreferences(getString(R.string.shared_preference_name), Context.MODE_PRIVATE);

        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        phoneET = (EditText)view.findViewById(R.id.phone_et);
        passwordET = (EditText)view.findViewById(R.id.password_et);
        loginButton = (Button)view.findViewById(R.id.loginButton);
        loginButton.setOnClickListener(this);
        createAccountButton = (Button)view.findViewById(R.id.register_button);
        createAccountButton.setOnClickListener(this);
        forgotPwdTV = (TextView)view.findViewById(R.id.forgot_password_tv);
        forgotPwdTV.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.register_button:
                String savedUserPhone = sharedPreferences.getString(PojoPreferredStrings.POJO_USER_PHONE_NUMBER.toString(), null);

                if(sharedPreferences.getBoolean(PojoPreferredStrings.POJO_IS_USER.toString(), false)) {
                    Intent intent = new Intent(getContext(), UserRegistrationActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                }else {
                    getFragmentManager().beginTransaction()
                            .add(android.R.id.content, new PriestRegistrationFragment()).addToBackStack(null).commit();
                }

                break;
            case R.id.loginButton:
                String errorMessage1 = null;

                if(passwordET.getText() == null || passwordET.getText().toString().trim().isEmpty()
                        || phoneET.getText() != null && phoneET.getText().toString().trim().isEmpty()){
//                    if(passwordET.getText() != null && passwordET.getText().toString().length()>0){
//                        errorMessage = "Passwords can not be only blank spaces.";
//                    }else {
                        errorMessage1 = "Both fields are mandatory.";
//                    }
                    showErrorDialogMessage(errorMessage1);
                }else if(phoneET.getText().toString().trim().length()!=10){
                    errorMessage1 = "Please give a valid phone number.";
                    showErrorDialogMessage(errorMessage1);
                }else {
                    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_PHONE_STATE)
                            != PackageManager.PERMISSION_GRANTED) {
                        if (!sharedPreferences.getBoolean(PojoPreferredStrings.IS_DO_NOT_SHOW_DEVICE_ID_REQUEST_CHECKED.toString(), false)) {
                            alertDialogForPhoneStatePermissionRequest(R.string.device_info_permission_title,R.string.device_info_permission_message);
                        }
                        else {
                            alertDialogForPhoneStatePermissionRequest(R.string.device_info_permission_title_2,R.string.device_info_permission_message_2);
                        }
                    }else {
                        telephonyManager = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(PojoPreferredStrings.DEVICE_ID.toString() , telephonyManager.getDeviceId());
                        editor.commit();

                        boolean isUserBooking = false;
                        Object[] params = {phoneET.getText().toString(), passwordET.getText().toString(), isUserBooking};
                        LoginAsyncCall loginAsyncCall = new LoginAsyncCall(getContext());
                        loginAsyncCall.execute(params);
                    }
                }
                break;
            case R.id.forgot_password_tv:
                // TODO: 1/7/17 goto forgot password activity
                Intent forgotPwdIntent = new Intent(getContext(), PhoneVerificationActivity.class);
                startActivity(forgotPwdIntent);
//                boolean isUserBooking = false;
//                Object[] params = {phoneET.getText().toString(), passwordET.getText().toString(), isUserBooking};
//                LoginAsyncCall loginAsyncCall = new LoginAsyncCall(getContext());
//                loginAsyncCall.execute(params);
//                showDialogForOtp();
                break;
        }
    }

    private void showErrorDialogMessage(String errorMessage){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.fragment_dialog_message, null);
        TextView messageTV = (TextView)view.findViewById(R.id.message_tv);
        messageTV.setVisibility(View.VISIBLE);
        ImageView imageView = (ImageView)view.findViewById(R.id.dialog_iv);
        imageView.setImageResource(R.drawable.alert_128);
        messageTV.setText(errorMessage);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.setCancelable(true);
        dialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (Build.VERSION.SDK_INT >= 23) {

            if (requestCode == MY_PERMISSIONS_REQUEST_READ_PHONE_STATE) {
                if (getActivity().shouldShowRequestPermissionRationale(Manifest.permission.READ_PHONE_STATE)) {
                    editor.putBoolean(PojoPreferredStrings.IS_DO_NOT_SHOW_DEVICE_ID_REQUEST_CHECKED.toString(), false);
                    editor.commit();

                } else {
                    editor.putBoolean(PojoPreferredStrings.IS_DO_NOT_SHOW_DEVICE_ID_REQUEST_CHECKED.toString(), true);
                    editor.commit();
                }
                if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    telephonyManager = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
                    editor.putString(PojoPreferredStrings.DEVICE_ID.toString(), telephonyManager.getDeviceId());
                    editor.commit();

                    boolean isUserBooking = false;
                    Object[] params = {phoneET.getText().toString(), passwordET.getText().toString(), isUserBooking};
                    LoginAsyncCall loginAsyncCall = new LoginAsyncCall(getContext());
                    loginAsyncCall.execute(params);
                }
            }
        }
    }

    private void alertDialogForPhoneStatePermissionRequest(int title, int message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        TextView titleView = new TextView(getActivity());
        titleView.setGravity(Gravity.CENTER_HORIZONTAL);
        titleView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        titleView.setPadding(0, 10, 0, 10);
        titleView.setText(title);
        titleView.setTextSize(20);
        titleView.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        builder.setCustomTitle(titleView);

        TextView messageView = new TextView(getActivity());
        messageView.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER);
        messageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        messageView.setText(getResources().getString(message));
        messageView.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
        messageView.setPadding(30, 10, 30, 10);
        messageView.setTextSize(15);
        builder.setView(messageView);
        if (!sharedPreferences.getBoolean(PojoPreferredStrings.IS_DO_NOT_SHOW_DEVICE_ID_REQUEST_CHECKED.toString(), false)) {
            builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);

                }
            });
        }else {
            builder.setNeutralButton(getContext().getResources().getString(R.string.alert_later_button_text), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);

                    requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE},
                            MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
                }
            });
            builder.setPositiveButton(getContext().getResources().getString(R.string.alert_settings_button_text), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Uri uri = Uri.fromParts("package", getContext().getPackageName(), null);
                    intent.setData(uri);
                    getContext().startActivity(intent);
                }
            });
        }

        AlertDialog alert = builder.create();
        alert.show();
    }
}
