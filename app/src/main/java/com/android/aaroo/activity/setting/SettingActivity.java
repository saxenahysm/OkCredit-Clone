package com.android.aaroo.activity.setting;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.aaroo.R;
import com.android.aaroo.activity.profile.ProfileActivity;
import com.android.aaroo.activity.switchnumber.SwitchNumberActivity;
import com.android.aaroo.helper.AppPrefrence;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class SettingActivity extends AppCompatActivity {

    LinearLayout ll_profile, ll_signout, ll_changeNumber, ll_fingerPrint, ll_seqPin;
    AppPrefrence appPrefrence;

    SwitchCompat switchFinger, switchPin;
    BottomSheetDialog bottomSheetPinSet;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        appPrefrence = new AppPrefrence(this);

        getSupportActionBar().setTitle("Setting");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        back = findViewById(R.id.back);

        ll_profile = findViewById(R.id.ll_profile);
        ll_seqPin = findViewById(R.id.ll_seqPin);
        ll_changeNumber = findViewById(R.id.ll_mobChange);
        ll_fingerPrint = findViewById(R.id.ll_fingerLock);
        ll_signout = findViewById(R.id.ll_logout);
        switchPin = findViewById(R.id.sw_seqPin);
        switchFinger = findViewById(R.id.sw_finger_lock);


        ll_profile.setOnClickListener(v -> {

            startActivity(new Intent(SettingActivity.this, ProfileActivity.class));

        });
        ll_changeNumber.setOnClickListener(v -> {
            startActivity(new Intent(SettingActivity.this, SwitchNumberActivity.class));

        });


        if (appPrefrence.getPinsetStatus().equals("1")) {
            switchPin.setChecked(true); // true is open, false is close.

        } else {
            switchPin.setChecked(false); // true is open, false is close.
        }


        if (appPrefrence.getFingerLock().equals("1")) {
            switchFinger.setChecked(true); // true is open, false is close.

        } else {
            switchFinger.setChecked(false); // true is open, false is close.
        }


        switchFinger.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {

                appPrefrence.setFingerLock("1");
                appPrefrence.setPinsetStatus("0");

            } else {
                //close job.
                appPrefrence.setFingerLock("0");
            }
        });


        switchPin.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                //open job.
                // startActivity(new Intent(SettingActivity.this, SequrityPinActivity.class));
                openBottomSheet();
            } else {
                //close job.
                appPrefrence.setPinsetStatus("0");
            }
        });
    }


    private void openBottomSheet() {

        bottomSheetPinSet = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        View bottomSheetView = LayoutInflater.from(this)
                .inflate(R.layout.pinset_layout,
                        (ConstraintLayout) findViewById(R.id.pin_cons));
        bottomSheetView.setOnClickListener(null);

        EditText pinEdt = (EditText) bottomSheetView.findViewById(R.id.edt_pin);
        EditText cPinEdt = (EditText) bottomSheetView.findViewById(R.id.edt_cPin);
        Button btn_set = (Button) bottomSheetView.findViewById(R.id.card_view_submit);


        btn_set.setOnClickListener(v -> {
            if (pinEdt.getText().toString().equals(cPinEdt.getText().toString())) {
                bottomSheetPinSet.cancel();
                appPrefrence.setPinset(cPinEdt.getText().toString());
                appPrefrence.setPinsetStatus("1");
                appPrefrence.setFingerLock("0");
            } else {
                Toast.makeText(this, "Does not match!", Toast.LENGTH_SHORT).show();
            }

        });

        bottomSheetPinSet.setContentView(bottomSheetView);
        bottomSheetPinSet.show();
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