package com.blisscom.gourava.jaiho.fragment.user;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blisscom.gourava.jaiho.R;
import com.blisscom.gourava.jaiho.model.PoojaDetails;
import com.blisscom.gourava.jaiho.model.UserPoojaBookingHistoryItem;

/**
 * Created by gourava on 1/11/17.
 */

public class UserBookedPoojaDetailsFragment extends Fragment {

    private PoojaDetails poojaDetails;
    private UserPoojaBookingHistoryItem bookingHistoryItem;
    private TextView poojaNameTV, priestNameTV, priestExperienceTV, priestNumberTV,
            poojaBookingTimeTV, poojaStartTimeTV, dakshinaTV, prepCostTV, totalChargeTV,
            bookingStatus1TV, bookingStatus2TV, bookingStatus3TV, bookingStatus4TV, cancelledStatusTv, cancellationTV, poojaLangTV;

    private LinearLayout priestDetailsLL, bookingStatusTrackLL;
    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle){
        super.onCreateView(layoutInflater, viewGroup, bundle);
        poojaDetails = (PoojaDetails)getArguments().getSerializable("selectedPoojaInBooking");
        bookingHistoryItem = (UserPoojaBookingHistoryItem) getArguments().getSerializable("selectedPoojaBookingDetails");
        return layoutInflater.inflate(R.layout.fragment_user_pooja_booking_history_details, viewGroup, false);
    }

    @Override
    public void onViewCreated(View view, Bundle bundle){
        super.onViewCreated(view, bundle);

        poojaNameTV = (TextView)view.findViewById(R.id.history_pooja_name_tv);
        priestNameTV = (TextView)view.findViewById(R.id.details_priest_name);
        priestNumberTV = (TextView)view.findViewById(R.id.details_phone_tv);
        priestExperienceTV = (TextView)view.findViewById(R.id.details_experience_tv);
        poojaBookingTimeTV = (TextView)view.findViewById(R.id.history_pooja_booking_time);
        poojaStartTimeTV = (TextView)view.findViewById(R.id.history_start_time);
        dakshinaTV = (TextView)view.findViewById(R.id.history_dakshina_tv);
        prepCostTV = (TextView)view.findViewById(R.id.history_prep_cost_tv);
        totalChargeTV = (TextView)view.findViewById(R.id.history_total_cost_tv);
        priestDetailsLL = (LinearLayout)view.findViewById(R.id.details_priest_ll);
        bookingStatus1TV = (TextView)view.findViewById(R.id.first_step_tv) ;
        bookingStatus2TV = (TextView)view.findViewById(R.id.second_step_tv) ;
        bookingStatus3TV = (TextView)view.findViewById(R.id.third_step_tv) ;
        bookingStatus4TV = (TextView)view.findViewById(R.id.final_step_tv) ;
        cancelledStatusTv = (TextView)view.findViewById(R.id.cancelled_status_tv);
        cancellationTV = (TextView)view.findViewById(R.id.cancel_tv);
        bookingStatusTrackLL = (LinearLayout)view.findViewById(R.id.booking_status_track_ll);
        poojaLangTV = (TextView)view.findViewById(R.id.details_lang_tv);

        setCurrentStatusHighlighted();

        poojaNameTV.setText(poojaDetails.getName());
        poojaLangTV.setText("Language selected for pooja : "+bookingHistoryItem.getPoojaLanguage());
        poojaBookingTimeTV.setText(bookingHistoryItem.getBookingRequestTime());
        poojaStartTimeTV.setText(bookingHistoryItem.getPoojaStartTime());
        dakshinaTV.setText(bookingHistoryItem.getOfferedPrice() +" Rs");
        if(bookingHistoryItem.getPrepBy().equalsIgnoreCase("user")){
            prepCostTV.setText("0 Rs (preparation by you)");
            totalChargeTV.setText(bookingHistoryItem.getOfferedPrice() +" Rs");
        }else {
            prepCostTV.setText(poojaDetails.getPrepCost() + " Rs");
            totalChargeTV.setText(poojaDetails.getPrepCost()+bookingHistoryItem.getOfferedPrice()  + " Rs");
        }
        priestExperienceTV.setText("Please call priest to discuss pooja details.");
        if(bookingHistoryItem.getPriestName() == null){
            priestDetailsLL.setVisibility(View.GONE);
        }else {
            priestDetailsLL.setVisibility(View.VISIBLE);
            priestNameTV.setText(bookingHistoryItem.getPriestName());
            priestNumberTV.setText(bookingHistoryItem.getPriestPhoneNumber());
        }
    }

    private void setCurrentStatusHighlighted(){
        switch (bookingHistoryItem.getPoojaBookingStatus()){
            case "pending":
                priestDetailsLL.setVisibility(View.GONE);
                cancelledStatusTv.setVisibility(View.GONE);
                bookingStatus1TV.setBackgroundResource(R.drawable.rounded_clickables_background);
                break;
            case "accepted":
                cancelledStatusTv.setVisibility(View.GONE);
                bookingStatus2TV.setBackgroundResource(R.drawable.rounded_clickables_background);
                break;
            case "expired":
                cancellationTV.setVisibility(View.GONE);
                priestDetailsLL.setVisibility(View.GONE);
                bookingStatusTrackLL.setAlpha(0.3f);
                cancelledStatusTv.setVisibility(View.VISIBLE);
                break;
            case "cancelled":
                cancellationTV.setVisibility(View.GONE);
                priestDetailsLL.setVisibility(View.GONE);
                bookingStatusTrackLL.setAlpha(0.3f);
                cancelledStatusTv.setVisibility(View.VISIBLE);
                cancelledStatusTv.setText("Expired");
                break;
            case "started":
                cancellationTV.setVisibility(View.GONE);
                bookingStatus3TV.setBackgroundResource(R.drawable.rounded_clickables_background);
                break;
            case "completed":
                cancellationTV.setVisibility(View.GONE);
                priestDetailsLL.setVisibility(View.GONE);
                bookingStatus4TV.setBackgroundResource(R.drawable.rounded_clickables_background);
                break;
        }

    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        ((Activity)getContext()).finish();
    }
}
