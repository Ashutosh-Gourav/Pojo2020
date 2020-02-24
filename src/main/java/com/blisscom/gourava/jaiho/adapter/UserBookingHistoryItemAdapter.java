package com.blisscom.gourava.jaiho.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blisscom.gourava.jaiho.R;
import com.blisscom.gourava.jaiho.activity.user.UserPoojaBookingDetailsActivity;
import com.blisscom.gourava.jaiho.model.PoojaDetails;
import com.blisscom.gourava.jaiho.model.UserPoojaBookingHistoryItem;

import java.util.List;

/**
 * Created by gourava on 1/10/17.
 */

public class UserBookingHistoryItemAdapter extends BaseAdapter{

    private Context context;
    private List<UserPoojaBookingHistoryItem> bookingList;
    private UserBookingHistoryItemAdapter.ViewHolder holder;
    private List<PoojaDetails> poojaDetailsList;
    private TextView moreTextView;
    private int currentPosition;
    private LinearLayout historyItemLayout;
    @Override
    public int getCount() {
        return bookingList.size();
    }

    public UserBookingHistoryItemAdapter(Context context, List<UserPoojaBookingHistoryItem> bookingList, List<PoojaDetails> poojaDetailsList ) {
        this.context = context;
        this.bookingList = bookingList;
        this.poojaDetailsList = poojaDetailsList;
    }

    @Override
    public Object getItem(int position) {
        return bookingList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return getCount();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.user_booking_item_layout, null);
            holder = new UserBookingHistoryItemAdapter.ViewHolder();
            holder.poojaNameTV = (TextView)convertView.findViewById(R.id.his_pooja_name_tv);
            holder.poojaStartDateTV = (TextView)convertView.findViewById(R.id.his_pooja_start_date_tv);
            holder.poojaBookingDateTV = (TextView)convertView.findViewById(R.id.his_pooja_booking_date_tv);
            holder.bookingStatusTV = (TextView)convertView.findViewById(R.id.his_pooja_booking_status_tv);
//            holder.alphabetTV = (TextView)convertView.findViewById(R.id.pooja_name_first_alphabet_tv);
//            holder.poojaImageTV = (TextView)convertView.findViewById(R.id.image_view_tv);
            moreTextView = (TextView)convertView.findViewById(R.id.more_details_tv);
            convertView.setTag(holder);
        }else {
            holder = (UserBookingHistoryItemAdapter.ViewHolder) convertView.getTag();
            moreTextView = (TextView)convertView.findViewById(R.id.more_details_tv);

        }
        historyItemLayout = (LinearLayout)convertView.findViewById(R.id.his_item_ll);
        if(bookingList.get(position).getPoojaBookingStatus().equalsIgnoreCase("cancelled")
                || bookingList.get(position).getPoojaBookingStatus().equalsIgnoreCase("expired")
                || bookingList.get(position).getPoojaBookingStatus().equalsIgnoreCase("completed")){
            historyItemLayout.setAlpha(0.3f);
        }
        holder.poojaNameTV.setText(poojaDetailsList.get(bookingList.get(position).getPoojaId()).getName());
        holder.poojaBookingDateTV.setText(bookingList.get(position).getBookingRequestTime());
        holder.poojaStartDateTV.setText(bookingList.get(position).getPoojaStartTime());
        holder.bookingStatusTV.setText(bookingList.get(position).getPoojaBookingStatus());
//        currentPosition = position;
        moreTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UserPoojaBookingDetailsActivity.class);
                intent.putExtra("selectedPoojaBookingDetails", bookingList.get(position));
                intent.putExtra("selectedPoojaInBooking", poojaDetailsList.get(bookingList.get(position).getPoojaId()));
                context.startActivity(intent);
            }
        });

        return convertView;
    }

//    @Override
//    public void onClick(View v) {
//        switch (v.getId()){
//            case R.id.more_details_tv:
////                ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().add(android.R.id.content, new UserBookedPoojaDetailsFragment()).commit();
//                Intent intent = new Intent(context, UserPoojaBookingDetailsActivity.class);
//                intent.putExtra("selectedPoojaBookingDetails", bookingList.get(currentPosition));
//                intent.putExtra("selectedPoojaInBooking", poojaDetailsList.get(bookingList.get(currentPosition).getPoojaId()));
//                context.startActivity(intent);
//                break;
//        }
//    }


    static class ViewHolder {
        private TextView poojaNameTV;
        private TextView poojaStartDateTV;
        private TextView poojaBookingDateTV;
        private TextView bookingStatusTV;
    }
}
