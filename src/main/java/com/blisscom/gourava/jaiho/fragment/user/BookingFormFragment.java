package com.blisscom.gourava.jaiho.fragment.user;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.blisscom.gourava.jaiho.R;
import com.blisscom.gourava.jaiho.activity.MapsActivity;
import com.blisscom.gourava.jaiho.activity.user.LoginWhileBookingActivity;
import com.blisscom.gourava.jaiho.activity.user.RegisterWhileBookingActivity;
import com.blisscom.gourava.jaiho.activity.user.UserHomeActivity;
import com.blisscom.gourava.jaiho.model.PojoGpsTracker;
import com.blisscom.gourava.jaiho.model.PojoPreferredStrings;
import com.blisscom.gourava.jaiho.model.PoojaDetails;
import com.blisscom.gourava.jaiho.model.UserPoojaBookingAsyncCall;
import com.blisscom.gourava.jaiho.model.UserPoojaBookingRequest;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by gourava on 12/27/16.
 */

public class BookingFormFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener{

    public static final String tag = "BookingFormFragment";
    private LinearLayout timeLL, dateLL;
    private TextView timeTV, dateTV;
    private TextView poojaStreetAddressTV, formTitlePoojaNameTV, prepDetailsTV;
    private String[] defaultCities, defaultLanguages;
    private SharedPreferences sharedPreference;
    private Spinner cSpinner, lSpinner, prepSpinner;
    private String selectedCityName, selectedLanguage, preparer;
    private Timestamp poojaStartTimestamp, currentTimestamp;
    private Button proceedButton;
    private String dateTvActualJavaDate;
    private ImageView getLocationIV;
    private PoojaDetails selectedPoojaDetails;
    private EditText dakshinaET;
    private UserPoojaBookingRequest userPoojaBookingRequest;
    private String currentTimeInFormat, poojaStartTimeInFormat;

    private static final int MY_PERMISSIONS_REQUEST_GPS = 0;

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle){
        super.onCreateView(layoutInflater, viewGroup, bundle);
        sharedPreference = getContext().getSharedPreferences(getString(R.string.shared_preference_name), Context.MODE_PRIVATE);
        return layoutInflater.inflate(R.layout.fragment_booking_form, viewGroup, false);
    }

    @Override
    public void onViewCreated(View view, Bundle bundle){
        super.onViewCreated(view, bundle);
        timeLL = (LinearLayout)view.findViewById(R.id.time_ll);
        timeLL.setOnClickListener(this);
        dateLL = (LinearLayout)view.findViewById(R.id.date_ll);
        dateLL.setOnClickListener(this);
        timeTV = (TextView)view.findViewById(R.id.time_tv);
        dateTV = (TextView)view.findViewById(R.id.date_tv);
        dakshinaET = (EditText)view.findViewById(R.id.dakshina_et);
        prepareSpinners(view);

        poojaStreetAddressTV = (TextView)view.findViewById(R.id.pooja_street_tv);
        proceedButton = (Button)view.findViewById(R.id.proceed_booking_button);
        proceedButton.setOnClickListener(this);
        getLocationIV = (ImageView) view.findViewById(R.id.booking_form_address_iv);
        getLocationIV.setOnClickListener(this);
        formTitlePoojaNameTV = (TextView)view.findViewById(R.id.booking_form_title_pooja_tv);
        prepDetailsTV = (TextView)view.findViewById(R.id.prep_details_tv);
        prepDetailsTV.setOnClickListener(this);

        Bundle bundle1 = getArguments();
        selectedPoojaDetails = (PoojaDetails)bundle1.get("selectedPoojaDetails");
        formTitlePoojaNameTV.setText(selectedPoojaDetails.getName());
    }

    public void setAddressFromMapToBookingForm(String addressFromMapToTV){
        poojaStreetAddressTV.setText(addressFromMapToTV);
    }

    @Override
    public void onResume(){
        super.onResume();
        setAddressFromMapToBookingForm(sharedPreference.getString(PojoPreferredStrings.LAST_FULL_ADDRESS_TAKEN_FROM_MAP.toString(), null));
    }

    @Override
    public void onClick(View v) {
        String validationErrorMessage = null;

        switch (v.getId()){
            case R.id.proceed_booking_button:
                boolean isEverythingValid = false;
                if(dateTV.getText() == null || dateTV.getText().toString().isEmpty()){
                    validationErrorMessage = getString(R.string.date_validation_error);
                    showErrorDialogMessage(validationErrorMessage);
                }else if(timeTV.getText() == null || timeTV.getText().toString().isEmpty()){
                    validationErrorMessage = getString(R.string.time_empty_error);
                    showErrorDialogMessage(validationErrorMessage);
                }else {
                    Calendar calendar = Calendar.getInstance();
                    Date today = calendar.getTime();
                    currentTimestamp = new Timestamp(today.getTime());
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.S");
                    currentTimeInFormat = formatter.format(currentTimestamp.getTime());
                    Log.d("currentTimeInFormat=", currentTimeInFormat);

                    String selectedDateAndTime = dateTV.getText().toString() + " " + timeTV.getText().toString() + ":00.0";
                    poojaStartTimeInFormat = selectedDateAndTime;
//                    String poojaStartTimeInFormat = formatter.format(selectedDateAndTime);
                    Log.d("selectedDateAndTime=", selectedDateAndTime);
//                    Log.d("poojaStartTimeInFormat=", poojaStartTimeInFormat);

                    try {
                        poojaStartTimestamp = new Timestamp(formatter.parse(selectedDateAndTime).getTime());
                    } catch(ParseException e){
                        e.printStackTrace();
                    }


                    long timeDiff = poojaStartTimestamp.getTime() - currentTimestamp.getTime();
                    Log.d("timeDiff is =", ""+timeDiff);
                    Log.d("poojaStartTimestamp=", "poojaStartTimestamp"+poojaStartTimestamp);
                    Log.d("currentTimestamp=", "currentTimestamp"+currentTimestamp);

                    Log.d("poojaStartTimestamp=", "poojaStartTimestamp"+new Date(poojaStartTimestamp.getTime()));
                    if(timeDiff < 0){
                        validationErrorMessage = getString(R.string.time_in_past_error);
                        showErrorDialogMessage(validationErrorMessage);

                    }else if(timeDiff < 3000*1000){
                        validationErrorMessage = getString(R.string.time_improper_error);
                        showErrorDialogMessage(validationErrorMessage);

                    }else if(poojaStreetAddressTV.getText()== null || poojaStreetAddressTV.getText().toString().trim().isEmpty()){
                        validationErrorMessage = getString(R.string.booking_address_empty_error);
                        showErrorDialogMessage(validationErrorMessage);

                    }else if(poojaStreetAddressTV.getText().toString().equalsIgnoreCase(getString(R.string.booking_address_invalid_text))){
                        validationErrorMessage = getString(R.string.booking_address_invalid_error);
                        showErrorDialogMessage(validationErrorMessage);

                    }
//                    else if(poojaStreetAddressTV.getText().toString().equalsIgnoreCase(getString(R.string.booking_address_invalid_text))
//                    || (!(poojaStreetAddressTV.getText().toString().contains("Bangalore")
//                            ||poojaStreetAddressTV.getText().toString().contains("Bengaluru")
//                            || poojaStreetAddressTV.getText().toString().contains("bangalore")
//                            || poojaStreetAddressTV.getText().toString().contains("bengaluru")))) {
//                        validationErrorMessage = getString(R.string.booking_address_invalid_error);
//                        showErrorDialogMessage(validationErrorMessage);
//
//                    }
                    else if(dakshinaET.getText() == null || dakshinaET.getText().toString().trim().isEmpty()
                            || Integer.parseInt(dakshinaET.getText().toString())<selectedPoojaDetails.getCost()/2 || Integer.parseInt(dakshinaET.getText().toString())>selectedPoojaDetails.getCost()*2) {
                        validationErrorMessage = getString(R.string.dakshina_amount_error) + "Standard offered dakshina is "+ selectedPoojaDetails.getCost() +" Rs.";
                        showErrorDialogMessage(validationErrorMessage);

                    }else if(lSpinner.getSelectedItem().toString().equalsIgnoreCase(getString(R.string.language_spinner_hint))){
                        validationErrorMessage = getString(R.string.language_not_selected_error);
                        showErrorDialogMessage(validationErrorMessage);

                    }else if(prepSpinner.getSelectedItem().toString().equalsIgnoreCase(getString(R.string.prep_spinner_hint))){
                        validationErrorMessage = getString(R.string.prep_not_selected_error);
                        showErrorDialogMessage(validationErrorMessage);

                    }else if(prepSpinner.getSelectedItemPosition() == 1 && timeDiff < 3*3600*1000){
                        validationErrorMessage = getString(R.string.prep_within_limit_hrs_gap_error);
                        showErrorDialogMessage(validationErrorMessage);
                    }else {
                        isEverythingValid = true;
                    }
                    String costSummaryMessage = null;
                    if(isEverythingValid == true){
                        if(prepSpinner.getSelectedItem().toString().equalsIgnoreCase(getString(R.string.prep_by_priest))) {
                            costSummaryMessage = "You will be paying \n\tDakshina :" + dakshinaET.getText().toString() + " Rs" +
                                    " \n\t and preparation Cost: " + selectedPoojaDetails.getPrepCost() + " Rs" +
                                    " \n\t Total cost:" + (Integer.parseInt(dakshinaET.getText().toString()) + selectedPoojaDetails.getPrepCost()) + " Rs only.";
                        }else {
                            costSummaryMessage = "You will be paying \n\tDakshina :" + dakshinaET.getText().toString() + " Rs" +
                                    " \n\t Total cost:" + dakshinaET.getText().toString()+ " Rs only.";
                        }
                        showCostSummaryMessage(costSummaryMessage);
//                        Fragment costFragment = new ProceedWithCostFragment();
//                        Bundle bundle = new Bundle();
//                        bundle.putSerializable("requestedPoojaDetails", selectedPoojaDetails);
//                        bundle.putString("poojaAddress", poojaStreetAddressTV.getText().toString());
//                        bundle.putString("poojaLanguage", lSpinner.getSelectedItem().toString());
//                        bundle.putString("prepBy", prepSpinner.getSelectedItem().toString());
//                        bundle.putString("userSelectedDateAndTime", dateTV.getText().toString() +" , "+ timeTV.getText().toString());
//                        bundle.putString("selectedDateAndTimeForStamp", dateTV.getText().toString() + " " + timeTV.getText().toString() + ":00.0");
//                        costFragment.setArguments(bundle);
//                        getFragmentManager().beginTransaction().addToBackStack(null).replace(android.R.id.content, new ProceedWithCostFragment()).commit();
                    }
                }
//                Sarva Bhuta Hita
                break;
            case R.id.time_ll:
                showTimePickerDialogAndGetTime(v);
                break;
            case R.id.date_ll:
                showDatePickerDialogAndGetTime();
                break;
            case R.id.booking_form_address_iv:
                if ( Build.VERSION.SDK_INT >= 23 &&
                        ContextCompat.checkSelfPermission( getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission( getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    if (!sharedPreference.getBoolean(PojoPreferredStrings.IS_DO_NOT_SHOW_GPS_REQUEST_CHECKED.toString(), false)) {
                        alertDialogForPermissionRequest(R.string.location_info_permission_title_1,R.string.location_info_permission_message_1);
                    }
                    else {
                        alertDialogForPermissionRequest(R.string.location_info_permission_title_2,R.string.location_info_permission_message_2);
                    }
                }else {
                    getAddressFromMap();
                }
                break;
            case R.id.prep_details_tv:
                showPoojaPreparationDetails();
                break;

        }
    }

    private void alertDialogForPermissionRequest(int title, int message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        TextView titleView = new TextView(getContext());
        titleView.setGravity(Gravity.CENTER_HORIZONTAL);
        titleView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        titleView.setPadding(0, 10, 0, 10);
        titleView.setText(title);
        titleView.setTextSize(20);
        titleView.setTextColor(Color.RED);
        titleView.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        builder.setCustomTitle(titleView);

        TextView messageView = new TextView(getContext());
        messageView.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER);
        messageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        messageView.setText(getResources().getString(message));
        messageView.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
        messageView.setTextColor(Color.BLUE);
        messageView.setPadding(30, 10, 30, 10);
        messageView.setTextSize(15);
        builder.setView(messageView);
        if (!sharedPreference.getBoolean(PojoPreferredStrings.IS_DO_NOT_SHOW_GPS_REQUEST_CHECKED.toString(), false)) {
            builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_GPS);
                }
            });
        }else {
            builder.setNeutralButton(getContext().getResources().getString(R.string.alert_later_button_text), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_GPS);
                }
            });
            builder.setPositiveButton(getContext().getResources().getString(R.string.alert_settings_button_text), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                    intent.setData(uri);
                    getContext().startActivity(intent);
                }
            });
        }

        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if ( Build.VERSION.SDK_INT >= 23) {
            SharedPreferences.Editor editor = sharedPreference.edit();
            if (getActivity().shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                editor.putBoolean(PojoPreferredStrings.IS_DO_NOT_SHOW_GPS_REQUEST_CHECKED.toString(), false);
            } else {
                editor.putBoolean(PojoPreferredStrings.IS_DO_NOT_SHOW_GPS_REQUEST_CHECKED.toString(), true);
            }
            editor.commit();
        }
        if (requestCode == MY_PERMISSIONS_REQUEST_GPS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getAddressFromMap();
            }
        }
    }

    private void getAddressFromMap(){
         LocationManager locationManager = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);

        // getting GPS status
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // getting network status
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if(!isGPSEnabled && !isNetworkEnabled) {
            showGpsSettingsAlert();
        }else {
            Intent intent = new Intent(getContext(), MapsActivity.class);
            startActivity(intent);
        }
    }

    private void showGpsSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        alertDialog.setTitle("GPS in settings");
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                getContext().startActivity(intent);
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
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

    private void showCostSummaryMessage(String errorMessage){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.fragment_dialog_message, null);
        TextView messageTV = (TextView)view.findViewById(R.id.message_tv);
        messageTV.setVisibility(View.VISIBLE);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(sharedPreference.getString(PojoPreferredStrings.POJO_USER_PHONE_NUMBER.toString(), null) == null
                        || sharedPreference.getString(PojoPreferredStrings.POJO_USER_PHONE_NUMBER.toString(), null).isEmpty()){
//                if(true){
                    showNotLoggedInUserMessage();
                }else {
                    // TODO: 1/2/17 make async call to getPoojaBookingRequestApi
                    //// TODO: 1/2/17 if success, go to history page by popping up the Booking formFragment , on sucessfull registration

                    userPoojaBookingRequest = new UserPoojaBookingRequest();
                    userPoojaBookingRequest.setPoojaAddressLongitude(sharedPreference.getFloat(PojoPreferredStrings.LAST_LONGITUDE_TAKEN_FROM_MAP.toString(), 0.0f));
                    userPoojaBookingRequest.setPoojaAddress(sharedPreference.getString(PojoPreferredStrings.LAST_FULL_ADDRESS_TAKEN_FROM_MAP.toString(), null));
                    userPoojaBookingRequest.setPoojaAddressLatitude(sharedPreference.getFloat(PojoPreferredStrings.LAST_LATTITUDE_TAKEN_FROM_MAP.toString(), 0.0f));
                    userPoojaBookingRequest.setPoojaBookingStatus("pending");
                    userPoojaBookingRequest.setPoojaLanguage(lSpinner.getSelectedItem().toString());
                    userPoojaBookingRequest.setUserId(sharedPreference.getString(PojoPreferredStrings.POJO_USER_PHONE_NUMBER.toString(), null));
                    userPoojaBookingRequest.setPoojaId(selectedPoojaDetails.getId());
                    userPoojaBookingRequest.setOfferedPrice(Integer.parseInt(dakshinaET.getText().toString()));
                    String prepby = "user";
                    if(prepSpinner.getSelectedItem().toString().equalsIgnoreCase(getString(R.string.prep_by_user))){
                        prepby = "priest";
                    }
                    userPoojaBookingRequest.setPrepBy(prepby);
                    userPoojaBookingRequest.setBookingRequestTime(currentTimeInFormat);
                    userPoojaBookingRequest.setPoojaStartTime(poojaStartTimeInFormat);
                    Calendar calendar = Calendar.getInstance();
                    userPoojaBookingRequest.setPoojaBookingRequestId("PSN"+(Long.parseLong(sharedPreference.getString(PojoPreferredStrings.POJO_USER_PHONE_NUMBER.toString(), null))+12121212)+""+calendar.getTimeInMillis());
                    Object[] params = {userPoojaBookingRequest};
                    UserPoojaBookingAsyncCall userPoojaBookingAsyncCall = new UserPoojaBookingAsyncCall(getContext());
                    userPoojaBookingAsyncCall.execute(params);
//                    getActivity().finish();
                }
            }
        });
        messageTV.setText(errorMessage);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.setCancelable(true);
        dialog.show();
    }

    private void showPoojaPreparationDetails(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.fragment_dialog_message, null);
        TextView messageTV = (TextView)view.findViewById(R.id.message_tv);
        messageTV.setVisibility(View.VISIBLE);
        messageTV.setText(selectedPoojaDetails.getProcedure()+"\n Items required to conduct pooja: "+selectedPoojaDetails.getPoojaItems());
        builder.setView(view);
        builder.setTitle("Arrange for pooja");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setCancelable(true);
        dialog.show();
    }

    private void showNotLoggedInUserMessage(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.fragment_dialog_message, null);
        TextView messageTV = (TextView)view.findViewById(R.id.message_tv);
        messageTV.setVisibility(View.VISIBLE);
        messageTV.setText("For booking you need to have an account.");
        builder.setView(view);
        builder.setPositiveButton("I have an account", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

//                getFragmentManager().beginTransaction().addToBackStack(null).replace(android.R.id.content, new LoginFormFragment()).commit();
                Intent intent = new Intent(getContext(), LoginWhileBookingActivity.class);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("Create my account", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

//                getFragmentManager().beginTransaction().addToBackStack(null).replace(android.R.id.content, new UserRegistrationFragment()).commit();
                Intent intent = new Intent(getContext(), RegisterWhileBookingActivity.class);
                startActivity(intent);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setCancelable(true);
        dialog.show();
    }

    public void showTimePickerDialogAndGetTime(View v) {
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                timeTV.setText( selectedHour + ":" + selectedMinute );
            }
        }, hour, minute, true);//Yes 24 hour time
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }

    public void showDatePickerDialogAndGetTime(){

        Calendar currentDate = Calendar.getInstance();
        int day = currentDate.get(Calendar.DAY_OF_MONTH);
        int month = currentDate.get(Calendar.MONTH);
        int year = currentDate.get(Calendar.YEAR);
        DatePickerDialog datePickerDialog;
        datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                dateTvActualJavaDate = dayOfMonth+"/"+monthOfYear+"/"+year;
                dateTV.setText(dayOfMonth+"/"+(monthOfYear+1)+"/"+year);
            }
        }, year, month, day);
        datePickerDialog.setTitle("Select Date");
        datePickerDialog.show();
    }

    private void prepareSpinners(View view) {
        lSpinner = (Spinner)view.findViewById(R.id.lang_spinner);
        prepSpinner = (Spinner)view.findViewById(R.id.prep_spinner);
        defaultLanguages = getResources().getStringArray(R.array.default_languages);


        List<String> lList = new ArrayList<>();
        lList.add(getResources().getString(R.string.language_spinner_hint));
        for (String l : defaultLanguages) {
            lList.add(l);
        }
        ArrayAdapter<String> lAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, lList);
        lAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        lSpinner.setAdapter(lAdapter);
        lSpinner.setOnItemSelectedListener(this);

        List<String> pList = new ArrayList<>();
        pList.add(getString(R.string.prep_spinner_hint));
        pList.add(getString(R.string.prep_by_priest));
        pList.add(getString(R.string.prep_by_user));
        ArrayAdapter<String> pAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, pList);
        pAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        prepSpinner.setAdapter(pAdapter);
        prepSpinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        switch (parent.getId()){

            case R.id.lang_spinner:
                if (position == 0) {
                    ((TextView) parent.getChildAt(0)).setTextColor(Color.parseColor("#808080"));
                }
                selectedLanguage = parent.getItemAtPosition(position).toString();
                break;

            case R.id.prep_spinner:
                if (position == 0) {
                    ((TextView) parent.getChildAt(0)).setTextColor(Color.parseColor("#808080"));
                }
                preparer = parent.getItemAtPosition(position).toString();
                break;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

//        qSpinner.setPrompt(getResources().getString(R.string.hint_security_question));

    }

}
