package com.hackharvard.lucas.textit;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by lucas on 22/10/16.
 */

public class AlarmFragment extends Fragment implements DbHelper.DbListener {
    DbHelper dbHelper;
    LinearLayout listContent;

    @Override
    public void onDataChanged() {
        Log.v("On Data Set Changed", "Method call");
        loadAlarms();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view = inflater.inflate(R.layout.content_alarm, null);
        listContent = (LinearLayout)view.findViewById(R.id.list_content);

        dbHelper = DbHelper.getInstance(container.getContext());
        dbHelper.addListener(this);
        loadAlarms();

       /* Intent intent = new Intent("android.provider.Telephony.SMS_DELIVER");
        List<ResolveInfo> infos = getPackageManager().queryBroadcastReceivers(intent, 0);
        for (ResolveInfo info : infos) {
            System.out.println("Receiver name:" + info.activityInfo.name + "; priority=" + info.priority);
        }*/
        return view;
    }

    private void loadAlarms() {
        listContent.removeAllViews();

        List<Alarm> alarms = dbHelper.getAllAlarms();
        Log.v("Number of alarms", String.valueOf(alarms.size()));
        for (Alarm alarm : alarms) {
            View root = getActivity().getLayoutInflater().inflate(R.layout.alarm_item, null);

            TextView circeItem = (TextView)root.findViewById(R.id.circle_item);
            TextView data1 = (TextView)root.findViewById(R.id.data1);
            TextView data2 = (TextView)root.findViewById(R.id.data2);
            TextView data3 = (TextView)root.findViewById(R.id.data3);

            GradientDrawable gradientDrawable = (GradientDrawable)circeItem.getBackground();
            gradientDrawable.setColor(ContextCompat.getColor(getActivity(), AlarmHelper.getColor(alarm.getDescription())));

            circeItem.setText(String.valueOf(alarm.getDescription().charAt(0)));
            data1.setText(alarm.getDescription());
            data2.setText(alarm.getCreator());
            data3.setText(alarm.getTime());

            listContent.addView(root);
        }
    }
}
