package com.blisscom.gourava.jaiho.fragment.user;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.blisscom.gourava.jaiho.R;
import com.blisscom.gourava.jaiho.adapter.BookingItemAdapter;
import com.blisscom.gourava.jaiho.adapter.UserBookingHistoryItemAdapter;
import com.blisscom.gourava.jaiho.fragment.UserHistoryView;
import com.blisscom.gourava.jaiho.infra.PojoDbHelper;
import com.blisscom.gourava.jaiho.model.PojoPreferredStrings;
import com.blisscom.gourava.jaiho.model.PoojaDetails;
import com.blisscom.gourava.jaiho.model.UserPoojaBookingHistoryAsyncCall;
import com.blisscom.gourava.jaiho.model.UserPoojaBookingHistoryItem;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gourava on 12/20/16.
 */

public class BookingHistoryFragment extends Fragment implements UserHistoryView {

    private List<UserPoojaBookingHistoryItem> allBookingsList;
    private UserBookingHistoryItemAdapter bookingItemAdapter;
    private ListView bookingsListView;
    private List<PoojaDetails> poojaDetailsList;
    private PojoDbHelper dbHelper;
    private LinearLayout zeroLinearLayout;
    private TextView zeroTextView;
    private SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle){
        super.onCreateView(layoutInflater, viewGroup, bundle);
        sharedPreferences = getContext().getSharedPreferences(getString(R.string.shared_preference_name), Context.MODE_PRIVATE);
        dbHelper = new PojoDbHelper(getContext());

        if(!(sharedPreferences.getString(PojoPreferredStrings.POJO_USER_PHONE_NUMBER.toString(), null) == null
                || sharedPreferences.getString(PojoPreferredStrings.POJO_USER_PHONE_NUMBER.toString(), null).trim().isEmpty())) {
            if(!sharedPreferences.getBoolean(PojoPreferredStrings.IS_DRIVEN_TO_HISTORY.toString(), false)){
                UserPoojaBookingHistoryAsyncCall userPoojaBookingHistoryAsyncCall = new UserPoojaBookingHistoryAsyncCall(getContext(), this);
                userPoojaBookingHistoryAsyncCall.execute();
            }else {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(PojoPreferredStrings.IS_DRIVEN_TO_HISTORY.toString(), false);
                editor.commit();
            }
            allBookingsList = dbHelper.getAllUserPoojaBookingHistoryItem();
        }
        return layoutInflater.inflate(R.layout.fragment_user_booking_history, viewGroup, false);
    }

    @Override
    public void onViewCreated(View view, Bundle bundle){
        super.onViewCreated(view, bundle);
        poojaDetailsList = getPoojaList();
        zeroLinearLayout = (LinearLayout)view.findViewById(R.id.zero_bookings_ll);
        zeroTextView = (TextView)view.findViewById(R.id.zero_state_message_tv);
        bookingsListView = (ListView)view.findViewById(R.id.userBookingsList);

        if(allBookingsList != null && allBookingsList.size()>0){
            zeroLinearLayout.setVisibility(View.GONE);
            bookingItemAdapter = new UserBookingHistoryItemAdapter(getContext(), allBookingsList, poojaDetailsList);
            bookingsListView.setAdapter(bookingItemAdapter);
            bookingsListView.smoothScrollToPosition(allBookingsList.size());
        }else {
            zeroLinearLayout.setVisibility(View.VISIBLE);
            zeroTextView.setText("No booking history Found. If logged in, refresh to see new bookings.");
        }
    }

    @Override
    public void showHistoryItems(List<UserPoojaBookingHistoryItem> historyItems) {
        if(historyItems == null || historyItems.isEmpty()){
//            zeroLinearLayout.setVisibility(View.VISIBLE);
//            zeroTextView.setText("No booking history Found. Please refresh to see new bookings.");
        }else {
            zeroLinearLayout.setVisibility(View.GONE);
            allBookingsList = historyItems;
            dbHelper.deleteAllUserPoojaBookingHistoryItems();
            dbHelper.addUserPoojaBookingHistoryItems(allBookingsList);
            bookingItemAdapter = new UserBookingHistoryItemAdapter(getContext(), allBookingsList, poojaDetailsList);
            bookingsListView.setAdapter(bookingItemAdapter);
            bookingItemAdapter.notifyDataSetChanged();
        }
    }

    protected static <T> List<T> mapJsonToObjectList(T typeDef, String json, Class clazz) throws Exception {
        List<T> list;
        ObjectMapper mapper = new ObjectMapper();
        TypeFactory t = TypeFactory.defaultInstance();
        list = mapper.readValue(json, t.constructCollectionType(ArrayList.class, clazz));
        return list;
    }

    private String convertInputStreamToString(InputStream is) throws IOException {
        String json = null;
        try {
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    private List<PoojaDetails> getPoojaList(){
        List<PoojaDetails>poojaDetailsList=new ArrayList<>();
        String jsonString = null;
        try {
            jsonString = convertInputStreamToString(getContext().getResources().openRawResource(R.raw.allpooja));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try{
            poojaDetailsList=mapJsonToObjectList(new PoojaDetails(),jsonString,PoojaDetails.class);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return poojaDetailsList;
    }

}
