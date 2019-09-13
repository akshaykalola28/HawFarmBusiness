package com.project.hawfarmbusiness;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Thread timerThread = new Thread() {
            public void run() {
                try {
                    sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    Intent intent = new Intent(SplashScreenActivity.this, LogInActivity.class);
                    startActivity(intent);
                }
            }
        };

        /*Thread intentThread = new Thread(){
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreenActivity.this,LogInActivity.class);
                startActivity(intent);
            }
        };*/
        timerThread.start();
    }
}
