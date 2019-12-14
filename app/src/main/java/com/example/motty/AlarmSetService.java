package com.example.motty;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.motty.utils.Constants;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.example.motty.utils.Constants.KEY_ALARM_TIME;

public class AlarmSetService extends Service {

    private static final String TAG = "AlarmSetService";

    public AlarmSetService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG,"Inside onStartCommand..creating service thread..");

        Intent notificationIntent = new Intent(this,MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,notificationIntent,0);

        Notification notification = new NotificationCompat.Builder(this,App.CHANNEL_ID)
                .setContentTitle("Alarm Set")
                .setContentText("Motty has set your alarm..Be ready to wake up when I call you")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .build();

        long timeToAlarm = intent.getExtras().getLong(KEY_ALARM_TIME,0);


        setAlarmAt(timeToAlarm);

        startForeground(1,notification);

//        PowerManager pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
//        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MOTTY:LOCK");
//        //Acquire the lock
//        wl.acquire();
//
//        //You can do the processing here update the widget/remote views.
//        Bundle extras = intent.getExtras();
//        String urlToPlay = Constants.DEFAULT_URL;
//        if(extras != null){
//            urlToPlay = extras.getString(Constants.KEY_URL,Constants.DEFAULT_URL);
//        }
//
////        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREF_NAME, 0);
////        String urlToPlay = sharedPreferences.getString(Constants.KEY_URL, Constants.DEFAULT_URL);
//
//        Intent dialogIntent = new Intent(this, YoutubeMainActivity.class);
//        dialogIntent.putExtra(Constants.KEY_URL,urlToPlay);
//        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(dialogIntent);
//
//
//        //Release the lock
//        wl.release();
//        stopSelf();
        return Service.START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        killAllAlarms();
        super.onDestroy();
    }

    private void killAllAlarms() {

        Intent intent = new Intent(this, AlarmManagerBroadcastReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }

    private void setAlarmAt(long timeToAlarm) {
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent alarmHandlerIntent = new Intent(this, AlarmManagerBroadcastReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(), 0, alarmHandlerIntent, 0);
        am.setRepeating(AlarmManager.RTC_WAKEUP, timeToAlarm, AlarmManager.INTERVAL_DAY, pi);
    }


}
