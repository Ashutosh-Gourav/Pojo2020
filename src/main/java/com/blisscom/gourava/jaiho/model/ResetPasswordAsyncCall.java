package com.blisscom.gourava.jaiho.model;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import com.blisscom.gourava.jaiho.R;

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

public class ResetPasswordAsyncCall extends AsyncTask<Object, Integer, Integer> {

    private Context context;
    private Dialog progressDialog;
    private boolean isComingFromBookingFlow;
    private String phoneNumber, password;
//        private static String hostname = "http://10.0.2.2:8080/pojo"; // for emulator
    private static String hostname = "http://192.168.43.7:8080/pojo"; //for real mobile, use IP of wifi
//    private static String hostname = "http://192.168.43.98:8080/pojo"; //for real mobile, use IP of wifi
//private static String hostname = "http://192.168.1.100:8080/pojo";

    //    private static String hostname = "http://INBL2-0MPFD57.local:8080/pojo";
    private static final String USER_RESET_PASSWORD_URI = "/svc/api/v1/user/password/reset";
    private SharedPreferences sharedPreferences;

    public ResetPasswordAsyncCall(Context context){
        super();
        this.context = context;
    }

    @Override
    public void onPreExecute(){
        super.onPreExecute();
        sharedPreferences = context.getSharedPreferences(context.getString(R.string.shared_preference_name), Context.MODE_PRIVATE);
        showLoadingDialog(R.string.updating_password);

    }


    @Override
    public Integer doInBackground(Object... params) {
        String url = hostname + USER_RESET_PASSWORD_URI;
//        UserOtp userOtp = new UserOtp();
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Accept-Language", "en-US,en;q=0.8");
        headers.set("Connection", "Close");
        Integer responseStatusCode = null;
        try {
            JSONObject body = new JSONObject();
            phoneNumber = params[0].toString();
            body.put("mobileNumber", phoneNumber);
            body.put("email", params[1].toString());
            body.put("password", params[2].toString());
            body.put("isUser", sharedPreferences.getBoolean(PojoPreferredStrings.POJO_IS_USER.toString(), false));

            HttpEntity<String> entity = new HttpEntity<>(body.toString(), headers);
            ResponseEntity exchange = restTemplate.exchange(url, HttpMethod.PUT, entity, Void.class);
            responseStatusCode = exchange.getStatusCode().value();
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
                Log.d("pojo", "no phone number found :::" + re.getMessage());
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }catch (Exception ex) {
            ex.printStackTrace();
            Log.d("pojo", "Error in pwd update :::" + ex.getMessage());
            publishProgress(2);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
//        hideLoadingDialog();
        return responseStatusCode;
    }

    @Override
    public void onPostExecute(final Integer code){
        super.onPostExecute(code);
        if (code != null && code == 204) {
//            showLoadingDialog(R.string.password_update_message);
//            new ResetPasswordAsyncCall.WaitToShowProgress().execute();
            hideLoadingDialog();
            ((Activity)context).finish();

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