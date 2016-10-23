package com.hackharvard.lucas.textit;

import android.database.Cursor;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by lucas on 22/10/16.
 */

public class ContactsFragment extends Fragment implements DbHelper.DbContactsListener {
    DbHelper dbHelper;
    LinearLayout listContent;

    @Override
    public void onDataChanged() {
        Log.v("On Data Set Changed", "Method call");
        loadContacts();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view = inflater.inflate(R.layout.contacts_fragment, null);
        listContent = (LinearLayout)view.findViewById(R.id.list_content);

        dbHelper = DbHelper.getInstance(container.getContext());
        dbHelper.addListener(this);
        loadContacts();

        return view;
    }

    private void loadContacts() {
        Cursor phones;
        try {
            phones = getContext().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        }
        catch (SecurityException ex) {
            return;
        }

        phones.moveToFirst();
        while (!phones.isAfterLast()) {
            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER));
            String d1 = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER));
            String d2 = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER));
            if (!dbHelper.hasContact(name, phoneNumber)) {
                dbHelper.addContact(name, phoneNumber, "false");
            }
            phones.moveToNext();
        }
        phones.close();

        listContent.removeAllViews();

        List<Contact> contacts = dbHelper.getAllContacts();
        Log.v("Number of contacts", String.valueOf(contacts.size()));
        for (Contact contact : contacts) {
            View root = getActivity().getLayoutInflater().inflate(R.layout.contact_item, null);

            TextView data1 = (TextView)root.findViewById(R.id.data1);
            TextView data2 = (TextView)root.findViewById(R.id.data2);
            TextView data3 = (TextView)root.findViewById(R.id.data3);

            final ImageView add = (ImageView)root.findViewById(R.id.add);
            final ImageView remove = (ImageView)root.findViewById(R.id.remove);

            final int id = contact.getId();
            boolean allowInputData = Boolean.valueOf(contact.getAllowInputData());

            if (allowInputData) {
                add.setVisibility(View.VISIBLE);
                remove.setVisibility(View.GONE);
            }
            else {
                add.setVisibility(View.GONE);
                remove.setVisibility(View.VISIBLE);
            }

            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    add.setVisibility(View.GONE);
                    remove.setVisibility(View.VISIBLE);
                    dbHelper.updateContact(id, false);
                }
            });

            remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    add.setVisibility(View.VISIBLE);
                    remove.setVisibility(View.GONE);
                    dbHelper.updateContact(id, true);
                }
            });

            /*GradientDrawable gradientDrawable = (GradientDrawable)circeItem.getBackground();
            gradientDrawable.setColor(ContextCompat.getColor(getActivity(), AlarmHelper.getColor(contact.getName())));

            circeItem.setText(String.valueOf(contact.getName().charAt(0)));*/
            data1.setText(contact.getName());
            data2.setText(contact.getNumber());
            data3.setText(contact.getAllowInputData());

            listContent.addView(root);
        }
    }
}

