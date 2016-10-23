package com.hackharvard.lucas.textit;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
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

public class ListItemFragment extends Fragment implements DbHelper.DbAlarmListener {
    DbHelper dbHelper;
    LinearLayout listContent;

    @Override
    public void onDataChanged() {
        Log.v("On Data Set Changed", "Method call");
        loadListItems();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view = inflater.inflate(R.layout.alarms_fragment, null);
        listContent = (LinearLayout)view.findViewById(R.id.list_content);

        dbHelper = DbHelper.getInstance(container.getContext());
        dbHelper.addListener(this);
        loadListItems();

        return view;
    }

    private void loadListItems() {
        listContent.removeAllViews();

        List<ListItem> listItems = dbHelper.getAllListItems();
        Log.v("Number of alarms", String.valueOf(listItems.size()));
        for (ListItem listItem : listItems) {
            View root = getActivity().getLayoutInflater().inflate(R.layout.list_item, null);

            TextView data1 = (TextView)root.findViewById(R.id.data1);
            TextView data2 = (TextView)root.findViewById(R.id.data2);
            TextView data3 = (TextView)root.findViewById(R.id.data3);

            data1.setText(listItem.getDescription());
            data2.setText(listItem.getCreator());
            //data3.setText(alarm.getTime());

            final ImageView add = (ImageView)root.findViewById(R.id.add);
            final ImageView remove = (ImageView)root.findViewById(R.id.remove);

            final int id = listItem.getId();
            boolean allowInputData = Boolean.valueOf(listItem.getActive());

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
                    dbHelper.updateListItem(id, false);
                }
            });

            remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    add.setVisibility(View.VISIBLE);
                    remove.setVisibility(View.GONE);
                    dbHelper.updateListItem(id, true);
                }
            });

            listContent.addView(root);
        }
    }
}
