package com.hackharvard.lucas.textit;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by lucas on 21/10/16.
 */

public class DbHelper extends SQLiteOpenHelper {

    public interface DbListItemListener {
        void onDataChanged();
    }

    public interface DbAlarmListener {
        void onDataChanged();
    }

    public interface DbContactsListener {
        void onDataChanged();
    }

    private List<DbAlarmListener> alarmListeners = new ArrayList<>();
    private List<DbContactsListener> contactListeners = new ArrayList<>();
    private List<DbListItemListener> listItemListeners = new ArrayList<>();

    private static final String DATABASE_NAME = "HackHarvard.db";

    private static final String ALARMS_TABLE_NAME = "alarms";
    private static final String ALARMS_COLUMN_ID = "id";
    private static final String ALARMS_COLUMN_DESCRIPTION = "description";
    private static final String ALARMS_COLUMN_CREATOR = "creator";
    private static final String ALARMS_COLUMN_TIME = "time";
    private static final String ALARMS_COLUMN_ACTIVE = "active";

    private static final String CONTACTS_TABLE_NAME = "contacts";
    private static final String CONTACTS_COLUMN_ID = "id";
    private static final String CONTACTS_COLUMN_NAME = "name";
    private static final String CONTACTS_COLUMN_NUMBER = "number";
    private static final String CONTACTS_COLUMN_ALLOW_INPUT_DATA = "allow_input_data";

    private static final String LIST_ITEM_TABLE_NAME = "list_items";
    private static final String LIST_ITEM_COLUMN_ID = "id";
    private static final String LIST_ITEM_COLUMN_DESCRIPTION = "description";
    private static final String LIST_ITEM_COLUMN_CREATOR = "creator";
    private static final String LIST_ITEM_COLUMN_ACTIVE = "active";

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

    private void broadcastListItemChange() {
        for (DbListItemListener listener : listItemListeners) {
            listener.onDataChanged();
        }
    }

    public void addListener(DbAlarmListener listener) {
        alarmListeners.add(listener);
    }

    public void addListener(DbContactsListener listener) {
        contactListeners.add(listener);
    }

    public void addListener(DbListItemListener listener) {
        listItemListeners.add(listener);
    }

    //TODO: call these when views destroyed?
    public void removeListener(DbAlarmListener listener) {
        alarmListeners.remove(listener);
    }
    public void removeListener(DbContactsListener listener) {
        contactListeners.remove(listener);
    }
    public void removeListener(DbListItemListener listener) {
        listItemListeners.remove(listener);
    }

    private DbHelper(Context context) {
        super(context, DATABASE_NAME , null, 5);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table " + ALARMS_TABLE_NAME +
                        " (id integer primary key autoincrement, description text, creator text, time text, active text)");
        db.execSQL(
                "create table " + CONTACTS_TABLE_NAME +
                        " (id integer primary key autoincrement, name text, number text, allow_input_data text)");
        db.execSQL(
                "create table " + LIST_ITEM_TABLE_NAME +
                        " (id integer primary key autoincrement, description text, creator text, active text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS alarms");
        db.execSQL("DROP TABLE IF EXISTS contacts");
        db.execSQL("DROP TABLE IF EXISTS list_items");
        onCreate(db);
    }

    public long addAlarm(String description, String creator, String time, String active) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ALARMS_COLUMN_DESCRIPTION, description);
        contentValues.put(ALARMS_COLUMN_CREATOR, creator);
        contentValues.put(ALARMS_COLUMN_TIME, time);
        contentValues.put(ALARMS_COLUMN_ACTIVE, active);
        long id = db.insert(ALARMS_TABLE_NAME, null, contentValues);
        broadcastAlarmChange();
        return id;
    }

    public void addListItem(String description, String creator, String active) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(LIST_ITEM_COLUMN_DESCRIPTION, description);
        contentValues.put(LIST_ITEM_COLUMN_CREATOR, creator);
        contentValues.put(LIST_ITEM_COLUMN_ACTIVE, active);
        db.insert(LIST_ITEM_TABLE_NAME, null, contentValues);
        broadcastListItemChange();
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

    public void updateAlarm(int id, boolean active) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE alarms SET active = ? WHERE id = ?",
                new Object[]{Boolean.toString(active), Integer.toString(id)});
    }

    public void updateListItem(int id, boolean active) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE list_items SET active = ? WHERE id = ?",
                new Object[]{Boolean.toString(active), Integer.toString(id)});
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

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("select * from " + ALARMS_TABLE_NAME, null);
        res.moveToFirst();

        while(!res.isAfterLast()){
            Alarm alarm = new Alarm(res.getInt(res.getColumnIndex(ALARMS_COLUMN_ID)),
                    res.getString(res.getColumnIndex(ALARMS_COLUMN_DESCRIPTION)),
                    res.getString(res.getColumnIndex(ALARMS_COLUMN_CREATOR)),
                    res.getString(res.getColumnIndex(ALARMS_COLUMN_TIME)),
                    res.getString(res.getColumnIndex(ALARMS_COLUMN_ACTIVE)));
            array_list.add(alarm);
            res.moveToNext();
        }

        res.close();
        return array_list;
    }

    public ArrayList<ListItem> getAllListItems() {
        ArrayList<ListItem> array_list = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("select * from " + LIST_ITEM_TABLE_NAME, null);
        res.moveToFirst();

        while(!res.isAfterLast()){
            ListItem listItem = new ListItem(res.getInt(res.getColumnIndex(LIST_ITEM_COLUMN_ID)),
                    res.getString(res.getColumnIndex(LIST_ITEM_COLUMN_DESCRIPTION)),
                    res.getString(res.getColumnIndex(LIST_ITEM_COLUMN_CREATOR)),
                    res.getString(res.getColumnIndex(LIST_ITEM_COLUMN_ACTIVE)));
            array_list.add(listItem);
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

    public Alarm getAlarm(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("select * from alarms WHERE id = ?", new String[]{String.valueOf(id)});
        res.moveToFirst();

        Alarm alarm = null;
        if(!res.isAfterLast()){
            alarm = new Alarm(res.getInt(res.getColumnIndex(ALARMS_COLUMN_ID)),
                    res.getString(res.getColumnIndex(ALARMS_COLUMN_DESCRIPTION)),
                    res.getString(res.getColumnIndex(ALARMS_COLUMN_CREATOR)),
                    res.getString(res.getColumnIndex(ALARMS_COLUMN_TIME)),
                    res.getString(res.getColumnIndex(ALARMS_COLUMN_ACTIVE)));
        }

        res.close();
        return alarm;
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
