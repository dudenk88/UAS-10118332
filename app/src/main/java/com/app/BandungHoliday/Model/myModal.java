package com.app.BandungHoliday.Model;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.location.LocationManager.GPS_PROVIDER;
//13-10-2021 - 10118332 - Nais Farid - IF8
public class myModal {
    private myModelListener listener;
    private myConnectSqlite sqLite;
    //data
    private  ArrayList<Place> listAllPlace =new ArrayList<>();
    private ArrayList<String> listHistory=new ArrayList<>();
    private  ArrayList<Integer> listIDPlaceSave=new ArrayList<>();

    public myModal(myModelListener listener){
        this.listener=listener;
    }

    public static ArrayList<Object> getLocationFrom(String address, Context context) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addressList = geocoder.getFromLocationName(address, 1);
            if (addressList.size() > 0) {
                ArrayList<Object> ListResult= new ArrayList<>();
                Location location = new Location(GPS_PROVIDER);
                location.setLatitude(addressList.get(0).getLatitude());
                location.setLongitude(addressList.get(0).getLongitude());
                String add=addressList.get(0).getAddressLine(0);
                ListResult.add(location);
                ListResult.add(add);
                ListResult.add(add.split(",")[0]);
                return  ListResult;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public  void  readData(Context context) {
        sqLite = new myConnectSqlite(context);
        listAllPlace = sqLite.getAllPlace();
        if (listAllPlace == null) {
            if (readFromJson(context))
                sqLite.insertOfPlace(listAllPlace);
             else {
                Log.e("Tag", "Data Fail");
                listener.onFail();
            }
        } else {
        listHistory = sqLite.getListHistory();
        listIDPlaceSave = sqLite.getIDsSaved();
        listener.onSuccess(listIDPlaceSave,listHistory);
        }
    }

    public  void  saveData(){
        Log.e("Tag","Save Data");
        sqLite.saveToSQLite(listIDPlaceSave,listHistory);
    }

    public  ArrayList<Place> getListWithDistance(int distance, Location location){
        ArrayList<Place> places = new ArrayList<>();
        for (Place p: listAllPlace) {
            if (p.distanceTo(location)<=distance)
                places.add(p);
        }
        return  places;
    }

    //List save
    public ArrayList<Place> getListPlaceSave(ArrayList<Integer> listID){
        ArrayList<Place> placeSave= new ArrayList<>();
        if (listID.size()>0)
            for (int i=0;i<listID.size();i++){
                placeSave.add(listAllPlace.get(listID.get(i)));
            }
        return  placeSave;
    }

    private boolean readFromJson(Context context){
        myJson json= new myJson();
        try {
            listAllPlace=  json.ReadFile(context);
            return  true;
        } catch (JSONException e) {
            return  false;
        }
    }

    public  interface  myModelListener{
        void onSuccess( ArrayList<Integer> lisIDSave, ArrayList<String> lisHistory );
        void  onFail();
    }
}
