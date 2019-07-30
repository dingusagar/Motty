package com.example.motty;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PowerManager;

import com.example.motty.utils.Constants;

public class AlarmManagerBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
//        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
//        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MOTTY:LOCK");
//        //Acquire the lock
//        wl.acquire();
//
//        //You can do the processing here update the widget/remote views.
//        Bundle extras = intent.getExtras();
//        StringBuilder msgStr = new StringBuilder();
//
//
//        Format formatter = new SimpleDateFormat("hh:mm:ss a");
//        msgStr.append(formatter.format(new Date()));
//
//        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREF_NAME, 0);
//        String urlToPlay = sharedPreferences.getString(Constants.KEY_URL, Constants.DEFAULT_URL);
//
//        Intent dialogIntent = new Intent(context, YoutubeMainActivity.class);
//        dialogIntent.putExtra(Constants.KEY_URL,urlToPlay);
//        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        context.startActivity(dialogIntent);
//
//
//
//        //Release the lock
//        wl.release();

    }


}
