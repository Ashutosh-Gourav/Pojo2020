package com.blisscom.gourava.jaiho.activity.user;

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
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
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
import com.blisscom.gourava.jaiho.model.LoginAsyncCall;
import com.blisscom.gourava.jaiho.model.PojoPreferredStrings;
import com.blisscom.gourava.jaiho.model.RegistrationAsyncCall;

public class LoginWhileBookingActivity extends FragmentActivity implements View.OnClickListener{

    public static final String tag = "LoginFormFragment";;
    private Button loginButton, createAccountButton;
    private EditText userPhoneET, userPasswordET;
    private SharedPreferences sharedPreferences;
    private TextView forgotPwdTV;
    private TelephonyManager telephonyManager;
    private static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 1;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_login_while_booking);
        sharedPreferences = getSharedPreferences("POJO_PREFERENCE", Context.MODE_PRIVATE);

        userPhoneET = (EditText)findViewById(R.id.user_phone_et);
        userPasswordET = (EditText)findViewById(R.id.password_et);
        loginButton = (Button)findViewById(R.id.loginButton);
        loginButton.setOnClickListener(this);
        createAccountButton = (Button)findViewById(R.id.register_button);
        createAccountButton.setOnClickListener(this);
        forgotPwdTV = (TextView)findViewById(R.id.forgot_password_tv);
        forgotPwdTV.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.register_button:
                String savedUserPhone = sharedPreferences.getString(PojoPreferredStrings.POJO_USER_PHONE_NUMBER.toString(), null);
                Intent intent = new Intent(this, RegisterWhileBookingActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.loginButton:
                String errorMessage1 = null;

                if(userPasswordET.getText() == null || userPasswordET.getText().toString().trim().isEmpty()
                        || userPhoneET.getText() != null && userPhoneET.getText().toString().trim().isEmpty()){
//                    if(userPasswordET.getText() != null && userPasswordET.getText().toString().length()>0){
//                        errorMessage = "Passwords can not be only blank spaces.";
//                    }else {
                    errorMessage1 = "Both fields are mandatory.";
//                    }
                    showErrorDialogMessage(errorMessage1);
                }else if(userPhoneET.getText().toString().trim().length()!=10){
                    errorMessage1 = "Please give a valid phone number.";
                    showErrorDialogMessage(errorMessage1);
                }else {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                            != PackageManager.PERMISSION_GRANTED) {
                        if (!sharedPreferences.getBoolean(PojoPreferredStrings.IS_DO_NOT_SHOW_DEVICE_ID_REQUEST_CHECKED.toString(), false)) {
                            alertDialogForPhoneStatePermissionRequest(R.string.device_info_permission_title,R.string.device_info_permission_message);
                        }
                        else {
                            alertDialogForPhoneStatePermissionRequest(R.string.device_info_permission_title_2,R.string.device_info_permission_message_2);
                        }
                    }else {
                        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(PojoPreferredStrings.DEVICE_ID.toString(), telephonyManager.getDeviceId());
                        editor.commit();

//                    AsyncApiCalls asyncApiCalls = new AsyncApiCalls(getActivity());
                        boolean isUserBooking = true;
                        Object[] params = {userPhoneET.getText().toString(), userPasswordET.getText().toString(), isUserBooking};
                        LoginAsyncCall loginAsyncCall = new LoginAsyncCall(this);
                        loginAsyncCall.execute(params);
                    }
                }
                break;
            case R.id.forgot_password_tv:
                // TODO: 1/7/17 goto forgot password activity
                Intent forgotPwdIntent = new Intent(this, PhoneVerificationActivity.class);
                startActivity(forgotPwdIntent);
//                boolean isUserBooking = false;
//                Object[] params = {userPhoneET.getText().toString(), userPasswordET.getText().toString(), isUserBooking};
//                LoginAsyncCall loginAsyncCall = new LoginAsyncCall(getContext());
//                loginAsyncCall.execute(params);
//                showDialogForOtp();
                break;
        }
    }

    private void showErrorDialogMessage(String errorMessage){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

    private void alertDialogForPhoneStatePermissionRequest(int title, int message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        TextView titleView = new TextView(this);
        titleView.setGravity(Gravity.CENTER_HORIZONTAL);
        titleView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        titleView.setPadding(0, 10, 0, 10);
        titleView.setText(title);
        titleView.setTextSize(20);
        titleView.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        builder.setCustomTitle(titleView);

        TextView messageView = new TextView(this);
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
//                    requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
                    ActivityCompat.requestPermissions(LoginWhileBookingActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);


                }
            });
        }else {
            builder.setNeutralButton(getResources().getString(R.string.alert_later_button_text), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
//                    requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
                    ActivityCompat.requestPermissions(LoginWhileBookingActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);

                }
            });
            builder.setPositiveButton(getResources().getString(R.string.alert_settings_button_text), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
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

            if (requestCode == MY_PERMISSIONS_REQUEST_READ_PHONE_STATE) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.READ_PHONE_STATE)) {
                    editor.putBoolean(PojoPreferredStrings.IS_DO_NOT_SHOW_DEVICE_ID_REQUEST_CHECKED.toString(), false);
                    editor.commit();

                } else {
                    editor.putBoolean(PojoPreferredStrings.IS_DO_NOT_SHOW_DEVICE_ID_REQUEST_CHECKED.toString(), true);
                    editor.commit();

                }
                if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                    editor.putString(PojoPreferredStrings.DEVICE_ID.toString(), telephonyManager.getDeviceId());
                    editor.commit();

//                    AsyncApiCalls asyncApiCalls = new AsyncApiCalls(getActivity());
                    boolean isUserBooking = true;
                    Object[] params = {userPhoneET.getText().toString(), userPasswordET.getText().toString(), isUserBooking};
                    LoginAsyncCall loginAsyncCall = new LoginAsyncCall(this);
                    loginAsyncCall.execute(params);
                }
            }
        }
    }
}

