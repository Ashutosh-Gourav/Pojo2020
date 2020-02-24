package com.blisscom.gourava.jaiho.model;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import com.blisscom.gourava.jaiho.R;
import com.blisscom.gourava.jaiho.fragment.UserHistoryView;
import com.blisscom.gourava.jaiho.infra.PojoDbHelper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by gourava on 1/9/17.
 */

public class UserPoojaBookingHistoryAsyncCall extends AsyncTask<Object, Integer, List<UserPoojaBookingHistoryItem>> {

    private Context context;
    private Dialog progressDialog;
    private ObjectMapper mapper = new ObjectMapper();
    private UserHistoryView userHistoryView;

//        private static String hostname = "http://10.0.2.2:8080/pojo"; // for emulator
    private static String hostname = "http://192.168.43.7:8080/pojo"; //for real mobile, use IP of wifi
    //    private static String hostname = "http://INBL2-0MPFD57.local:8080/pojo";
//    private static String hostname = "http://192.168.43.98:8080/pojo"; //for real mobile, use IP of wifi
//private static String hostname = "http://192.168.1.100:8080/pojo";

    private static final String USER_POOJA_BOOKING_HISTORY_URI = "/svc/api/booking/user/history";
    private SharedPreferences sharedPreferences;

    public UserPoojaBookingHistoryAsyncCall(Context context, UserHistoryView userHistoryView){
        super();
        this.userHistoryView = userHistoryView;
        this.context = context;
    }

    @Override
    public void onPreExecute(){
        super.onPreExecute();
        showLoadingDialog(R.string.getting_all_bookings_history);
        sharedPreferences = context.getSharedPreferences(context.getString(R.string.shared_preference_name), Context.MODE_PRIVATE);
    }


    @Override
    public List<UserPoojaBookingHistoryItem> doInBackground(Object... params) {
        String url = hostname + USER_POOJA_BOOKING_HISTORY_URI;
//        UserOtp userOtp = new UserOtp();
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Accept-Language", "en-US,en;q=0.8");
        headers.set("Connection", "Close");
        List<UserPoojaBookingHistoryItem> userPoojaBookingHistoryItemList = new ArrayList<>();
        try {
            JSONObject body = new JSONObject();
            body.put("userId", sharedPreferences.getString(PojoPreferredStrings.POJO_USER_PHONE_NUMBER.toString(), null));

            HttpEntity<String> entity = new HttpEntity<>(body.toString(), headers);
            ResponseEntity<ArrayList> exchange = restTemplate.exchange(url, HttpMethod.POST, entity, ArrayList.class);
            userPoojaBookingHistoryItemList =  mapper.convertValue(exchange.getBody(), new TypeReference<List<UserPoojaBookingHistoryItem>>() {
            });
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
                Log.d("pojo", "internal error in update booking history :::" + re.getMessage());
                publishProgress(2);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }catch (Exception ex) {
            ex.printStackTrace();
            Log.d("pojo", "Error in History update :::" + ex.getMessage());
            publishProgress(2);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return userPoojaBookingHistoryItemList;
    }

    @Override
    public void onPostExecute(List<UserPoojaBookingHistoryItem> historyItems){
        super.onPostExecute(historyItems);
        if(!(historyItems == null || historyItems.isEmpty())){
            hideLoadingDialog();
            userHistoryView.showHistoryItems(historyItems);
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
}
