package com.hackharvard.lucas.textit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.provider.Telephony;
import android.telephony.SmsMessage;

/**
 * Created by lucas on 21/10/16.
 */

public class SmsListener extends BroadcastReceiver {

    DbHelper dbHelper;

    @Override
    public void onReceive(Context context, Intent intent) {
        dbHelper = DbHelper.getInstance(context);

        System.out.println("On REceiveeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee");

        if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(intent.getAction())) {
            for (SmsMessage smsMessage : Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                String messageBody = smsMessage.getMessageBody();
                String creator = smsMessage.getDisplayOriginatingAddress();
                dbHelper.addAlarm(messageBody, creator, null);
            }
        }
    }
}