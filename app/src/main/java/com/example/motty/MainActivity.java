package com.example.motty;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.motty.adapter.YoutubeVideoAdapter;
import com.example.motty.utils.Constants;
import com.example.motty.utils.RecyclerViewOnClickListener;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.motty.utils.Constants.KEY_ALARM_TIME;
import static com.example.motty.utils.Constants.KEY_URL;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MyLOG";

    private EditText urlEditText;
    private TextView timeTextView, dateTextView;
    private ToggleButton toggleButton;

    private AlarmManagerBroadcastReceiver alarm;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private final Calendar alarmDateTime = Calendar.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        sharedPreferences = getApplicationContext().getSharedPreferences(Constants.SHARED_PREF_NAME, 0);
        editor = sharedPreferences.edit();


        alarm = new AlarmManagerBroadcastReceiver();
        urlEditText = findViewById(R.id.urlTextView);
        dateTextView = findViewById(R.id.dateTextView);
        dateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDateForAlarm();
            }
        });
        timeTextView = findViewById(R.id.timeTextView);
        timeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getTimeForAlarm();
            }
        });
        toggleButton = findViewById(R.id.alarmToggleButton);
        toggleButton.setChecked(Boolean.FALSE);
        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean setAlarmOn = toggleButton.isChecked();
                if (saveAlarmSettings()) {
                    toggleAlarm(setAlarmOn);
                }

            }
        });

        handleVideoShareByOtherApps();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.stopService) {
            // do something
            stopService(new Intent(this, AlarmHandlerService.class));
            return true;
        }
        return super.onContextItemSelected(item);
    }

    private void handleVideoShareByOtherApps() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String urlToPlay = extras.getString(Intent.EXTRA_TEXT, Constants.EMPTY_STRING);
            Log.i(TAG, "Got shared message : " + urlToPlay);
            //TODO : handle share by apps other than youtube
            String videoId = getVideoIdFromYoutubeUrl(urlToPlay);
            urlEditText.setText(videoId);
            Toast.makeText(getApplicationContext(), "Setting video id :  " + videoId, Toast.LENGTH_LONG).show();
        } else {
            Log.i(TAG, "Received nothing from other apps ");
        }
    }

    private String getVideoIdFromYoutubeUrl(String url) {
        String videoId = Constants.EMPTY_STRING;
        String regex = "http(?:s)?:\\/\\/(?:m.)?(?:www\\.)?youtu(?:\\.be\\/|be\\.com\\/(?:watch\\?(?:feature=youtu.be\\&)?v=|v\\/|embed\\/|user\\/(?:[\\w#]+\\/)+))([^&#?\\n]+)";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            videoId = matcher.group(1);
        }
        return videoId;
    }

    @Override
    protected void onStart() {
        super.onStart();
        alarmDateTime.setTimeInMillis(System.currentTimeMillis());
    }

    private void getDateForAlarm() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                dateTextView.setText(dayOfMonth + " : " + month + " : " + year);
                alarmDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                alarmDateTime.set(Calendar.MONTH, month);
                alarmDateTime.set(Calendar.YEAR, year);
            }
        }, alarmDateTime.get(Calendar.YEAR), alarmDateTime.get(Calendar.MONTH), alarmDateTime.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void getTimeForAlarm() {
        int hour = alarmDateTime.get(Calendar.HOUR_OF_DAY);
        int minute = alarmDateTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                timeTextView.setText(selectedHour + ":" + selectedMinute);
                alarmDateTime.set(Calendar.HOUR_OF_DAY, selectedHour);
                alarmDateTime.set(Calendar.MINUTE, selectedMinute);
            }
        }, hour, minute, true);//Yes 24 hour time
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }

    private Boolean saveAlarmSettings() {
        String url = urlEditText.getText().toString();
        if (url.isEmpty()) {
            Toast.makeText(getApplicationContext(), "give url..", Toast.LENGTH_LONG).show();
            return Boolean.FALSE;
        }
        editor.putString(KEY_URL, url);
        editor.putLong(KEY_ALARM_TIME, alarmDateTime.getTimeInMillis());
        editor.commit();

        return Boolean.TRUE;
    }

    private void toggleAlarm(Boolean setAlarmOn) {
        if (setAlarmOn) {
            Log.d(TAG, "alarm set to : " + alarmDateTime.toString());
            Toast.makeText(getApplicationContext(), "alarm set to : " + alarmDateTime.toString(), Toast.LENGTH_LONG).show();
            setAlarm();
        } else {
            cancelAlarm();
        }
    }


//    public void setAlarm() {
//        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//        Intent intent = new Intent(MainActivity.this, AlarmManagerBroadcastReceiver.class);
//        PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
//        am.setRepeating(AlarmManager.RTC_WAKEUP, alarmDateTime.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pi);
//    }
//
//    public void cancelAlarm() {
//        Intent intent = new Intent(MainActivity.this, AlarmManagerBroadcastReceiver.class);
//        PendingIntent sender = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
//        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//        alarmManager.cancel(sender);
//    }

    public void setAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(MainActivity.this, AlarmHandlerService.class);
        intent.putExtra(KEY_URL,urlEditText.getText().toString());
        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarmDateTime.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    public void cancelAlarm() {
        Intent intent = new Intent(MainActivity.this, AlarmHandlerService.class);
        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }


}


