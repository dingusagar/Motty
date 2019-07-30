package com.example.motty;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import com.example.motty.utils.Constants;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AlarmHandlerService extends Service {

    private static final String TAG = "AlarmHandlerService";

    public AlarmHandlerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG,"Inside onStartCommand..creating service thread..");
        PowerManager pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MOTTY:LOCK");
        //Acquire the lock
        wl.acquire();

        //You can do the processing here update the widget/remote views.
        Bundle extras = intent.getExtras();
        String urlToPlay = Constants.DEFAULT_URL;
        if(extras != null){
            urlToPlay = extras.getString(Constants.KEY_URL,Constants.DEFAULT_URL);
        }

//        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREF_NAME, 0);
//        String urlToPlay = sharedPreferences.getString(Constants.KEY_URL, Constants.DEFAULT_URL);

        Intent dialogIntent = new Intent(this, YoutubeMainActivity.class);
        dialogIntent.putExtra(Constants.KEY_URL,urlToPlay);
        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(dialogIntent);


        //Release the lock
        wl.release();
        stopSelf();
        return Service.START_STICKY;
    }
}
