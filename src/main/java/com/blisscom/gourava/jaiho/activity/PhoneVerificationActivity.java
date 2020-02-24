package com.blisscom.gourava.jaiho.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.blisscom.gourava.jaiho.R;
import com.blisscom.gourava.jaiho.model.UserPhoneNumberVerificationAsyncCall;

public class PhoneVerificationActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_phone_verification);
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

    @Override
    public void onBackPressed(){
        super.onBackPressed();
//        finish();
    }
}
