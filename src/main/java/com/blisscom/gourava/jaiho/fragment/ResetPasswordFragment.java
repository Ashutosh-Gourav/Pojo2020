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
import com.blisscom.gourava.jaiho.model.ResetPasswordAsyncCall;

/**
 * Created by gourava on 1/7/17.
 */

public class ResetPasswordFragment extends Fragment implements View.OnClickListener{

    private EditText confPwdET, newPwdET;
    private Button changePwdButton;
    @Override
    public View onCreateView(LayoutInflater layoutInflater,  ViewGroup viewGroup, Bundle bundle){
        super.onCreateView(layoutInflater, viewGroup, bundle);
        return layoutInflater.inflate(R.layout.fragment_reset_password, viewGroup, false);
    }

    @Override
    public void onViewCreated(View view, Bundle bundle){
        super.onViewCreated(view, bundle);
        newPwdET = (EditText)view.findViewById(R.id.new_pwd_et);
//        newPwdET.setOnClickListener(this);
        confPwdET = (EditText)view.findViewById(R.id.conf_pwd_et);
//        confPwdET.setOnClickListener(this);
        changePwdButton = (Button)view.findViewById(R.id.submit_pwd_button);
        changePwdButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.submit_pwd_button:
                if(newPwdET.getText() == null || newPwdET.getText().toString().isEmpty() || newPwdET.getText().toString().length()>15) {
                    showErrorDialogMessage("New Password can not be empty and can not have length more than 15.");
                }else if(confPwdET.getText() == null || confPwdET.getText().toString().isEmpty()|| confPwdET.getText().toString().length()>15) {
                    showErrorDialogMessage("Confirm Password can not be empty and can not have length more than 15.");
                }else if(!(newPwdET.getText().toString()).equals(confPwdET.getText().toString())){
                    showErrorDialogMessage("Both password entries do not match.");
                }else {
                    Object[] params = {getArguments().getString("verifiedPhoneNumber"), getArguments().getString("verifiedMailId"), newPwdET.getText().toString()};
                    ResetPasswordAsyncCall resetPasswordAsyncCall = new ResetPasswordAsyncCall(getContext());
                    resetPasswordAsyncCall.execute(params);
                }
                break;
        }
    }

    private void showErrorDialogMessage(String errorMessage){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
}
