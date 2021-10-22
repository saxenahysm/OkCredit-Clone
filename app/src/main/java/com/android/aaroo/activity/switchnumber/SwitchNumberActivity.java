package com.android.aaroo.activity.switchnumber;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.ProgressDialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.aaroo.R;
import com.android.aaroo.fragment.OtpVerifyDialogFragment;
import com.android.aaroo.helper.APIs;
import com.android.aaroo.helper.AppPrefrence;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONException;
import org.json.JSONObject;

public class SwitchNumberActivity extends AppCompatActivity {
    AppPrefrence appPrefrence;
    public static final String TAG = SwitchNumberActivity.class.getSimpleName();
    ProgressDialog progressDialog;
    TextView mobiletv;
    LinearLayout ll_verifyMob;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_switch_number);

        getSupportActionBar().setTitle("Change Number");
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      getSupportActionBar().setDisplayShowHomeEnabled(true);

        progressDialog = new ProgressDialog(this);
        appPrefrence = new AppPrefrence(this);

        mobiletv = findViewById(R.id.tvMobile);
        ll_verifyMob = findViewById(R.id.ll_verifyMoblie);


        mobiletv.setText(getResources().getString(R.string.msg3)+" "+appPrefrence.getMobile());

        ll_verifyMob.setOnClickListener(v -> {

            progressDialog.setMessage("please wait...");
            progressDialog.show();
            progressDialog.setCancelable(true);
            progressDialog.setCanceledOnTouchOutside(false);
            getVerifyUser();
        });

    }

    private void getVerifyUser() {

        JSONObject request_data = new JSONObject();
        try {
            request_data.put("userid", appPrefrence.getUserID());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        AndroidNetworking.post(APIs.CHANGE_OTP_API)
                .addJSONObjectBody(request_data)
               // .addBodyParameter("userid", appPrefrence.getUserID())
                .setTag("Verify User API")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            progressDialog.cancel();
                            Log.d(TAG, "onResponse: " + response.toString());

                            if (response.get("success").equals(true)) {

                                String resMsg = response.getString("msg");
                                Toast.makeText(SwitchNumberActivity.this, resMsg, Toast.LENGTH_SHORT).show();

                                OtpVerifyDialogFragment otpVerifyDialogFragment = new OtpVerifyDialogFragment();
                                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                                Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
                                if (prev != null) {
                                    ft.remove(prev);
                                }
                                ft.addToBackStack(null);
                                otpVerifyDialogFragment.show(ft, "dialog");


                            } else if (response.get("success").equals(false)) {

                                String resMsg = response.getString("msg");

                                Toast.makeText(SwitchNumberActivity.this, resMsg, Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException ex) {
                            progressDialog.cancel();
                            ex.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        progressDialog.cancel();
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