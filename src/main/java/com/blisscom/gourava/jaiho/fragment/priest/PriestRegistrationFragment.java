package com.blisscom.gourava.jaiho.fragment.priest;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.blisscom.gourava.jaiho.R;
import com.blisscom.gourava.jaiho.activity.MapsActivity;
import com.blisscom.gourava.jaiho.model.PojoGpsTracker;
import com.blisscom.gourava.jaiho.model.PojoPreferredStrings;
import com.blisscom.gourava.jaiho.model.PriestRegistrationAsyncCall;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by gourava on 1/15/17.
 */

public class PriestRegistrationFragment  extends Fragment implements View.OnClickListener, View.OnTouchListener {

    private static final int MY_PERMISSIONS_REQUEST_GPS = 0;
    private static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 1;

    public static final String TAG = "PriestRegistrationFragment";
    private EditText nameET, phoneET, mailET;
    private TextView addressTV;
    private Button registerButton;
    private ImageView  getLocationIV;
    private SharedPreferences sharedPreferences;
    private TelephonyManager telephonyManager;


    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle bundle){
        super.onCreateView(layoutInflater, container, bundle);
        sharedPreferences = getContext().getSharedPreferences(getString(R.string.shared_preference_name), Context.MODE_PRIVATE);
        return layoutInflater.inflate(R.layout.fragment_priest_registration, container, false);

    }

    @Override
    public void onViewCreated(View view, Bundle bundle){
        super.onViewCreated(view, bundle);
        registerButton = (Button)view.findViewById(R.id.priestRegButton);
        registerButton.setOnClickListener(this);
        nameET = (EditText)view.findViewById(R.id.priest_register_name_et);
        phoneET = (EditText)view.findViewById(R.id.priest_register_phone_ET);
        mailET = (EditText)view.findViewById(R.id.priest_register_mail_et);
        addressTV = (TextView)view.findViewById(R.id.priest_register_address_tv);
        getLocationIV = (ImageView)view.findViewById(R.id.priest_loc_iv);
        getLocationIV.setOnClickListener(this);
    }

    public void setAddressFromMapToRegisterTV(String lastAddressFromMap){
        addressTV.setText(lastAddressFromMap);
    }



    @Override
    public void onActivityCreated(Bundle bundle){
        super.onActivityCreated(bundle);

    }

    @Override
    public void onResume(){
        super.onResume();
        setAddressFromMapToRegisterTV(sharedPreferences.getString(PojoPreferredStrings.LAST_FULL_ADDRESS_TAKEN_FROM_MAP.toString(), null));
    }


    @Override
    public void onSaveInstanceState(Bundle bundle){
        super.onSaveInstanceState(bundle);
    }



    @Override
    public void onDestroyView(){
        super.onDestroyView();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.priest_loc_iv:

                if ( Build.VERSION.SDK_INT >= 23 &&
                        ContextCompat.checkSelfPermission( getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission( getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    if (!sharedPreferences.getBoolean(PojoPreferredStrings.IS_DO_NOT_SHOW_GPS_REQUEST_CHECKED.toString(), false)) {
                        alertDialogForPermissionRequest(R.string.location_info_permission_title_1,R.string.location_info_permission_message_1);
                    }
                    else {
                        alertDialogForPermissionRequest(R.string.location_info_permission_title_2,R.string.location_info_permission_message_2);
                    }
                }else {

                    getAddressFromMap();
                }

                break;
            case R.id.priestRegButton:
                String errorMessage = null;
                if(phoneET.getText() != null && phoneET.getText().toString().trim().isEmpty()
                        || nameET.getText() != null && nameET.getText().toString().trim().isEmpty()
                        || mailET.getText() != null && mailET.getText().toString().trim().isEmpty()
                        || addressTV.getText() != null && addressTV.getText().toString().trim().isEmpty()){

                    errorMessage = "All fields are mandatory.";
                    showErrorDialogMessage(errorMessage);
                }else if(phoneET.getText().toString().trim().length()!=10){
                    errorMessage = "Please give a valid phone number.";
                    showErrorDialogMessage(errorMessage);
                }else if(!isValidEmailAddress(mailET.getText().toString())){
                    errorMessage = "Please give a valid Email Id.";
                    showErrorDialogMessage(errorMessage);
                } else {
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

                        // TODO: 1/15/17 make priest reg async call then save priest phone number in sharedpreference when successfull
                        PriestRegistrationAsyncCall registrationAsyncCall = new PriestRegistrationAsyncCall(getContext());
                        boolean isUserBooking = false;
                        Object[] params = {mailET.getText().toString(), phoneET.getText().toString(), nameET.getText().toString(), addressTV.getText().toString(), false};
                        registrationAsyncCall.execute(params);
                    }
                }
                break;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
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

    private boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        Pattern p = Pattern.compile(ePattern);
        Matcher m = p.matcher(email);
        return m.matches();
    }

    private void alertDialogForPermissionRequest(int title, int message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        TextView titleView = new TextView(getContext());
        titleView.setGravity(Gravity.CENTER_HORIZONTAL);
        titleView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        titleView.setPadding(0, 10, 0, 10);
        titleView.setText(title);
        titleView.setTextSize(20);
        titleView.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        builder.setCustomTitle(titleView);

        TextView messageView = new TextView(getContext());
        messageView.setGravity(Gravity.CENTER | Gravity.CENTER);
        messageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        messageView.setText(getResources().getString(message));
        messageView.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
        messageView.setPadding(30, 10, 30, 10);
        builder.setView(messageView);
        if (!sharedPreferences.getBoolean(PojoPreferredStrings.IS_DO_NOT_SHOW_GPS_REQUEST_CHECKED.toString(), false)) {
            builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_GPS);
                }
            });
        }else {
            builder.setNeutralButton(getContext().getResources().getString(R.string.alert_later_button_text), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_GPS);
                }
            });
            builder.setPositiveButton(getContext().getResources().getString(R.string.alert_settings_button_text), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                    intent.setData(uri);
                    getContext().startActivity(intent);
                }
            });
        }

        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (Build.VERSION.SDK_INT >= 23) {

            if (requestCode == MY_PERMISSIONS_REQUEST_GPS) {

                if (getActivity().shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    editor.putBoolean(PojoPreferredStrings.IS_DO_NOT_SHOW_GPS_REQUEST_CHECKED.toString(), false);
                } else {
                    editor.putBoolean(PojoPreferredStrings.IS_DO_NOT_SHOW_GPS_REQUEST_CHECKED.toString(), true);
                }
                if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getAddressFromMap();
                }
            }
            if (requestCode == MY_PERMISSIONS_REQUEST_READ_PHONE_STATE) {
                if (getActivity().shouldShowRequestPermissionRationale(Manifest.permission.READ_PHONE_STATE)) {
                    editor.putBoolean(PojoPreferredStrings.IS_DO_NOT_SHOW_DEVICE_ID_REQUEST_CHECKED.toString(), false);
                } else {
                    editor.putBoolean(PojoPreferredStrings.IS_DO_NOT_SHOW_DEVICE_ID_REQUEST_CHECKED.toString(), true);
                }
                if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    telephonyManager = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
                    editor.putString(PojoPreferredStrings.DEVICE_ID.toString() , telephonyManager.getDeviceId());
                    // TODO: 1/15/17 make priest reg async call then save priest phone number in sharedpreference when successfull

                }
            }
            editor.commit();
        }

    }

    private void getAddressFromMap(){
        PojoGpsTracker gps = new PojoGpsTracker(getContext());
        if (gps.canGetLocation()) {
            Intent intent = new Intent(getContext(), MapsActivity.class);
            startActivity(intent);
        }else {
            if (!gps.isGpsOn()) {
                gps.showGpsSettingsAlert();
            } else {
                if (!gps.canAccessInternet()) {
                    gps.showInternetSettingsAlert();
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

                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.READ_PHONE_STATE},
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
