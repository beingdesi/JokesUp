package com.vinyentertainment.jokesup;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
/*
Source:
https://android.jlelse.eu/the-complete-android-splash-screen-guide-c7db82bce565
*/
public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // Make sure this is before calling super.onCreate

        setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);

        Log.i("SplashActivity","oncreate worked");
        Intent entryActivityIntent = new Intent(this,EntryActivity.class);
        startActivity(entryActivityIntent);
        finish();
    }
}
