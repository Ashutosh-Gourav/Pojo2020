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
import com.blisscom.gourava.jaiho.activity.user.UserHomeActivity;
import com.blisscom.gourava.jaiho.infra.PojoDbHelper;

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
 * Created by gourava on 1/6/17.
 */

public class UserPoojaBookingAsyncCall extends AsyncTask<Object, Integer, Integer> {

    private Context context;
    private Dialog progressDialog;
//        private static String hostname = "http://10.0.2.2:8080/pojo"; // for emulator
    private static String hostname = "http://192.168.43.7:8080/pojo"; //for real mobile, use IP of wifi
    //    private static String hostname = "http://INBL2-0MPFD57.local:8080/pojo";
//    private static String hostname = "http://192.168.43.98:8080/pojo"; //for real mobile, use IP of wifi
//private static String hostname = "http://192.168.1.100:8080/pojo";

    private static final String USER_POOJA_BOOKING_URI = "/svc/api/booking/user";
    private UserPoojaBookingRequest userPoojaBookingRequest;

    public UserPoojaBookingAsyncCall(Context context){
        super();
        this.context = context;
    }

    @Override
    public void onPreExecute(){
        super.onPreExecute();
        showLoadingDialog(R.string.booking_in_progress);

    }


    @Override
    public Integer doInBackground(Object... params) {
        String url = hostname + USER_POOJA_BOOKING_URI;
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
            userPoojaBookingRequest = (UserPoojaBookingRequest)params[0];
            body.put("bookingRequestTime", userPoojaBookingRequest.getBookingRequestTime());
            body.put("poojaAddress", userPoojaBookingRequest.getPoojaAddress());
            body.put("poojaAddressLatitude", userPoojaBookingRequest.getPoojaAddressLatitude());
            body.put("poojaAddressLongitude", userPoojaBookingRequest.getPoojaAddressLongitude());
            body.put("poojaBookingRequestId", userPoojaBookingRequest.getPoojaBookingRequestId());
            body.put("poojaBookingStatus", userPoojaBookingRequest.getPoojaBookingStatus());
            body.put("poojaLanguage", userPoojaBookingRequest.getPoojaLanguage());
            body.put("userId", userPoojaBookingRequest.getUserId());
            body.put("poojaStartTime", userPoojaBookingRequest.getPoojaStartTime());
            body.put("poojaId", userPoojaBookingRequest.getPoojaId());
            body.put("prepBy", userPoojaBookingRequest.getPrepBy());
            body.put("offeredPrice", userPoojaBookingRequest.getOfferedPrice());

            HttpEntity<String> entity = new HttpEntity<>(body.toString(), headers);
            ResponseEntity exchange = restTemplate.exchange(url, HttpMethod.PUT, entity, Void.class);
            responseStatusCode = exchange.getStatusCode().value();
        } catch (ResourceAccessException ex) {
            publishProgress(1);
            ex.printStackTrace();
            try {
                Thread.sleep(2000);
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
            if (re.getStatusCode() == HttpStatus.FORBIDDEN) {
                publishProgress(3);
                Log.d("pojo", "Duplicate request for same pooja same day :::" + re.getMessage());
                try {
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }catch (Exception ex) {
            ex.printStackTrace();
            Log.d("pojo", "Error in booking :::" + ex.getMessage());
            publishProgress(2);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return responseStatusCode;
    }

    @Override
    public void onPostExecute(Integer responseStatusCode){
        super.onPostExecute(responseStatusCode);
        if(responseStatusCode != null && responseStatusCode == 204){
            Integer[] params = {responseStatusCode};
            new WaitToShowSuccess().execute(params);
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
            showLoadingDialog(R.string.booking_not_allowed_message);
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

    private class WaitToShowSuccess extends AsyncTask<Integer, Integer, Integer>{
        @Override
        public void onPreExecute(){
            super.onPreExecute();
            showLoadingDialog(R.string.user_pooja_booking_success_message);

        }

        @Override
        public Integer doInBackground(Integer[] params) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.shared_preference_name), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(PojoPreferredStrings.IS_DRIVEN_TO_HISTORY.toString(), true);
            editor.commit();
            UserPoojaBookingHistoryItem poojaBookingHistoryItem = new UserPoojaBookingHistoryItem();
            poojaBookingHistoryItem.setPoojaBookingRequestId(userPoojaBookingRequest.getPoojaBookingRequestId());
            poojaBookingHistoryItem.setUserId(userPoojaBookingRequest.getUserId());
            poojaBookingHistoryItem.setPoojaAddress(userPoojaBookingRequest.getPoojaAddress());
            poojaBookingHistoryItem.setPoojaId(userPoojaBookingRequest.getPoojaId());
            poojaBookingHistoryItem.setBookingRequestTime(userPoojaBookingRequest.getBookingRequestTime());
            poojaBookingHistoryItem.setPoojaStartTime(userPoojaBookingRequest.getPoojaStartTime());
            poojaBookingHistoryItem.setPoojaLanguage(userPoojaBookingRequest.getPoojaLanguage());
            poojaBookingHistoryItem.setPrepBy(userPoojaBookingRequest.getPrepBy());
            poojaBookingHistoryItem.setPoojaAddressLongitude(userPoojaBookingRequest.getPoojaAddressLongitude());
            poojaBookingHistoryItem.setPoojaAddressLatitude(userPoojaBookingRequest.getPoojaAddressLatitude());
            poojaBookingHistoryItem.setPoojaBookingStatus(userPoojaBookingRequest.getPoojaBookingStatus());
            poojaBookingHistoryItem.setPriestName(null);
            poojaBookingHistoryItem.setPriestPhoneNumber(null);
            poojaBookingHistoryItem.setOfferedPrice(userPoojaBookingRequest.getOfferedPrice());
            PojoDbHelper dbHelper = new PojoDbHelper(context);
            dbHelper.addUserPoojaBookingHistoryItem(poojaBookingHistoryItem);

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return (Integer)params[0];
        }

        @Override
        protected void onPostExecute(Integer ob){
//            if(ob == 204){
                hideLoadingDialog();
                ((Activity)context).finish();
//            }
        }
    }
}
