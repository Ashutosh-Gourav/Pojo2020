package com.blisscom.gourava.jaiho.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.blisscom.gourava.jaiho.R;
import com.blisscom.gourava.jaiho.model.UserPhoneNumberVerificationAsyncCall;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by gourava on 1/8/17.
 */

public class PhoneVerificationFragment extends Fragment implements View.OnClickListener {
    private EditText numberET, mailET;
    private Button submitButton;

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle){
        super.onCreateView(layoutInflater, viewGroup, bundle);
        return layoutInflater.inflate(R.layout.fragment_phone_verification, viewGroup, false);
    }

    @Override
    public void onViewCreated(View view, Bundle bundle){
        super.onViewCreated(view, bundle);
        numberET = (EditText)view.findViewById(R.id.forgot_pwd__phoneNumber_et);
        mailET = (EditText)view.findViewById(R.id.forgot_pwd__email_et);
        submitButton = (Button)view.findViewById(R.id.submit_number_button);
        submitButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.submit_number_button:
                if(numberET.getText().toString() == null
                        || numberET.getText().toString().isEmpty()
                        || numberET.getText().toString().length()<10){
                    showErrorDialogMessage("Please enter a valid phone number.");
                }else if(mailET.getText().toString() == null || !isValidEmailAddress(mailET.getText().toString())){
                    showErrorDialogMessage("Please give a valid Email Id.");
                }else {
                    // TODO: 1/7/17 make async call to userPhoneNumberVerification/
                    // todo priestPhoneVerificationAsyncCall based on isUser?????

                    Object[] params = {numberET.getText().toString(), mailET.getText().toString()};
                    UserPhoneNumberVerificationAsyncCall userPhoneNumberVerificationAsyncCall = new UserPhoneNumberVerificationAsyncCall(getContext());
                    userPhoneNumberVerificationAsyncCall.execute(params);
//                    getSupportFragmentManager().beginTransaction().add(android.R.id.content, new ResetPasswordFragment()).commit();
                }
                break;
        }
    }

    private void showErrorDialogMessage(String errorMessage){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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
}
