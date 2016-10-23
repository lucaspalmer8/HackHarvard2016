package com.hackharvard.lucas.textit;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by lucas on 21/10/16.
 */

public class SmsListener extends BroadcastReceiver {

    private DbHelper dbHelper;
    private static String KEY_WORD = "textit";

    @Override
    public void onReceive(Context context, Intent intent) {
        dbHelper = DbHelper.getInstance(context);

        Log.v(this.getClass().toString(), "On receive called.");

        if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(intent.getAction())) {
            for (SmsMessage smsMessage : Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                String messageBody = smsMessage.getMessageBody();
                String creator = smsMessage.getDisplayOriginatingAddress();

                String[] data = messageBody.split("/");
                if (data.length != 8) {
                    return;
                }

                for (int i = 0; i < data.length; i++) {
                    data[i] = data[i].trim();
                }

                String keyword = data[0];
                String monthString = data[1];
                String dayString = data[2];
                String yearString = data[3];
                String hourString = data[4];
                String minuteString = data[5];
                String noon = data[6];
                String message = data[7];

                if (!keyword.toLowerCase().equals(KEY_WORD)) {
                    return;
                }

                /*if (!noon.equals("am") && !noon.equals("pm")) {
                    return;
                }*/

                /*int month;
                int day;
                int year;
                int hour;
                int minute;

                try {
                    month = Integer.parseInt(monthString);
                    day = Integer.parseInt(dayString);
                    year = Integer.parseInt(yearString);
                    hour = Integer.parseInt(hourString);
                    minute = Integer.parseInt(minuteString);

                }
                catch (NumberFormatException ex) {
                    return;
                }*/

                String format = "M/d/y/h/m/a";
                String dateString = monthString + "/" + dayString + "/" + yearString + "/" +
                        hourString + "/" + minuteString + "/" + noon;

                SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.CANADA);
                dateFormat.setLenient(false);
                Date date = null;
                try {
                    date = dateFormat.parse(dateString);
                }
                catch (Exception ex) {
                    return;
                }

                if (date.before(new Date())) {
                    return;
                }

                Contact contact = dbHelper.getContact(creator);
                if (contact == null || !Boolean.valueOf(contact.getAllowInputData())) {
                    return;
                }

                dbHelper.addAlarm(message, creator, dateFormat.format(date), "true");
                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

                Intent alarmIntent = new Intent(context, AlarmListener.class);
                alarmIntent.setAction("com.hackharvard.lucas.textit.ALARM_FIRED");
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), 1, alarmIntent, PendingIntent.FLAG_ONE_SHOT);

                alarmManager.set(AlarmManager.RTC_WAKEUP, date.getTime(), pendingIntent);
            }
        }
    }
}