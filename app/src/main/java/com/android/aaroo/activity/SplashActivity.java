package com.android.aaroo.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.android.aaroo.MainActivity;
import com.android.aaroo.R;
import com.android.aaroo.activity.fingerlock.FingerLockActivity;
import com.android.aaroo.activity.intro.IntroActivity;
import com.android.aaroo.activity.login.LoginActivity;
import com.android.aaroo.activity.sequritypin.SequrityPinActivity;
import com.android.aaroo.helper.AppPrefrence;

public class SplashActivity extends AppCompatActivity {
    AppPrefrence appPrefrence;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        appPrefrence = new AppPrefrence(this);

        Log.d("Splash", "onCreate: status > "+appPrefrence.getIsLogin());

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (appPrefrence.getIsLogin().equals("1")) {

                    if (appPrefrence.getFingerLock().equals("1")){
                        Intent intent =new Intent(SplashActivity.this, FingerLockActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else if (appPrefrence.getPinsetStatus().equals("1")){
                        Intent intent =new Intent(SplashActivity.this, SequrityPinActivity.class);
                        startActivity(intent);
                        finish();
                    }else {
                        Intent intent =new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }

                }else if(appPrefrence.getIsLogin().equals("0")) {
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    Intent intent = new Intent(SplashActivity.this, IntroActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, 2000);

    }
}