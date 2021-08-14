package com.app.BandungHoliday.Map;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.app.BandungHoliday.Model.Place;
import com.app.BandungHoliday.R;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

import static android.location.LocationManager.GPS_PROVIDER;
//13-10-2021 - 10118332 - Nais Farid - IF8
public class MapHelper {
    public static final int REQUEST_CODE = 1000;
    private final String[] PERMISSION = {Manifest.permission.ACCESS_FINE_LOCATION};
    private static final int UPDATE_INTERVAL = 10 * 1000;
    private static final int FASTER_UPDATE_INTERVAL = UPDATE_INTERVAL / 2;

    //Attribute
    private GoogleMap googleMap;
    private LocationRequest requestLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    private Context context;
    private Activity activity;
    private  String title_pin="Your here";
    private MapHelperListener listener;

    //Constructor
    public MapHelper(Context context, Activity activity, GoogleMap googleMap, MapHelperListener listener) {
        this .activity=activity;
        this.context = context;
        this.googleMap=googleMap;
        this.listener=listener;
        requestLocation = new LocationRequest();
    }

    //Show Location Curr
    public  void  requestLocationCurr(){
        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(context);
        locationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult!=null){
                    listener.onGetLocationSuccess(locationResult.getLastLocation());
                    fusedLocationProviderClient.removeLocationUpdates(locationCallback);
                    Log.e("TAG","REMOVE - LOCATION UPDATE");
                }
            }
        };
        Log.e("TAG","REQUEST - LOCATION UPDATE");
        fusedLocationProviderClient.requestLocationUpdates(requestLocation,locationCallback, null);
    }

    //show Nearby
    public  ArrayList<Marker>  showNearby(float distance, Location locationCenter, ArrayList<Place> listPlace, GoogleMap map) {
        createCircle(distance, locationCenter);
        ArrayList<Marker> markers= displayListLatLng(listPlace, map,locationCenter);
        LatLngBounds bounds = setCamera(locationCenter, distance);
        LatLng latLngCenter= new LatLng(locationCenter.getLatitude(),locationCenter.getLongitude());
        map.animateCamera(CameraUpdateFactory.newLatLng(latLngCenter));
        map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds,100));
        return  markers;
    }

    public void  checkPermisionAndLocation(){
        Log.e("TAG","Check - Method");
        LocationRequest requestLocation =createRequestLocation();
        if (checkPermission()){
            if (Build.VERSION.SDK_INT<=23)
                checkAndRequestGPS();
            else
                checkAndRequestLocation(requestLocation);
        }
        else
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(10.8257424, 106.6284044), 12));
    }

    //region Check Location & Permission
    private boolean checkPermission() {
        Log.e("TAG","Check Permission "+"API - " +Build.VERSION.SDK_INT );
        if(Build.VERSION.SDK_INT>=23)
        {
            if (ActivityCompat.checkSelfPermission(context, PERMISSION[0]) == PackageManager.PERMISSION_GRANTED)
                return true;
            else {
                createAlertPermission();
                return false;
            }
        }
        else
            return false;
    }

    private void createAlertPermission(){
        Log.e("TAG","Create Alert Permission");
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("You need permission Location for App ");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                ActivityCompat.requestPermissions(activity, PERMISSION, REQUEST_CODE);
            }
        });
        builder.setNegativeButton("No and Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                activity.finishAndRemoveTask();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

        //region Check Location for API > 23
    private LocationRequest createRequestLocation() {
        Log.e("TAG","Create Request Location");
        requestLocation = new LocationRequest();
        requestLocation.setInterval(UPDATE_INTERVAL);
        requestLocation.setFastestInterval(FASTER_UPDATE_INTERVAL);
        requestLocation.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return requestLocation;
    }

    private void checkAndRequestLocation(LocationRequest requestLocation) {
        Log.e("TAG","Request Turn On Location");
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(requestLocation);
        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(context).checkLocationSettings(builder.build());
        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse response = task.getResult(com.google.android.gms.common.api.ApiException.class);
                    Log.e("TAG", "LOCATION - TURN ON");
                } catch (ApiException e) {
                    Log.e("TAG", "LOCATION - TURN OFF");
                    if (e.getStatusCode() == CommonStatusCodes.RESOLUTION_REQUIRED) {
                        try {
                            ResolvableApiException resolvable = (ResolvableApiException) e;
                            Log.e("TAG"," Show dialog request Location");
                            resolvable.startResolutionForResult(activity, LocationRequest.PRIORITY_HIGH_ACCURACY);
                        } catch (IntentSender.SendIntentException | ClassCastException exception) {
                            exception.printStackTrace();
                        }
                    }
                }
            }

        });
    }
        //endregion

        //region Check Location for API <= 23
    private void checkAndRequestGPS(){
        Log.e("TAG","Check GPS");
        LocationManager manager= (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        if (manager!=null&&!manager.isProviderEnabled(GPS_PROVIDER)){
            createAlterGPS();
        }
    }

    private void createAlterGPS(){
        Log.e("TAG","Create Alert GPS");
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("You need turn on GPS of device !!! ");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent openSetting= new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                activity.startActivity(openSetting);
            }
        });
        builder.setNegativeButton("No and Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                activity.finishAndRemoveTask();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }
        //endregion

    //endregion

    //region Displays on GG MyMap
    //Display Location
    public void displayLocation(Location location) {
        googleMap.clear();
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOp = new MarkerOptions().
                position(latLng).
                title(title_pin).
                icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_marker_user_foreground));
//        Icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
        googleMap.addMarker(markerOp).showInfoWindow();
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
    }

    public  void  changeTitlePin(String title_pin){
        this.title_pin=title_pin;
    }

    // Display list location
    private ArrayList<Marker> displayListLatLng(ArrayList<Place> listPlace, GoogleMap map,Location location) {
        ArrayList<Marker> markers= new ArrayList<>();
        for (int i = 0; i < listPlace.size(); i++) {
            Place temp=listPlace.get(i);
            LatLng latLng= new LatLng(temp.getLatitude(),temp.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(latLng)
                    .title(temp.distanceTo(location)+" m")
                    .snippet(temp.getName());
            // .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_maker));
            Marker marker = map.addMarker(markerOptions);
            marker.setTag(i);
            markers.add(marker);
        }
        return  markers;
    }

    //Display Circle
    private LatLngBounds setCamera(Location locationCenter, double distance) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        double lat = locationCenter.getLatitude();
        double lon = locationCenter.getLongitude();
        double minute = distance / 30d;
        double second = minute / 3600d;
        LatLng North = new LatLng(lat + second, lon);
        LatLng South = new LatLng(lat - second, lon);
        LatLng West = new LatLng(lat, lon + second);
        LatLng East = new LatLng(lat, lon - second);
        builder.include(North).include(South).include(West).include(East);
        return builder.build();
    }

    //Custom Circle
    private void createCircle(float distance, Location locationCenter) {
        googleMap.clear();
        displayLocation(locationCenter);
        CircleOptions circleOptions = new CircleOptions()
                .center(new LatLng(locationCenter.getLatitude(), locationCenter.getLongitude()))
                .radius(distance)
                .fillColor(0x10CAD1FB)
                .strokeWidth(5f).strokeColor(Color.parseColor("#cad1fb"));
        googleMap.addCircle(circleOptions);
    }

    public  interface MapHelperListener{
        void  onGetLocationSuccess(Location location);
    }

}
