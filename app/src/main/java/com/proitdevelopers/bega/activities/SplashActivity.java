package com.proitdevelopers.bega.activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;

import com.proitdevelopers.bega.helper.MetodosUsados;
import com.proitdevelopers.bega.localDB.AppPref;
import com.proitdevelopers.bega.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MetodosUsados.changeStatusBarColor(this, ContextCompat.getColor(this, R.color.white));
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

//                if (TextUtils.isEmpty(AppPref.getInstance(SplashActivity.this).getAuthToken())) {
                if (TextUtils.isEmpty(AppPref.getInstance().getAuthToken())) {
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                    return;
                }

//                if (!TextUtils.isEmpty(AppPref.getInstance(SplashActivity.this).getAuthToken())) {
                if (!TextUtils.isEmpty(AppPref.getInstance().getAuthToken())) {

                    launchHomeScreen();
                }


            }
        },3000);
    }

    private void launchHomeScreen() {
        Intent intent = new Intent(SplashActivity.this, MenuActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
