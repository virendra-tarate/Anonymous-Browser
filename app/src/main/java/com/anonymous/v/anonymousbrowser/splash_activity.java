package com.anonymous.v.anonymousbrowser;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

public class splash_activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //removing title bar
        splash_activity.this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);
        
        //removing action bar
        getSupportActionBar().hide();


        //runable method for threading

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //creating intent to switch between activity
                Intent iii = new Intent(splash_activity.this,MainActivity.class);

                //starting activity
                startActivity(iii);

                //remove thi activity from stack after 3.5 sec
                finish();

            }
        },3500);

    }
}