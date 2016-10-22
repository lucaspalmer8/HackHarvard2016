package com.hackharvard.lucas.nocauseforalarm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by lucas on 21/10/16.
 */

public class DbHelper extends SQLiteOpenHelper {

    public interface DbListener {
        void onDataChanged();
    }

    private List<DbListener> listeners = new ArrayList<>();

    public static final String DATABASE_NAME = "HackHarvard.db";
    public static final String ALARMS_TABLE_NAME = "alarms";
    public static final String ALARMS_COLUMN_ID = "id";
    public static final String ALARMS_COLUMN_DESCRIPTION = "description";
    public static final String ALARMS_COLUMN_CREATOR = "creator";
    public static final String ALARMS_COLUMN_TIME = "time";

    private static DbHelper instance = null;

    public static DbHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DbHelper(context);
        }
        return instance;
    }

    private void broadcastChange() {
        for (DbListener listener : listeners) {
            listener.onDataChanged();
        }
    }

    public void addListener(DbListener listener) {
        listeners.add(listener);
    }

    public void removeListener(DbListener listener) {
        listeners.remove(listener);
    }

    private DbHelper(Context context) {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table " + ALARMS_TABLE_NAME +
                        " (id integer primary key, description text, creator text, time text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS contacts");
        onCreate(db);
    }

    public void addAlarm(String description, String creator, String time) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ALARMS_COLUMN_DESCRIPTION, description);
        contentValues.put(ALARMS_COLUMN_CREATOR, creator);
        contentValues.put(ALARMS_COLUMN_TIME, time);
        db.insert(ALARMS_TABLE_NAME, null, contentValues);
        broadcastChange();
    }

    /*public Cursor getData(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from contacts where id="+id+"", null );
        return res;
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, CONTACTS_TABLE_NAME);
        return numRows;
    }*/

    /*public boolean updateContact(Integer id, String name, String phone, String email, String street,String place)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("phone", phone);
        contentValues.put("email", email);
        contentValues.put("street", street);
        contentValues.put("place", place);
        db.update("contacts", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        return true;
    }*/

    public Integer deleteAlarm(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Integer i = db.delete("contacts",
                "id = ? ",
                new String[] { Integer.toString(id) });
        broadcastChange();
        return i;
    }

    public ArrayList<Alarm> getAllAlarms()
    {
        ArrayList<Alarm> array_list = new ArrayList<>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from " + ALARMS_TABLE_NAME, null);
        res.moveToFirst();

        while(!res.isAfterLast()){
            Alarm alarm = new Alarm(res.getString(res.getColumnIndex(ALARMS_COLUMN_DESCRIPTION)),
                    res.getString(res.getColumnIndex(ALARMS_COLUMN_CREATOR)),
                    res.getString(res.getColumnIndex(ALARMS_COLUMN_TIME)));
            array_list.add(alarm);
            res.moveToNext();
        }

        res.close();
        return array_list;
    }
}
