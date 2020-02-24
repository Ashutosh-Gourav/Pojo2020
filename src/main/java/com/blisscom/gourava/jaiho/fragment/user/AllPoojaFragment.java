package com.blisscom.gourava.jaiho.fragment.user;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import com.blisscom.gourava.jaiho.R;
import com.blisscom.gourava.jaiho.activity.user.UserPoojaBookingActivity;
import com.blisscom.gourava.jaiho.adapter.BookingItemAdapter;
import com.blisscom.gourava.jaiho.model.PoojaDetails;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by gourava on 12/20/16.
 */

public class AllPoojaFragment extends Fragment {
    private List<PoojaDetails> allPooja;
    private EditText searchPoojaET;
    private GridView poojaGridView;
    private BookingItemAdapter poojaAdapter;

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle){
        super.onCreateView(layoutInflater, viewGroup, bundle);
        View view = layoutInflater.inflate(R.layout.fragment_booking_pooja, viewGroup, false);


        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle bundle){
        super.onViewCreated(view, bundle);
        poojaGridView = (GridView) view.findViewById(R.id.gridview);
        allPooja = getPoojaList();
        poojaAdapter = new BookingItemAdapter(getContext(), allPooja);
        poojaGridView.setAdapter(poojaAdapter);
        poojaGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id)
            {
                v.setSelected(true);
                showPoojaImportanceDetails(v, position);
            }
        });
        searchPoojaET = (EditText)view.findViewById(R.id.pooja_search_et);
        searchPoojaET.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
//                String text = cs.toString().toLowerCase(Locale.getDefault());
//                poojaAdapter.filter(text);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
                String text = searchPoojaET.getText().toString().toLowerCase(Locale.getDefault());
                poojaAdapter.filter(text);
            }
        });

    }

    private void showPoojaImportanceDetails(final View v, final int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.fragment_dialog_message, null);
        TextView messageTV = (TextView)view.findViewById(R.id.message_tv);
        messageTV.setVisibility(View.VISIBLE);
        messageTV.setText(allPooja.get(position).getImportance());
        builder.setView(view);
        builder.setTitle(allPooja.get(position).getName());
        builder.setPositiveButton("Select pooja", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(getContext(), UserPoojaBookingActivity.class);
                intent.putExtra("selectedPoojaDetails", allPooja.get(position));
                startActivity(intent);
//                Bundle bundle1 = new Bundle();
//                Fragment fragment = new BookingFormFragment();
//                bundle1.putSerializable("selectedPoojaDetails", allPooja.get(position));
//                fragment.setArguments(bundle1);
//                FragmentManager fragmentManager = getFragmentManager();
//                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                fragmentTransaction.addToBackStack(null);
//                fragmentTransaction.add(android.R.id.content, fragment);
//                fragmentTransaction.commit();
            }
        });
        builder.setNeutralButton("see others", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                v.setSelected(false);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setCancelable(true);
        dialog.show();
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
