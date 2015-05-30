package com.example.jay.smart_brochure;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Jay on 2015-05-17.
 */
public class Database {
    private static final String DB_NAME = "Smart_Brochure";
    private static final int VERSION = 1;
    public final static String S_Brochure = "smart_brochure";
    private static final String ID = "_id";
    public final static String ONOFF = "onoff";
    public final static String ADDRESS = "address";
    public final static String NAME = "name";
    public final static String Beacon_List = "beacon_list";

    String[] list = {ID, ADDRESS, NAME};
    String[] beacons = {ID, ADDRESS};
    String[] history = {ID, ADDRESS, NAME};
    String[] onoff = { ID, ONOFF };
    // 0---- 1--- 2----

    private static final String DB_CREATE_STATE = "CREATE TABLE " + S_Brochure
            + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,  " + ADDRESS + " TEXT NOT NULL, " + NAME + " TEXT NOT NULL " + ");";
    private static final String DB_CREATE_LIST = " CREATE TABLE " + Beacon_List + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + ADDRESS + " TEXT NOT NULL " + ");";
    private static final String DB_DESTROY_STATE = "DROP TABLE IF EXISTS "
            + S_Brochure;
    private static final String DB_DESTROY_LIST = "DROP TABLE IF EXISTS" + Beacon_List;

    private Context mContext;
    private DataBaseHelper mHelper;
    public SQLiteDatabase mDB;

    private static class DataBaseHelper extends SQLiteOpenHelper {

        public DataBaseHelper(Context context) {
            super(context, DB_NAME, null, VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DB_CREATE_STATE);
            db.execSQL(DB_CREATE_LIST);
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(DB_DESTROY_STATE);
            db.execSQL(DB_DESTROY_LIST);
        }
    }

    public Database(Context context) {
        this.mContext = context;
        mHelper = new DataBaseHelper(context);
    }

    public void open() {
        mDB = mHelper.getWritableDatabase();
    }

    public void close() {
        mDB.close();
    }

    public void flush() {
        mDB.execSQL(DB_DESTROY_STATE);
        mDB.execSQL(DB_CREATE_STATE);
        mDB.execSQL(DB_DESTROY_LIST);
        mDB.execSQL(DB_CREATE_LIST);
    }

    public Boolean check() {
        Cursor cursor = mDB.query(S_Brochure, list, null, null, null, null, ID);
        if (cursor.moveToFirst())
            return false;
        else
            return true;
    }


    public void init() {
        mDB = mHelper.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(ADDRESS, "78:A5:04:13:3C:FA");

        mDB.insert(Beacon_List, null, values);
    }

    // 기존 멤버인지 검색

// OnOff 상태 체크
    public int getOnoff() {

        Cursor cursor = mDB.query(S_Brochure, onoff, null, null, null, null, ID);
        int onOff = 0;

        cursor.moveToFirst();
        onOff = cursor.getInt(1);

        return onOff;
    }

    // OnOff 눌렀을 경우 체크해서 수정
    public void editOnoff(String value) {
        ContentValues row = new ContentValues();

        if ("1".equals(value)) {
            row.put(ONOFF, "1");
        } else {
            row.put(ONOFF, "null");
        }
        mDB.update(S_Brochure, row, ID + "=" + "'" + 1 + "'", null);
    }


    public Cursor getlist() {
        return mDB.query(S_Brochure, null, null, null, null, null, ID);
    }

    public ArrayList<String> getContent() {

        Cursor cursor = mDB.query(S_Brochure, list, null, null, null, null, ID);
        int int_count = cursor.getCount();
        cursor.moveToFirst();

        ArrayList<String> Arl = new ArrayList<String>();

        for (int i = 0; i < int_count; i++) {
            Arl.add(cursor.getString(1).toString());
            cursor.moveToNext();
        }

        return Arl;
    }

    public ArrayList<String> getHistory() {
        Cursor cursor = mDB.query(S_Brochure, history, null, null, null, null, ID);
        int int_count = cursor.getCount();
        cursor.moveToFirst();

        ArrayList<String> Arl = new ArrayList<String>();

        for (int i = 0; i < int_count; i++) {
            Arl.add(cursor.getString(2).toString());
            cursor.moveToNext();
        }

        return Arl;
    }

    // 사용하는 비콘 리스트 저장
    public void beaconList(){

        Cursor cursor;
        cursor = mDB.query(Beacon_List, beacons, null, null, null, null, null);

        mDB = mHelper.getWritableDatabase();

        ContentValues values = new ContentValues();

        int int_count = cursor.getCount();
        cursor.moveToFirst();
/*        if(cursor.getCount() == 0){
            values.put(ADDRESS, "Beacon_list");
            mDB.insert(Beacon_List, null, values);
            return;
        }*/

        String beacon[] = {"78:A5:04:13:3C:FA"};
        ArrayList<String> beacons = new ArrayList<String>();
        for(int i = 0 ; i < beacon.length; i++){
            beacons.add(beacon[i]);
            Log.i("ttt", "beacons.get(" + i + ") = " + beacons.get(i).toString());
            Log.i("ttt", "beacons.size() " + beacons.size());
        }

        if(beacons.size() != int_count){
            int k = beacons.size() - int_count;
            for(int i = 0 ; i < k; i++) {
                values.put(ADDRESS, beacons.get(beacons.size() - i - 1));
            }
            mDB.insert(Beacon_List, null, values);
        }
    }

    // 비콘 리스트 불러오기
    public ArrayList<String> getBeacons() {
        Cursor cursor;
        cursor = mDB.query(Beacon_List, beacons, null, null, null, null, null);

        int int_count = cursor.getCount();
        Log.i("getBeacons", "getBeacons. size  : : " + int_count);
        cursor.moveToFirst();

        ArrayList<String> Arl = new ArrayList<String>();

        for (int i = 0; i < int_count; i++) {
            Arl.add(cursor.getString(1).toString());
            cursor.moveToNext();
        }
        Log.i("getBeacons", "getBeacons. 리스트 : : " + Arl.get(0).toString());

        return Arl;

    }


    public void addHistory(History history) {
        mDB = mHelper.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(ADDRESS, history.getAddress());
        values.put(NAME, history.getName());

        mDB.insert(S_Brochure, null, values);
    }

    public ArrayList<String> getId(String a) {
        Cursor cursor;
        // 인자로 "list"를 넘기면 list 테이블에서 id 검색, "history"를 넘기면 history 테이블에서 id 검색
        if (a == "list")
            cursor = mDB.query(S_Brochure, list, null, null, null, null, null);
        else
            cursor = mDB.query(S_Brochure, history, null, null, null, null, null);

            int int_count = cursor.getCount();
            cursor.moveToFirst();

            ArrayList<String> Arl = new ArrayList<String>();

            for (int i = 0; i < int_count; i++) {
                Arl.add(cursor.getString(0).toString());
                cursor.moveToNext();
            }

            return Arl;

    }


    public void delete(String id) {
        mDB.execSQL("delete from smart_brochure where _id ='" + id + "';");
    }

    public void deleteAll() {
        mDB.execSQL("delete from smart_brochure;");
    }

}
