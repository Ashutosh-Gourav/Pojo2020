package com.blisscom.gourava.jaiho.model;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.blisscom.gourava.jaiho.R;
import com.blisscom.gourava.jaiho.activity.priest.PriestHomeActivity;
import com.blisscom.gourava.jaiho.activity.user.UserHomeActivity;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

public  class LoginAsyncCall extends AsyncTask<Object, Integer, UserOtp> {

    private  Context context;
    private  Dialog progressDialog;
    private boolean isComingFromBookingFlow, isUser;
    private String phoneNumber, password;
    private static String hostname = "http://10.0.2.2:8080/pojo"; // for emulator
//    private static String hostname = "http://10.96.116.221:8080/pojo"; //for real mobile, use IP of wifi
//    private static String hostname = "http://192.168.43.98:8080/pojo"; //for real mobile, use IP of wifi
//    private static String hostname = "http://INBL2-0MPFD57.local:8080/pojo";
//    private static String hostname = "http://192.168.1.100:8080/pojo";

    private static final String USER_LOGIN_URI = "/svc/api/v1/user/login";
    private SharedPreferences sharedPreferences ;

    public LoginAsyncCall(Context context){
        super();
        this.context = context;
    }

    @Override
    public void onPreExecute(){
        super.onPreExecute();
        sharedPreferences = context.getSharedPreferences(context.getString(R.string.shared_preference_name), Context.MODE_PRIVATE);
        showLoadingDialog(R.string.login_progress_message);

    }


    @Override
    public UserOtp doInBackground(Object... params) {
        String url = hostname + USER_LOGIN_URI;
        isComingFromBookingFlow = (boolean)params[2];
        UserOtp userOtp = new UserOtp();
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Accept-Language", "en-US,en;q=0.8");
        headers.set("Connection", "Close");
        try {
            JSONObject body = new JSONObject();
            phoneNumber = params[0].toString();
            password = params[1].toString();
            isUser = sharedPreferences.getBoolean(PojoPreferredStrings.POJO_IS_USER.toString(), false);
            body.put("mobileNumber", phoneNumber);
            body.put("password", password);
            body.put("isUser", isUser);
            body.put("deviceId", sharedPreferences.getString(PojoPreferredStrings.DEVICE_ID.toString(), null));
            body.put("fcmRegistrationToken", sharedPreferences.getString(PojoPreferredStrings.SAVED_FCM_TOKEN.toString(), null));

            HttpEntity<String> entity = new HttpEntity<>(body.toString(), headers);
            ResponseEntity<UserOtp> exchange = restTemplate.exchange(url, HttpMethod.POST, entity, UserOtp.class);
            userOtp = exchange.getBody();

        } catch (ResourceAccessException ex) {
            publishProgress(1);
            ex.printStackTrace();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

    }catch (HttpClientErrorException re) {
            if (re.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
                Log.d("pojo", "Error in user login :::" + re.getMessage());
                publishProgress(2);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (re.getStatusCode() == HttpStatus.NOT_FOUND) {
                publishProgress(3);
                Log.d("pojo", "no entry with this phone number found :::" + re.getMessage());
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (re.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                publishProgress(4);
                Log.e("pojo", "phone number Active but password mismatch :::" + re.getMessage());
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (re.getStatusCode() == HttpStatus.PRECONDITION_FAILED) {
                publishProgress(5);
                Log.e("pojo", "Error in user login :::" + re.getMessage());
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }catch (Exception ex) {
            ex.printStackTrace();
            Log.e("pojo", "Error in user login :::" + ex.getMessage());
            publishProgress(2);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
//        hideLoadingDialog();
        return userOtp;
    }

    @Override
    public void onPostExecute(final UserOtp userOtp){
        super.onPostExecute(userOtp);
        super.onPostExecute(userOtp);
        if (userOtp != null && userOtp.getOtp() != null) {
            Log.d("pojo", "login otp :::" + userOtp.getOtp());

            //todo check status: if success show otp dialog else show error message and then hide spinner
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
// Add the buttons
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.fragment_otp_dialog, null);
            TextView otpTV = (TextView) view.findViewById(R.id.otp_tv);
            otpTV.setText(context.getString(R.string.otp_title)+userOtp.getOtp());
            final EditText otpET = (EditText) view.findViewById(R.id.otp_et);
            otpET.setHint(context.getString(R.string.otp_hint));
            otpET.setMaxLines(1);
            otpET.setInputType(InputType.TYPE_CLASS_NUMBER);
            int maxLength = 6;
            otpET.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
            builder.setView(view);
//                builder.setTitle(R.string.otp_title);
//                builder.setIcon(R.drawable.app_icon);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    //// TODO: 12/12/16 poll and read message from messages and confirm it
                    // todo: if incorrect show error message and hide spinner
                    //// TODO: 12/12/16  if correct go to home activity and Async call otp confirmation api
                    if (otpET.getText().toString() != null && otpET.getText().toString().equalsIgnoreCase(userOtp.getOtp())) {
                        showLoadingDialog(R.string.login_success_message);
                        new LoginAsyncCall.WaitToShowProgress().execute();
                        sharedPreferences = context.getSharedPreferences(context.getString(R.string.shared_preference_name), Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        if(sharedPreferences.getBoolean(PojoPreferredStrings.POJO_IS_USER.toString(), false)) {

                            editor.putString(PojoPreferredStrings.POJO_USER_PHONE_NUMBER.toString(), phoneNumber);
                            editor.putBoolean(PojoPreferredStrings.IS_USER_REGISTRATION_OTP_CONFIRMED.toString(), true);
                        }else {

                            editor.putString(PojoPreferredStrings.POJO_PRIEST_PHONE_NUMBER.toString(), phoneNumber);
                            editor.putBoolean(PojoPreferredStrings.IS_PRIEST_REGISTRATION_OTP_CONFIRMED.toString(), true);
                        }
                        editor.commit();
                        if(!isComingFromBookingFlow) {
                            hideLoadingDialog();
                            if(sharedPreferences.getBoolean(PojoPreferredStrings.POJO_IS_USER.toString(), false)) {

                                Intent intent = new Intent(context, UserHomeActivity.class);
                                context.startActivity(intent);
                            }else {
                                Intent intent = new Intent(context, PriestHomeActivity.class);
                                editor.putBoolean(PojoPreferredStrings.IS_PRIEST_VERIFIED.toString(), true);
                                editor.commit();
                                context.startActivity(intent);
                            }
                            ((Activity) context).finish();
                        }else {
                            hideLoadingDialog();
                            ((Activity) context).finish();
                        }
                    } else {
                        showLoadingDialog(R.string.wrong_otp_text);
                        new LoginAsyncCall.WaitToShowProgress().execute();
                    }
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    showLoadingDialog(R.string.otp_cancel_message);
                    new LoginAsyncCall.WaitToShowProgress().execute();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.setCancelable(false);
            dialog.show();

        }else {
        hideLoadingDialog();
        }

    }

    @Override
    protected void onProgressUpdate(Integer... pro) {
        if (pro[0] == 1) {
            showLoadingDialog(R.string.no_network_access_error);
        }

        if (pro[0] == 2) {
            showLoadingDialog(R.string.Something_went_wrong);
        }

        if (pro[0] == 3) {
            showLoadingDialog(R.string.user_not_found_login_error);
        }
        if (pro[0] == 4) {
            showLoadingDialog(R.string.user_active_but_password_wrong_error);
        }
        if (pro[0] == 5) {
            showLoadingDialog(R.string.user_found_inactive);
        }

    }

    private void showLoadingDialog(int messageId) {
        if(progressDialog == null || !progressDialog.isShowing()) {
            progressDialog = new Dialog(context, android.R.style.Theme_Translucent);
            progressDialog.setContentView(R.layout.fragment_progress_dialog);
            TextView textView = (TextView) progressDialog.findViewById(R.id.progress_dialog_tv);
            textView.setText(context.getString(messageId));
            progressDialog.setCancelable(false);
            progressDialog.show();
        }else{
            TextView textView = (TextView) progressDialog.findViewById(R.id.progress_dialog_tv);
            textView.setText(context.getString(messageId));
        }
    }

    private void hideLoadingDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private class WaitToShowProgress extends AsyncTask{

        @Override
        protected Object doInBackground(Object[] params) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object ob){
            hideLoadingDialog();
        }
    }
}