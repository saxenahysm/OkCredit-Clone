package com.android.aaroo.activity.sequritypin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.android.aaroo.MainActivity;
import com.android.aaroo.R;
import com.android.aaroo.helper.AppPrefrence;
import com.hanks.passcodeview.PasscodeView;

public class SequrityPinActivity extends AppCompatActivity {
    PasscodeView passcodeView;
    AppPrefrence appPrefrence;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sequrity_pin);
        appPrefrence = new AppPrefrence(this);
        passcodeView = findViewById(R.id.passcodeView);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        passcodeView.setPasscodeLength(4)
                .setLocalPasscode(appPrefrence.getPinset())
                .setListener(new PasscodeView.PasscodeViewListener() {
                    @Override
                    public void onFail() {
                        Toast.makeText(SequrityPinActivity.this, "Pin is Wrong.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(String number) {

                        startActivity(new Intent(SequrityPinActivity.this, MainActivity.class));
                        finish();

                    }
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}