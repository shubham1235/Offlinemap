package com.example.shubham_v.trigentmap;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.shubham_v.trigentmap.Gps.GoogleGpsTracker;

public class LogoActivity extends AppCompatActivity {

    GoogleGpsTracker googleGpsTracker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);


        googleGpsTracker = new GoogleGpsTracker(this);


        Thread welcomeThread = new Thread() {

            @Override
            public void run() {
                try {
                    super.run();
                    sleep(5000);  //Delay of 10 seconds
                } catch (Exception e) {
                } finally {
                    Intent i = new Intent(LogoActivity.this, MainActivity.class);
                    startActivity(i);

                }
            }
        };
        welcomeThread.start();
    }




}
