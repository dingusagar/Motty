package com.example.motty;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.widget.Toast;

import com.example.motty.utils.Constants;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

public class OpenVideoService extends IntentService {

    public OpenVideoService() {
        super("OpenVideoService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        String url = extras.getString(Constants.KEY_URL,Constants.DEFAULT_URL);
        Intent yt_play = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        yt_play.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if (yt_play.resolveActivity(getPackageManager()) != null) {
            startActivity(yt_play);
        }
    }
}
