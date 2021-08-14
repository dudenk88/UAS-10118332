package com.app.BandungHoliday.Model;

import android.content.Context;
import android.util.Log;

import com.app.BandungHoliday.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
//13-10-2021 - 10118332 - Nais Farid - IF8
public class myJson {

    public myJson() {
    }
    ArrayList<Place> ReadFile(Context context) throws JSONException {
        Log.e("TAG","Read to Json");
        ArrayList<Place> placeArrayList= new ArrayList<>();
        String json = null;
        try {
            InputStream is = context.getResources().openRawResource(R.raw.data);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        JSONObject object= new JSONObject(json);
        JSONArray ar= object.getJSONArray("data");
        for (int i= 0;i<ar.length();i++){
            JSONObject p= ar.getJSONObject(i);
            String name= p.getString("name");
            String address= p.getString("address");
            Double lati=p.getDouble("latitude");
            Double longi=p.getDouble("longitude");
            placeArrayList.add(new Place(i,name,address,lati,longi));
        }
        return placeArrayList;
    }
}
