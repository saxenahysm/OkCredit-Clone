package com.android.aaroo.activity.fingerlock;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.aaroo.MainActivity;
import com.android.aaroo.R;
import com.android.aaroo.activity.SplashActivity;

import java.util.concurrent.Executor;

public class FingerLockActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finger_lock);

        ImageView fingerImg = findViewById(R.id.fingerImg);
        TextView msg = findViewById(R.id.msg);

        getSupportActionBar().setTitle("Finger Lock");

        BiometricManager biometricManager = BiometricManager.from(this);
        switch (biometricManager.canAuthenticate()){
            case BiometricManager.BIOMETRIC_SUCCESS:

                msg.setText("You can use the fingerprint sensor to login.");
                msg.setTextColor(Color.parseColor("#Fafafa"));
                Intent intent =new Intent(FingerLockActivity.this, MainActivity.class);
                startActivity(intent);
                finish();

                break;

            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:

                msg.setText("The device don't have a fingerprint sensor.");
                msg.setTextColor(Color.parseColor("#E91E1E"));
               // fingerImg.setVisibility(View.GONE);
                break;

            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:

                msg.setText("The biometric sensor is currently unavailable.");
               // fingerImg.setVisibility(View.GONE);
                break;

            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:

                msg.setText("Your device don't have any fingerprint saved ,please check your security settings.");
               // fingerImg.setVisibility(View.GONE);
                break;
        }

        Executor executor = ContextCompat.getMainExecutor(this);

        BiometricPrompt biometricPrompt =new BiometricPrompt(FingerLockActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);

                Toast.makeText(FingerLockActivity.this, "Login Success ", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
            }
        });

        BiometricPrompt.PromptInfo promptInfo= new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Login")
                .setDescription("Uses your fingerprint to login to your app")
                .setNegativeButtonText("Cancel")
                .build();


        fingerImg.setOnClickListener(v -> {
            biometricPrompt.authenticate(promptInfo);
        });

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
    }
}