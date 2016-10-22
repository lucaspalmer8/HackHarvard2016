package com.hackharvard.lucas.textit;

import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hackharvard.lucas.textit.DbHelper;

import java.util.List;

public class AlarmActivity extends AppCompatActivity implements DbHelper.DbListener {

    DbHelper dbHelper;
    LinearLayout listContent;

    @Override
    public void onDataChanged() {
        Log.v("On Data Set Changed", "Method call");
        loadAlarms();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.hackharvard.lucas.textit.R.layout.activity_alarm);
        Toolbar toolbar = (Toolbar) findViewById(com.hackharvard.lucas.textit.R.id.toolbar);
        setSupportActionBar(toolbar);

        listContent = (LinearLayout)findViewById(com.hackharvard.lucas.textit.R.id.list_content);

        FloatingActionButton fab = (FloatingActionButton) findViewById(com.hackharvard.lucas.textit.R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        dbHelper = DbHelper.getInstance(this);
        dbHelper.addListener(this);
        loadAlarms();

        Intent intent = new Intent("android.provider.Telephony.SMS_DELIVER");
        List<ResolveInfo> infos = getPackageManager().queryBroadcastReceivers(intent, 0);
        for (ResolveInfo info : infos) {
            System.out.println("Receiver name:" + info.activityInfo.name + "; priority=" + info.priority);
        }
    }

    private void loadAlarms() {
        listContent.removeAllViews();

        List<Alarm> alarms = dbHelper.getAllAlarms();
        Log.v("Number of alarms", String.valueOf(alarms.size()));
        for (Alarm alarm : alarms) {
            View root = getLayoutInflater().inflate(com.hackharvard.lucas.textit.R.layout.alarm_item, null);
            TextView data1 = (TextView)root.findViewById(com.hackharvard.lucas.textit.R.id.data1);
            TextView data2 = (TextView)root.findViewById(com.hackharvard.lucas.textit.R.id.data2);
            TextView data3 = (TextView)root.findViewById(com.hackharvard.lucas.textit.R.id.data3);

            data1.setText(alarm.getDescription());
            data2.setText(alarm.getCreator());
            data3.setText(alarm.getTime());

            listContent.addView(root);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(com.hackharvard.lucas.textit.R.menu.menu_alarm, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == com.hackharvard.lucas.textit.R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
