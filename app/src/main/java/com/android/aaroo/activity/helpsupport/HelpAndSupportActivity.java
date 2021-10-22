package com.android.aaroo.activity.helpsupport;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.LinearLayout;

import com.android.aaroo.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class HelpAndSupportActivity extends AppCompatActivity {

    LinearLayout call1, call2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_and_support);

        getSupportActionBar().setTitle("Help and Support");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        call1 = findViewById(R.id.cont1);
        call2 = findViewById(R.id.cont2);

        call1.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + "+916268105112"));
            startActivity(intent);
        });

        call2.setOnClickListener(v -> {
            Intent intent2 = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + "+918871181890"));
            startActivity(intent2);
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