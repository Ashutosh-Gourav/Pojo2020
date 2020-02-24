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
import com.blisscom.gourava.jaiho.activity.priest.PriestVerificationActivity;
import com.blisscom.gourava.jaiho.activity.user.UserHomeActivity;
import com.fasterxml.jackson.databind.ObjectMapper;

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

/**
 * Created by gourava on 1/25/17.
 */

public class PriestRegistrationAsyncCall extends AsyncTask<Object, Integer, UserOtp> {

    private Context context;
    private Dialog progressDialog;

    private ObjectMapper objectMapper;
//        private static String hostname = "http://10.0.2.2:8080/pojo"; // for emulator
    private static String hostname = "http://192.168.43.7:8080/pojo"; //for real mobile, use IP of wifi
//    private static String hostname = "http://192.168.43.98:8080/pojo"; //for real mobile, use IP of wifi
    //    private static String hostname = "http://INBL2-0MPFD57.local:8080/pojo";
//private static String hostname = "http://192.168.1.100:8080/pojo";

    private static final String REGISTRATION_URI = "/svc/api/v1/user/register";
    private static final String otp_confirmation_URI = "/svc/api/v1/user/confirm/otp/mail";
    private boolean isComingFromBookingFlow;
    private String email, name, password, phoneNumber, address;
    private SharedPreferences sharedPreferences;

    public PriestRegistrationAsyncCall(Context context) {
        super();
        this.context = context;
    }

    @Override
    public void onPreExecute() {
        super.onPreExecute();
        showLoadingDialog(R.string.registration_progress_message);
        sharedPreferences = context.getSharedPreferences(context.getString(R.string.shared_preference_name), Context.MODE_PRIVATE);
//        new WaitToShowProgress().execute();
    }


    @Override
    public UserOtp doInBackground(Object... params) {
        String url = hostname + REGISTRATION_URI;
        isComingFromBookingFlow = (boolean)params[4];
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
            email = params[0].toString();
            phoneNumber = params[1].toString();
            name = params[2].toString();
            body.put("email", email);
            body.put("mobileNumber", phoneNumber);
            body.put("fullname", name);
            body.put("registeredAddress", params[3].toString());
            body.put("deviceId", sharedPreferences.getString(PojoPreferredStrings.DEVICE_ID.toString(), null));
            body.put("fcmRegistrationToken", sharedPreferences.getString(PojoPreferredStrings.SAVED_FCM_TOKEN.toString(), null));
//            body.put("address")

            HttpEntity<String> entity = new HttpEntity<>(body.toString(), headers);
            ResponseEntity<UserOtp> exchange = restTemplate.exchange(url, HttpMethod.POST, entity, UserOtp.class);
            userOtp = exchange.getBody();
        } catch (JSONException e) {
            Log.d("cant read response otp", e.getMessage());
        } catch (ResourceAccessException ex) {
            publishProgress(1);
            ex.printStackTrace();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Log.e("error userregrequest", ex.getMessage());

        }catch (HttpClientErrorException re) {
            if (re.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
                Log.e("pojo", "Error in user reg :::" + re.getMessage());
                publishProgress(2);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (re.getStatusCode() == HttpStatus.FORBIDDEN) {
                publishProgress(3);
                Log.e("pojo", "forbidden in user reg :::" + re.getMessage());
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }catch (Exception ex) {
            ex.printStackTrace();
            publishProgress(2);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Log.e("pojo","user reg went wrong::::::"+ ex.getMessage());
        }
//        hideLoadingDialog();
        return userOtp;
    }

    @Override
    public void onPostExecute(final UserOtp userOtp) {
        super.onPostExecute(userOtp);
        if (userOtp != null && userOtp.getOtp() != null) {
            Log.d("pojo", "user registration otp :::" + userOtp.getOtp());

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
                        new PriestRegistrationAsyncCall.ConfirmOtpAndMAilAsyncCall().execute();
//                        sharedPreferences = context.getSharedPreferences(context.getString(R.string.shared_preference_name), Context.MODE_PRIVATE);
//                        SharedPreferences.Editor editor = sharedPreferences.edit();
//                        editor.putString(PojoPreferredStrings.POJO_USER_PHONE_NUMBER.toString(), phoneNumber);
//                        //editor.putBoolean(PojoPreferredStrings.IS_USER_REGISTRATION_OTP_CONFIRMED.toString(), true);
//                        editor.commit();
//                        if(!isComingFromBookingFlow) {
//                            Intent intent = new Intent(context, UserHomeActivity.class);
//                            context.startActivity(intent);
//                            ((Activity) context).finish();
//                        }else {
//                            ((Activity) context).finish();
//                        }
                    } else {
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
            showLoadingDialog(R.string.already_registered_user);
        }

    }


    private void showLoadingDialog(int messageId) {
        if (progressDialog == null || !progressDialog.isShowing()) {
            progressDialog = new Dialog(context, android.R.style.Theme_Translucent);
            progressDialog.setContentView(R.layout.fragment_progress_dialog);
            TextView textView = (TextView) progressDialog.findViewById(R.id.progress_dialog_tv);
            textView.setText(context.getString(messageId));
            progressDialog.setCancelable(false);
            progressDialog.show();
        } else {
            TextView textView = (TextView) progressDialog.findViewById(R.id.progress_dialog_tv);
            textView.setText(context.getString(messageId));
        }
    }

    private void hideLoadingDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private class WaitToShowProgress extends AsyncTask {

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
        protected void onPostExecute(Object ob) {
            hideLoadingDialog();
        }
    }

    private class ConfirmOtpAndMAilAsyncCall extends AsyncTask <Object, Integer, Integer>{

        @Override
        public void onPreExecute(){
            super.onPreExecute();
            showLoadingDialog(R.string.confirming_otp);
        }
        @Override
        protected Integer doInBackground(Object[] params) {
            String url = hostname + otp_confirmation_URI;
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Accept-Language", "en-US,en;q=0.8");
            headers.set("Connection", "Close");
            Integer statuscode = null ;
            try {
                JSONObject body = new JSONObject();
                body.put("mobileNumber", phoneNumber);
                body.put("emailId", email);
                body.put("event", "priestRegistration");

                HttpEntity<String> entity = new HttpEntity<>(body.toString(), headers);
                ResponseEntity exchange = restTemplate.exchange(url, HttpMethod.POST, entity, Void.class);
                statuscode = exchange.getStatusCode().value();

            } catch (JSONException e) {
                Log.e("pojo", "cant read void priest reg-response::::::"+e.getMessage());
            } catch (ResourceAccessException ex) {
                publishProgress(1);
                ex.printStackTrace();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Log.e("pojo","error mail-otp priest reg-request::::::"+ ex.getMessage());

            } catch (Exception ex) {
                ex.printStackTrace();
                publishProgress(2);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Log.e("pojo", "mail-otp went wrong priest reg::::::"+ex.getMessage());

            }
            return statuscode;
        }

        @Override
        protected void onPostExecute(Integer statuscode) {
            if(statuscode == HttpStatus.NO_CONTENT.value()){
                showLoadingDialog(R.string.registration_success_message);
//                new WaitToShowProgress().execute();
                sharedPreferences = context.getSharedPreferences(context.getString(R.string.shared_preference_name), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(PojoPreferredStrings.POJO_PRIEST_PHONE_NUMBER.toString(), phoneNumber);
                editor.putString(PojoPreferredStrings.POJO_PRIEST_EMAIL.toString(), email);
                editor.commit();
//                if(!isComingFromBookingFlow) {
//                    hideLoadingDialog();
                    Intent intent = new Intent(context, PriestVerificationActivity.class);
                    context.startActivity(intent);
                    ((Activity) context).finish();
//                }else {
//                    hideLoadingDialog();
//                    ((Activity) context).finish();
//                }
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

        }
    }
}
