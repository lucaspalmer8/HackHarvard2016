package com.hackharvard.lucas.nocauseforalarm;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

public class AlarmActivity extends AppCompatActivity implements DbHelper.DbListener {

    DbHelper dbHelper;
    NestedScrollView scrollView;

    @Override
    public void onDataChanged() {
        loadAlarms();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        scrollView = (NestedScrollView)findViewById(R.id.list_view);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
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
    }

    private void loadAlarms() {
        scrollView.removeAllViews();

        List<Alarm> alarms = dbHelper.getAllAlarms();
        Log.v("Number of alarms", String.valueOf(alarms.size()));
        for (Alarm alarm : alarms) {
            View root = getLayoutInflater().inflate(R.layout.alarm_item, null);
            TextView data1 = (TextView)root.findViewById(R.id.data1);
            TextView data2 = (TextView)root.findViewById(R.id.data2);
            TextView data3 = (TextView)root.findViewById(R.id.data3);

            data1.setText(alarm.getDescription());
            data2.setText(alarm.getCreator());
            data3.setText(alarm.getTime());

            scrollView.addView(root);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_alarm, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
