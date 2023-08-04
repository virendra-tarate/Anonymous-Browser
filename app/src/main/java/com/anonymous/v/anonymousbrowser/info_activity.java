package com.anonymous.v.anonymousbrowser;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class info_activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        //hiding action bar
        getSupportActionBar().hide();



    }

    @Override
    public void onBackPressed() {
        info_activity.this.finish();
        super.onBackPressed();
    }
}