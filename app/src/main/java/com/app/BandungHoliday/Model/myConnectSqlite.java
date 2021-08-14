package com.app.BandungHoliday.Model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
//13-10-2021 - 10118332 - Nais Farid - IF8
public class myConnectSqlite extends SQLiteOpenHelper {

    //DB
    private static final int DB_VERSION=1;
    private static final String DB_NAME="PLACE";

    //region TABLE
    //ALL PLACE
    private static final String TABLE_PLACE="ALL_PLACE";
    private static final String ID_PLACE="_ID_PLACE";
    private static final String NAME_PLACE="NAME_PLACE";
    private static final String ADDRESS_PLACE="ADDRESS";
    private static final String LATITUDE="LATITUDE";
    private static final String LONGITUDE="LONGITUDE";
    //PLACE SAVE
    private static final  String TABLE_SAVE="TABLE_SAVE";
    private static final  String ID_SAVE="_ID_SAVE";
    private static final  String PLACE_SAVE="PLACE_SAVE";
    //HISTORY
    private static final  String TABLE_HISTORY="TABLE_H";
    private static final  String ID_HISTORY="_ID_H";
    private static final String ADDRESS_HISTORY="ADDRESS_H";
    //endregion

    //region Query
    private static final String createPlace ="CREATE  TABLE " +TABLE_PLACE +" ( "
            + ID_PLACE+ " INTEGER PRIMARY KEY, "
            + NAME_PLACE+ " TEXT, "
            + ADDRESS_PLACE +" TEXT, "
            + LATITUDE + " DOUBLE, "
            + LONGITUDE + " DOUBLE  ) ";
    private static final String createSave ="CREATE  TABLE " +TABLE_SAVE +" ( "
            + ID_SAVE+ " INTEGER PRIMARY KEY  ,"
            + PLACE_SAVE+ " INTEGER ) ";
    private static final String createHistory ="CREATE  TABLE " +TABLE_HISTORY +" ( "
            + ID_HISTORY+ " INTEGER  PRIMARY KEY  , "
            + ADDRESS_HISTORY+ " TEXT )";
    //endregion

    public myConnectSqlite(@Nullable Context context) {
        super(context,DB_NAME, null,DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.e("TAG","CREATE - SQL");
        db.execSQL(createPlace);
        db.execSQL(createSave);
        db.execSQL(createHistory);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.e("TAG","UPGRADE - SQL");
        String drop="DROP TABLE IF EXISTS "+TABLE_PLACE;
        db.execSQL(drop);
        onCreate(db);
    }

    //region Read Data
        //All Place
    public ArrayList<Place> getAllPlace(){
        SQLiteDatabase db   = getReadableDatabase();
        String select= "SELECT "+ID_PLACE+", "+NAME_PLACE +", "+ ADDRESS_PLACE+", "+ LATITUDE+", "+LONGITUDE + "  FROM " +TABLE_PLACE;
        Cursor cursor= db.rawQuery(select, null);
        if (cursor.getCount()<=0){
            cursor.close();
            return  null;
        }
        ArrayList<Place> placeList= new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            int id= cursor.getInt(0);
            String name=cursor.getString(1);
            String address= cursor.getString(2);
            double lati= cursor.getDouble(3);
            double longi= cursor.getDouble(4);
            placeList.add(new Place(id,name,address,lati,longi));
            cursor.moveToNext();
        }
        cursor.close();
        db.close();
        return placeList;
    }
        //Save
    public  ArrayList<Integer> getIDsSaved(){
        ArrayList<Integer> placeList= new ArrayList<>();
        SQLiteDatabase db   = getReadableDatabase();
        String select= "SELECT   "+PLACE_SAVE+ " FROM " +TABLE_SAVE;
        Cursor cursor= db.rawQuery(select, null);
        if (cursor.getCount()<=0){
            cursor.close();
            return  placeList;
        }
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            int idPlace= cursor.getInt(0);
            placeList.add(idPlace);
            cursor.moveToNext();
        }
        cursor.close();
        db.close();
        return placeList;
    }
        //History
    public  ArrayList<String> getListHistory(){
        ArrayList<String> list= new ArrayList<>();
        SQLiteDatabase db   = getReadableDatabase();
        String select= "SELECT  "+ADDRESS_HISTORY + "  FROM " +TABLE_HISTORY;
        Cursor cursor= db.rawQuery(select, null);
        if (cursor.getCount()<=0){
            cursor.close();
            return  list;
        }
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            String history= cursor.getString(0);
            list.add(history);
            cursor.moveToNext();
        }
        cursor.close();
        db.close();
        return list;
    }
    //endregion

    //region Insert data
    public void  insertOfPlace(ArrayList<Place> listPlace){
        SQLiteDatabase db= getWritableDatabase();
        for (int i=0;i<listPlace.size();i++) {
            Place obj = listPlace.get(i);
            ContentValues values = new ContentValues();
            values.put(ID_PLACE, obj.getId());
            values.put(NAME_PLACE, obj.getName());
            values.put(ADDRESS_PLACE, obj.getAddress());
            values.put(LATITUDE, obj.getLatitude());
            values.put(LONGITUDE,obj.getLongitude());
            db.insert(TABLE_PLACE,null,values);
        }
        db.close();
    }

    private void insertOfSave(int id,int idPlace , SQLiteDatabase db){
        ContentValues values= new ContentValues();
        values.put(ID_SAVE,id);
        values.put(PLACE_SAVE,idPlace);
        db.insert(TABLE_SAVE,null,values);
    }

    private void insertOfHistory(int id ,String history, SQLiteDatabase db) {
        ContentValues values= new ContentValues();
        values.put(ID_HISTORY,id);
        values.put(ADDRESS_HISTORY,history);
        db.insert(TABLE_HISTORY,null,values);
    }
    //endregion

    public  void  saveToSQLite(ArrayList<Integer> listSave, ArrayList<String> listHistory){
        SQLiteDatabase db =getWritableDatabase();
        String delete_save = "DELETE FROM " + TABLE_SAVE ;
        db.execSQL(delete_save);
        if (listSave!=null&&listSave.size()!=0) {
            for (int i = 0; i < listSave.size(); i++) {
                insertOfSave( i,listSave.get(i), db);
            }
        }
        String delete_history = "DELETE FROM " + TABLE_HISTORY ;
        db.execSQL(delete_history);
        if (listHistory!=null&&listHistory.size()!=0) {
            for (int i =0; i < listHistory.size(); i++) {
                insertOfHistory(i,listHistory.get(i), db);
            }
        }
        db.close();
    }

}
