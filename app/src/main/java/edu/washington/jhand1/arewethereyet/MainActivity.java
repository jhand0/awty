package edu.washington.jhand1.arewethereyet;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {
    private boolean startPressed; //determines whether alarm is currently on or off
    private AlarmManager am;
    private PendingIntent pi;
    private int interval; //milliseconds between messages
    private String message;
    private String phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startPressed = false;
        EditText phone = (EditText) findViewById(R.id.edtPhone);
        phone.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        BroadcastReceiver alarmReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Toast.makeText(MainActivity.this, phoneNumber + ": " + message,
                        Toast.LENGTH_SHORT).show();
            }
        };
        registerReceiver(alarmReceiver, new IntentFilter("edu.washington.jhand1.alarmsOnly"));

        Intent i = new Intent();
        i.setAction("edu.washington.jhand1.alarmsOnly");
        pi = PendingIntent.getBroadcast(this, 0, i, 0);

        final Button start = (Button) findViewById(R.id.btnStart);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!startPressed) {
                    getUserInput();
                    if (message != null && phoneNumber != null && interval > 0) {
                        am.setRepeating(AlarmManager.RTC, System.currentTimeMillis() - interval,
                                interval, pi);
                        start.setText("Stop");
                        startPressed = !startPressed;
                    }
                } else {
                    start.setText("Start");
                    startPressed = !startPressed;
                    am.cancel(pi);
                }
            }
        });
    }

    private void getUserInput() {
        EditText message = (EditText) findViewById(R.id.edtMessage);
        EditText phoneNumber = (EditText) findViewById(R.id.edtPhone);
        EditText minutes = (EditText) findViewById(R.id.edtMinutes);

        this.message = message.getText().toString();
        this.phoneNumber = phoneNumber.getText().toString();
        this.interval = Integer.parseInt(minutes.getText().toString()) * 60000;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        am.cancel(pi);
        pi.cancel();
    }

}
