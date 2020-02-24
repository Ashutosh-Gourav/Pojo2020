package com.blisscom.gourava.jaiho.model;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

/**
 * Created by gourava on 12/16/16.
 */

public class AsyncApiCalls {
    private  Context context;
    private  Dialog progressDialog;
    private  ObjectMapper objectMapper;

    //    private static String hostname = "http://10.0.2.2:8080/pojo"; // for emulator
    private static String hostname = "http://192.168.43.7:8080/pojo"; //for real mobile, use IP of wifi
    //    private static String hostname = "http://INBL2-0MPFD57.local:8080/pojo";
        private static final String REGISTRATION_URI = "/svc/api/v1/user/register";

    public AsyncApiCalls(Context context){
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public class LoginAsyncCall extends AsyncTask<Long, Integer, Integer> {

        @Override
        public void onPreExecute(){
            super.onPreExecute();
            showLoadingDialog(R.string.login_progress_message);

        }


        @Override
        public Integer doInBackground(Long... params) {
            try {
                Thread.sleep(params[0]);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public void onPostExecute(Integer progress){
            super.onPostExecute(progress);
            if(true){
                //todo check status: if success show otp dialog else show error message and then hide spinner
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
// Add the buttons
                LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = layoutInflater.inflate(R.layout.fragment_otp_dialog, null);
                TextView otpTV = (TextView)view.findViewById(R.id.otp_tv);
                otpTV.setText(context.getString(R.string.otp_title));
                EditText otpET = (EditText)view.findViewById(R.id.otp_et);
                otpET.setHint(context.getString(R.string.otp_hint));
                otpET.setMaxLines(1);
                otpET.setInputType(InputType.TYPE_CLASS_NUMBER);
                int maxLength = 4;
                otpET.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength)});
                builder.setView(view);
//                builder.setTitle(R.string.otp_title);
//                builder.setIcon(R.drawable.app_icon);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //// TODO: 12/12/16 poll and read message from messages and confirm it
                        // todo: if incorrect show error message and hide spinner
                        //// TODO: 12/12/16  if correct go to home activity and Aync call otp confirmation api
                        if(true){
                            Intent intent = new Intent(context, UserHomeActivity.class);
                            context.startActivity(intent);
                            ((Activity) context).finish();
                        }else{
                            showLoadingDialog(R.string.wrong_otp_text);
                            new WaitToShowProgress().execute();
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        showLoadingDialog(R.string.otp_cancel_message);
                        new WaitToShowProgress().execute();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.setCancelable(false);
                dialog.show();

            }
            else {
                showLoadingDialog(R.string.create_account_message);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
//            hideLoadingDialog();

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

    private  void hideLoadingDialog() {
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
//            hideLoadingDialog();
        }
    }


    public class UserRegistrationAsyncCall extends AsyncTask<String, Integer, UserOtp> {

        @Override
        public void onPreExecute(){
            super.onPreExecute();
            showLoadingDialog(R.string.login_progress_message);

        }


        @Override
        public UserOtp doInBackground(String... params) {
            String url = hostname + REGISTRATION_URI;

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
                body.put("email", params[0].toString());
                body.put("password", params[1].toString());
                body.put("mobileNumber", params[2].toString());
                body.put("fullname", params[3].toString());



                HttpEntity<String> entity = new HttpEntity<>(body.toString(), headers);
                ResponseEntity<UserOtp> exchange = restTemplate.exchange(url, HttpMethod.POST, entity, UserOtp.class);
                userOtp = exchange.getBody();
            } catch (JSONException e) {
                Log.d("cant read response otp", e.getMessage());
            } catch (Exception ex){
                ex.printStackTrace();
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return userOtp;
        }

        @Override
        public void onPostExecute(final UserOtp userOtp){
            super.onPostExecute(userOtp);
            if(userOtp != null){
                //todo check status: if success show otp dialog else show error message and then hide spinner
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
// Add the buttons
                LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = layoutInflater.inflate(R.layout.fragment_otp_dialog, null);
                TextView otpTV = (TextView)view.findViewById(R.id.otp_tv);
                otpTV.setText(context.getString(R.string.otp_title));
                final EditText otpET = (EditText)view.findViewById(R.id.otp_et);
                otpET.setHint(context.getString(R.string.otp_hint));
                otpET.setMaxLines(1);
                otpET.setInputType(InputType.TYPE_CLASS_NUMBER);
                int maxLength = 4;
                otpET.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength)});
                builder.setView(view);
//                builder.setTitle(R.string.otp_title);
//                builder.setIcon(R.drawable.app_icon);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //// TODO: 12/12/16 poll and read message from messages and confirm it
                        // todo: if incorrect show error message and hide spinner
                        //// TODO: 12/12/16  if correct go to home activity and Async call otp confirmation api
                        if(otpET.getText().toString() != null && otpET.getText().toString().equalsIgnoreCase(userOtp.getOtp())){
                            showLoadingDialog(R.string.registration_success_message);
                            new AsyncApiCalls.WaitToShowProgress().execute();
                            Intent intent = new Intent(context, UserHomeActivity.class);
                            context.startActivity(intent);
                            ((Activity) context).finish();
                        }else{
                            showLoadingDialog(R.string.wrong_otp_text);
                            new AsyncApiCalls.WaitToShowProgress().execute();
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        showLoadingDialog(R.string.otp_cancel_message);
                        new AsyncApiCalls.WaitToShowProgress().execute();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.setCancelable(false);
                dialog.show();

            }
            else {
                showLoadingDialog(R.string.create_account_message);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            hideLoadingDialog();

        }
    }

}
