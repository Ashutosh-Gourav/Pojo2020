package com.blisscom.gourava.jaiho.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.blisscom.gourava.jaiho.R;
import com.blisscom.gourava.jaiho.model.PoojaDetails;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by gourava on 12/26/16.
 */

public class BookingItemAdapter extends BaseAdapter {

    private Context context;
    private List<PoojaDetails> poojaDetailsList;
    private ViewHolder holder;
    private ArrayList<PoojaDetails> arraylist; // to implement search filter


    public BookingItemAdapter(Context context, List<PoojaDetails> poojaDetailsList) {
        this.context = context;
        this.poojaDetailsList = poojaDetailsList;
        this.arraylist = new ArrayList<PoojaDetails>();
        this.arraylist.addAll(poojaDetailsList);
    }

    @Override
    public int getCount() {
        return poojaDetailsList.size();
    }


    @Override
    public Object getItem(int position) {
        return poojaDetailsList.get(position);
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
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.pooja_item_layout, null);
            holder = new ViewHolder();
            holder.poojaNameTV = (TextView)convertView.findViewById(R.id.pooja_name_tv);
            holder.poojaIV = (ImageView)convertView.findViewById(R.id.pooja_iv);
//            holder.alphabetTV = (TextView)convertView.findViewById(R.id.pooja_name_first_alphabet_tv);
//            holder.poojaImageTV = (TextView)convertView.findViewById(R.id.image_view_tv);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
//            holder.poojaNameTV = (TextView)convertView.findViewById(R.id.pooja_name_tv);
        }
        holder.poojaNameTV.setText(poojaDetailsList.get(position).getName());
        String imageName = poojaDetailsList.get(position).getImageName();
        setImageInItem(imageName);
//        holder.poojaImageTV.setText(poojaDetailsList.get(position).getImageName().toString());
        String firstAlphabet = poojaDetailsList.get(position).getName().toString().toUpperCase().substring(0,0);
        Log.d("First char", firstAlphabet);
//        holder.alphabetTV.setText(firstAlphabet);

        return convertView;
    }

    private void setImageInItem(String imageName){
        switch (imageName){
            case "shiva":
                holder.poojaIV.setImageResource(R.drawable.shiva);
                break;
            case "ganesha":
                holder.poojaIV.setImageResource(R.drawable.ganesha);
                break;
            case "durga":
                holder.poojaIV.setImageResource(R.drawable.durga);
                break;
            case "vishnu":
                holder.poojaIV.setImageResource(R.drawable.vishnu);
                break;
            case "satyanarayanapooja":
                holder.poojaIV.setImageResource(R.drawable.satyanarayanapooja);
                break;
            case "vahanpooja":
                holder.poojaIV.setImageResource(R.drawable.vahanpooja);
                break;
            case "laxmi":
                holder.poojaIV.setImageResource(R.drawable.laxmi);
                break;
            case "kartikeya":
                holder.poojaIV.setImageResource(R.drawable.kartikeya);
                break;
            case "officepooja":
                holder.poojaIV.setImageResource(R.drawable.officepooja);
                break;
            case "janmavidhi":
                holder.poojaIV.setImageResource(R.drawable.janmavidhi);
                break;
            case "shradh":
                holder.poojaIV.setImageResource(R.drawable.shradh);
                break;
            case "grahshanti":
                holder.poojaIV.setImageResource(R.drawable.grahshanti);
                break;
            case "navagraha":
                holder.poojaIV.setImageResource(R.drawable.navagraha);
                break;
            case "kaal_bhirava":
                holder.poojaIV.setImageResource(R.drawable.kaal_bhirava);
                break;
            case "saraswati":
                holder.poojaIV.setImageResource(R.drawable.saraswati);
                break;
        }
    }

    static class ViewHolder {
        private TextView poojaNameTV;
        private ImageView poojaIV;//pooja image view
        private TextView alphabetTV;
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        poojaDetailsList.clear();
        if (charText.length() == 0) {
            poojaDetailsList.addAll(arraylist);
        } else {
            for (PoojaDetails wp : arraylist) {
                if (wp.getName().toLowerCase(Locale.getDefault())
                        .contains(charText)) {
                    poojaDetailsList.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }
}
