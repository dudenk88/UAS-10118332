package com.app.BandungHoliday.Main;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.app.BandungHoliday.Destinasi.DestinationActivity;
import com.app.BandungHoliday.R;
//13-10-2021 - 10118332 - Nais Farid - IF8
public class SplashActivity extends AppCompatActivity {

    private int loading=4000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent splash = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(splash);
            }
        },loading);
    }
}