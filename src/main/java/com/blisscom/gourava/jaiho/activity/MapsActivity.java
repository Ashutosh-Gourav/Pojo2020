package com.blisscom.gourava.jaiho.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.blisscom.gourava.jaiho.R;
import com.blisscom.gourava.jaiho.model.InternetConnectivityReceiver;
import com.blisscom.gourava.jaiho.model.PojoApplication;
import com.blisscom.gourava.jaiho.model.PojoPreferredStrings;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMarkerDragListener,
        GoogleMap.OnMapLongClickListener, InternetConnectivityReceiver.ConnectivityReceiverListener,
        View.OnClickListener {

    private GoogleMap mMap;

    //To store longitude and latitude from map
    private double longitude;
    private double latitude;

    private Button done_button;
    private ImageButton current_address_ib;
    private TextView marker_address_tv;
    private SharedPreferences sharedPreferences;

    //Google ApiClient
    private GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Initializing googleapi client
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        done_button = (Button) findViewById(R.id.done_address_button);
        current_address_ib = (ImageButton) findViewById(R.id.current_address_ib);
        marker_address_tv = (TextView) findViewById(R.id.marker_address_tv);
        done_button.setOnClickListener(this);
        current_address_ib.setOnClickListener(this);
//        marker_address_tv.setOnClickListener(this);
        sharedPreferences = getSharedPreferences(getString(R.string.shared_preference_name), MODE_PRIVATE);
    }

    @Override
    public void onClick(View v) {
        if (v == current_address_ib) {
            getCurrentLocation();
            moveMap();
        }
        if (v == done_button) {
            this.finish();
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("you are in sydney").draggable(true));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.setOnMarkerDragListener(this);
        mMap.setOnMapLongClickListener(this);

    }

    //Getting current location
    private void getCurrentLocation() {
        mMap.clear();
        //Creating a location object
        Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (location != null) {
            //Getting longitude and latitude
            longitude = location.getLongitude();
            latitude = location.getLatitude();

            //moving the map to location
            moveMap();
        }
    }

    //Function to move the map(camera) and set a zoom-value, add new marker, and set marker address in text-view
    private void moveMap() {
        //String to display current latitude and longitude
        String msg = latitude + ", " + longitude;

        //Creating a LatLng Object to store Coordinates
        LatLng latLng = new LatLng(latitude, longitude);

        //Adding marker to map
        mMap.addMarker(new MarkerOptions()
                .position(latLng) //setting position
                .draggable(true) //Making the marker draggable
                .title("you are here")); //Adding a title

        //Moving the camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        //Animating the camera
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

        //Displaying current coordinates in toast
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
//        marker_address_tv.setText(msg);
        setAddressInTVFromLatlong(latitude, longitude);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        getCurrentLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        Toast.makeText(this, "No internet connection", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        //Clearing all the markers
        mMap.clear();

        //Adding a new marker to the current pressed position we are also making the draggable true
        mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .draggable(true).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

//        Toast.makeText(this, "longitude=" + latLng.longitude + "latitude=" + latLng.latitude, Toast.LENGTH_LONG).show();
        String msg = latLng.longitude + ", " + latLng.latitude;
//        marker_address_tv.setText(msg);
        setAddressInTVFromLatlong(latLng.latitude, latLng.longitude);

    }

    @Override
    public void onMarkerDragStart(Marker marker) {

        marker.setZIndex(15);
    }

    @Override
    public void onMarkerDrag(Marker marker) {
        marker.setZIndex(15);
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        //Getting the coordinates
        marker.setZIndex(0);
        latitude = marker.getPosition().latitude;
        longitude = marker.getPosition().longitude;
//        Moving the map
        mMap.clear();
        moveMap();
    }

    @Override
    protected void onStart() {
        googleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

    public void setAddressInTVFromLatlong(double la, double lo) {
        if (la < 32 && la >= 8 && lo < 90 && lo > 70) {
            String cityName = null;
            String addressFound = null;
            Geocoder gcd = new Geocoder(this, Locale.getDefault());
            List<Address> addresses;
            try {
                addresses = gcd.getFromLocation(la, lo, 1);
                String addressLines = "";
                if (addresses.size() > 0) {
                    int addressLineNumber = addresses.get(0).getMaxAddressLineIndex();
                    int i = 0;
                    for(i=0; i<addressLineNumber; i++){
                        addressLines = addressLines + addresses.get(0).getAddressLine(i)+",";
                        Log.d("addressLines", i+"-"+addresses.get(0).getAddressLine(i));
                    }
                    addressLines = addressLines + addresses.get(0).getAddressLine(i);
//                    cityName = addresses.get(0).getAddressLine(0) + "," + addresses.get(0).getLocality();
                    addressFound = addressLines;
                    marker_address_tv.setText(addressFound);

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(PojoPreferredStrings.LAST_FULL_ADDRESS_TAKEN_FROM_MAP.toString(), addressFound);
                    editor.putFloat("lastAddressLattitudeTakenFromMap", ((float) la));
                    editor.putFloat("lastAddressLongitudeTakenFromMap", (float) lo);
                    editor.commit();

                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Weak internet connection. Try harder.", Toast.LENGTH_SHORT).show();
                Log.e("POJO", "Weak internet connection.");
            }

        } else {
            marker_address_tv.setText("Not a valid address");
        }
    }

    // Method to manually check connection status
    private boolean checkConnection() {
        return InternetConnectivityReceiver.isConnected();
    }


    @Override
    protected void onResume() {
        super.onResume();
        // register internet connection status listener
        PojoApplication.getInstance().setConnectivityListener(this);
        if (!checkConnection()) {
            showLoadingDialog(R.string.no_internet_connection);
        }
    }

    /**
     * Callback will be triggered when there is change in
     * network connection
     */
    @Override
    public void onNetworkConnectionChanged(boolean isInternetConnected) {
        if(isInternetConnected){
            hideLoadingDialog();
            getCurrentLocation();
        }else {
            showLoadingDialog(R.string.no_internet_connection);
        }
    }


    private void showToastForInternetConnection(boolean isConnected) {

        if (isConnected) {
            Toast.makeText(this, "good internet access.", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "No internet access. Please check your connections.", Toast.LENGTH_LONG).show();
        }
    }

    private Dialog progressDialog;
    private void showLoadingDialog(int messageId) {
        if (progressDialog == null || !progressDialog.isShowing()) {
            progressDialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
            progressDialog.setContentView(R.layout.fragment_progress_dialog);
            TextView textView = (TextView) progressDialog.findViewById(R.id.progress_dialog_tv);
            textView.setText(getString(messageId));
            progressDialog.setCancelable(true);
            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    finish();
                }
            });
            progressDialog.show();

        } else {
            TextView textView = (TextView) progressDialog.findViewById(R.id.progress_dialog_tv);
            textView.setText(getString(messageId));
        }
    }

    private void hideLoadingDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

}
