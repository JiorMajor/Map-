package com.svbtle.johnmajor.map;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements View.OnClickListener {

    static final LatLng SG = new LatLng(1.292333, 103.776815);
    static LatLng current = null;
    public LocationManager lm;
    public Location loc;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();
        //Current Location
        Button button1 = (Button) findViewById(R.id.button1);
        button1.setOnClickListener(this);
    }

    //Checking internet connection is available or not
    public boolean Internet(){
        boolean connected =  false;
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        }
        else{
            connected = false;
        }

        return connected;
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        Button button1 = (Button) findViewById(R.id.button1);
        button1.setOnClickListener(this);
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if(Internet()){
            if (mMap == null) {
                // Try to obtain the map from the SupportMapFragment.
                mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                        .getMap();
                // Check if we were successful in obtaining the map.
                if (mMap != null) {
                    setUpMap();
                }
            }
        }
        else {
            Toast.makeText(MapsActivity.this, "Cannot connect to the Internet!", Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        if(Internet()){

            lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            List<String> providers = lm.getProviders(true);
            loc = lm.getLastKnownLocation(providers.get(0));
            current = new LatLng(loc.getLatitude(),loc.getLongitude());
            Geocoder gc = new Geocoder(this, Locale.getDefault());
            List<Address> addresses= null;

            try
            {
                addresses  = gc.getFromLocation(current.latitude, current.longitude,1);
            }
            catch (Exception e)
            {
                Toast.makeText(MapsActivity.this, "Location update failed.", Toast.LENGTH_SHORT).show();
            }
            Address cur = addresses.get(0);

            if(cur.equals(null)){
                mMap.addMarker(new MarkerOptions()
                        .position(SG)
                        .title("ISS/NUS")
                        .snippet("MTech, SA-DIP"));
                mMap.setMyLocationEnabled(true);
                CameraPosition c = new CameraPosition.Builder()
                        .target(SG)
                        .zoom(18)
                        .build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(c));
            }
            else{
                mMap.addMarker(new MarkerOptions()
                        .position(current)
                        .title(cur.getAddressLine(0))
                        .snippet(cur.getPostalCode()+", "+cur.getCountryName()));
                mMap.setMyLocationEnabled(true);
                CameraPosition c = new CameraPosition.Builder()
                        .target(current)
                        .zoom(15)
                        .build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(c));
            }


        }else {
            Toast.makeText(MapsActivity.this, "Cannot connect to internet!", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onClick(View v) {
        EditText edittext1 = (EditText) findViewById(R.id.edittext1);
        if(edittext1.getText().toString().matches("")){
            Toast.makeText(MapsActivity.this, "Please fill in address.", Toast.LENGTH_SHORT).show();
            setUpMap();
        }else {
            searchMap(edittext1.getText().toString());
        }

    }



    //Search Address
    private void searchMap(String location) {

        Geocoder gc = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        double lat = 0; double lng =0;
        try{
            addresses = gc.getFromLocationName(location,1);
            if(addresses.equals(null)){
                Toast.makeText(MapsActivity.this, location+" is not such a place.", Toast.LENGTH_LONG).show();
            }
            //Location name to Latitude & Longitude
            if (addresses != null && addresses.size() > 0) {
                lat = addresses.get(0).getLatitude();
                lng = addresses.get(0).getLongitude();
            }
        }catch (IOException e){

            Toast.makeText(MapsActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
        }

        LatLng place = new LatLng(lat, lng);
        mMap.addMarker(new MarkerOptions()
                .position(place)
                .title(location)
                .snippet(addresses.get(0).getAddressLine(0)+" "+addresses.get(0).getCountryName()));
        mMap.setMyLocationEnabled(true);
        CameraPosition c = new CameraPosition.Builder()
                .target(place)
                .zoom(18)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(c));
    }
}
