//Warning
// Unauthorized use, reproduction, or distribution of this code, in whole or in part, without the explicit permission of the owner, is strictly prohibited and may result in severe legal consequences under the relevant IT Act and other applicable laws.
// To use this code, you must first obtain written permission from the owner. For inquiries regarding licensing, collaboration, or any other use of the code, please contact virendratarte22@gmail.com.
// Thank you for respecting the intellectual property rights of the owner.
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
