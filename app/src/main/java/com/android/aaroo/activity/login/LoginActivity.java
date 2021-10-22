package com.android.aaroo.activity.login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.aaroo.MainActivity;
import com.android.aaroo.R;
import com.android.aaroo.activity.otp_verify.OtpVerifyActivity;
import com.android.aaroo.fragment.RegisterFragment;
import com.android.aaroo.helper.APIs;
import com.android.aaroo.helper.AppPrefrence;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    public static final String TAG = LoginActivity.class.getSimpleName();

    EditText edt_mobile;
    Button otpRequestBtn;
    String mobileNo,otp,newUserMsg;
     ProgressDialog progressDialog;
    AppPrefrence appPrefrence;
     String android_id;
    private static final int SMS_CONSENT_REQUEST = 2;

    private final BroadcastReceiver smsVerificationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (SmsRetriever.SMS_RETRIEVED_ACTION.equals(intent.getAction())) {
                Bundle extras = intent.getExtras();
                Status smsRetrieverStatus = (Status) extras.get(SmsRetriever.EXTRA_STATUS);
                switch (smsRetrieverStatus.getStatusCode()) {
                    case CommonStatusCodes.SUCCESS:
                        Intent consentIntent = extras.getParcelable(SmsRetriever.EXTRA_CONSENT_INTENT);
                        try {
                            progressDialog.cancel();
                            startActivityForResult(consentIntent, SMS_CONSENT_REQUEST);
                        } catch (ActivityNotFoundException e) {
                        }
                        break;
                    case CommonStatusCodes.TIMEOUT:
                        break;
                }
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        appPrefrence = new AppPrefrence(this);
        progressDialog = new ProgressDialog(this);
        otpRequestBtn = findViewById(R.id.btn_send_otp);
        edt_mobile = findViewById(R.id.edt_mobile);

        if (appPrefrence.getIsLogin().equals("1")) {
            Intent intent =new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        android_id = Settings.Secure.getString(this.getContentResolver(),Settings.Secure.ANDROID_ID);

        appPrefrence.setDeviceId(android_id);

        edt_mobile.setOnEditorActionListener(editorListener);

        edt_mobile.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                if (s.length() ==10) {

                    otpRequestBtn.setEnabled(true);
                    otpRequestBtn.setClickable(true);
                    otpRequestBtn.setBackground(getResources().getDrawable(R.drawable.verify_button_dark));

                }
            }
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                otpRequestBtn.setClickable(false);
                otpRequestBtn.setEnabled(false);
                otpRequestBtn.setBackground(getResources().getDrawable(R.drawable.verify_button_light));

            }
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (s.length() ==10) {
                    otpRequestBtn.setEnabled(true);
                    otpRequestBtn.setClickable(true);
                    otpRequestBtn.setBackground(getResources().getDrawable(R.drawable.verify_button_dark));

                }
            }
        });

        Task<Void> task = SmsRetriever.getClient(LoginActivity.this).startSmsUserConsent(null);
        IntentFilter intentFilter = new IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION);
        registerReceiver(smsVerificationReceiver, intentFilter, SmsRetriever.SEND_PERMISSION, null);

        otpRequestBtn.setClickable(false);
        otpRequestBtn.setEnabled(false);
        otpRequestBtn.setBackground(getResources().getDrawable(R.drawable.verify_button_light));


        otpRequestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mobileNo = edt_mobile.getText().toString().trim();

                if (TextUtils.isEmpty(edt_mobile.getText().toString()) || edt_mobile.length() < 10) {

                    edt_mobile.setError("Invalid Number!");
                    edt_mobile.setFocusable(true);

                    Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Please enter 10 digit mobile number", Snackbar.LENGTH_LONG);
                    snackbar.show();
                    return;

                }else {

                    progressDialog.setMessage("please wait...");
                    progressDialog.show();
                    progressDialog.setCancelable(true);
                    progressDialog.setCanceledOnTouchOutside(false);
                    otpRequestBtn.setEnabled(false);
                    loginUser(mobileNo);
                }

            }
        });


    }
    private void actionDone() {
        mobileNo = edt_mobile.getText().toString().trim();

        if (TextUtils.isEmpty(edt_mobile.getText().toString()) && edt_mobile.length() < 10) {

            edt_mobile.setError("Invalid Number!");
            edt_mobile.setFocusable(true);

            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Please enter 10 digit mobile number", Snackbar.LENGTH_LONG);
            snackbar.show();
            return;

        }
        progressDialog.setMessage("please wait...");
        progressDialog.show();
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(false);
        otpRequestBtn.setEnabled(false);

        loginUser(mobileNo);
    }

    private TextView.OnEditorActionListener editorListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            switch (actionId) {
                case EditorInfo.IME_ACTION_DONE:
                    //Toast.makeText(LoginActivity.this, "Done", Toast.LENGTH_SHORT).show();
                    actionDone();

                    break;
                case EditorInfo.IME_ACTION_SEND:
                    Toast.makeText(LoginActivity.this, "Send", Toast.LENGTH_SHORT).show();
                    break;
            }
            return false;
        }
    };

    private void loginUser(final String mobile) {

        AndroidNetworking.post(APIs.LOGIN_API)
                .addBodyParameter("mobile",mobile)
                .setTag("LOGIN API")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            progressDialog.cancel();
                            otpRequestBtn.setEnabled(true);
                            Log.d(TAG, "onResponse: " + response.toString());
                            if (response.get("success").equals(true)) {

                                String resMsg = response.getString("msg");

                                Intent intent =new Intent(LoginActivity.this,OtpVerifyActivity.class);
                                intent.putExtra("mobile",mobileNo);
                             //   intent.putExtra("otp",otp);
                                startActivity(intent);
                                finish();

                                Toast.makeText(LoginActivity.this,  resMsg, Toast.LENGTH_SHORT).show();

                            } else if (response.get("success").equals(false)) {

                                String resMsg = response.getString("msg");

                                Toast.makeText(LoginActivity.this,  resMsg, Toast.LENGTH_SHORT).show();

                            }

                        } catch (JSONException ex) {
                            progressDialog.cancel();
                            otpRequestBtn.setEnabled(true);
                            ex.printStackTrace();
                        }
                    }
                    @Override
                    public void onError(ANError error) {
                        progressDialog.cancel();
                        otpRequestBtn.setEnabled(true);
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SMS_CONSENT_REQUEST:
                if (resultCode == RESULT_OK) {
                    String message = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE);
                /*    if (message != null) {
                        otp = parseCode(message);

                        Intent intent =new Intent(LoginActivity.this,OtpVerifyActivity.class);
                        intent.putExtra("mobile",mobileNo);
                        intent.putExtra("otp",otp);
                        startActivity(intent);
                        finish();
                    }*/
                }
                break;
        }
    }

    public String parseCode(String message) {
        String code = "";
        try {
            Pattern p = Pattern.compile("\\b\\d{4}\\b");
            Matcher m = p.matcher(message.toString());
            while (m.find()) {
                code = m.group(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return code;
    }

}