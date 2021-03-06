package com.hackharvard.lucas.textit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by lucas on 21/10/16.
 */

public class AlarmListener extends BroadcastReceiver {

    MediaPlayer mp;
    private DbHelper dbHelper;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v(this.getClass().toString(), "On receive called.");

        dbHelper = DbHelper.getInstance(context);
        long id = intent.getExtras().getLong(SmsListener.INTENT_ALARM_ID);

        Alarm alarm = dbHelper.getAlarm(id);
        if (alarm == null || !Boolean.valueOf(alarm.getActive())) {
            return;
        }

        Toast.makeText(context, "Alarm Alarm Alarm!", Toast.LENGTH_SHORT).show();

        /*Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(context.getApplicationContext(), notification);
        r.play();*/

        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        mp = MediaPlayer.create(context.getApplicationContext(), notification);
        mp.start();
    }
}
