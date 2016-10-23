package com.hackharvard.lucas.textit;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by lucas on 21/10/16.
 */

public class DbHelper extends SQLiteOpenHelper {

    public interface DbAlarmListener {
        void onDataChanged();
    }

    public interface DbContactsListener {
        void onDataChanged();
    }

    private List<DbAlarmListener> alarmListeners = new ArrayList<>();
    private List<DbContactsListener> contactListeners = new ArrayList<>();

    public static final String DATABASE_NAME = "HackHarvard.db";

    public static final String ALARMS_TABLE_NAME = "alarms";
    public static final String ALARMS_COLUMN_ID = "id";
    public static final String ALARMS_COLUMN_DESCRIPTION = "description";
    public static final String ALARMS_COLUMN_CREATOR = "creator";
    public static final String ALARMS_COLUMN_TIME = "time";

    public static final String CONTACTS_TABLE_NAME = "contacts";
    public static final String CONTACTS_COLUMN_ID = "id";
    public static final String CONTACTS_COLUMN_NAME = "name";
    public static final String CONTACTS_COLUMN_NUMBER = "number";
    public static final String CONTACTS_COLUMN_ALLOW_INPUT_DATA = "allow_input_data";

    private static DbHelper instance = null;

    public static DbHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DbHelper(context);
        }
        return instance;
    }

    private void broadcastAlarmChange() {
        for (DbAlarmListener listener : alarmListeners) {
            listener.onDataChanged();
        }
    }

    private void broadcastContactChange() {
        for (DbContactsListener listener : contactListeners) {
            listener.onDataChanged();
        }
    }

    public void addListener(DbAlarmListener listener) {
        alarmListeners.add(listener);
    }

    public void addListener(DbContactsListener listener) {
        contactListeners.add(listener);
    }

    //TODO: call these when views destroyed?
    public void removeListener(DbAlarmListener listener) {
        alarmListeners.remove(listener);
    }
    public void removeListener(DbContactsListener listener) {
        contactListeners.remove(listener);
    }

    private DbHelper(Context context) {
        super(context, DATABASE_NAME , null, 3);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table " + ALARMS_TABLE_NAME +
                        " (id integer primary key autoincrement, description text, creator text, time text)");
        db.execSQL(
                "create table " + CONTACTS_TABLE_NAME +
                        " (id integer primary key autoincrement, name text, number text, allow_input_data text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS alarms");
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
        broadcastAlarmChange();
    }

    /**
     * Phone number from contacts or from sms messages can have different formats.
     * @param number A phone number string.
     * @return A phone number string without non-numerical characters and without a leading 1.
     */
    private String formatPhoneNumber(String number) {
        number = number.replaceAll("[^0-9]", "");
        if (number.charAt(0) == '1') {
            number = number.substring(1);
        }
        return number;
    }

    public void addContact(String name, String number, String allowInputData) {
        number = formatPhoneNumber(number);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CONTACTS_COLUMN_NAME, name);
        contentValues.put(CONTACTS_COLUMN_NUMBER, number);
        contentValues.put(CONTACTS_COLUMN_ALLOW_INPUT_DATA, allowInputData);
        db.insert(CONTACTS_TABLE_NAME, null, contentValues);
        broadcastContactChange();
    }

    public void updateContact(int id, boolean allowInputData) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE contacts SET allow_input_data = ? WHERE id = ?",
                new Object[]{Boolean.toString(allowInputData), Integer.toString(id)});
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
        broadcastAlarmChange();
        return i;
    }

    public ArrayList<Alarm> getAllAlarms() {
        ArrayList<Alarm> array_list = new ArrayList<>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("select * from " + ALARMS_TABLE_NAME, null);
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

    public boolean hasContact(String name, String number) {
        //TODO: update name if name has changed
        number = formatPhoneNumber(number);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("select * from contacts where number = ?", new String[] {number});

        boolean isThere = res.getCount() > 0;
        //res.moveToFirst();
        //boolean isThere = !res.isAfterLast();
        res.close();

        return isThere;
    }

    public Contact getContact(String number) {
        number = formatPhoneNumber(number);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("select * from contacts where number = ?", new String[] {number});

        res.moveToFirst();

        Contact contact = null;
        if (!res.isAfterLast()) {
            contact = new Contact(res.getInt(res.getColumnIndex(CONTACTS_COLUMN_ID)),
                    res.getString(res.getColumnIndex(CONTACTS_COLUMN_NAME)),
                    res.getString(res.getColumnIndex(CONTACTS_COLUMN_NUMBER)),
                    res.getString(res.getColumnIndex(CONTACTS_COLUMN_ALLOW_INPUT_DATA)));
        }

        res.close();

        return contact;
    }

    public ArrayList<Contact> getAllContacts() {
        ArrayList<Contact> array_list = new ArrayList<>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from " + CONTACTS_TABLE_NAME, null);
        res.moveToFirst();

        while(!res.isAfterLast()){
            Contact contact = new Contact(res.getInt(res.getColumnIndex(CONTACTS_COLUMN_ID)),
                    res.getString(res.getColumnIndex(CONTACTS_COLUMN_NAME)),
                    res.getString(res.getColumnIndex(CONTACTS_COLUMN_NUMBER)),
                    res.getString(res.getColumnIndex(CONTACTS_COLUMN_ALLOW_INPUT_DATA)));
            array_list.add(contact);
            res.moveToNext();
        }

        res.close();
        return array_list;
    }
}
