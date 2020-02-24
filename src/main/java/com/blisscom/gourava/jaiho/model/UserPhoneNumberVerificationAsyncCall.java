package com.blisscom.gourava.jaiho.model;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.blisscom.gourava.jaiho.R;
import com.blisscom.gourava.jaiho.fragment.ResetPasswordFragment;

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

/**
 * Created by gourava on 1/7/17.
 */

public class UserPhoneNumberVerificationAsyncCall extends AsyncTask<Object, Integer, UserOtp> {

    private Context context;
    private Dialog progressDialog;
    private boolean isComingFromBookingFlow;
    private String phoneNumber, mailId;
//        private static String hostname = "http://10.0.2.2:8080/pojo"; // for emulator
    private static String hostname = "http://192.168.43.7:8080/pojo"; //for real mobile, use IP of wifi
//    private static String hostname = "http://192.168.43.98:8080/pojo"; //for real mobile, use IP of wifi
//private static String hostname = "http://192.168.1.100:8080/pojo";

    //    private static String hostname = "http://INBL2-0MPFD57.local:8080/pojo";
    private static final String USER_phone_verification_URI = "/svc/api/v1/user/number/verification";
    private SharedPreferences sharedPreferences;

    public UserPhoneNumberVerificationAsyncCall(Context context){
        super();
        this.context = context;
    }

    @Override
    public void onPreExecute(){
        super.onPreExecute();
        sharedPreferences = context.getSharedPreferences(context.getString(R.string.shared_preference_name), Context.MODE_PRIVATE);

        hideLoadingDialog();
        showLoadingDialog(R.string.verifying_phone_number);

    }


    @Override
    public UserOtp doInBackground(Object... params) {
        String url = hostname + USER_phone_verification_URI;
//        isComingFromBookingFlow = (boolean)params[2];
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
            mailId = params[1].toString();
            body.put("mobileNumber", phoneNumber);
            body.put("email", mailId);
            body.put("isUser", sharedPreferences.getBoolean(PojoPreferredStrings.POJO_IS_USER.toString(), false));

            HttpEntity<String> entity = new HttpEntity<>(body.toString(), headers);
            ResponseEntity<UserOtp> exchange = restTemplate.exchange(url, HttpMethod.POST, entity, UserOtp.class);
            userOtp = exchange.getBody();

        } catch (ResourceAccessException ex) {
            publishProgress(1);
            ex.printStackTrace();
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }catch (HttpClientErrorException re) {
            if (re.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
                Log.d("pojo", "internal error in user verification :::" + re.getMessage());
                publishProgress(2);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (re.getStatusCode() == HttpStatus.NOT_FOUND) {
                publishProgress(3);
                Log.d("pojo", "no phone number-email combination found :::" + re.getMessage());
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
            if (re.getStatusCode() == HttpStatus.PRECONDITION_FAILED) {
                publishProgress(3);
                Log.d("pojo", "no phone number-email combination found :::" + re.getMessage());
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }catch (Exception ex) {
            ex.printStackTrace();
            Log.d("pojo", "Error in user verification :::" + ex.getMessage());
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
            Log.d("pojo", "user phone verification otp :::" + userOtp.getOtp());

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
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    if (otpET.getText().toString() != null && otpET.getText().toString().equalsIgnoreCase(userOtp.getOtp())) {

                        hideLoadingDialog();
                        Fragment resetPwdFragment = new ResetPasswordFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("verifiedPhoneNumber", phoneNumber);
                        bundle.putString("verifiedMailId", mailId);
                        resetPwdFragment.setArguments(bundle);
                        ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().add(android.R.id.content, resetPwdFragment).commit();

                    } else {
                        showLoadingDialog(R.string.wrong_otp_text);
                        new UserPhoneNumberVerificationAsyncCall.WaitToShowProgress().execute();
                    }
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    showLoadingDialog(R.string.otp_cancel_message);
                    new UserPhoneNumberVerificationAsyncCall.WaitToShowProgress().execute();
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